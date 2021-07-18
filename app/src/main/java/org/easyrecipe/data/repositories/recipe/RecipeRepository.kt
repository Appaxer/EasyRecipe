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

package org.easyrecipe.data.repositories.recipe

import org.easyrecipe.model.*

interface RecipeRepository {
    suspend fun getAllLocalRecipes(): List<Recipe>
    suspend fun getAllIngredients(): List<Ingredient>
    suspend fun getRemoteRecipes(name: String, mealType: List<MealType>): List<Recipe>

    suspend fun createRecipe(
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        ingredients: Map<String, String>,
        stepList: List<String>,
        imageUri: String,
        uid: String,
    )

    suspend fun deleteRecipe(recipeId: Long)

    suspend fun updateRecipe(
        id: Long,
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        ingredients: Map<String, String>,
        stepList: List<String>,
        imageUri: String,
        uid: String,
    )

    suspend fun getRecipeById(recipeId: Long): LocalRecipe

    suspend fun favoriteRemoteRecipe(recipeId: String, isFavorite: Boolean)

    suspend fun favoriteLocalRecipe(recipeId: Long, isFavorite: Boolean)

    suspend fun getFavoriteRecipes(): List<Recipe>
}
