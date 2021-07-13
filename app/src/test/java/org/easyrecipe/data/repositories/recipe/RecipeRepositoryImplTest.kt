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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.data.sources.LocalDataSource
import org.easyrecipe.data.sources.RemoteDataSource
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipeRepositoryImplTest {
    private lateinit var recipeRepository: RecipeRepository

    private var msg = "There was an unexpected error"
    private var recipes = listOf(
        LocalRecipe(recipeId = 1,
            name = "Fried Chicken",
            type = listOf(RecipeType.Hot, RecipeType.Meat),
            description = "Yummmmmmm",
            time = 30,
            image = ""),
        LocalRecipe(recipeId = 2,
            name = "Fried Chicken",
            type = listOf(RecipeType.Vegan),
            description = "This is not fried chicken",
            time = 20,
            image = ""),
        LocalRecipe(recipeId = 3,
            name = "Chicken Fried",
            type = listOf(RecipeType.Meat),
            description = "It's delicious!",
            time = 20,
            image = ""),
        LocalRecipe(recipeId = 4,
            name = "Fried Chicken",
            type = listOf(RecipeType.Meat, RecipeType.Hot, RecipeType.GlutenFree),
            description = "Yet another fried chicken recipe",
            time = 40,
            image = ""),
        LocalRecipe(recipeId = 5,
            name = "Spicy Fried Chicken",
            type = listOf(RecipeType.Hot, RecipeType.Meat, RecipeType.Spicy),
            description = "Don't eat this for your own good.",
            time = 45,
            image = "")
    )

    private val recipeName = "Fish and chips"
    private val recipeDescription = "Delicious"
    private val recipeTime = 10
    private val recipeTypes = listOf(RecipeType.Hot, RecipeType.Fish)
    private val recipeIngredients = mutableMapOf("Fish" to "1", "Potato" to "2")
    private val recipeSteps = listOf("First", "Second")
    private val recipeImage = ""
    private val recipeId = 1L
    private val remoteRecipeId = "uri"

    private val localFavorites = recipes.take(3)
    private val remoteFavorites = recipes.takeLast(2)

    private val localRecipe = LocalRecipe(
        name = recipeName,
        description = recipeDescription,
        time = recipeTime,
        type = recipeTypes,
        image = recipeImage
    ).also {
        it.setSteps(recipeSteps)
    }

    private val ingredientEntityList = listOf(
        Ingredient("Fish"),
        Ingredient("Potato")
    )

    private val favoriteRemoteRecipes = listOf(
        "uri"
    )

    private val remoteRecipes = listOf(RemoteRecipe(
        recipeId = "uri",
        name = "Salted Pecan Caramel Corn",
        image = "image",
        source = "source",
        url = "url",
        type = listOf(RecipeType.Vegetarian, RecipeType.GlutenFree),
        ingredients = listOf("3 cups Pecan Halves, Roughly Chopped"),
        time = 75
    ))

    private val favoriteRemoteRecipeList = remoteRecipes.onEach { it.toggleFavorite() }

    private val mealType = mutableListOf<MealType>()

    @MockK
    private lateinit var localDataSource: LocalDataSource

    @MockK
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setUp() {
        localDataSource = mockk()
        remoteDataSource = mockk()
        recipeRepository = RecipeRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error then OtherError state is loaded`() = runBlockingTest {
        coEvery { localDataSource.getAllRecipes() } throws CommonException.OtherError(msg)
        recipeRepository.getAllLocalRecipes()
    }

    @Test
    fun `when the recipes are found in the database then the recipe list is returned`() =
        runBlockingTest {
            coEvery { localDataSource.getAllRecipes() } returns recipes

            val recipeList = recipeRepository.getAllLocalRecipes()
            assertThat(recipeList, `is`(recipes))
        }

    @Test(expected = Exception::class)
    fun `when getting all ingredients there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.getAllIngredients() } throws Exception(msg)
            recipeRepository.getAllIngredients()
        }

    @Test
    fun `when getting all ingredients there is no error then a list of ingredients is returned`() =
        runBlockingTest {
            coEvery { localDataSource.getAllIngredients() } returns ingredientEntityList

            val result = recipeRepository.getAllIngredients()
            assertThat(result.size, isEqualTo(ingredientEntityList.size))
        }

    @Test(expected = Exception::class)
    fun `when creating a recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.insertRecipe(any(), any(), any(), any(), any(), any())
            } throws Exception(msg)
            recipeRepository.createRecipe(
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage
            )
        }

    @Test
    fun `when creating a recipe there is no error then the recipe is created`() =
        runBlockingTest {
            coEvery {
                localDataSource.insertRecipe(any(), any(), any(), any(), any(), any())
            } returns localRecipe

            coEvery {
                localDataSource.addIngredients(any(), any())
            } returns Unit

            recipeRepository.createRecipe(
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage
            )

            coVerify {
                localDataSource.addIngredients(localRecipe, any())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection it should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { localDataSource.getAllRemoteFavorites() } returns favoriteRemoteRecipes
            coEvery {
                remoteDataSource.getRecipes(recipeName, mealType)
            } throws CommonException.NoInternetException
            recipeRepository.getRemoteRecipes(recipeName, mealType)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error it should throw OtherError`() = runBlockingTest {
        coEvery {
            localDataSource.getAllRemoteFavorites()
        } throws CommonException.OtherError(msg)
        coEvery {
            remoteDataSource.getRecipes(recipeName, mealType)
        } throws CommonException.OtherError(msg)
        recipeRepository.getRemoteRecipes(recipeName, mealType)
    }

    @Test
    fun `when there is no error then it should return a list of recipes`() = runBlockingTest {
        coEvery { localDataSource.getAllRemoteFavorites() } returns favoriteRemoteRecipes
        coEvery { remoteDataSource.getRecipes(recipeName, mealType) } returns remoteRecipes
        val result = recipeRepository.getRemoteRecipes(recipeName, mealType)

        assertThat(result, isEqualTo(favoriteRemoteRecipeList))
    }

    @Test(expected = Exception::class)
    fun `when deleting the recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.deleteRecipe(any()) } throws Exception()
            recipeRepository.deleteRecipe(recipeId)
        }

    @Test
    fun `when deleting the recipe there is no error then the delete recipe method is called`() =
        runBlockingTest {
            coEvery { localDataSource.deleteRecipe(any()) } returns Unit
            recipeRepository.deleteRecipe(recipeId)

            coVerify {
                localDataSource.deleteRecipe(any())
            }
        }

    @Test(expected = Exception::class)
    fun `when updating the recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.updateRecipe(any(), any(), any(), any(), any(), any(), any())
            } throws Exception()

            recipeRepository.updateRecipe(
                recipeId,
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage
            )
        }

    @Test
    fun `when updating the recipe there is no error then the recipe is updated`() =
        runBlockingTest {
            coEvery {
                localDataSource.updateRecipe(any(), any(), any(), any(), any(), any(), any())
            } returns localRecipe

            coEvery {
                localDataSource.updateIngredients(localRecipe, any())
            } returns Unit

            recipeRepository.updateRecipe(
                recipeId,
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage
            )

            coVerify {
                localDataSource.updateRecipe(any(), any(), any(), any(), any(), any(), any())
                localDataSource.updateIngredients(localRecipe, any())
            }
        }

    @Test(expected = Exception::class)
    fun `when getting a recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getRecipeById(any())
            } throws Exception()

            recipeRepository.getRecipeById(recipeId)
        }

    @Test
    fun `when getting a recipe there is no error then the recipe is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getRecipeById(any())
            } returns localRecipe

            val result = recipeRepository.getRecipeById(recipeId)
            assertThat(result.recipeId, isEqualTo(localRecipe.recipeId))
        }

    @Test(expected = Exception::class)
    fun `when adding a favorite recipe and there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteRemoteRecipe(any()) } throws Exception()
            coEvery { localDataSource.removeFavoriteRemoteRecipe(any()) } throws Exception()
            recipeRepository.favoriteRemoteRecipe(remoteRecipeId, true)
        }

    @Test(expected = Exception::class)
    fun `when removing a favorite recipe and there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteRemoteRecipe(any()) } throws Exception()
            coEvery { localDataSource.removeFavoriteRemoteRecipe(any()) } throws Exception()
            recipeRepository.favoriteRemoteRecipe(remoteRecipeId, false)
        }

    @Test
    fun `when adding favorite remote recipe there is no error then addFavoriteRecipe is called`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteRemoteRecipe(any()) } returns Unit
            coEvery { localDataSource.removeFavoriteRemoteRecipe(any()) } returns Unit
            recipeRepository.favoriteRemoteRecipe(remoteRecipeId, false)

            coVerify {
                localDataSource.addFavoriteRemoteRecipe(remoteRecipeId)
            }
        }

    @Test
    fun `when removing favorite recipe there is no error then removeFavoriteRecipe is called`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteRemoteRecipe(any()) } returns Unit
            coEvery { localDataSource.removeFavoriteRemoteRecipe(any()) } returns Unit
            recipeRepository.favoriteRemoteRecipe(remoteRecipeId, true)

            coVerify {
                localDataSource.removeFavoriteRemoteRecipe(remoteRecipeId)
            }
        }

    @Test(expected = Exception::class)
    fun `when favorite local recipe there is an unexpected error then exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteLocalRecipe(any()) } throws Exception()
            coEvery { localDataSource.removeFavoriteLocalRecipe(any()) } throws Exception()
            recipeRepository.favoriteLocalRecipe(recipeId, false)
        }

    @Test
    fun `when adding favorite local recipe then addFavoriteLocalRecipe is called`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteLocalRecipe(any()) } returns Unit
            coEvery { localDataSource.removeFavoriteLocalRecipe(any()) } returns Unit
            recipeRepository.favoriteLocalRecipe(recipeId, false)

            coVerify {
                localDataSource.addFavoriteLocalRecipe(recipeId)
            }

            coVerify(exactly = 0) {
                localDataSource.removeFavoriteLocalRecipe(any())
            }
        }

    @Test
    fun `when removing favorite local recipe then removeFavoriteLocalRecipe is called`() =
        runBlockingTest {
            coEvery { localDataSource.addFavoriteLocalRecipe(any()) } returns Unit
            coEvery { localDataSource.removeFavoriteLocalRecipe(any()) } returns Unit
            recipeRepository.favoriteLocalRecipe(recipeId, true)

            coVerify {
                localDataSource.removeFavoriteLocalRecipe(any())
            }

            coVerify(exactly = 0) {
                localDataSource.addFavoriteLocalRecipe(recipeId)
            }
        }

    @Test(expected = Exception::class)
    fun `when getting local favorites recipes there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.getFavoriteRecipes() } throws Exception()
            coEvery { remoteDataSource.getFavoriteRecipes(any()) } returns emptyList()

            recipeRepository.getFavoriteRecipes()
        }

    @Test(expected = Exception::class)
    fun `when getting remote favorites recipes there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { localDataSource.getFavoriteRecipes() } returns emptyList()
            coEvery { remoteDataSource.getFavoriteRecipes(any()) } throws Exception()

            recipeRepository.getFavoriteRecipes()
        }

    @Test
    fun `when there are no error then the list of favorites recipes is returned`() =
        runBlockingTest {
            coEvery { localDataSource.getFavoriteRecipes() } returns localFavorites
            coEvery { localDataSource.getAllRemoteFavorites() } returns favoriteRemoteRecipes
            coEvery { remoteDataSource.getFavoriteRecipes(any()) } returns remoteFavorites

            val recipes = recipeRepository.getFavoriteRecipes()
            assertThat(recipes, isEqualTo(localFavorites.union(remoteFavorites).toList()))
        }
}
