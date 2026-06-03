package com.example.vistaraapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.vistaraapp.ui.theme.VistaraTheme
import java.util.Calendar

// ========== DYNAMIC CONTENT HELPERS ==========
fun getDynamicGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}

fun getDynamicBoldPhrase(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11 -> "Nairobi is Waking Up!"
        in 12..16 -> "The Savannah Awa awaits You!"
        else -> "Unwind in the Wild Tonight!"
    }
}

// ========== MAIN HOME SCREEN ENTRIES ==========

@Composable
fun HomeScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel
) {
    val weatherState by weatherViewModel.weatherState.collectAsState()

    HomeScreenContent(
        navController = navController,
        weatherState = weatherState,
        onRetryWeather = { weatherViewModel.fetchWeather() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    navController: NavController,
    weatherState: WeatherState,
    onRetryWeather: () -> Unit
) {
    val brandGreen = Color(0xFF029602)
    val pureWhite = Color(0xFFFFFFFF)
    val lightGray = Color(0xFFF5F7FA)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(pureWhite)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Vistara",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = brandGreen
                        )
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("notifications") }) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "View Alert Notifications",
                                tint = brandGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = pureWhite),
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 28.dp,
                                bottomEnd = 28.dp
                            )
                        )
                        .drawBehind {
                            drawRect(color = pureWhite)
                        }
                )
            }
        },
        containerColor = lightGray
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HeroDashboardCard(weatherState) }
            item { StatsRow() }
            item { RealTimeWeatherCard(brandGreen, weatherState, onRetryWeather) }
            item { WildlifeDiscoveryCard(navController, brandGreen) }
            item { PicnicSiteCard() }

            // 🚨 INTEGRATED EMERGENCY INCIDENT CARD WITH SELECTION FORM
            item {
                EmergencyInfoCard(
                    onSendEmergencyReport = { emergencyType, details ->
                        // Pass parameters down to your navigation stack or backend api routes
                        navController.navigate("sos?type=$emergencyType&details=$details")
                    },
                    brandGreen = brandGreen
                )
            }
        }
    }
}

// ========== 1. THREE-LINE DYNAMIC HERO CARD ==========
@Composable
fun HeroDashboardCard(weatherState: WeatherState) {
    val greeting = getDynamicGreeting()
    val boldPhrase = getDynamicBoldPhrase()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.zebu),
            contentDescription = "Nairobi National Park",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = greeting,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = boldPhrase,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Start your day with nature. 50+ wild animals are waiting for you.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 16.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Open Today", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    Text("6:00 AM - 6:00 PM", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Column(horizontalAlignment = Alignment.End) {
                    when (weatherState) {
                        is WeatherState.Success -> {
                            val weather = weatherState.weather
                            val currentTemp = weather.current_weather.temperature.toInt()
                            val weatherCode = weather.current_weather.weathercode

                            Text(text = getWeatherEmoji(weatherCode), fontSize = 28.sp)
                            Text(
                                text = "$currentTemp°C • ${getWeatherDescription(weatherCode)}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        else -> {
                            Text("🌤️", fontSize = 28.sp)
                            Text("Loading...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        }
    }
}

// ========== 2. STATS ROW ==========
@Composable
fun StatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(value = "117 km²", label = "Park Area", color = Color(0xFF029602), modifier = Modifier.weight(1f))
        StatCard(value = "7 km", label = "From CBD", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
        StatCard(value = "100+", label = "Mammals", color = Color(0xFF3B82F6), modifier = Modifier.weight(1f))
        StatCard(value = "400+", label = "Birds", color = Color(0xFFEF4444), modifier = Modifier.weight(1f))
    }
}

@Composable
fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

// ========== 3. REAL-TIME WEATHER CARD ==========
@Composable
fun RealTimeWeatherCard(
    brandGreen: Color,
    weatherState: WeatherState,
    onRetryWeather: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (weatherState) {
                is WeatherState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Loading weather...", fontSize = 13.sp, color = Color.Gray)
                }

                is WeatherState.Success -> {
                    val weather = weatherState.weather
                    val currentTemp = weather.current_weather.temperature.toInt()
                    val weatherCode = weather.current_weather.weathercode
                    val weatherEmoji = getWeatherEmoji(weatherCode)
                    val weatherDesc = getWeatherDescription(weatherCode)

                    Text(text = weatherEmoji, fontSize = 40.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Vistara Weather",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = brandGreen
                        )
                        Text(
                            text = "$currentTemp°C, $weatherDesc",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = if (currentTemp in 20..28) "Perfect for a safari!" else "Plan your visit accordingly",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                is WeatherState.Error -> {
                    Text("⚠️", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Weather Unavailable",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tap to retry",
                            fontSize = 12.sp,
                            color = brandGreen,
                            modifier = Modifier.clickable { onRetryWeather() }
                        )
                    }
                }
            }
        }
    }
}

// ========== 4. SAFARI DISCOVERY CARD ==========
@Composable
fun WildlifeDiscoveryCard(navController: NavController, brandGreen: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = brandGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "SAFARI DISCOVERY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Lions can hear a prey's roar from up to 8 kilometers away!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Escape the concrete jungle. Over 50+ incredible wild animals are waiting to be discovered just minutes outside the city center. Perfect conditions predicted for today's drive.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("wildlife") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = brandGreen)
                ) {
                    Text("Meet Animals", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = { navController.navigate("booking/1") },
                    modifier = Modifier.weight(1.4f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Book Safari ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ========== 5. KINGFISHER PICNIC SITE CARD ==========
@Composable
fun PicnicSiteCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kingfisher Picnic Site", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF029602))
                Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFFEF3C7)) {
                    Text("Popular", fontSize = 10.sp, color = Color(0xFFD97706), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Perfect spot for a picnic with family. Enjoy the stunning views of the savannah and wildlife.", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoChip("2.5 km from Main Gate")
            }
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color(0xFFF3F4F6), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 10.sp, color = Color.Gray)
    }
}

// ========== CLEAN PREVIEW IMPLEMENTATION ==========
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    VistaraTheme {
        val dummyNavController = rememberNavController()

        HomeScreenContent(
            navController = dummyNavController,
            weatherState = WeatherState.Loading,
            onRetryWeather = {}
        )
    }
}