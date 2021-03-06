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

package org.easyrecipe.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.easyrecipe.data.dao.RecipeDao
import org.easyrecipe.data.dao.UserDao
import org.easyrecipe.data.entities.*

@Database(
    entities = [
        UserEntity::class,
        RecipeEntity::class,
        UserRecipe::class,
        IngredientEntity::class,
        RecipeIngredient::class,
        FavoriteRemoteRecipeEntity::class,
        UserFavoriteRemoteRecipe::class,
        UserRemoteRecipe::class,
    ],
    version = 7
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        const val NAME = "recipes"
    }
}
