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

package org.easyrecipe.data.sources

import org.easyrecipe.common.CommonException

/**
 * Executes a dao method
 *
 * @param T The return type of dao method
 * @param onExecuteDao The execution of the dao method
 * @return The result of the dao method
 * @throws CommonException.OtherError if there is any error
 */
suspend fun <T> runDao(onExecuteDao: suspend () -> T): T = try {
    onExecuteDao()
} catch (e: Exception) {
    throw CommonException.OtherError(e.stackTraceToString())
}
