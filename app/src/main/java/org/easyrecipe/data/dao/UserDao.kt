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

import androidx.room.*
import org.easyrecipe.data.entities.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(userEntity: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRecipe(userRecipe: UserRecipe)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteRemoteRecipe(favoriteRemoteRecipeEntity: FavoriteRemoteRecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRemoteRecipe(userRemoteRecipe: UserRemoteRecipe)

    @Query("select * from favorite_remote_recipe")
    suspend fun getAllFavoriteRemoteRecipes(): List<FavoriteRemoteRecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserFavoriteRemoteRecipe(favoriteRemoteRecipe: UserFavoriteRemoteRecipe)

    @Delete
    suspend fun deleteFavoriteRemoteRecipe(favoriteRemoteRecipeEntity: FavoriteRemoteRecipeEntity)

    @Delete
    suspend fun deleteUserRemoteRecipe(userRemoteRecipe: UserRemoteRecipe)

    @Update
    suspend fun updateUser(userEntity: UserEntity)

    @Query("update recipes set is_favorite = :isFavorite where recipe_id = :recipeId")
    suspend fun updateFavoriteLocalRecipe(recipeId: Long, isFavorite: Int)

    @Query("update user_recipes set is_favourite = :isFavorite where user_id = :userId and recipe_id = :recipeId")
    suspend fun updateUserFavoriteLocalRecipe(userId: Long, recipeId: Long, isFavorite: Int)

    @Query("select * from recipes where is_favorite = 1")
    suspend fun getFavoriteLocalRecipes(): List<RecipeEntity>

    @Query("select * from users where uid = :uid")
    suspend fun getUserByUid(uid: String): UserEntity?

    @Query("select count(*) from users")
    suspend fun getUserAmount(): Int

    @Query("select remote_recipe_id from user_remote_recipes where user_id = :userId")
    suspend fun getUserFavoriteRemoteRecipes(userId: Long): List<String>
}
