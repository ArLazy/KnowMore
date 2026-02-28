package com.example.knowmore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.knowmore.data.local.Word
import com.example.knowmore.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FlashcardUiState(
    val wordsToReview: List<Word> = emptyList(),
    val currentWordIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true,
    val isTransitioning: Boolean = false,
    val sessionComplete: Boolean = false,
    val reviewedCount: Int = 0,
    val nextReviewDate: Long? = null
) {
    val currentWord: Word?
        get() = wordsToReview.getOrNull(currentWordIndex)
}

class FlashcardViewModel(
    private val repository: WordRepository,
    private val languageFilter: String? = null,
    private val categoryFilter: String? = null,
    private val genericFilter: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        loadWordsForReview()
    }

    private fun loadWordsForReview() {
        viewModelScope.launch {
            val wordsFlow = when {
                genericFilter != null -> repository.getWordsForReviewByFilter(genericFilter)
                languageFilter != null -> repository.getWordsForReviewByLanguage(languageFilter)
                categoryFilter != null -> repository.getWordsForReviewByCategory(categoryFilter)
                else -> repository.getWordsForReview()
            }
            
            wordsFlow.collect { words ->
                _uiState.update { state ->
                    state.copy(
                        wordsToReview = words,
                        isLoading = false,
                        sessionComplete = words.isEmpty()
                    )
                }
            }
        }
    }

    fun flipCard() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun rateWord(quality: Int) {
        val currentState = _uiState.value
        val currentWord = currentState.currentWord ?: return

        _uiState.update { it.copy(isTransitioning = true) }
        
        viewModelScope.launch {
            val updatedWord = repository.reviewWord(currentWord, quality)
            
            val newReviewedCount = currentState.reviewedCount + 1
            val nextIndex = currentState.currentWordIndex + 1
            val isComplete = nextIndex >= currentState.wordsToReview.size
            
            val currentMinReviewDate = currentState.nextReviewDate
            val newMinReviewDate = if (currentMinReviewDate == null || updatedWord.nextReviewDate < currentMinReviewDate) {
                updatedWord.nextReviewDate
            } else {
                currentMinReviewDate
            }

            _uiState.update { state ->
                state.copy(
                    currentWordIndex = if (isComplete) state.currentWordIndex else nextIndex,
                    isFlipped = false,
                    isTransitioning = false,
                    sessionComplete = isComplete,
                    reviewedCount = newReviewedCount,
                    nextReviewDate = if (isComplete) newMinReviewDate else state.nextReviewDate
                )
            }
        }
    }

    fun resetSession() {
        _uiState.update { state ->
            state.copy(
                currentWordIndex = 0,
                isFlipped = false,
                sessionComplete = false,
                reviewedCount = 0,
                nextReviewDate = null
            )
        }
    }

    class Factory(
        private val repository: WordRepository,
        private val languageFilter: String? = null,
        private val categoryFilter: String? = null,
        private val genericFilter: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FlashcardViewModel::class.java)) {
                return FlashcardViewModel(repository, languageFilter, categoryFilter, genericFilter) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
