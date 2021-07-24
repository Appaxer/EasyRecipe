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

import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.MealType
import org.easyrecipe.model.Recipe
import org.easyrecipe.model.User

interface RemoteDataSource {
    suspend fun getRecipes(name: String, mealType: List<MealType>): List<Recipe>

    suspend fun getFavoriteRecipes(recipeIds: List<String>): List<Recipe>

    suspend fun createUserIfNotExisting(uid: String)

    suspend fun getUser(uid: String): User

    suspend fun addLocalRecipesToRemoteDataBaseUser(
        uid: String,
        lastUpdate: Long,
        localRecipes: List<LocalRecipe>,
    )

    suspend fun insertRecipe(uid: String, localRecipe: LocalRecipe, lastUpdate: Long)

    suspend fun updateRecipe(
        uid: String,
        originalName: String,
        localRecipe: LocalRecipe,
        lastUpdate: Long,
    )
}
