package ru.adept.mobilelabsapp

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class IntentService : IntentService("IntentService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            for (i in 0..2) {
                Thread.sleep(1000)
                Log.d("IntentService", "Progress: ${i + 1} seconds")
            }
            Toast.makeText(applicationContext, "Intent service is complete!", Toast.LENGTH_SHORT).show()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}