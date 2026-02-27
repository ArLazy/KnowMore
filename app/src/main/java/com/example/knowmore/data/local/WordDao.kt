package com.example.knowmore.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Long): Word?

    @Query("SELECT * FROM words WHERE nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    fun getWordsForReview(currentTime: Long): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE language = :language ORDER BY createdAt DESC")
    fun getWordsByLanguage(language: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY createdAt DESC")
    fun getWordsByCategory(category: String): Flow<List<Word>>

    @Query("SELECT COUNT(*) FROM words")
    fun getTotalWordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE nextReviewDate <= :currentTime")
    fun getWordsToReviewCount(currentTime: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("DELETE FROM words WHERE id = :id")
    suspend fun deleteWordById(id: Long)

    @Query("SELECT DISTINCT language FROM words")
    fun getAllLanguages(): Flow<List<String>>

    @Query("SELECT DISTINCT category FROM words")
    fun getAllCategories(): Flow<List<String>>
}
