/*
 * Copyright (C) 2021 Appaxer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.easyrecipe.common.extensions

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.easyrecipe.R

/**
 * Show a dialog with [title] and [message].
 *
 * @param title The title of the dialog
 * @param message The message of the dialog
 * @param positiveButtonText The text of the positive button
 * @param positiveButtonAction The action that takes place when the positive button is pressed,
 * only if [positiveButtonAction] is not null, being closing dialog the default behaviour
 * @param negativeButtonText The text of the negative button
 * @param negativeButtonAction The action that takes place when the negative button is pressed,
 * only if [negativeButtonText] is not null, being closing dialog the default behaviour
 * @param neutralButtonText The text of the neutral button
 * @param neutralButtonAction The action that takes place when the neutral button is pressed,
 * only if [neutralButtonText] is not null, being closing dialog the default behaviour
 * @param isCancelable It defines whether the dialog should be cancelable or not
 */
fun Context.showDialog(
    title: String,
    message: String,
    positiveButtonText: String? = getString(android.R.string.ok),
    positiveButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    negativeButtonText: String? = null,
    negativeButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    neutralButtonText: String? = null,
    neutralButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    isCancelable: Boolean = true,
) {
    val builder = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText, positiveButtonAction)
        .setNegativeButton(negativeButtonText, negativeButtonAction)
        .setNeutralButton(neutralButtonText, neutralButtonAction)
        .setCancelable(isCancelable)

    val dialog = builder.create()
    dialog.show()
}

/**
 * Show a dialog with [title] and [message].
 *
 * @param title The id of the title of the dialog
 * @param message The id of message of the dialog
 * @param positiveButtonText The text of the positive button
 * @param positiveButtonAction The action that takes place when the positive button is pressed,
 * only if [positiveButtonAction] is not null, being closing dialog the default behaviour
 * @param negativeButtonText The text of the negative button
 * @param negativeButtonAction The action that takes place when the negative button is pressed,
 * only if [negativeButtonText] is not null, being closing dialog the default behaviour
 * @param neutralButtonText The text of the neutral button
 * @param neutralButtonAction The action that takes place when the neutral button is pressed,
 * only if [neutralButtonText] is not null, being closing dialog the default behaviour
 * @param isCancelable It defines whether the dialog should be cancelable or not
 */
fun Context.showDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    positiveButtonText: String? = getString(android.R.string.ok),
    positiveButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    negativeButtonText: String? = null,
    negativeButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    neutralButtonText: String? = null,
    neutralButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    isCancelable: Boolean = true,
) {
    showDialog(
        getString(title),
        getString(message),
        positiveButtonText,
        positiveButtonAction,
        negativeButtonText,
        negativeButtonAction,
        neutralButtonText,
        neutralButtonAction,
        isCancelable
    )
}

private var loadingDialog: Dialog? = null

/**
 * Show a [loadingDialog]. It can only be canceled with [cancelLoadingDialog].
 */
fun Context.showLoadingDialog() {
    loadingDialog = Dialog(this)
    loadingDialog?.let {
        it.setContentView(R.layout.loading_dialog)
        it.setCancelable(false)
        it.show()
    }
}

/**
 * Dismiss the [loadingDialog] if it is shown.
 */
fun cancelLoadingDialog() {
    loadingDialog?.let {
        if (it.isShowing) {
            it.dismiss()
        }
    }
}

/**
 * Show a toast in the screen given its message.
 *
 * @param msg The message to be displayed in the toast
 * @param duration The duration of the toast
 */
fun Context.toast(msg: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, duration).show()
}

/**
 * Show a toast in the screen given the id of the message.
 *
 * @param msgId The message id to be displayed in the toast
 * @param duration The duration of the toast
 */
fun Context.toast(@StringRes msgId: Int, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msgId, duration).show()
}

/**
 * Converts the specified [size] from dp units to pixel units.
 *
 * @param size The size in dp units
 * @return The size in pixel units
 */
fun Context.dpToPixels(size: Int): Int {
    return (size * resources.displayMetrics.density + 0.5f).toInt()
}
