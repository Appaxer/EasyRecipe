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

import org.easyrecipe.data.dao.RemoteDataBaseDao
import org.easyrecipe.data.dao.RemoteRecipeDao
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.MealType
import org.easyrecipe.model.Recipe
import org.easyrecipe.model.User
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val remoteRecipeDao: RemoteRecipeDao,
    private val remoteDataBaseDao: RemoteDataBaseDao,
) : RemoteDataSource {

    override suspend fun getRecipes(name: String, mealType: List<MealType>): List<Recipe> {
        return if (mealType.isNullOrEmpty()) {
            remoteRecipeDao.getRecipes(name)
        } else {
            remoteRecipeDao.getRecipesByMealType(name, mealType)
        }
    }

    override suspend fun getFavoriteRecipes(recipeIds: List<String>): List<Recipe> {
        return remoteRecipeDao.getFavoriteRecipes(recipeIds)
    }

    override suspend fun createUserIfNotExisting(uid: String) {
        if (!remoteDataBaseDao.isUserExisting(uid)) {
            remoteDataBaseDao.createUser(uid)
        }
    }

    override suspend fun getUser(uid: String): User {
        return remoteDataBaseDao.getUser(uid)
    }

    override suspend fun addLocalRecipesToRemoteDataBaseUser(
        uid: String,
        lastUpdate: Long,
        localRecipes: List<LocalRecipe>,
    ) {
        remoteDataBaseDao.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, localRecipes)
    }

    override suspend fun insertRecipe(uid: String, localRecipe: LocalRecipe, lastUpdate: Long) {
        remoteDataBaseDao.insertRecipe(uid, localRecipe, lastUpdate)
    }

    override suspend fun updateRecipe(
        uid: String,
        originalName: String,
        localRecipe: LocalRecipe,
        lastUpdate: Long,
    ) {
        remoteDataBaseDao.updateRecipe(uid, originalName, localRecipe, lastUpdate)
    }
}
