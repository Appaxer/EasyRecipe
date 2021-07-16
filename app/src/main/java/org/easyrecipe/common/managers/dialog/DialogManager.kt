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
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.SharedFlow

abstract class DialogManager {
    abstract val dialog: SharedFlow<DialogState>

    /**
     * Show a dialog whose data is represented with string resource ids.
     *
     * @param data The data to be displayed
     */
    abstract fun showDialog(data: IntDialog)

    /**
     * Show a dialog whose data is obtained from lambda with the current context as its parameter.
     *
     * @param data The data to be displayed
     */
    abstract fun showDialog(data: LambdaDialog)

    /**
     * Show the loading dialog.
     */
    abstract fun showLoadingDialog()

    /**
     * Cancel the loading dialog.
     */
    abstract fun cancelLoadingDialog()

    /**
     * Show a toast given the string to be displayed.
     *
     * @param msg The message to be displayed
     * @param duration The duration of the toast
     */
    abstract fun toast(msg: String, duration: Int = Toast.LENGTH_LONG)

    /**
     * Show a toast given the string resource id to be displayed.
     *
     * @param msgIs The id of the message to be displayed
     * @param duration The duration of the toast
     */
    abstract fun toast(@StringRes msgId: Int, duration: Int = Toast.LENGTH_LONG)

    /**
     * Show a toast given a lambda that calculates the message to be displayed.
     *
     * @param duration The duration of the toast
     * @param msg The lambda to calculate the message to be displayed
     */
    abstract fun toast(duration: Int, msg: (Context) -> String)
}
