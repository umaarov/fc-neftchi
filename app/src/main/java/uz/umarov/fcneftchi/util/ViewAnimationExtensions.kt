package uz.umarov.fcneftchi.util

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isGone
import androidx.core.view.isVisible

fun View.slideUp() {
    if (isVisible) return
    visibility = View.VISIBLE
    alpha = 0f
    translationY = height.toFloat()
    animate()
        .setDuration(300)
        .translationY(0f)
        .alpha(1f)
        .setInterpolator(DecelerateInterpolator())
        .start()
}

fun View.slideDown() {
    if (isGone) return
    animate()
        .setDuration(300)
        .translationY(height.toFloat())
        .alpha(0f)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
        }
        .start()
}

fun View.slideInFromTop() {
    if (isVisible) return
    visibility = View.VISIBLE
    alpha = 0f
    translationY = -height.toFloat()
    animate()
        .setDuration(300)
        .translationY(0f)
        .alpha(1f)
        .setInterpolator(DecelerateInterpolator())
        .start()
}

fun View.slideOutToTop() {
    if (isGone) return
    animate()
        .setDuration(300)
        .translationY(-height.toFloat())
        .alpha(0f)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            visibility = View.GONE
        }
        .start()
}