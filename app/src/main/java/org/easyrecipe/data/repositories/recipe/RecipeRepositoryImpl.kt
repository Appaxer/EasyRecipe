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
        uid: String,
    ): LocalRecipe {
        val lastUpdate = System.currentTimeMillis()
        val localRecipe = localDataSource.insertRecipe(
            name,
            description,
            time,
            types,
            stepList,
            imageUri,
            uid,
            lastUpdate
        )

        localDataSource.addIngredients(localRecipe, ingredients)
        remoteDataSource.insertRecipe(uid, localRecipe, lastUpdate)

        return localRecipe
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
        uid: String,
    ) {
        val lastUpdate = System.currentTimeMillis()
        val originalRecipe = localDataSource.getRecipeById(id)
        val localRecipe = localDataSource.updateRecipe(
            id,
            name,
            description,
            time,
            types,
            stepList,
            imageUri,
            uid,
            lastUpdate
        )

        localDataSource.updateIngredients(localRecipe, ingredients)

        originalRecipe?.let { recipe ->
            remoteDataSource.updateRecipe(uid, recipe.name, localRecipe, lastUpdate)
        }
    }

    override suspend fun getRecipeById(recipeId: Long): LocalRecipe =
        localDataSource.getRecipeById(recipeId)

    override suspend fun favoriteRemoteRecipe(recipeId: String, isFavorite: Boolean) {
        when (isFavorite) {
            true -> localDataSource.removeFavoriteRemoteRecipe(recipeId)
            false -> localDataSource.addFavoriteRemoteRecipe(recipeId)
        }
    }

    override suspend fun favoriteLocalRecipe(localRecipe: LocalRecipe, uid: String) {
        when (localRecipe.favorite) {
            true -> {
                localDataSource.removeFavoriteLocalRecipe(localRecipe.recipeId, uid)
                remoteDataSource.removeFavoriteLocalRecipe(localRecipe.name, uid)
            }
            false -> {
                localDataSource.addFavoriteLocalRecipe(localRecipe.recipeId, uid)
                remoteDataSource.addFavoriteLocalRecipe(localRecipe.name, uid)
            }
        }
    }

    override suspend fun getFavoriteRecipes(): List<Recipe> {
        return localDataSource.getFavoriteRecipes()
            .union(remoteDataSource.getFavoriteRecipes(localDataSource.getAllRemoteFavorites()))
            .toList()
    }

    override suspend fun getAllRecipesFromUser(user: User): List<Recipe> {
        val localRecipes = localDataSource.getAllRecipesFromUser(user.uid)
        val remoteUser = remoteDataSource.getUser(user.uid)

        return when {
            user.lastUpdate > remoteUser.lastUpdate -> {
                syncRemoteRecipesWithLocal(user, localRecipes)
            }
            user.lastUpdate < remoteUser.lastUpdate -> {
                syncLocalRecipesWithRemote(user, remoteUser)
            }
            else -> localRecipes
        }
    }

    private suspend fun syncRemoteRecipesWithLocal(
        user: User,
        localRecipes: List<LocalRecipe>,
    ): List<LocalRecipe> {
        remoteDataSource.addLocalRecipesToRemoteDataBaseUser(
            user.uid,
            user.lastUpdate,
            localRecipes
        )
        return localRecipes
    }

    private suspend fun syncLocalRecipesWithRemote(
        user: User,
        remoteUser: User,
    ): List<Recipe> {
        localDataSource.addRemoteDatabaseRecipesToUser(
            user.uid,
            remoteUser.lastUpdate,
            remoteUser.recipes
        )
        return remoteUser.recipes
    }
}
