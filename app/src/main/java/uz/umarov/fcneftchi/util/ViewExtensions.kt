package uz.umarov.fcneftchi.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun View.applySystemBarPadding(top: Boolean = false, bottom: Boolean = false) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

        val paddingTop = if (top) insets.top else 0
        val paddingBottom = if (bottom) insets.bottom else 0

        view.updatePadding(top = paddingTop, bottom = paddingBottom)

        windowInsets
    }
}