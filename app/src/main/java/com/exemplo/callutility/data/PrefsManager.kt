package com.exemplo.callutility.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class BlockedCall(
    val number: String,
    val timestamp: Long,
    val reason: String = "Bloqueado no Chip (SIM)"
)

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("call_shield_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BLOCK_SIM = "key_block_sim"
        private const val KEY_RECORD_CALLS = "key_record_calls"
        private const val KEY_BLOCKED_COUNT = "key_blocked_count"
        private const val KEY_RECORDINGS_COUNT = "key_recordings_count"
        private const val KEY_BLOCKED_LOGS = "key_blocked_logs"
    }

    var isBlockSimEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_SIM, false)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_SIM, value).apply()

    var isRecordCallsEnabled: Boolean
        get() = prefs.getBoolean(KEY_RECORD_CALLS, false)
        set(value) = prefs.edit().putBoolean(KEY_RECORD_CALLS, value).apply()

    var blockedCallsCount: Int
        get() = prefs.getInt(KEY_BLOCKED_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_BLOCKED_COUNT, value).apply()

    var recordingsCount: Int
        get() = prefs.getInt(KEY_RECORDINGS_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_RECORDINGS_COUNT, value).apply()

    fun addBlockedCall(number: String, timestamp: Long = System.currentTimeMillis()) {
        blockedCallsCount++

        val logs = getBlockedCalls().toMutableList()
        // Adiciona no início da lista (mais recente primeiro)
        logs.add(0, BlockedCall(number, timestamp))

        // Mantém os últimos 50 registros
        val trimmedLogs = if (logs.size > 50) logs.take(50) else logs

        val jsonArray = JSONArray()
        for (item in trimmedLogs) {
            val obj = JSONObject()
            obj.put("number", item.number)
            obj.put("timestamp", item.timestamp)
            obj.put("reason", item.reason)
            jsonArray.put(obj)
        }

        prefs.edit().putString(KEY_BLOCKED_LOGS, jsonArray.toString()).apply()
    }

    fun getBlockedCalls(): List<BlockedCall> {
        val jsonStr = prefs.getString(KEY_BLOCKED_LOGS, null) ?: return emptyList()
        val list = mutableListOf<BlockedCall>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val number = obj.optString("number", "Desconhecido")
                val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                val reason = obj.optString("reason", "Bloqueado no Chip (SIM)")
                list.add(BlockedCall(number, timestamp, reason))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun clearBlockedCalls() {
        prefs.edit()
            .putInt(KEY_BLOCKED_COUNT, 0)
            .remove(KEY_BLOCKED_LOGS)
            .apply()
    }
}
