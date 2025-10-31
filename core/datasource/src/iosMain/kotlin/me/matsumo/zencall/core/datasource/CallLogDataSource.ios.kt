package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.CallLog

class IosCallLogDataSource : CallLogDataSource {
    @Suppress("UNUSED_PARAMETER")
    override suspend fun getCallLogs(limit: Int, offset: Int): List<CallLog> = emptyList()
}
