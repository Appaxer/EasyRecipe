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
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.easyrecipe.R
import org.easyrecipe.common.managers.dialog.IntDialog
import org.easyrecipe.common.managers.dialog.LambdaDialog

/**
 * Show a dialog whose values are given with android string ids.
 *
 * @param data The data to be shown
 */
fun Context.showIntDialog(data: IntDialog) = with(data) {
    val negativeText = negativeButtonText?.let { textId -> getString(textId) } ?: ""
    val neutralText = neutralButtonText?.let { textId -> getString(textId) } ?: ""

    val builder = MaterialAlertDialogBuilder(this@showIntDialog)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(getString(positiveButtonText), positiveButtonAction)
        .setNegativeButton(negativeText, negativeButtonAction)
        .setNeutralButton(neutralText, neutralButtonAction)
        .setCancelable(isCancelable)

    val dialog = builder.create()
    dialog.show()
}

/**
 * Show a dialog whose values are given with lambdas.
 *
 * @param data The data to be shown
 */
fun Context.showLambdaDialog(data: LambdaDialog) = with(data) {
    val builder = MaterialAlertDialogBuilder(this@showLambdaDialog)
        .setTitle(title(this@showLambdaDialog))
        .setMessage(message(this@showLambdaDialog))
        .setPositiveButton(positiveButtonText(this@showLambdaDialog), positiveButtonAction)
        .setNegativeButton(negativeButtonText(this@showLambdaDialog), negativeButtonAction)
        .setNeutralButton(neutralButtonText(this@showLambdaDialog), neutralButtonAction)
        .setCancelable(isCancelable)

    val dialog = builder.create()
    dialog.show()
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
