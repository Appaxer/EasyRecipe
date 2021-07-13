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

/**
 * Interface for representing the use cases of the application. Although the use cases might only
 * have one input or output parameter, both must be a data class in order to make the code more
 * changeable. To ensure that, the input parameter must implement the [UseCaseRequest] interface
 * and the output one, the [UseCaseResponse] interface. Moreover, the use cases cannot be executed
 * in the main thread, so it is advisable to use coroutines.
 *
 * @param I The input of the use case
 * @param O The output of the use case
 */
interface UseCase<I : UseCase.UseCaseRequest, O : UseCase.UseCaseResponse> {

    /**
     * Executes the use case.
     *
     * @param request The input parameters of the use case
     * @return The result of the use case
     */
    suspend fun execute(request: I): UseCaseResult<O>

    /**
     * Interface representing the input parameters of a use case.
     */
    interface UseCaseRequest

    /**
     * Interface representing the output parameters of a use case.
     */
    interface UseCaseResponse
}
