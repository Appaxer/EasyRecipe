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
 * Class that represents the possible results of a use case.
 *
 * @param O The output of the use case
 */
sealed class UseCaseResult<O : UseCase.UseCaseResponse> {

    /**
     * The use case has been executed successfully and [result] contains the result of it.
     *
     * @param O The output of the use case
     * @property result The result of the use case
     */
    class Success<O : UseCase.UseCaseResponse>(val result: O) : UseCaseResult<O>()

    /**
     * There has been an error when executing the use case.
     *
     * @param O The output of the use case
     * @property exception The error that caused the use case to fail
     */
    class Error<O : UseCase.UseCaseResponse>(val exception: Exception) : UseCaseResult<O>()
}
