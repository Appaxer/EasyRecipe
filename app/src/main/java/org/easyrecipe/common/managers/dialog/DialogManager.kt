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

package org.easyrecipe.common.managers.dialog

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.SharedFlow
import org.easyrecipe.R

abstract class DialogManager {
    abstract val dialog: SharedFlow<DialogState>

    abstract fun showDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes positiveButtonText: Int = R.string.ok,
        positiveButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
        negativeButtonText: Int? = null,
        negativeButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
        neutralButtonText: Int? = null,
        neutralButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
        isCancelable: Boolean = true,
    )

    abstract fun showDialog(
        title: (Context) -> String,
        message: (Context) -> String,
        positiveButtonText: (Context) -> String? = { context -> context.getString(R.string.ok) },
        positiveButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
        negativeButtonText: (Context) -> String? = { null },
        negativeButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
        neutralButtonText: (Context) -> String? = { null },
        neutralButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
        isCancelable: Boolean = true,
    )

    abstract fun showLoadingDialog()

    abstract fun cancelLoadingDialog()

    abstract fun toast(msg: String, duration: Int = Toast.LENGTH_LONG)

    abstract fun toast(@StringRes msgId: Int, duration: Int = Toast.LENGTH_LONG)
}
