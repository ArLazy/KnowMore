package com.example.knowmore.data.repository

import com.example.knowmore.data.local.Word
import com.example.knowmore.data.local.WordDao
import com.example.knowmore.domain.usecase.SpacedRepetitionAlgorithm
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()

    suspend fun getWordById(id: Long): Word? = wordDao.getWordById(id)

    fun getWordsForReview(): Flow<List<Word>> = 
        wordDao.getWordsForReview(System.currentTimeMillis())

    fun getWordsByLanguage(language: String): Flow<List<Word>> = 
        wordDao.getWordsByLanguage(language)

    fun getWordsByCategory(category: String): Flow<List<Word>> = 
        wordDao.getWordsByCategory(category)

    fun getTotalWordCount(): Flow<Int> = wordDao.getTotalWordCount()

    fun getWordsToReviewCount(): Flow<Int> = 
        wordDao.getWordsToReviewCount(System.currentTimeMillis())

    fun getAllLanguages(): Flow<List<String>> = wordDao.getAllLanguages()

    fun getAllCategories(): Flow<List<String>> = wordDao.getAllCategories()

    fun getWordsForReviewByLanguage(language: String): Flow<List<Word>> = 
        wordDao.getWordsForReviewByLanguage(System.currentTimeMillis(), language)

    fun getWordsForReviewByCategory(category: String): Flow<List<Word>> = 
        wordDao.getWordsForReviewByCategory(System.currentTimeMillis(), category)

    fun getWordsToReviewCountByLanguage(language: String): Flow<Int> = 
        wordDao.getWordsToReviewCountByLanguage(System.currentTimeMillis(), language)

    fun getWordsToReviewCountByCategory(category: String): Flow<Int> = 
        wordDao.getWordsToReviewCountByCategory(System.currentTimeMillis(), category)

    fun getWordsForReviewByFilter(filter: String): Flow<List<Word>> = 
        wordDao.getWordsForReviewByFilter(System.currentTimeMillis(), filter)

    suspend fun addWord(word: Word): Long = wordDao.insertWord(word)

    suspend fun updateWord(word: Word) = wordDao.updateWord(word)

    suspend fun deleteWord(word: Word) = wordDao.deleteWord(word)

    suspend fun deleteWordById(id: Long) = wordDao.deleteWordById(id)

    suspend fun reviewWord(word: Word, quality: Int): Word {
        val result = SpacedRepetitionAlgorithm.calculateNextReview(word, quality)
        val updatedWord = word.copy(
            easeFactor = result.easeFactor,
            interval = result.interval,
            repetitions = result.repetitions,
            nextReviewDate = result.nextReviewDate,
            lastReviewDate = System.currentTimeMillis()
        )
        wordDao.updateWord(updatedWord)
        return updatedWord
    }
}
