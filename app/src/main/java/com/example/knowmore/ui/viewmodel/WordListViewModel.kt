package com.example.knowmore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.knowmore.data.local.Word
import com.example.knowmore.data.repository.WordRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class WordListUiState(
    val words: List<Word> = emptyList(),
    val filteredWords: List<Word> = emptyList(),
    val selectedLanguage: String? = null,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

class WordListViewModel(private val repository: WordRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(WordListUiState())
    val uiState: StateFlow<WordListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedLanguage = MutableStateFlow<String?>(null)
    private val _selectedCategory = MutableStateFlow<String?>(null)

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            combine(
                repository.getAllWords(),
                _searchQuery,
                _selectedLanguage,
                _selectedCategory
            ) { words, query, language, category ->
                val filtered = words.filter { word ->
                    val matchesQuery = query.isEmpty() ||
                        word.originalWord.contains(query, ignoreCase = true) ||
                        word.translatedWord.contains(query, ignoreCase = true)
                    val matchesLanguage = language == null || word.language == language
                    val matchesCategory = category == null || word.category == category
                    matchesQuery && matchesLanguage && matchesCategory
                }
                WordListUiState(
                    words = words,
                    filteredWords = filtered,
                    selectedLanguage = language,
                    selectedCategory = category,
                    searchQuery = query,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLanguageFilter(language: String?) {
        _selectedLanguage.value = language
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun addWord(originalWord: String, translatedWord: String, language: String, category: String) {
        viewModelScope.launch {
            val word = Word(
                originalWord = originalWord,
                translatedWord = translatedWord,
                language = language,
                category = category
            )
            repository.addWord(word)
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            repository.updateWord(word)
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
            if (modelClass.isAssignableFrom(WordListViewModel::class.java)) {
                return WordListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
