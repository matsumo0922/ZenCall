package me.matsumo.zencall.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.widget.Toast

class IncomingCallService: CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        callDetails.handle.schemeSpecificPart.also {
            Toast.makeText(baseContext, "Calling! $it", Toast.LENGTH_LONG).show()
        }
    }
}