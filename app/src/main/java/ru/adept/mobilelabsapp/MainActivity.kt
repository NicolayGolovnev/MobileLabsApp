package ru.adept.mobilelabsapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.adept.mobilelabsapp.ui.theme.MobileLabsAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainCompose()
                }
            }
        }
    }

    @Composable
    fun MainCompose() {
        val context: Context = LocalContext.current

        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.size(20.dp))

            MusicServiceCompose(context = context)

            Spacer(modifier = Modifier.size(60.dp))

            IntentServiceCompose()

            Spacer(modifier = Modifier.size(60.dp))

            TimestampServiceCompose()
        }
    }

    @Composable
    fun MusicServiceCompose(context: Context) {
        var checked by remember { mutableStateOf(false) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Start/stop Service")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked =
                            if (it) {
                                val musicIntent = Intent(context, MusicService::class.java)
                                startService(musicIntent)
                                true
                            }
                            else {
                                val musicIntent = Intent(context, MusicService::class.java)
                                stopService(musicIntent)
                                false
                            }
                    }
                )
                Text(text = "Play or stop music", textAlign = TextAlign.Center)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MusicServiceComposePreview() {
        val context: Context = LocalContext.current
        MobileLabsAppTheme {
            MusicServiceCompose(context = context)
        }
    }

    @Composable
    fun IntentServiceCompose() {
        val context = LocalContext.current
        var currentProgress by remember { mutableFloatStateOf(0f) }
        var loading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope() // Create a coroutine scope

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Intent Service")
            Spacer(modifier = Modifier.size(15.dp))
            LinearProgressIndicator(
                progress = currentProgress,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.size(15.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    scope.launch {
                        loadProgress {
                            currentProgress = it
                        }
                        loading = false
                    }
                    loading = true

                    Thread.sleep(1500)
                    val intentService = Intent(context, IntentService::class.java)
                    startService(intentService)
                }) { Text(text = "Start") }
                Spacer(modifier = Modifier.size(15.dp))
                Button(onClick = {
                    scope.cancel()
                    loading = false
                }) { Text(text = "Stop") }
            }
        }
    }

    suspend fun loadProgress(updateProgress: (Float) -> Unit) {
        for (i in 1..100) {
            updateProgress(i.toFloat() / 100)
            delay(100)
        }
    }

    @Composable
    fun TimestampServiceCompose() {
        val context: Context = LocalContext.current
        var timestampService: TimestampService? by remember(context) { mutableStateOf(null) }
        val timestampServiceConnection: ServiceConnection = remember(context) {
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    timestampService = (service as TimestampService.LocalBinder).service
                    Log.d("TimestampService", "Service ${name?.shortClassName} connected")
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    timestampService = null
                    Log.d("TimestampService", "Service ${name?.shortClassName} disconnected")
                }
            }
        }

        var text by remember { mutableStateOf("") }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Bound Service", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.size(25.dp))
            Text(
                text = text,
                modifier = Modifier
                    .background(Color.Gray)
                    .fillMaxWidth(0.8f)
                    .height(30.dp)
            )
            Spacer(modifier = Modifier.size(15.dp))
            Button(
                onClick = {
                    text =
                        timestampService?.let { "Timestamp: ${it.getTimestamp()}" }
                            ?: "Service not found"
                }
            ) { Text(text = "Get timestamp") }
            Button(
                onClick = {
                    if (timestampService == null) {
                        Intent(context, TimestampService::class.java)
                            .also {
                                bindService(it, timestampServiceConnection, Context.BIND_AUTO_CREATE)
                            }

                        Toast
                            .makeText(applicationContext, "Service is binding!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            ) { Text(text = "Bind service") }
            Button(
                onClick = {
                    if (timestampService != null) {
                        unbindService(timestampServiceConnection)
                        // Если попробовать unbind еще раз - то упадет,
                        // поэтому обнуляем сервис
                        timestampService = null

                        Toast
                            .makeText(applicationContext, "Service is unbinding!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            ) { Text(text = "Unbind service") }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun TimestampServiceComposePreview() {
        MobileLabsAppTheme {
            TimestampServiceCompose()
        }
    }
}