package ru.adept.mobilelabsapp

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimestampService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        val service: TimestampService
            get() = this@TimestampService
    }

    fun getTimestamp(): String =
        LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"))
            .toString()

    override fun onBind(intent: Intent?): IBinder = binder
}