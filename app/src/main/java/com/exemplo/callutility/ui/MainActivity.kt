package com.exemplo.callutility.ui

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.exemplo.callutility.R
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permissão de microfone concedida", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissão necessária para gravação", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUI()
    }

    private fun setupUI() {
        val switchBlock = findViewById<MaterialSwitch>(R.id.switchBlockCalls)
        val switchRecord = findViewById<MaterialSwitch>(R.id.switchRecordCalls)
        val btnUninstall = findViewById<Button>(R.id.btnUninstall)

        switchBlock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestCallScreeningRole()
            }
        }

        switchRecord.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkAudioPermission()
            }
        }

        btnUninstall.setOnClickListener {
            val packageUri = Uri.parse("package:$packageName")
            val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri).apply {
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
            }
            startActivity(uninstallIntent)
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
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
}
