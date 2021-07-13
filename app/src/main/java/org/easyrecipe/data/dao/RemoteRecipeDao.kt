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

import androidx.room.Dao
import org.easyrecipe.model.MealType
import org.easyrecipe.model.RemoteRecipe

@Dao
interface RemoteRecipeDao {
    suspend fun getRecipes(name: String): List<RemoteRecipe>
    suspend fun getRecipesByMealType(name: String, mealTypes: List<MealType>): List<RemoteRecipe>
    suspend fun getFavoriteRecipes(recipeIds: List<String>): List<RemoteRecipe>
}
