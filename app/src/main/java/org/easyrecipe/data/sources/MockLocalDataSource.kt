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

class MockLocalDataSource : LocalDataSource {
    private val recipesData = mutableListOf(
        LocalRecipe(recipeId = 1,
            name = "Fried Chicken",
            type = listOf(RecipeType.Hot, RecipeType.Meat),
            description = "Yummmmmmm",
            time = 30,
            image = ""
        ),
        LocalRecipe(recipeId = 2,
            name = "Fried Chicken",
            type = listOf(RecipeType.Vegan),
            description = "This is not fried chicken",
            time = 20,
            image = ""
        ),
        LocalRecipe(recipeId = 3,
            name = "Chicken Fried",
            type = listOf(RecipeType.Meat),
            description = "It's delicious!",
            time = 20,
            image = ""
        ),
        LocalRecipe(recipeId = 4,
            name = "Fried Chicken",
            type = listOf(RecipeType.Meat, RecipeType.Hot, RecipeType.GlutenFree),
            description = "Yet another fried chicken recipe",
            time = 40,
            image = ""
        ),
        LocalRecipe(recipeId = 5,
            name = "Spicy Fried Chicken",
            type = listOf(RecipeType.Hot, RecipeType.Meat, RecipeType.Spicy),
            description = "Don't eat this for your own good.",
            time = 45,
            image = ""
        )
    )

    private val ingredientsData = mutableListOf(
        Ingredient("Chicken")
    )

    private val favoriteRemoteRecipes = mutableListOf(
        "recipe1"
    )

    override suspend fun getAllRecipes(): List<LocalRecipe> {
        return recipesData
    }

    override suspend fun getAllIngredients(): List<Ingredient> {
        return ingredientsData
    }

    override suspend fun insertRecipe(
        name: String,
        description: String,
        time: Int,
        types: List<RecipeType>,
        stepList: List<String>,
        imageUri: String,
        uid: String,
        lastUpdate: Long,
    ): LocalRecipe {
        val recipe = LocalRecipe(
            recipeId = recipesData.size.inc().toLong(),
            name = name,
            type = types,
            description = description,
            time = time,
            image = imageUri
        ).also {
            it.setSteps(stepList)
        }

        recipesData.add(recipe)
        return recipe
    }

    override suspend fun addIngredients(
        recipe: LocalRecipe,
        ingredients: Map<String, String>,
    ) {
        ingredients.forEach { (name, quantity) ->
            var ingredient = ingredientsData.find { it.name == name }
            if (ingredient == null) {
                ingredient = Ingredient(name)
                ingredientsData.add(ingredient)
            }

            recipe.addIngredient(ingredient, quantity)
        }
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        recipesData.removeIf { it.recipeId == recipeId }
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
        lastUpdate: Long,
    ): LocalRecipe {
        val recipe = recipesData.find { it.recipeId == recipeId }?.apply {
            name = updateName
            description = updateDescription
            time = updateTime
            type = updateTypes
            imageLocation = updateImageUri
        } ?: throw Exception("Local recipe not found")

        return recipe.also {
            it.setSteps(updateStepList)
        }
    }

    override suspend fun updateIngredients(
        localRecipe: LocalRecipe,
        ingredients: Map<String, String>,
    ) {
        localRecipe.removeAllIngredients()
        ingredients.forEach { (ingredientName, quantity) ->
            val ingredient = ingredientsData.find { it.name == ingredientName }
                ?: addNewIngredient(ingredientName)
            localRecipe.addIngredient(ingredient, quantity)
        }
    }

    override suspend fun getRecipeById(recipeId: Long): LocalRecipe =
        recipesData.find { it.recipeId == recipeId } ?: throw Exception("Recipe not found")

    private fun addNewIngredient(ingredientName: String): Ingredient {
        val ingredient = Ingredient(ingredientName)
        ingredientsData.add(ingredient)

        return ingredient
    }

    override suspend fun getAllRemoteFavorites(): List<String> {
        return favoriteRemoteRecipes
    }

    override suspend fun addFavoriteRemoteRecipe(recipeId: String) {
        favoriteRemoteRecipes.add(recipeId)
    }

    override suspend fun removeFavoriteRemoteRecipe(recipeId: String) {
        favoriteRemoteRecipes.remove(recipeId)
    }

    override suspend fun addFavoriteLocalRecipe(recipeId: Long) {
        recipesData.find { it.recipeId == recipeId }?.toggleFavorite()
    }

    override suspend fun removeFavoriteLocalRecipe(recipeId: Long) {
        recipesData.find { it.recipeId == recipeId }?.toggleFavorite()
    }

    override suspend fun getFavoriteRecipes(): List<Recipe> {
        return recipesData
    }

    override suspend fun getOrCreateUser(uid: String) = User(uid, System.currentTimeMillis())

    @Suppress("UNCHECKED_CAST")
    override suspend fun addRemoteDatabaseRecipesToUser(
        uid: String,
        lastUpdate: Long,
        recipes: List<Recipe>,
    ) {
        (recipes as? List<LocalRecipe>)?.let { localRecipes ->
            recipesData.addAll(localRecipes)
        }
    }

    override suspend fun getAllRecipesFromUser(uid: String): List<LocalRecipe> {
        return recipesData
    }
}
