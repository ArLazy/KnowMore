package com.example.knowmore

import com.example.knowmore.data.local.Word
import com.example.knowmore.domain.usecase.SpacedRepetitionAlgorithm
import org.junit.Test
import org.junit.Assert.*

class SpacedRepetitionAlgorithmTest {

    @Test
    fun testFirstReviewWithQuality5() {
        val word = Word(
            id = 1,
            originalWord = "Hello",
            translatedWord = "Hola",
            language = "Spanish",
            easeFactor = 2.5f,
            interval = 0,
            repetitions = 0
        )

        val result = SpacedRepetitionAlgorithm.calculateNextReview(word, 5)

        assertEquals(2.5f, result.easeFactor, 0.01f)
        assertEquals(1, result.interval)
        assertEquals(1, result.repetitions)
    }

    @Test
    fun testSecondReviewWithQuality5() {
        val word = Word(
            id = 1,
            originalWord = "Hello",
            translatedWord = "Hola",
            language = "Spanish",
            easeFactor = 2.5f,
            interval = 1,
            repetitions = 1
        )

        val result = SpacedRepetitionAlgorithm.calculateNextReview(word, 5)

        assertEquals(2.5f, result.easeFactor, 0.01f)
        assertEquals(6, result.interval)
        assertEquals(2, result.repetitions)
    }

    @Test
    fun testFailedReviewResetsRepetitions() {
        val word = Word(
            id = 1,
            originalWord = "Hello",
            translatedWord = "Hola",
            language = "Spanish",
            easeFactor = 2.5f,
            interval = 6,
            repetitions = 2
        )

        val result = SpacedRepetitionAlgorithm.calculateNextReview(word, 1)

        assertTrue(result.easeFactor < 2.5f)
        assertEquals(1, result.interval)
        assertEquals(0, result.repetitions)
    }

    @Test
    fun testEaseFactorNeverBelowMinimum() {
        var word = Word(
            id = 1,
            originalWord = "Hello",
            translatedWord = "Hola",
            language = "Spanish",
            easeFactor = 1.3f,
            interval = 1,
            repetitions = 0
        )

        repeat(10) {
            val result = SpacedRepetitionAlgorithm.calculateNextReview(word, 0)
            assertTrue(result.easeFactor >= SpacedRepetitionAlgorithm.MIN_EASE_FACTOR)
            word = word.copy(easeFactor = result.easeFactor)
        }
    }

    @Test
    fun testQualityDescriptions() {
        assertEquals("Complete blackout", SpacedRepetitionAlgorithm.getQualityDescription(0))
        assertEquals("Incorrect response; the correct one remembered", SpacedRepetitionAlgorithm.getQualityDescription(1))
        assertEquals("Perfect response", SpacedRepetitionAlgorithm.getQualityDescription(5))
        assertEquals("Unknown", SpacedRepetitionAlgorithm.getQualityDescription(6))
    }

    @Test
    fun testQualityClamping() {
        val word = Word(
            id = 1,
            originalWord = "Hello",
            translatedWord = "Hola",
            language = "Spanish"
        )

        val resultNegative = SpacedRepetitionAlgorithm.calculateNextReview(word, -5)
        val resultTooHigh = SpacedRepetitionAlgorithm.calculateNextReview(word, 10)

        assertTrue(resultNegative.interval >= 0)
        assertTrue(resultTooHigh.interval >= 0)
    }
}
