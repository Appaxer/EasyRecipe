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

package org.easyrecipe.common.usecases

import android.util.Log

/**
 * Function for running a use case and creating the correct output of them. If an exception is
 * thrown by the use case, it is catch here and the [UseCaseResult.Error] is returned with it.
 * Otherwise, the [UseCaseResult.Success] is returned with the result of the use case.
 *
 * @param O The output of the use case
 * @param method The method that has to be executed in the use case
 * @return The [UseCaseResult] depending on the result of the use case
 */
suspend fun <O : UseCase.UseCaseResponse> runUseCase(
    method: suspend () -> O,
): UseCaseResult<O> = try {
    val result = method()
    UseCaseResult.Success(result)
} catch (e: Exception) {
    Log.d("UseCaseRunner", "runUseCase: ${e.printStackTrace()}")
    UseCaseResult.Error(e)
}
