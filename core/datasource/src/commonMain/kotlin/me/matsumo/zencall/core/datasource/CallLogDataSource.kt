package me.matsumo.zencall.core.datasource

import me.matsumo.zencall.core.model.call.CallLog

interface CallLogDataSource {
    suspend fun getCallLogs(limit: Int, offset: Int = 0): List<CallLog>
}
