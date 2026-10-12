package com.exemplo.callutility.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.exemplo.callutility.data.PrefsManager

class SimCallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val prefs = PrefsManager(this)

        // Se o usuário desativou a proteção no app, não bloqueia
        if (!prefs.isBlockSimEnabled) {
            val allowResponse = CallResponse.Builder().build()
            respondToCall(callDetails, allowResponse)
            return
        }

        if (callDetails.callDirection == Call.Details.DIRECTION_INCOMING) {
            val rawNumber = callDetails.handle?.schemeSpecificPart ?: "Número Desconhecido"

            // Registra a chamada bloqueada nas preferências
            prefs.addBlockedCall(rawNumber, System.currentTimeMillis())

            val blockResponse = CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()

            respondToCall(callDetails, blockResponse)
        }
    }
}
