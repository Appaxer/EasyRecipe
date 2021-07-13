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

package org.easyrecipe.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.easyrecipe.common.handlers.UseCaseResultHandler
import org.easyrecipe.common.usecases.UseCase

/**
 * Class from which all view models must extend from. It is a subclass of [ViewModel] but it adds
 * the screen state [LiveData], a method to update it and a method to execute a use case and handle
 * its result.
 */
abstract class BaseViewModel : ViewModel() {
    private val _screenState = MutableLiveData<ScreenState>(ScreenState.Loading)
    val screenState: LiveData<ScreenState>
        get() = _screenState

    /**
     * Loads a new state to the fragment.
     *
     * @param state The state to be loaded
     */
    protected fun loadState(state: ScreenState) {
        _screenState.value = state
    }

    /**
     * Executes a [UseCase] and handles its result. The [prepareInput] method is called to prepare
     * the input of the [UseCase].
     *
     * @param I The input type of the use case
     * @param O The output type of the use case
     * @param useCaseResultHandler The handler for the result of the use case
     * @param isExecutingUseCaseStateLoaded The [ScreenState.ExecutingUseCase] state is loaded
     * @param prepareInput Method to prepare the input of the use case
     */
    protected suspend fun <I : UseCase.UseCaseRequest, O : UseCase.UseCaseResponse> executeUseCase(
        useCase: UseCase<I, O>,
        useCaseResultHandler: UseCaseResultHandler<O>,
        isExecutingUseCaseStateLoaded: Boolean = true,
        prepareInput: () -> I,
    ) {
        if (isExecutingUseCaseStateLoaded) {
            loadState(ScreenState.ExecutingUseCase)
        }

        val input = prepareInput()
        val result = withContext(Dispatchers.IO) {
            useCase.execute(input)
        }

        loadState(useCaseResultHandler.getScreenState(result))
    }

    /**
     * Loads the [ScreenState.Nothing] state.
     */
    fun onLoadNothing() {
        loadState(ScreenState.Nothing)
    }
}
