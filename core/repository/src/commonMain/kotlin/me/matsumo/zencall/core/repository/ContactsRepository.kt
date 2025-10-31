package me.matsumo.zencall.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import me.matsumo.zencall.core.datasource.CallLogDataSource
import me.matsumo.zencall.core.datasource.ContactsDataSource
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.repository.paging.CallLogPagingSource

class ContactsRepository(
    private val contactsDataSource: ContactsDataSource,
    private val callLogDataSource: CallLogDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())

    suspend fun getContactByNumber(phoneNumber: String) = contactsDataSource.getContactByPhoneNumber(phoneNumber)

    suspend fun getCallLogs(offset: Int = 0): List<CallLog> = callLogDataSource.getCallLogs(
        limit = LIMIT,
        offset = offset,
    )

    fun getCallLogsPager(): Flow<PagingData<CallLog>> = Pager(
        config = PagingConfig(pageSize = LIMIT),
        initialKey = null,
        pagingSourceFactory = { CallLogPagingSource(this) },
    )
        .flow
        .cachedIn(scope)

    companion object {
        private const val LIMIT = 50
    }
}