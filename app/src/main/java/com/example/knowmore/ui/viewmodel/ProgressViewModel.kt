package com.example.knowmore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.knowmore.data.local.Word
import com.example.knowmore.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ProgressStats(
    val totalWords: Int = 0,
    val wordsLearned: Int = 0,
    val wordsToReview: Int = 0,
    val averageEaseFactor: Float = 0f,
    val totalReviews: Int = 0,
    val masteredWords: Int = 0
)

data class ProgressUiState(
    val stats: ProgressStats = ProgressStats(),
    val wordsByLanguage: Map<String, Int> = emptyMap(),
    val wordsByCategory: Map<String, Int> = emptyMap(),
    val recentWords: List<Word> = emptyList(),
    val isLoading: Boolean = true
)

class ProgressViewModel(private val repository: WordRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgressData()
    }

    private fun loadProgressData() {
        viewModelScope.launch {
            combine(
                repository.getAllWords(),
                repository.getWordsToReviewCount()
            ) { words, toReview ->
                val masteredWords = words.count { it.repetitions >= 5 }
                val learnedWords = words.count { it.repetitions > 0 }
                val totalReviews = words.sumOf { it.repetitions }
                val avgEase = if (words.isNotEmpty()) {
                    words.map { it.easeFactor }.average().toFloat()
                } else 2.5f

                val byLanguage = words.groupBy { it.language }.mapValues { it.value.size }
                val byCategory = words.groupBy { it.category }.mapValues { it.value.size }

                ProgressUiState(
                    stats = ProgressStats(
                        totalWords = words.size,
                        wordsLearned = learnedWords,
                        wordsToReview = toReview,
                        averageEaseFactor = avgEase,
                        totalReviews = totalReviews,
                        masteredWords = masteredWords
                    ),
                    wordsByLanguage = byLanguage,
                    wordsByCategory = byCategory,
                    recentWords = words.sortedByDescending { it.lastReviewDate ?: it.createdAt }.take(10),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    class Factory(private val repository: WordRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
                return ProgressViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
