package kz.anam.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kz.anam.ApiConfig
import kz.anam.data.api.ClaudeApiService
import kz.anam.data.database.AnamDatabase
import kz.anam.data.repository.*
import kz.anam.ui.screens.*
import kz.anam.ui.theme.*
import kz.anam.viewmodels.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Onboarding")
    object Auth : Screen("auth", "Auth")
    object Home : Screen("home", "HomePage", Icons.Default.Home)
    object Chat : Screen("chat", "Helper", Icons.Default.Chat)
    object Articles : Screen("articles", "Articles", Icons.Default.Article)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object FoodLog : Screen("food_log", "Nutrition")
    object SymptomLog : Screen("symptom_log", "Symptoms")
    object MealPlan : Screen("meal_plan", "Menu")
    object AIInsights : Screen("ai_insights", "Analytics")
    object Documents : Screen("documents", "Documents")
    object MediaLibrary : Screen("media", "Media")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Chat,
    Screen.Articles,
    Screen.Settings
)

@Composable
fun AnamNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    val database = remember { AnamDatabase.getDatabase(context) }
    val foodRepository = remember { FoodRepository(database.foodEntryDao()) }
    val symptomRepository = remember { SymptomRepository(database.symptomEntryDao()) }

    val homeViewModel = remember { HomeViewModel(foodRepository, symptomRepository) }
    val foodLogViewModel = remember { FoodLogViewModel(foodRepository) }
    val symptomLogViewModel = remember { SymptomLogViewModel(symptomRepository) }

    val aiChatRepository = remember {
        AIChatRepository(
            claudeApi = ClaudeApiService.create(),
            foodRepository = foodRepository,
            symptomRepository = symptomRepository,
            apiKey = ApiConfig.CLAUDE_API_KEY
        )
    }

    val chatViewModel = remember { ChatViewModel(aiChatRepository) }

    val mealGeneratorViewModel = remember {
        MealGeneratorViewModel(aiChatRepository, foodRepository)
    }

    val insightsRepository = remember {
        InsightsRepository(foodRepository, symptomRepository, aiChatRepository)
    }

    val aiInsightsViewModel = remember {
        AIInsightsViewModel(insightsRepository)
    }

    val documentsViewModel = remember {
        DocumentsViewModel(DocumentRepository(database.documentDao()))
    }

    val mediaRepository = remember { MediaRepository() }

    val mediaLibraryViewModel = remember {
        MediaLibraryViewModel(mediaRepository)
    }

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = PureWhite,
        topBar = {
            if (showBottomBar) {
                PremiumTopBar(
                    title = when (currentDestination?.route) {
                        Screen.Home.route -> Screen.Home.title
                        Screen.Chat.route -> Screen.Chat.title
                        Screen.Articles.route -> Screen.Articles.title
                        Screen.Settings.route -> Screen.Settings.title
                        else -> "Anam"
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                PremiumBottomNavigation(
                    currentRoute = currentDestination?.route,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToFoodLog = { navController.navigate(Screen.FoodLog.route) },
                    onNavigateToSymptomLog = { navController.navigate(Screen.SymptomLog.route) },
                    onNavigateToMealPlan = { navController.navigate(Screen.MealPlan.route) },
                    onNavigateToAIInsights = { navController.navigate(Screen.AIInsights.route) },
                    onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                    onNavigateToMedia = { navController.navigate(Screen.MediaLibrary.route) }
                )
            }

            composable(Screen.Chat.route) {
                ChatScreen(viewModel = chatViewModel)
            }

            composable(Screen.Articles.route) {
                ArticlesScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(Screen.FoodLog.route) {
                FoodLogScreen(viewModel = foodLogViewModel)
            }

            composable(Screen.SymptomLog.route) {
                SymptomLogScreen(viewModel = symptomLogViewModel)
            }

            composable(Screen.MealPlan.route) {
                MealPlanScreen(viewModel = mealGeneratorViewModel)
            }

            composable(Screen.AIInsights.route) {
                AIInsightsScreen(viewModel = aiInsightsViewModel)
            }

            composable(Screen.Documents.route) {
                DocumentsScreen(viewModel = documentsViewModel)
            }

            composable(Screen.MediaLibrary.route) {
                MediaLibraryScreen(viewModel = mediaLibraryViewModel)
            }
        }
    }
}

@Composable
fun PremiumTopBar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PureWhite)
            .padding(horizontal = 28.dp, vertical = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = TextPrimary
        )
    }
}

@Composable
fun PremiumBottomNavigation(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        color = PureWhite,
        shadowElevation = 0.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LightBorder)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                bottomNavItems.forEach { screen ->
                    PremiumNavItem(
                        icon = screen.icon!!,
                        label = screen.title,
                        isSelected = currentRoute == screen.route,
                        onClick = { onNavigate(screen) }
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.size(width = 72.dp, height = 64.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) RoyalViolet else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) PureWhite else Inactive
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (isSelected) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = RoyalViolet,
                    maxLines = 1
                )
            }
        }
    }
}