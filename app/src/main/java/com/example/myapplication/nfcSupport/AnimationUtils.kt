package com.example.myapplication.nfcSupport

import android.animation.ObjectAnimator
import android.view.View

object AnimationUtils {

    fun startLineVerticalAnimation(view: View, durationPerCycle: Long): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "translationY", 2200f).apply {
            duration = durationPerCycle
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            start()
        }
    }
}