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

import org.easyrecipe.common.CommonException
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.usecases.UseCase
import org.easyrecipe.common.usecases.UseCaseResult

/**
 * Handler for loading the [ScreenState] depending on the [UseCaseResult].
 *
 * @param T The [UseCase.UseCaseResponse] of the use case
 * @property onSuccess The function that determines which state should be loaded if the use case
 * result is [UseCaseResult.Success]
 * @property onError The function that determines which state should be loaded if the use case
 * result is [UseCaseResult.Error] and it is not one of the [CommonException]
 */
@Suppress("UNCHECKED_CAST")
@Deprecated("The use of states is deprecated, you should use managers instead")
class UseCaseResultHandler<T : UseCase.UseCaseResponse>(
    private val onSuccess: (T) -> ScreenState,
    private val onError: (Exception) -> ScreenState,
) {

    /**
     * Obtain the [ScreenState] depending on the [UseCaseResult].
     *
     * @param useCaseResult The result of the use case
     */
    fun getScreenState(useCaseResult: UseCaseResult<*>) = when (useCaseResult) {
        is UseCaseResult.Success -> onSuccess(useCaseResult.result as T)
        else -> showOnError(useCaseResult)
    }

    private fun showOnError(useCaseResult: UseCaseResult<*>) =
        when (val exception = (useCaseResult as UseCaseResult.Error).exception) {
            CommonException.NoInternetException -> ScreenState.NoInternet
            is CommonException.OtherError -> ScreenState.OtherError
            else -> onError(exception)
        }
}
