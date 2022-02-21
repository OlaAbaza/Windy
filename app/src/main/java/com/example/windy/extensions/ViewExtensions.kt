package com.example.windy.extensions

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.windy.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: CharSequence, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

fun View.isVisibile(): Boolean = visibility == View.VISIBLE

fun View.isGone(): Boolean = visibility == View.GONE

fun View.isInvisible(): Boolean = visibility == View.INVISIBLE

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.showSnackbar(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    action: Snackbar.() -> Unit
) {
    val snackBar = Snackbar.make(this, resources.getString(messageRes), length)
    snackBar.apply {
        action()
        duration.minus(1)
        show()
    }

}

fun Snackbar.action(
    @StringRes actionRes: Int,
    listener: (View) -> Unit,
    onDismissed: (() -> Unit?)? = null
) {
    setAction(actionRes, listener)
    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onShown(transientBottomBar: Snackbar?) {
            super.onShown(transientBottomBar)
        }

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                onDismissed?.let {
                    onDismissed.invoke()
                }
            }

        }
    })
    setTextColor(Color.parseColor("#FFFFFFFF"))
    setActionTextColor(Color.parseColor("#FFBB86FC"))
    setBackgroundTint(Color.parseColor("#616161"))
}

fun Activity.showCheckNetworkDialog() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.check_internet)
        .setCancelable(false)
        .setPositiveButton(R.string.connect) { dialog, _ ->
            this.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            dialog.dismiss()
        }
        .setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}


