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

import android.content.SharedPreferences
import android.util.Log
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import org.easyrecipe.BuildConfig
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.extensions.fromJson
import org.easyrecipe.data.edamam.EdamamHit
import org.easyrecipe.data.edamam.EdamamResponse
import org.easyrecipe.features.settings.SettingsFragment.Companion.API_LANGUAGE
import org.easyrecipe.model.MealType
import org.easyrecipe.model.RemoteRecipe
import java.util.*
import javax.inject.Inject

class RemoteRecipeDaoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
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
    private val url: String
        get() = when (sharedPreferences.getString(API_LANGUAGE, EN)) {
            ES -> ES_URL
            else -> EN_URL
        }

    override suspend fun getRecipes(name: String): List<RemoteRecipe> {
        resetParameters()

        if (name.isEmpty()) {
            addQueryParameter(('b'..'z').random().toString())
        } else {
            addQueryParameter(name)
        }
        parameters.add("random" to true)
        val (_, response, result) = url.httpGet(parameters).responseString()

        checkResponseCode(response)

        val edamamResponse: EdamamResponse = gson.fromJson(result.get())
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
        parameters.add("random" to true)
        val (_, response, result) = url.httpGet(parameters).responseString()

        checkResponseCode(response)

        val edamamResponse: EdamamResponse = gson.fromJson(result.get())
        return edamamResponse.hits.map { hit ->
            hit.recipe.toRemoteRecipe()
        }

    }

    override suspend fun getFavoriteRecipes(recipeIds: List<String>): List<RemoteRecipe> {
        resetParameters()
        return recipeIds.map {
            val reqUrl = "$url/${it.substringAfter('#')}"
            val (_, response, result) = reqUrl.httpGet(parameters).responseString()

            checkResponseCode(response)

            Log.i(this.javaClass.name, "getFavoriteRecipes: ${result.get()}")
            val edamamResponse: EdamamHit = gson.fromJson(result.get())
            edamamResponse.recipe.toRemoteRecipe()
        }
    }

    private fun addQueryParameter(name: String) {
        parameters.add("q" to name)
    }

    private fun addIdParameter(uri: String) {
        parameters.add("r" to uri)
    }

    private fun addQueryParameter(name: String, mealTypes: List<MealType>) {
        parameters.add("q" to name)
        mealTypes.map {
            parameters.add("mealType" to it.toString().lowercase())
        }
    }

    private fun checkResponseCode(response: Response) {
        Log.i("RemoteRecipeDaoImpl", "checkResponseCode: $response")
        response.statusCode.run {
            when (this) {
                NO_INTERNET -> throw CommonException.NoInternetException
                OK -> return@run
                else -> throw CommonException.OtherError("API error: response code $this")
            }
        }
    }

    private fun resetParameters() {
        parameters = defaultParameters.toMutableList()
    }

    companion object {
        private const val NO_INTERNET = -1
        private const val OK = 200
        private const val EN_URL = "https://api.edamam.com/api/recipes/v2"
        private const val ES_URL = "https://test-es.edamam.com/search"
        private const val ES = "es"
        private const val EN = "en"
    }
}
