package com.example.knowmore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.knowmore.data.local.Word
import com.example.knowmore.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val totalWords: Int = 0,
    val wordsToReview: Int = 0,
    val languages: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val recentWords: List<Word> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(private val repository: WordRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                repository.getTotalWordCount(),
                repository.getWordsToReviewCount(),
                repository.getAllLanguages(),
                repository.getAllCategories()
            ) { total, toReview, languages, categories ->
                DashboardUiState(
                    totalWords = total,
                    wordsToReview = toReview,
                    languages = languages,
                    categories = categories,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }

    class Factory(private val repository: WordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
