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

import org.easyrecipe.common.extensions.hash
import org.easyrecipe.data.LocalDatabase
import org.easyrecipe.data.entities.*
import org.easyrecipe.model.*
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    database: LocalDatabase,
) : LocalDataSource {
    private val userDao = database.userDao()
    private val recipeDao = database.recipeDao()

    override suspend fun getAllRecipes(uid: String): List<LocalRecipe> {
        val recipeEntities = recipeDao.getAllRecipes()
        val ingredients = getAllIngredients()
        val ingredientsMap = ingredients.groupBy { it.name }.mapValues { it.value.first() }

        return recipeEntities.map { recipeEntity ->
            LocalRecipe.fromEntity(recipeEntity).also { recipe ->
                val recipeIngredients = recipeDao.getAllIngredientsForRecipe(recipeEntity.recipeId)
                recipeIngredients.forEach { recipeIngredient ->
                    ingredientsMap[recipeIngredient.ingredientName]?.let { currentIngredient ->
                        val quantity = recipeIngredient.quantity
                        recipe.addIngredient(currentIngredient, quantity)
                    }
                }
            }
        }
    }

    override suspend fun getAllIngredients(): List<Ingredient> {
        return recipeDao.getAllIngredients().map { Ingredient.fromEntity(it) }
    }

    override suspend fun insertRecipe(
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        stepList: List<String>,
        imageUri: String,
        uid: String,
    ): LocalRecipe {
        val user = getUser(uid)
        val recipeEntity = RecipeEntity(
            name = name,
            description = description,
            time = time,
            type = types,
            steps = stepList,
            image = imageUri
        )

        val id = recipeDao.insertRecipe(recipeEntity)
        user?.let { currentUser ->
            insertUserRecipe(currentUser, recipeEntity)
        }

        return LocalRecipe.fromEntity(recipeEntity).apply {
            recipeId = id
        }
    }

    private suspend fun insertUserRecipe(
        user: UserEntity,
        recipe: RecipeEntity,
    ) {
        val userRecipe = UserRecipe(user.userId, recipe.recipeId)
        userDao.insertUserRecipe(userRecipe)
    }

    private suspend fun getUser(uid: String): UserEntity? {
        return userDao.getUserByUid(uid)
    }

    override suspend fun addIngredients(
        recipe: LocalRecipe,
        ingredients: Map<String, String>,
    ) {
        ingredients.forEach { (name, quantity) ->
            val ingredientName = name.lowercase()
            var ingredientEntity = recipeDao.getIngredient(ingredientName)
            if (ingredientEntity == null) {
                ingredientEntity = IngredientEntity(ingredientName)
                recipeDao.insertIngredient(ingredientEntity)
            }

            val recipeIngredient = RecipeIngredient(recipe.recipeId, ingredientName, quantity)
            recipeDao.insertRecipeIngredient(recipeIngredient)
        }
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        recipeDao.deleteRecipeIngredients(recipeId)
        recipeDao.deleteRecipe(recipeId)
    }

    override suspend fun updateRecipe(
        recipeId: Long,
        updateName: String,
        updateDescription: String,
        updateTime: Int,
        updateTypes: List<RecipeType>,
        updateStepList: List<String>,
        updateImageUri: String,
        uid: String,
    ): LocalRecipe {
        val recipeEntity = recipeDao.getRecipe(recipeId).apply {
            name = updateName
            description = updateDescription
            time = updateTime
            type = updateTypes
            steps = updateStepList
            image = if (updateImageUri.isEmpty()) null else updateImageUri
        }

        recipeDao.updateRecipe(recipeEntity)
        return LocalRecipe.fromEntity(recipeEntity)
    }

    override suspend fun updateIngredients(
        localRecipe: LocalRecipe,
        ingredients: Map<String, String>,
    ) {
        recipeDao.deleteRecipeIngredients(localRecipe.recipeId)
        addIngredients(localRecipe, ingredients)
    }

    override suspend fun getRecipeById(recipeId: Long): LocalRecipe {
        val recipeEntity = recipeDao.getRecipe(recipeId)
        return LocalRecipe.fromEntity(recipeEntity)
    }

    override suspend fun getAllRemoteFavorites(): List<String> {
        return userDao.getAllFavoriteRemoteRecipes().map { it.remoteRecipeId }
    }

    override suspend fun addFavoriteRemoteRecipe(recipeId: String) {
        val favoriteRemoteRecipeEntity = FavoriteRemoteRecipeEntity(recipeId)
        userDao.insertFavoriteRemoteRecipe(favoriteRemoteRecipeEntity)
    }

    override suspend fun removeFavoriteRemoteRecipe(recipeId: String) {
        val favoriteRemoteRecipeEntity = FavoriteRemoteRecipeEntity(recipeId)
        userDao.deleteFavoriteRemoteRecipe(favoriteRemoteRecipeEntity)

    }

    override suspend fun addFavoriteLocalRecipe(recipeId: Long) {
        userDao.updateFavoriteLocalRecipe(recipeId, 1)
    }

    override suspend fun removeFavoriteLocalRecipe(recipeId: Long) {
        userDao.updateFavoriteLocalRecipe(recipeId, 0)
    }

    override suspend fun getFavoriteRecipes(): List<Recipe> {
        return userDao.getFavoriteLocalRecipes().map { recipe -> LocalRecipe.fromEntity(recipe) }
    }

    override suspend fun getOrCreateUser(uid: String): User {
        return uid.toUid()?.let { currentUid ->
            userDao.getUserByUid(currentUid)?.let { userEntity ->
                User.fromEntity(userEntity)
            }
        } ?: createUser(uid)
    }

    override suspend fun addRecipesToUser(uid: String, lastUpdate: Long, recipes: List<Recipe>) {
        val userEntity = userDao.getUserByUid(uid)
    }

    private suspend fun createUser(uid: String): User {
        val userEntity = UserEntity(
            userId = 0,
            uid = uid.toUid(),
            lastUpdate = System.currentTimeMillis()
        )

        val id = userDao.insertUser(userEntity)
        userEntity.userId = id

        val user = User.fromEntity(userEntity)

        if (userDao.getUserAmount() == 1) {
            recipeDao.getAllRecipes().forEach { recipe ->
                insertUserRecipe(userEntity, recipe)
            }
        }

        return user
    }

    private fun String.toUid() = hash(HASH_ALGORITHM)

    companion object {
        const val HASH_ALGORITHM = "SHA-256"
    }
}
