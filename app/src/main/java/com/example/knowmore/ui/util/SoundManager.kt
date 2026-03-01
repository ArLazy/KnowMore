package com.example.knowmore.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.knowmore.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val tapClickSoundId: Int = soundPool.load(context, R.raw.tapclick, 1)
    private val whooshSoundId: Int = soundPool.load(context, R.raw.whoosh, 1)
    private val completeSoundId: Int = soundPool.load(context, R.raw.complete, 1)

    fun playTapClick() {
        try {
            soundPool.play(tapClickSoundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playWhoosh() {
        try {
            soundPool.play(whooshSoundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playComplete() {
        try {
            soundPool.play(completeSoundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            soundPool.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
