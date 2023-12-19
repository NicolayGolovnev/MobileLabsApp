package ru.adept.mobilelabsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.adept.mobilelabsapp.ui.theme.MobileLabsAppTheme

class MainActivity : ComponentActivity() {

    private val intentActivityHandler = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultValue = result.data?.getStringExtra("text")

            resultValue?.let { editedText.value = it }
//            Toast.makeText(this.applicationContext, "New text - $editedText", Toast.LENGTH_SHORT).show()
        }
    }

    private var editedText: MutableState<String> = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileLabsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainCompose(editedText)
                }
            }
        }
    }

    @Composable
    fun MainCompose(editText: MutableState<String>) {
        val context: Context = LocalContext.current

        Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = editText.value,
                onValueChange = { editText.value = it },
                label = { Text("Напиши сюда текст") }
            )
            Spacer(modifier = Modifier.size(30.dp))
            Button(onClick = {
                val secondIntent = Intent(context, SecondActivity::class.java)
                secondIntent.putExtra("text", editText.value)

                intentActivityHandler.launch(secondIntent)
//                Toast.makeText(context, "Launch activity", Toast.LENGTH_SHORT).show()
            }) {
                Text(text = "Переслать")
            }
        }
    }
}