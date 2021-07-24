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

import org.easyrecipe.model.*

interface LocalDataSource {
    suspend fun getAllRecipes(): List<LocalRecipe>
    suspend fun getAllIngredients(): List<Ingredient>
    suspend fun insertRecipe(
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        stepList: List<String>,
        imageUri: String,
        uid: String,
        lastUpdate: Long,
    ): LocalRecipe

    suspend fun addIngredients(recipe: LocalRecipe, ingredients: Map<String, String>)

    suspend fun deleteRecipe(recipeId: Long)

    suspend fun updateRecipe(
        recipeId: Long,
        updateName: String,
        updateDescription: String,
        updateTime: Int,
        updateTypes: List<RecipeType>,
        updateStepList: List<String>,
        updateImageUri: String,
        uid: String,
        lastUpdate: Long,
    ): LocalRecipe

    suspend fun updateIngredients(localRecipe: LocalRecipe, ingredients: Map<String, String>)

    suspend fun getRecipeById(recipeId: Long): LocalRecipe

    suspend fun getAllRemoteFavorites(): List<String>

    suspend fun addFavoriteRemoteRecipe(recipeId: String)

    suspend fun removeFavoriteRemoteRecipe(recipeId: String)

    suspend fun addFavoriteLocalRecipe(recipeId: Long)

    suspend fun removeFavoriteLocalRecipe(recipeId: Long)

    suspend fun getFavoriteRecipes(): List<Recipe>

    suspend fun getOrCreateUser(uid: String): User

    suspend fun addRemoteDatabaseRecipesToUser(uid: String, lastUpdate: Long, recipes: List<Recipe>)

    suspend fun getAllRecipesFromUser(uid: String): List<LocalRecipe>
}
