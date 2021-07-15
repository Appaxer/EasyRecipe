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

package org.easyrecipe.common.handlers

import android.content.Context
import org.easyrecipe.R
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.extensions.cancelLoadingDialog
import org.easyrecipe.common.extensions.showIntDialog
import org.easyrecipe.common.extensions.showLoadingDialog

/**
 * Handler for performing some actions depending on the screen state that is being currently
 * displayed in the fragment. It requires to have the fragment context in order to perform
 * some activity so it must be added to this class as soon as it is available, that is inside the
 * onCreateView of the fragment. In order to control the common states we can pass some functions
 * to the constructor. The default behaviour of [onLoading] is nothing and the one from
 * [onNoInternet] and [onOtherError] is showing a dialog. If the screen state is not any of the
 * common ones, then the [action] method is called with that state.
 *
 * @param T The states that are associated with the fragment
 * @property context The [Context] of the fragment
 * @property onLoading The [ScreenState.Loading] behaviour, being the default nothing
 * @property onNoInternet The [ScreenState.NoInternet] behaviour, being the default a dialog
 * @property onOtherError The [ScreenState.OtherError] behaviour, being the default a dialog
 * @property action The action that has to be performed depending of the [ScreenState]
 */
@Suppress("UNCHECKED_CAST")
class ScreenStateHandler<T : ScreenState>(
    var context: Context? = null,
    private val onLoading: (Context) -> Unit = {},
    private val onNoInternet: (Context) -> Unit = {
        it.showIntDialog(
            R.string.no_internet_title,
            R.string.no_internet_message
        )
    },
    private val onOtherError: (Context) -> Unit = {
        it.showIntDialog(
            R.string.other_error_title,
            R.string.other_error_message
        )
    },
    private val onExecutingUseCase: (Context) -> Unit = { it.showLoadingDialog() },
    private val action: (Context, T) -> Unit,
) {

    /**
     * Executes the action that is associated with [state].
     *
     * @param state The current stata of the fragment
     * @throws ContextNotSetException The context has not been set yet
     */
    fun executeStateAction(state: ScreenState) = context?.let {
        cancelLoadingDialog()
        when (state) {
            ScreenState.Loading -> onLoading(it)
            ScreenState.NoInternet -> onNoInternet(it)
            ScreenState.OtherError -> onOtherError(it)
            ScreenState.ExecutingUseCase -> onExecutingUseCase(it)
            ScreenState.Nothing -> {
            }
            else -> action(it, state as T)
        }
    } ?: throw ContextNotSetException()
}
