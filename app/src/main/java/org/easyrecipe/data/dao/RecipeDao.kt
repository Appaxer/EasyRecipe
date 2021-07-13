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
import org.easyrecipe.data.entities.IngredientEntity
import org.easyrecipe.data.entities.RecipeEntity
import org.easyrecipe.data.entities.RecipeIngredient

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRecipe(recipeEntity: RecipeEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIngredient(ingredientEntity: IngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient)

    @Update
    suspend fun updateRecipe(recipeEntity: RecipeEntity)

    @Query("delete from recipes where recipe_id = :recipeId")
    suspend fun deleteRecipe(recipeId: Long)

    @Query("delete from recipe_ingredient where recipe_id = :recipeId")
    suspend fun deleteRecipeIngredients(recipeId: Long)

    @Query("select * from recipes where recipe_id = :recipeId")
    suspend fun getRecipe(recipeId: Long): RecipeEntity

    @Query("select * from recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    @Query("select * from ingredients")
    suspend fun getAllIngredients(): List<IngredientEntity>

    @Query("select * from ingredients where name = :name")
    fun getIngredient(name: String): IngredientEntity?

    @Query("select * from recipe_ingredient where recipe_id = :recipeId")
    fun getAllIngredientsForRecipe(recipeId: Long): List<RecipeIngredient>
}
