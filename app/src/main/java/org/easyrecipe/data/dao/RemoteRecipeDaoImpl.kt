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

package org.easyrecipe.data.dao

import com.google.gson.Gson
import org.easyrecipe.BuildConfig
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.extensions.fromJson
import org.easyrecipe.common.extensions.getRequest
import org.easyrecipe.common.http.HttpResponse
import org.easyrecipe.data.edamam.EdamamHit
import org.easyrecipe.data.edamam.EdamamResponse
import org.easyrecipe.model.MealType
import org.easyrecipe.model.RemoteRecipe
import java.net.HttpURLConnection.HTTP_OK
import java.util.*
import javax.inject.Inject

class RemoteRecipeDaoImpl @Inject constructor(
    private val gson: Gson,
) : RemoteRecipeDao {
    private val appId = BuildConfig.EDAMAM_ID
    private val apiKey = BuildConfig.EDAMAM_KEY
    private val defaultParameters = mutableListOf(
        "app_id" to appId,
        "app_key" to apiKey,
        "type" to "public",
    )

    private var parameters: MutableList<Pair<String, Any>> = defaultParameters.toMutableList()

    override suspend fun getRecipes(name: String): List<RemoteRecipe> {
        resetParameters()

        if (name.isEmpty()) {
            addQueryParameter(('b'..'z').random().toString())
        } else {
            addQueryParameter(name)
        }
        addRandomParameter()
        val response = URL.getRequest(parameters)

        checkResponseCode(response)

        val edamamResponse: EdamamResponse = gson.fromJson(response.result.get())
        return edamamResponse.hits.map { hit ->
            hit.recipe.toRemoteRecipe()
        }
    }

    override suspend fun getRecipesByMealType(
        name: String,
        mealTypes: List<MealType>,
    ): List<RemoteRecipe> {
        resetParameters()
        addQueryParameter(name, mealTypes)
        addRandomParameter()
        val response = URL.getRequest(parameters)

        checkResponseCode(response)

        val edamamResponse: EdamamResponse = gson.fromJson(response.result.get())
        return edamamResponse.hits.map { hit ->
            hit.recipe.toRemoteRecipe()
        }

    }

    override suspend fun getFavoriteRecipes(recipeIds: List<String>): List<RemoteRecipe> {
        resetParameters()
        return recipeIds.map {
            val reqUrl = "$URL/${it.substringAfter('#')}"
            val response = reqUrl.getRequest(parameters)

            checkResponseCode(response)

            val edamamResponse: EdamamHit = gson.fromJson(response.result.get())
            val remoteRecipe = edamamResponse.recipe.toRemoteRecipe()
            remoteRecipe.toggleFavorite()
            remoteRecipe
        }
    }

    private fun addRandomParameter() {
        parameters.add("random" to true)
    }

    private fun addQueryParameter(name: String) {
        parameters.add("q" to name)
    }

    private fun addQueryParameter(name: String, mealTypes: List<MealType>) {
        parameters.add("q" to name)
        mealTypes.map {
            parameters.add("mealType" to it.toString().lowercase())
        }
    }

    private fun checkResponseCode(response: HttpResponse) {
        response.onStatusCode {
            if (it == HTTP_OK) {
                return@onStatusCode
            } else {
                throw CommonException.OtherError("API error: response code $this")
            }
        }
    }

    private fun resetParameters() {
        parameters = defaultParameters.toMutableList()
    }

    companion object {
        private const val URL = "https://api.edamam.com/api/recipes/v2"
    }
}
