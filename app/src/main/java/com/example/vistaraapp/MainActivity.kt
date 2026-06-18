package com.example.vistaraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.vistaraapp.database.ContactDatabase
import com.example.vistaraapp.database.ContactDao
import com.example.vistaraapp.screens.navigation.AppNavigation
import com.example.vistaraapp.screens.navigation.ModernBottomBar
import com.example.vistaraapp.ui.theme.VistaraTheme
import com.example.vistaraapp.utils.TokenManager
import com.example.vistaraapp.viewmodels.ContactViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }
//Dependency injection
    private val contactViewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ContactViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(applicationContext)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            VistaraTheme {
                // Passes db.dao directly into the main entry function layout
                VistaraApp(
                    contactViewModel = contactViewModel,
                    contactDao = db.dao
                )
            }
        }
    }
}

@Composable
fun VistaraApp(
    contactViewModel: ContactViewModel,
    contactDao: ContactDao
) {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(TokenManager.getToken()?.isNotEmpty() == true) }

    // Token is saved RIGHT HERE in memory and persisted.
    // This state stays alive as long as VistaraApp is running, surviving all screen navigation changes.
    var sessionToken by remember { mutableStateOf(TokenManager.getToken() ?: "") }
    val contactState by contactViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            if (isLoggedIn) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "home"
                val showBottomBar = currentRoute in listOf("home", "wildlife", "bookings", "profile")

                if (showBottomBar) {
                    ModernBottomBar(
                        currentRoute = currentRoute,
                        onItemSelected = { route ->
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            contactViewModel = contactViewModel,
            contactState = contactState,
            contactDao = contactDao,
            sessionToken = sessionToken, // Passes down the current token value string

            /*2. FIX: Explicitly name the incoming string parameter to guarantee
            that the state variable updates properly when LoginScreen triggers it.*/
            onTokenUpdated = { newTokenString ->
                sessionToken = newTokenString
                TokenManager.saveToken(newTokenString)
            },

            onLoginSuccess = { isLoggedIn = true },
            modifier = Modifier.padding(innerPadding)
        )
    }
}