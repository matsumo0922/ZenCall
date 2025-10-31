package me.matsumo.zencall.core.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.repository.ContactsRepository

class CallLogPagingSource(
    private val contactsRepository: ContactsRepository,
) : PagingSource<Int, CallLog>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CallLog> {
        return runCatching { contactsRepository.getCallLogs(params.key ?: 0) }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = null,
                    nextKey = (params.key ?: 0) + it.size
                )
            },
            onFailure = {
                LoadResult.Error(it)
            },
        )
    }

    override fun getRefreshKey(state: PagingState<Int, CallLog>): Int? = null
}