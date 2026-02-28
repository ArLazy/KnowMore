package com.example.knowmore.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Flashcards : Screen("flashcards/{language}/{category}")
    object WordList : Screen("word_list")
    object Progress : Screen("progress")
    object AddWord : Screen("add_word/{wordId}") {
        fun createRoute(wordId: Long? = null): String {
            return "add_word/${wordId ?: -1}"
        }
    }
}

object FlashcardsRoute {
    fun createRoute(language: String? = null, category: String? = null): String {
        return when {
            language != null && category != null -> "flashcards/$language/$category"
            language != null -> "flashcards/$language"
            category != null -> "flashcards/$category"
            else -> "flashcards"
        }
    }
}
