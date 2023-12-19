package ru.adept.mobilelabsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.adept.mobilelabsapp.ui.theme.MobileLabsAppTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainCompose(intent = intent)
                }
            }
        }
    }

    @Composable
    fun MainCompose(intent: Intent) {
        var editedText by remember {
            mutableStateOf(intent.extras?.getString("text") ?: "")
        }

        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Второе окно")
            Spacer(modifier = Modifier.size(60.dp))
            TextField(
                value = editedText,
                onValueChange = { editedText = it },
                label = { Text("Напиши сюда текст") }
            )
            Spacer(modifier = Modifier.size(30.dp))
            Button(onClick = {
                intent.putExtra("text", editedText)

                setResult(RESULT_OK, intent)
                finish()
            }) {
                Text(text = "Переслать обратно")
            }
        }
    }
}