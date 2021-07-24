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

package org.easyrecipe.common.extensions

import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.httpDelete
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.httpPut
import org.easyrecipe.common.http.HttpResponse
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Makes a get http request.
 *
 * @param parameters The parameters of the request
 * @return The result of the request
 */
suspend fun String.getRequest(parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpGet(parameters).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

/**
 * Makes a post http request.
 *
 * @param body The body of the request
 * @param parameters The parameters of the request
 * @return The result of the request
 */
suspend fun String.postRequest(body: Any, parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpPost(parameters).jsonBody(body).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

/**
 * Makes a put http request.
 *
 * @param parameters The parameters of the request
 * @return The result of the request
 */
suspend fun String.putRequest(parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpPut(parameters).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

/**
 * Makes a delete http request.
 *
 * @param parameters The parameters of the request
 * @return The result of the request
 */
suspend fun String.deleteRequest(parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpDelete(parameters).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

/**
 * Calculates a hash from a String.
 *
 * @param algorithm The algorithm used to hash
 * @return The string hash using the specified algorithm
 */
fun String.hash(algorithm: String): String? = try {
    MessageDigest.getInstance(algorithm)
        .digest(toByteArray())
        .fold("") { total, actual ->
            total + "%02x".format(actual)
        }
} catch (e: NoSuchAlgorithmException) {
    null
}
