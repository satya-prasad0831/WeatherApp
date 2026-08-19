package com.example.weatherapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapplication.ui.theme.login.LoginActivity
import kotlinx.coroutines.delay
import dagger.hilt.android.AndroidEntryPoint
import android.content.Context
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val sharedPreferences by lazy {
        getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            SplashScreen(
                onSplashFinished = {

                    val isLoggedIn =
                        sharedPreferences.getBoolean("isLoggedIn", false)

                    val intent = if (isLoggedIn) {

                        Intent(
                            this,
                            WeatherActivity::class.java
                        )

                    } else {

                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    }

                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {

    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A90E2),
                        Color(0xFF87CEEB)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "☁️",
                fontSize = 80.sp
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "WEATHER",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Your weather, wherever you are.",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen {  }
}