package uz.umarov.fcneftchi.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.ViewStateBinding

class StateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewStateBinding =
        ViewStateBinding.inflate(LayoutInflater.from(context), this)

    fun showLoading() {
        isVisible(true)
        binding.stateProgress.visibility = View.VISIBLE
        binding.stateMessageContainer.visibility = View.GONE
    }

    fun showError(
        @StringRes titleRes: Int = R.string.state_error_title,
        @StringRes messageRes: Int = R.string.state_error_message,
        @DrawableRes iconRes: Int = R.drawable.ic_error_outline,
        onRetry: (() -> Unit)? = null,
    ) {
        showMessage(
            titleRes = titleRes,
            messageRes = messageRes,
            iconRes = iconRes,
            onRetry = onRetry,
        )
    }

    fun showError(
        title: CharSequence?,
        message: CharSequence?,
        @DrawableRes iconRes: Int = R.drawable.ic_error_outline,
        onRetry: (() -> Unit)? = null,
    ) {
        showMessage(
            title = title,
            message = message,
            iconRes = iconRes,
            onRetry = onRetry,
        )
    }

    fun showEmpty(
        @StringRes titleRes: Int = R.string.state_empty_title,
        @StringRes messageRes: Int = R.string.state_empty_message,
        @DrawableRes iconRes: Int = R.drawable.ic_inbox,
    ) {
        showMessage(
            titleRes = titleRes,
            messageRes = messageRes,
            iconRes = iconRes,
            onRetry = null,
        )
    }

    fun hide() {
        visibility = View.GONE
    }

    private fun isVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun showMessage(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @DrawableRes iconRes: Int,
        onRetry: (() -> Unit)?,
    ) {
        showMessage(
            title = context.getString(titleRes),
            message = context.getString(messageRes),
            iconRes = iconRes,
            onRetry = onRetry,
        )
    }

    private fun showMessage(
        title: CharSequence?,
        message: CharSequence?,
        @DrawableRes iconRes: Int,
        onRetry: (() -> Unit)?,
    ) {
        isVisible(true)
        binding.stateProgress.visibility = View.GONE
        binding.stateMessageContainer.visibility = View.VISIBLE
        binding.stateIcon.setImageResource(iconRes)

        binding.stateTitle.apply {
            text = title
            visibility = if (title.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        binding.stateMessage.apply {
            text = message
            visibility = if (message.isNullOrBlank()) View.GONE else View.VISIBLE
        }
        binding.stateRetryButton.apply {
            if (onRetry != null) {
                visibility = View.VISIBLE
                setOnClickListener { onRetry() }
            } else {
                visibility = View.GONE
                setOnClickListener(null)
            }
        }
    }
}
