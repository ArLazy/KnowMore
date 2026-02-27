package com.example.knowmore.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Flashcards : Screen("flashcards")
    object WordList : Screen("word_list")
    object Progress : Screen("progress")
    object AddWord : Screen("add_word")
}
