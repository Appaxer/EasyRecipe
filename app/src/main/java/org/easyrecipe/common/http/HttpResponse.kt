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

package org.easyrecipe.common.http

import android.util.Log
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import org.easyrecipe.common.CommonException
import java.net.HttpURLConnection

class HttpResponse(
    val request: Request,
    val response: Response,
    val result: Result<String, FuelError>,
) {

    /**
     * Log the connection results.
     *
     * @return The current connection results.
     */
    fun logConnectionResults(): HttpResponse {
        Log.i("Server", "Request: $request")
        Log.i("Server", "Response: $response")
        Log.i("Server", "Result: $result")

        return this
    }

    /**
     * Perform an action with the status code. For the no internet and server error cases there is
     * a default behaviour implementation, which can be disabled with [isInternetErrorDefault] and
     * [isServerErrorDefault].
     *
     * @param isInternetErrorDefault Indicates whether the no internet default behaviour is enabled
     * @param isServerErrorDefault Indicates whether the server error default behaviour is enabled
     * @param action The action to be performed depending on the status code
     */
    fun onStatusCode(
        isInternetErrorDefault: Boolean = true,
        isServerErrorDefault: Boolean = true,
        action: (Int) -> Unit,
    ) {
        when {
            isInternetErrorDefault && response.statusCode == -1 -> throw CommonException.NoInternetException
            isServerErrorDefault && response.statusCode >= HttpURLConnection.HTTP_INTERNAL_ERROR ->
                throw CommonException.OtherError(result.component2()?.stackTraceToString() ?: "")
            else -> action(response.statusCode)
        }
    }
}
