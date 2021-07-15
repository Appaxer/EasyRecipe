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

suspend fun String.getRequest(parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpGet(parameters).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

suspend fun String.postRequest(body: Any, parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpPost(parameters).jsonBody(body).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

suspend fun String.putRequest(parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpPut(parameters).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}

suspend fun String.deleteRequest(parameters: Parameters? = null): HttpResponse {
    val (request, response, result) = httpDelete(parameters).responseString()
    return HttpResponse(request, response, result).logConnectionResults()
}
