package com.exemplo.callutility.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.exemplo.callutility.data.PrefsManager
import com.exemplo.callutility.service.CallRecorderService

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val prefs = PrefsManager(context)
        if (!prefs.isRecordCallsEnabled) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "Chamada"

        when (state) {
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                // Chamada atendida / em andamento: Inicia gravação
                val serviceIntent = Intent(context, CallRecorderService::class.java).apply {
                    putExtra("EXTRA_PHONE_NUMBER", incomingNumber)
                    putExtra("EXTRA_FORMAT", "M4A")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                // Chamada finalizada: Encerra serviço de gravação
                val serviceIntent = Intent(context, CallRecorderService::class.java)
                context.stopService(serviceIntent)
            }
        }
    }
}
