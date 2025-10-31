package me.matsumo.zencall.core.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.matsumo.zencall.core.common.utils.suspendRunCatching
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.repository.ContactsRepository
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours

class CallLogPagingSource(
    private val contactsRepository: ContactsRepository,
) : PagingSource<Int, CallLog>() {

    // Keep the trailing group so we can merge it with the next page when a cluster spans the page boundary.
    private var pendingGroup: MutableList<CallLog>? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CallLog> {
        if (params is LoadParams.Refresh) {
            pendingGroup = null
        }

        val offset = params.key ?: 0
        val loadSize = params.loadSize.coerceAtLeast(1)

        return suspendRunCatching {
            contactsRepository.getCallLogs(
                limit = loadSize,
                offset = offset,
            )
        }.fold(
            onSuccess = { raw ->
                val isEnd = raw.size < loadSize
                val (grouped, remainingPending) = aggregateCallLogs(raw, isEnd)
                pendingGroup = remainingPending

                LoadResult.Page(
                    data = grouped,
                    prevKey = null,
                    nextKey = if (raw.isEmpty()) null else offset + raw.size,
                )
            },
            onFailure = { LoadResult.Error(it) },
        )
    }

    override fun getRefreshKey(state: PagingState<Int, CallLog>): Int? = null

    private fun aggregateCallLogs(
        raw: List<CallLog>,
        isEnd: Boolean,
    ): Pair<List<CallLog>, MutableList<CallLog>?> {
        val combined = buildList {
            pendingGroup?.let { addAll(it) }
            addAll(raw)
        }

        if (combined.isEmpty()) {
            return emptyList<CallLog>() to null
        }

        val result = mutableListOf<CallLog>()
        var currentGroup = mutableListOf<CallLog>()

        for (log in combined) {
            if (currentGroup.isEmpty()) {
                currentGroup = mutableListOf(log)
                continue
            }

            val anchor = currentGroup.first()
            if (shouldGroup(anchor, log)) {
                currentGroup.add(log)
            } else {
                result.add(mergeGroup(currentGroup))
                currentGroup = mutableListOf(log)
            }
        }

        var pending: MutableList<CallLog>? = null
        if (currentGroup.isNotEmpty()) {
            if (isEnd) {
                result.add(mergeGroup(currentGroup))
            } else {
                pending = currentGroup
            }
        }

        return result to pending
    }

    private fun mergeGroup(group: List<CallLog>): CallLog {
        val latest = group.first()
        val totalCount = group.sumOf { it.groupedCount }
        return latest.copy(groupedCount = totalCount)
    }

    private fun shouldGroup(base: CallLog, candidate: CallLog): Boolean {
        val baseNumber = base.number?.takeIf { it.isNotBlank() } ?: return false
        val candidateNumber = candidate.number?.takeIf { it.isNotBlank() } ?: return false

        if (baseNumber != candidateNumber) {
            return false
        }

        val duration = abs(base.dateMillis - candidate.dateMillis)
        return duration <= GROUP_WINDOW_MILLIS
    }

    companion object {
        private val GROUP_WINDOW_MILLIS = 2.hours.inWholeMilliseconds
    }
}
