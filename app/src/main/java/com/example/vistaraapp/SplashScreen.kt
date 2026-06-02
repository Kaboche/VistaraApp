package com.example.vistaraapp

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SplashScreen(navController: NavController) {
    val brandGreen = Color(0xFF029602)
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. SCENIC BACKGROUND IMAGE (Use a nice landscape/savanna background asset)
        Image(
            painter = painterResource(id = R.drawable.toll), // Your monkey image or a park background
            contentDescription = "Welcome Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. SMOOTH GRADIENT OVERLAY (Darkened top and bottom for readability)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.40f),
                            Color.Black.copy(alpha = 0.90f)
                        )
                    )
                )
        )

        // 3. INTERFACE CONTAINER
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .alpha(contentAlpha.value),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🚀 NEW FEATURE: THE 3 ANIMALS ROW MATRIX
            Text(
                text = "DISCOVER PARK SPECIES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = brandGreen,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Animal 1: The Monkey
                AnimalAvatarBadge(imageResId = R.drawable.olpejeta, name = "Primate")

                // Animal 2: Lion (⚠️ Make sure you have these names/images in your res/drawable folder!)
                AnimalAvatarBadge(imageResId = R.drawable.lion, name = "Lion")

                // Animal 3: Rhino
                AnimalAvatarBadge(imageResId = R.drawable.rhino, name = "Rhino")
            }

            // 4. WELCOME TYPOGRAPHY TEXT
            Text(
                text = "Welcome to Vistara",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Experience Nairobi's incredible wildlife and pristine wilderness directly from your fingertips.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. ACTION GET STARTED BUTTON
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// A reusable sub-component helper layout that designs crisp round circular profile badges for each animal
@Composable
fun AnimalAvatarBadge(imageResId: Int, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp) // Perfect avatar size sizing
                .clip(CircleShape) // Cuts the square photo layout into a perfect circle sphere
                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape) // Gives a beautiful white trim ring outline
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Keeps the animal aspect ratio looking perfect inside the circle
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}