package com.example.knowmore.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.knowmore.data.local.AppDatabase
import com.example.knowmore.data.local.Word
import com.example.knowmore.data.repository.WordRepository
import com.example.knowmore.ui.screens.*
import com.example.knowmore.ui.viewmodel.*

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { WordRepository(database.wordDao()) }

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory(repository)
            )
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToFlashcards = { language, category -> 
                    navController.navigate(FlashcardsRoute.createRoute(language, category))
                },
                onNavigateToWordList = { navController.navigate(Screen.WordList.route) },
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToAddWord = { word: Word? ->
                    navController.navigate(Screen.AddWord.createRoute(word?.id))
                }
            )
        }

        composable("flashcards") {
            val viewModel: FlashcardViewModel = viewModel(
                factory = FlashcardViewModel.Factory(repository, null, null)
            )
            FlashcardScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "flashcards/{language}/{category}",
            arguments = listOf(
                navArgument("language") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("category") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val language = backStackEntry.arguments?.getString("language")?.takeIf { it.isNotEmpty() }
            val category = backStackEntry.arguments?.getString("category")?.takeIf { it.isNotEmpty() }
            val viewModel: FlashcardViewModel = viewModel(
                factory = FlashcardViewModel.Factory(repository, language, category)
            )
            FlashcardScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "flashcards/{filter}",
            arguments = listOf(
                navArgument("filter") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val filter = backStackEntry.arguments?.getString("filter")?.takeIf { it.isNotEmpty() }
            val viewModel: FlashcardViewModel = viewModel(
                factory = FlashcardViewModel.Factory(repository, null, null, filter)
            )
            FlashcardScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WordList.route) {
            val viewModel: WordListViewModel = viewModel(
                factory = WordListViewModel.Factory(repository)
            )
            WordListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAddWord = { word: Word? ->
                    navController.navigate(Screen.AddWord.createRoute(word?.id))
                }
            )
        }

        composable(Screen.Progress.route) {
            val viewModel: ProgressViewModel = viewModel(
                factory = ProgressViewModel.Factory(repository)
            )
            ProgressScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddWord.route,
            arguments = listOf(
                navArgument("wordId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getLong("wordId") ?: -1L
            val viewModel: WordListViewModel = viewModel(
                factory = WordListViewModel.Factory(repository)
            )
            AddWordScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                wordId = if (wordId > 0) wordId else null
            )
        }
    }
}
