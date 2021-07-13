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

import org.easyrecipe.data.sources.LocalDataSource
import org.easyrecipe.data.sources.RemoteDataSource
import org.easyrecipe.model.*
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : RecipeRepository {

    override suspend fun getAllLocalRecipes(): List<Recipe> {
        return localDataSource.getAllRecipes()
    }

    override suspend fun getAllIngredients(): List<Ingredient> {
        return localDataSource.getAllIngredients()
    }

    override suspend fun getRemoteRecipes(name: String, mealType: List<MealType>): List<Recipe> {
        val favoriteList = localDataSource.getAllRemoteFavorites()

        return remoteDataSource.getRecipes(name, mealType).onEach { recipe ->
            if (favoriteList.contains((recipe as RemoteRecipe).recipeId)) {
                recipe.toggleFavorite()
            }
        }
    }

    override suspend fun createRecipe(
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        ingredients: Map<String, String>,
        stepList: List<String>,
        imageUri: String,
    ) {
        val localRecipe =
            localDataSource.insertRecipe(name, description, time, types, stepList, imageUri)
        localDataSource.addIngredients(localRecipe, ingredients)
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        localDataSource.deleteRecipe(recipeId)
    }

    override suspend fun updateRecipe(
        id: Long,
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        ingredients: Map<String, String>,
        stepList: List<String>,
        imageUri: String,
    ) {
        val localRecipe =
            localDataSource.updateRecipe(id, name, description, time, types, stepList, imageUri)

        localDataSource.updateIngredients(localRecipe, ingredients)
    }

    override suspend fun getRecipeById(recipeId: Long): LocalRecipe =
        localDataSource.getRecipeById(recipeId)

    override suspend fun favoriteRemoteRecipe(recipeId: String, isFavorite: Boolean) {
        when (isFavorite) {
            true -> localDataSource.removeFavoriteRemoteRecipe(recipeId)
            false -> localDataSource.addFavoriteRemoteRecipe(recipeId)
        }
    }

    override suspend fun favoriteLocalRecipe(recipeId: Long, isFavorite: Boolean) {
        when (isFavorite) {
            true -> localDataSource.removeFavoriteLocalRecipe(recipeId)
            false -> localDataSource.addFavoriteLocalRecipe(recipeId)
        }
    }

    override suspend fun getFavoriteRecipes(): List<Recipe> {
        return localDataSource.getFavoriteRecipes()
            .union(remoteDataSource.getFavoriteRecipes(localDataSource.getAllRemoteFavorites()))
            .toList()
    }
}
