package com.example.ukrainehistorylearner

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ukrainehistorylearner.data.datastore.SettingsDataStore
import com.example.ukrainehistorylearner.ui.screens.LoginScreen
import com.example.ukrainehistorylearner.ui.screens.RegisterScreen
import com.example.ukrainehistorylearner.ui.screens.HistoricalArticleScreen
import com.example.ukrainehistorylearner.ui.screens.HistoricalQuizScreen
import com.example.ukrainehistorylearner.ui.theme.AppTheme
import com.example.ukrainehistorylearner.ui.screens.ProfileScreen
import com.example.ukrainehistorylearner.ui.screens.HomeScreen
import com.example.ukrainehistorylearner.ui.screens.SettingsScreen
import com.example.ukrainehistorylearner.ui.viewmodels.SettingsViewModel
import com.example.ukrainehistorylearner.utils.LocaleHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(dynamicColor = false) {
                MainApp()
                Log.println(Log.INFO, "OnCreate", "Main activity created")
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val langCode = runBlocking {
            SettingsDataStore(newBase!!).languageFlow.first()
        }
        val context = LocaleHelper.wrapContext(newBase!!, langCode)
        super.attachBaseContext(context)
    }

    override fun onStart() {
        super.onStart()
        Log.println(Log.INFO, "OnStart", "Main activity started")
    }

    override fun onStop() {
        super.onStop()
        Log.println(Log.INFO, "OnStop", "Main activity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.println(Log.INFO, "OnDestroy", "Main activity destroyed")
    }

    override fun onPause() {
        super.onPause()
        Log.println(Log.INFO, "OnPause", "Main activity paused")
    }
    override fun onResume() {
        super.onResume()
        Log.println(Log.INFO, "OnResume", "Main activity resumed")
    }
}

data class NavItem(
    val labelRes: Int,
    val icon: ImageVector,
    val route: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )

    LaunchedEffect(Unit) {
        settingsViewModel.applySavedLanguage()
    }

    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    val navItems = listOf(
        NavItem(R.string.nav_home, Icons.Default.Home, "home"),
        NavItem(R.string.nav_quiz, Icons.Default.Search, "quiz"),
        NavItem(R.string.nav_articles, Icons.Default.Home, "articles"),
        NavItem(R.string.nav_login, Icons.Default.ExitToApp, "login"),
        NavItem(R.string.nav_register, Icons.Default.Add, "register"),
        NavItem(R.string.nav_profile, Icons.Default.AccountCircle, "profile"),
        NavItem(R.string.nav_settings, Icons.Default.Settings, "settings")
    )

    when (windowSize.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            // Компактний макет з Bottom Navigation
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                bottomBar = {
                    NavigationBar {
                        val currentDestination = currentRoute?.destination

                        navItems.forEach { item ->
                            val label = stringResource(item.labelRes)
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = label) },
                                label = { Text(label) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AppNavHost(navController = navController)
                }
            }
        }

        WindowWidthSizeClass.MEDIUM -> {
            // Середній макет з Navigation Rail
            Row(Modifier.fillMaxSize()) {
                NavigationRail {
                    val currentDestination = currentRoute?.destination

                    navItems.forEach { item ->
                        val label = stringResource(item.labelRes)
                        NavigationRailItem(
                            icon = { Icon(item.icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp))

                Column(Modifier.weight(1f)) {
                    TopAppBar(
                        title = { Text(stringResource(R.string.app_name)) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    Scaffold(content = { paddingValues ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            AppNavHost(navController = navController)
                        }
                    })
                }
            }
        }

        WindowWidthSizeClass.EXPANDED -> {
            // Розширений макет з Navigation Drawer
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp)
                            )

                            val currentDestination = currentRoute?.destination

                            navItems.forEach { item ->
                                val label = stringResource(item.labelRes)
                                NavigationDrawerItem(
                                    label = { Text(label) },
                                    icon = { Icon(item.icon, contentDescription = label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    modifier = Modifier.widthIn(max = 165.dp)
                                )
                            }
                        }
                    }
                },
                content = {
                    Scaffold(content = { paddingValues ->
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            AppNavHost(navController = navController)
                        }
                    })
                }
            )
        }
        else -> Unit
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen()
        }
        composable(Routes.Register.route) {
            RegisterScreen()
        }
        composable(Routes.Quiz.route) {
            HistoricalQuizScreen()
        }
        composable(Routes.Articles.route) {
            HistoricalArticleScreen()
        }
        composable(Routes.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.Home.route) {
            HomeScreen()
        }
        composable(Routes.Settings.route) {
            SettingsScreen()
        }
    }
}