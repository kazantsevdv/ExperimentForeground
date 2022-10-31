package com.example.testforeground

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private lateinit var timerService: TimerService
    private var isTimerServiceBound: Boolean = false
    private val timerServiceConnection = object : ServiceConnection {


        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            timerService = (binder as TimerService.TimerBinder).service
            isTimerServiceBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isTimerServiceBound = false
        }
    }
    private val perms =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // startService(Intent(this, TimerService::class.java))
        val intent = Intent(this, TimerService::class.java)
        startForegroundService(/* context = */ this, /* intent = */ intent)
//        bindService(
//            Intent(this, TimerService::class.java),
//            timerServiceConnection,
//            Context.BIND_AUTO_CREATE
//        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent()
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }

        setContentView(R.layout.activity_main)
        perms.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

}