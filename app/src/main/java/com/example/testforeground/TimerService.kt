package com.example.testforeground

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PowerManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class TimerService : Service() {
    var player: ExoPlayer? = null
    private val binder: Binder = TimerBinder()
    private lateinit var wakeLock: PowerManager.WakeLock
    override fun onBind(p0: Intent?): IBinder = binder

    inner class TimerBinder : Binder() {
        val service: TimerService
            get() = this@TimerService
    }


    override fun onCreate() {
        super.onCreate()
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                acquire()
            }

        }
        //wakeLock.setReferenceCounted(false)
    }

    override fun onDestroy() {
        wakeLock.releaseSafe()
        super.onDestroy()
    }

    private fun PowerManager.WakeLock.releaseSafe() {
        if (wakeLock.isHeld) {
            release()
        }
    }
    private fun acquireWakeLock() {
        wakeLock.acquire(10000)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        player = ExoPlayer.Builder(applicationContext).build()
        Notifications.createChannel(
            context = applicationContext,
            channelName = R.string.app_name,
            channelDescription = R.string.app_name
        )
        val not = Notifications.createNotification(
            context = applicationContext,
            title = "NotificationTest",
            content = "This a test notification"
        )
        startForeground(1, not)
        val timer = object: CountDownTimer(24*60*60*1000, 30*1000) {
            override fun onTick(millisUntilFinished: Long) {
                Timber.e("TIMER TICK ${millisUntilFinished/1000}")
                playSoundOnExoPlayer(getRawFileUri(this@TimerService, R.raw.sound_connect_fail))
            }

            override fun onFinish() {
                playSoundOnExoPlayer(getRawFileUri(this@TimerService, R.raw.sound_connect_fail))
            }
        }
        timer.start()
//        MainScope().launch {
//            var last: Long = 0

//            while (true) {
//                //acquireWakeLock()
//                delay(30*1000)
//                val t = (System.currentTimeMillis() - last) / 1000
//                last = System.currentTimeMillis()
//                Timber.e("PING $t s")
//                playSoundOnExoPlayer(getRawFileUri(this@TimerService, R.raw.sound_connect_fail))
//               // wakeLock.releaseSafe()
//            }
//        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun getRawFileUri(context: Context, rawFileId: Int): Uri {
        return Uri.parse(
            "android.resource://"
                    + context.packageName.toString() + "/"
                    + rawFileId
        )
    }

    private fun getSoundForExoPlayer(soundUri: Uri) {
        val mediaItem = MediaItem.fromUri(soundUri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    private fun playSoundOnExoPlayer(soundUri: Uri) {
        if (player?.isPlaying == true) player?.stop()
        getSoundForExoPlayer(soundUri)
        player?.play()
    }
}
