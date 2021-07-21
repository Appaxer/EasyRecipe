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

package org.easyrecipe.data.edamam

import com.google.gson.annotations.SerializedName
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import kotlin.math.roundToInt

data class EdamamResponse(
    var hits: List<EdamamHit>,
)

data class EdamamHit(
    var recipe: EdamamRecipe,
)

data class EdamamRecipe(
    @SerializedName("uri")
    var id: String,
    @SerializedName("label")
    var name: String,
    var image: String,
    var source: String,
    var url: String,
    @SerializedName("ingredientLines")
    var ingredients: List<String>,
    @SerializedName("healthLabels")
    var types: List<String>,
    var totalTime: Float,
) {
    fun toRemoteRecipe(): RemoteRecipe {
        val recipeTypes: MutableList<RecipeType> = mutableListOf()
        types.forEach { type ->
            when (type) {
                "Vegan" -> recipeTypes.add(RecipeType.Vegan)
                "Vegetarian" -> recipeTypes.add(RecipeType.Vegetarian)
                "Gluten-Free" -> recipeTypes.add(RecipeType.GlutenFree)
            }
        }

        return RemoteRecipe(recipeId = id,
            name = name,
            image = image,
            source = source,
            url = url,
            type = recipeTypes,
            time = totalTime.roundToInt(),
            ingredients = ingredients)
    }
}
