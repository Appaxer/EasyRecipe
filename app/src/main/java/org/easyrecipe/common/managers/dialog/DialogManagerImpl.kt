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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class DialogManagerImpl : DialogManager() {
    private val _dialog = MutableSharedFlow<DialogState>(replay = 1)
    override val dialog: SharedFlow<DialogState>
        get() = _dialog

    override fun showDialog(data: IntDialog) {
        _dialog.tryEmit(DialogState.ShowIntDialog(data))
    }

    override fun showDialog(data: LambdaDialog) {
        _dialog.tryEmit(DialogState.ShowLambdaDialog(data))
    }

    override fun showLoadingDialog() {
        _dialog.tryEmit(DialogState.ShowLoadingDialog)
    }

    override fun cancelLoadingDialog() {
        _dialog.tryEmit(DialogState.CancelLoadingDialog)
    }

    override fun toast(msg: String, duration: Int) {
        _dialog.tryEmit(DialogState.ShowStringToast(msg, duration))
    }

    override fun toast(msgId: Int, duration: Int) {
        _dialog.tryEmit(DialogState.ShowIntToast(msgId, duration))
    }

    override fun toast(duration: Int, msg: (Context) -> String) {
        _dialog.tryEmit(DialogState.ShowLambdaToast(duration, msg))
    }
}
