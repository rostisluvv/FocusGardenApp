package com.example.focusgarden.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.focusgarden.model.SessionStatus

object SessionFeedback {
    fun playSessionFinished(
        context: Context,
        status: SessionStatus,
        soundEnabled: Boolean,
        vibrationEnabled: Boolean
    ) {
        if (soundEnabled) {
            playTone(success = status == SessionStatus.COMPLETED)
        }
        if (vibrationEnabled) {
            vibrate(context, success = status == SessionStatus.COMPLETED)
        }
    }

    private fun playTone(success: Boolean) {
        runCatching {
            val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
            tone.startTone(
                if (success) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK,
                150
            )
            Handler(Looper.getMainLooper()).postDelayed({ tone.release() }, 220)
        }
    }

    private fun vibrate(context: Context, success: Boolean) {
        val vibrator = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }.getOrNull() ?: return

        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = if (success) {
                VibrationEffect.createOneShot(90, VibrationEffect.DEFAULT_AMPLITUDE)
            } else {
                VibrationEffect.createWaveform(longArrayOf(0, 70, 60, 120), -1)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            if (success) vibrator.vibrate(90) else vibrator.vibrate(longArrayOf(0, 70, 60, 120), -1)
        }
    }
}
