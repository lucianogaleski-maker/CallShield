package com.exemplo.callutility.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.exemplo.callutility.R
import com.exemplo.callutility.data.PrefsManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.materialswitch.MaterialSwitch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: PrefsManager

    private lateinit var switchBlock: MaterialSwitch
    private lateinit var switchRecord: MaterialSwitch
    private lateinit var tvProtectionStatus: TextView
    private lateinit var badgeRoleStatus: TextView
    private lateinit var tvBlockedCount: TextView
    private lateinit var tvRecordingsCount: TextView
    private lateinit var cardBatteryWarning: MaterialCardView
    private lateinit var btnIgnoreBatteryOpt: Button
    private lateinit var containerCallLogs: LinearLayout
    private lateinit var tvEmptyLogs: TextView
    private lateinit var btnClearHistory: Button
    private lateinit var btnSimulateBlock: Button
    private lateinit var btnUninstall: Button

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            prefs.isRecordCallsEnabled = true
            switchRecord.isChecked = true
            Toast.makeText(this, "Permissão de microfone concedida", Toast.LENGTH_SHORT).show()
        } else {
            prefs.isRecordCallsEnabled = false
            switchRecord.isChecked = false
            Toast.makeText(this, "Permissão de microfone necessária para gravação", Toast.LENGTH_LONG).show()
        }
        updateDashboardUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PrefsManager(this)
        bindViews()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        updateDashboardUI()
        checkBatteryOptimization()
    }

    private fun bindViews() {
        switchBlock = findViewById(R.id.switchBlockCalls)
        switchRecord = findViewById(R.id.switchRecordCalls)
        tvProtectionStatus = findViewById(R.id.tvProtectionStatus)
        badgeRoleStatus = findViewById(R.id.badgeRoleStatus)
        tvBlockedCount = findViewById(R.id.tvBlockedCount)
        tvRecordingsCount = findViewById(R.id.tvRecordingsCount)
        cardBatteryWarning = findViewById(R.id.cardBatteryWarning)
        btnIgnoreBatteryOpt = findViewById(R.id.btnIgnoreBatteryOpt)
        containerCallLogs = findViewById(R.id.containerCallLogs)
        tvEmptyLogs = findViewById(R.id.tvEmptyLogs)
        btnClearHistory = findViewById(R.id.btnClearHistory)
        btnSimulateBlock = findViewById(R.id.btnSimulateBlock)
        btnUninstall = findViewById(R.id.btnUninstall)
    }

    private fun setupListeners() {
        // Switch de Bloqueio SIM com persistência
        switchBlock.setOnCheckedChangeListener { _, isChecked ->
            prefs.isBlockSimEnabled = isChecked
            if (isChecked) {
                requestCallScreeningRole()
            }
            updateDashboardUI()
        }

        // Switch de Gravação com persistência
        switchRecord.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasAudioPermission()) {
                    prefs.isRecordCallsEnabled = true
                } else {
                    requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            } else {
                prefs.isRecordCallsEnabled = false
            }
            updateDashboardUI()
        }

        // Otimização de bateria
        btnIgnoreBatteryOpt.setOnClickListener {
            requestIgnoreBatteryOptimizations()
        }

        // Botão de simular chamada para teste
        btnSimulateBlock.setOnClickListener {
            val randomPhone = "+55 (11) 9" + (1000..9999).random() + "-" + (1000..9999).random()
            prefs.addBlockedCall(randomPhone)
            Toast.makeText(this, "Chamada de teste bloqueada: $randomPhone", Toast.LENGTH_SHORT).show()
            updateDashboardUI()
        }

        // Limpar histórico
        btnClearHistory.setOnClickListener {
            prefs.clearBlockedCalls()
            Toast.makeText(this, "Histórico limpo", Toast.LENGTH_SHORT).show()
            updateDashboardUI()
        }

        // Desinstalar App
        btnUninstall.setOnClickListener {
            val packageUri = Uri.parse("package:$packageName")
            val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri).apply {
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
            }
            startActivity(uninstallIntent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateDashboardUI() {
        // Carrega estado persistido nos switches (evita ficarem "desligados" sozinhos)
        switchBlock.isChecked = prefs.isBlockSimEnabled
        switchRecord.isChecked = prefs.isRecordCallsEnabled

        // Atualiza contadores
        tvBlockedCount.text = prefs.blockedCallsCount.toString()
        tvRecordingsCount.text = prefs.recordingsCount.toString()

        // Status geral da proteção
        if (prefs.isBlockSimEnabled) {
            tvProtectionStatus.text = "● Proteção Ativa no Chip (SIM)"
            tvProtectionStatus.setTextColor(Color.parseColor("#10B981")) // Verde
        } else {
            tvProtectionStatus.text = "○ Proteção Desativada"
            tvProtectionStatus.setTextColor(Color.parseColor("#64748B")) // Cinza
        }

        // Status da permissão de Call Screening do Sistema
        val isRoleHeld = isCallScreeningRoleHeld()
        if (isRoleHeld) {
            badgeRoleStatus.text = "OPERADORA: ATIVA"
            badgeRoleStatus.setTextColor(Color.parseColor("#10B981"))
        } else {
            badgeRoleStatus.text = "OPERADORA: PENDENTE"
            badgeRoleStatus.setTextColor(Color.parseColor("#F59E0B"))
        }

        // Renderiza lista de histórico de chamadas bloqueadas
        renderBlockedCallsLogs()
    }

    private fun renderBlockedCallsLogs() {
        val logs = prefs.getBlockedCalls()

        // Remove itens dinâmicos antigos, mantendo apenas a view vazia
        containerCallLogs.removeAllViews()

        if (logs.isEmpty()) {
            tvEmptyLogs.visibility = View.VISIBLE
            containerCallLogs.addView(tvEmptyLogs)
            return
        }

        tvEmptyLogs.visibility = View.GONE

        val sdf = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault())

        for (call in logs.take(15)) {
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 10, 0, 10)
            }

            val tvNumber = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "🛑 " + call.number
                setTextColor(Color.parseColor("#F8FAFC"))
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvDate = TextView(this).apply {
                text = sdf.format(Date(call.timestamp))
                setTextColor(Color.parseColor("#94A3B8"))
                textSize = 11f
            }

            itemLayout.addView(tvNumber)
            itemLayout.addView(tvDate)

            containerCallLogs.addView(itemLayout)
        }
    }

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isCallScreeningRoleHeld(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as? RoleManager
            return roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) ?: false
        }
        return true
    }

    private fun requestCallScreeningRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                    startActivity(intent)
                }
            }
        }
    }

    private fun checkBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)
            cardBatteryWarning.visibility = if (isIgnoring) View.GONE else View.VISIBLE
        } else {
            cardBatteryWarning.visibility = View.GONE
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback para tela de detalhes do app
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
