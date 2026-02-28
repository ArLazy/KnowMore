package com.example.knowmore.domain.usecase

import com.example.knowmore.data.local.Word

data class SpacedRepetitionResult(
    val easeFactor: Float,
    val interval: Int,
    val repetitions: Int,
    val nextReviewDate: Long
)

object SpacedRepetitionAlgorithm {

    const val MIN_EASE_FACTOR = 1.3f
    const val MAX_EASE_FACTOR = 2.5f

    fun calculateNextReview(
        word: Word,
        quality: Int
    ): SpacedRepetitionResult {
        val clampedQuality = quality.coerceIn(0, 5)

        val newEaseFactor = calculateNewEaseFactor(word.easeFactor, clampedQuality)
        val (newInterval, newRepetitions) = calculateNewIntervalAndRepetitions(
            word.interval,
            word.repetitions,
            clampedQuality,
            newEaseFactor
        )

        val nextReviewDate = if (newInterval == 0) {
            System.currentTimeMillis() + (1 * 60 * 1000L)
        } else {
            System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)
        }

        return SpacedRepetitionResult(
            easeFactor = newEaseFactor,
            interval = newInterval,
            repetitions = newRepetitions,
            nextReviewDate = nextReviewDate
        )
    }

    private fun calculateNewEaseFactor(currentEaseFactor: Float, quality: Int): Float {
        val newEaseFactor = currentEaseFactor + (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
        return newEaseFactor.coerceAtLeast(MIN_EASE_FACTOR)
    }

    private fun calculateNewIntervalAndRepetitions(
        currentInterval: Int,
        currentRepetitions: Int,
        quality: Int,
        easeFactor: Float
    ): Pair<Int, Int> {
        return when {
            quality == 0 -> {
                Pair(0, 0)
            }
            quality < 3 -> {
                Pair(1, 0)
            }
            currentRepetitions == 0 -> {
                Pair(1, 1)
            }
            currentRepetitions == 1 -> {
                Pair(6, 2)
            }
            else -> {
                val multiplier = when (quality) {
                    3 -> easeFactor * 0.8f
                    4 -> easeFactor
                    5 -> easeFactor * 1.3f
                    else -> easeFactor
                }
                val newInterval = (currentInterval * multiplier).toInt().coerceAtLeast(1)
                Pair(newInterval, currentRepetitions + 1)
            }
        }
    }

    fun getQualityDescription(quality: Int): String {
        return when (quality) {
            0 -> "Complete blackout"
            1 -> "Incorrect response; the correct one remembered"
            2 -> "Incorrect response; where it seemed easy to recall"
            3 -> "Correct response with serious difficulty"
            4 -> "Correct response after hesitation"
            5 -> "Perfect response"
            else -> "Unknown"
        }
    }
}
