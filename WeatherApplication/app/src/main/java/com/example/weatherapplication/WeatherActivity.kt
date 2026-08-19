package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapplication.data.remote.WeatherResponse
import com.example.weatherapplication.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.verticalScroll
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.weatherapplication.data.remote.CurrentWeather
import com.example.weatherapplication.data.remote.DailyWeather
import com.example.weatherapplication.data.remote.HourlyWeather
import com.example.weatherapplication.location.LocationHelper
import kotlinx.coroutines.launch
import com.example.weatherapplication.data.remote.LocationResult
import com.example.weatherapplication.ui.location.LocationSearch
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.weatherapplication.ui.theme.login.LoginActivity


@AndroidEntryPoint
class WeatherActivity : ComponentActivity() {


    private lateinit var locationHelper: LocationHelper
    private val currentLocation = mutableStateOf<android.location.Location?>(null)
    private val currentLocationName =
        mutableStateOf("Loading location...")

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineLocationGranted || coarseLocationGranted) {
                loadCurrentLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        locationHelper = LocationHelper(this)

        setContent {
                WeatherScreen(
                    location = currentLocation.value,
                    locationName = currentLocationName.value
                )
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {

            loadCurrentLocation()

        } else {

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun loadCurrentLocation() {

        lifecycleScope.launch {

            val location = locationHelper.getCurrentLocation()

            if (location != null) {

                Log.d(
                    "LOCATION_TEST",
                    "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                )

                currentLocation.value = location

                currentLocationName.value =
                    locationHelper.getLocationName(location)
            }
        }
    }
}

@Composable
fun WeatherScreen(
    location: android.location.Location?,
    locationName: String,
    viewModel: WeatherViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(location) {

        if (location != null) {

            viewModel.getWeather(
                latitude = location.latitude,
                longitude = location.longitude,
                locationName = locationName
            )
        }
    }

    when {

        location == null -> {

            LoadingWeather()
        }

        uiState.isLoading -> {

            LoadingWeather()
        }

        uiState.error != null -> {

            WeatherError(
                message = uiState.error!!,
                onRetry = {

                    if (location != null) {

                        viewModel.getWeather(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            locationName = locationName
                        )
                    }
                }
            )
        }

        uiState.weather != null -> {

            WeatherContent(
                weather = uiState.weather!!,
                locationName = uiState.locationName,
                isFromCache = uiState.isFromCache,
                cachedAt = uiState.cachedAt,
                onLocationSelected = { location ->

                    viewModel.getWeather(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        locationName = location.name
                    )
                }
            )
        }
    }
}

@Composable
fun LoadingWeather() {

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
                text = "🌤️",
                fontSize = 70.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Loading weather...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Getting the latest forecast",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun WeatherError(
    message: String,
    onRetry: () -> Unit
) {

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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {

            Text(
                text = "🌧️",
                fontSize = 70.sp
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Unable to load weather",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1E3A5F)
                )
            ) {
                Text(
                    text = "Retry",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun WeatherContent(
    weather: WeatherResponse,
    locationName: String,
    isFromCache: Boolean,
    cachedAt: Long?,
    onLocationSelected: (LocationResult) -> Unit,
    showSearch: Boolean = true
) {
    val context = LocalContext.current

    val current = weather.current
    val backgroundImage = getWeatherBackground(
        current.weather_code
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.20f)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showSearch) {

                LocationSearch(
                    onLocationSelected = onLocationSelected
                )
            }
            if (isFromCache) {

                Text(
                    text = "Offline • Showing last saved weather",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            WeatherHeroCard(
                weather = weather,
                locationName = locationName
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                WeatherInfoCard(
                    title = "Humidity",
                    value = "${current.relative_humidity_2m}%",
                    modifier = Modifier.weight(1f)
                )

                WeatherInfoCard(
                    title = "Wind",
                    value = "${current.wind_speed_10m} km/h",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Hourly Forecast",
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HourlyForecast(
                weather = weather
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Today's Forecast",
                fontSize = 20.sp,
                color = Color.White
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            DailyForecast(
                weather = weather
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Button(
                onClick = {

                    context.getSharedPreferences(
                        "WeatherPrefs",
                        Context.MODE_PRIVATE
                    )
                        .edit()
                        .putBoolean("isLoggedIn", false)
                        .apply()

                    context.startActivity(
                        Intent(
                            context,
                            LoginActivity::class.java
                        )
                    )

                    (context as? Activity)?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = "Logout",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun WeatherInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.22f)
                )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = value,
                fontSize = 21.sp,
                color = Color.White
            )
        }
    }
}
@Composable
fun WeatherHeroCard(
    weather: WeatherResponse,
    locationName: String
) {

    val current = weather.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = locationName,
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Current Weather",
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = getWeatherIcon(current.weather_code),
            fontSize = 82.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "${current.temperature_2m.toInt()}°",
            fontSize = 72.sp,
            color = Color.White
        )

        Text(
            text = getWeatherDescription(current.weather_code),
            fontSize = 22.sp,
            color = Color.White
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Feels like ${current.apparent_temperature.toInt()}°",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun DailyForecast(
    weather: WeatherResponse
) {

    val daily = weather.daily

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        for (index in 0..4) {

            val dayName = when (index) {

                0 -> "Today"

                1 -> "Tomorrow"

                else -> {

                    val dateFormat = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                    val dayFormat = SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                    )

                    val date = dateFormat.parse(
                        daily.time[index]
                    )

                    if (date != null) {
                        dayFormat.format(date)
                    } else {
                        daily.time[index]
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.25f)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = dayName,
                        fontSize = 15.sp
                    )

                    Text(
                        text = getWeatherIcon(
                            daily.weather_code[index]
                        ),
                        fontSize = 28.sp
                    )

                    Text(
                        text = "${daily.temperature_2m_max[index]}° / ${daily.temperature_2m_min[index]}°",
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

fun getWeatherDescription(
    weatherCode: Int
): String {

    return when (weatherCode) {

        0 -> "Clear sky"

        1, 2, 3 -> "Cloudy"

        45, 48 -> "Fog"

        51, 53, 55 -> "Drizzle"

        56, 57 -> "Freezing drizzle"

        61, 63, 65 -> "Rain"

        66, 67 -> "Freezing rain"

        71, 73, 75, 77 -> "Snow"

        80, 81, 82 -> "Rain showers"

        85, 86 -> "Snow showers"

        95 -> "Thunderstorm"

        96, 99 -> "Thunderstorm with hail"

        else -> "Unknown"
    }
}
@Composable
fun HourlyForecast(
    weather: WeatherResponse
) {

    val hourly = weather.hourly
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {

        for (index in 0..5) {

            Column(
                modifier = Modifier
                    .padding(end = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = hourly.time[index].substring(11, 16),
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = getWeatherIcon(
                        hourly.weather_code[index]
                    ),
                    fontSize = 30.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "${hourly.temperature_2m[index].toInt()}°",
                    fontSize = 19.sp,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${hourly.precipitation_probability[index]}%",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
fun getWeatherIcon(
    weatherCode: Int
): String {

    return when (weatherCode) {

        0 -> "☀️"

        1, 2 -> "⛅"

        3 -> "☁️"

        45, 48 -> "🌫️"

        51, 53, 55, 56, 57 -> "🌦️"

        61, 63, 65, 66, 67 -> "🌧️"

        71, 73, 75, 77, 85, 86 -> "❄️"

        80, 81, 82 -> "🌦️"

        95, 96, 99 -> "⛈️"

        else -> "🌤️"
    }
}
fun getWeatherBackground(weatherCode: Int): Int {

    return when (weatherCode) {


        0 -> R.drawable.weather_clear


        1, 2 -> R.drawable.weather_partlycloudy


        3, 45, 48 -> R.drawable.weather_cloudy


        51, 53, 55,
        56, 57,
        61, 63, 65,
        66, 67,
        80, 81, 82 -> R.drawable.weather_rain


        71, 73, 75, 77,
        85, 86,
        95, 96, 99 -> R.drawable.weather_cloudy

        else -> R.drawable.weather_cloudy
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun WeatherDashboardPreview() {

    val sampleWeather = WeatherResponse(

        current = CurrentWeather(
            temperature_2m = 29.0,
            relative_humidity_2m = 72,
            apparent_temperature = 32.0,
            weather_code = 2,
            wind_speed_10m = 13.8
        ),

        hourly = HourlyWeather(
            time = listOf(
                "2026-08-17T10:00",
                "2026-08-17T11:00",
                "2026-08-17T12:00",
                "2026-08-17T13:00",
                "2026-08-17T14:00",
                "2026-08-17T15:00"
            ),

            temperature_2m = listOf(
                29.0, 30.0, 30.5, 31.0, 31.0, 30.0
            ),

            apparent_temperature = listOf(
                32.0, 33.0, 34.0, 34.5, 34.0, 33.0
            ),

            precipitation_probability = listOf(
                10, 10, 20, 30, 30, 20
            ),

            weather_code = listOf(
                2, 2, 3, 61, 61, 2
            )
        ),

        daily = DailyWeather(
            time = listOf(
                "2026-08-17",
                "2026-08-18",
                "2026-08-19",
                "2026-08-20",
                "2026-08-21"
            ),

            weather_code = listOf(
                2, 61, 3, 0, 2
            ),

            temperature_2m_max = listOf(
                31.0, 29.0, 30.0, 32.0, 31.0
            ),

            temperature_2m_min = listOf(
                25.0, 24.0, 25.0, 26.0, 25.0
            ),

            apparent_temperature_max = listOf(
                34.0, 32.0, 33.0, 35.0, 34.0
            ),

            apparent_temperature_min = listOf(
                27.0, 26.0, 27.0, 28.0, 27.0
            ),

            precipitation_probability_max = listOf(
                20, 60, 30, 10, 20
            ),

            sunrise = listOf(
                "2026-08-17T05:40",
                "2026-08-18T05:40",
                "2026-08-19T05:41",
                "2026-08-20T05:41",
                "2026-08-21T05:42"
            ),

            sunset = listOf(
                "2026-08-17T18:25",
                "2026-08-18T18:24",
                "2026-08-19T18:24",
                "2026-08-20T18:23",
                "2026-08-21T18:23"
            )
        )
    )

    WeatherContent(
        weather = sampleWeather,
        locationName = "Tallarevu",
        isFromCache = false,
        cachedAt = null,
        onLocationSelected = {},
        showSearch = false
    )
}