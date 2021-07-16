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
import androidx.annotation.StringRes

sealed class DialogState {
    class ShowIntDialog(val data: IntDialog) : DialogState()
    class ShowLambdaDialog(val data: LambdaDialog) : DialogState()
    object ShowLoadingDialog : DialogState()
    object CancelLoadingDialog : DialogState()
    class ShowStringToast(val data: String, val duration: Int) : DialogState()
    class ShowIntToast(@StringRes val data: Int, val duration: Int) : DialogState()
    class ShowLambdaToast(val duration: Int, val msg: (Context) -> String) : DialogState()
}
