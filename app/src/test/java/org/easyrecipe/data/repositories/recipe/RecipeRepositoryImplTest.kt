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
import org.easyrecipe.common.extensions.unionList
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
    private lateinit var recipeRepositoryImpl: RecipeRepositoryImpl

    private val uid = "1"
    private val lastUpdate = 0L
    private val newLastUpdate = 1L
    private val localUser = User(uid, lastUpdate)
    private val remoteUser = User(uid, lastUpdate)

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

    private lateinit var localRecipe: LocalRecipe

    private val ingredientEntityList = listOf(
        Ingredient("Fish"),
        Ingredient("Potato")
    )

    private val favoriteRemoteRecipes = listOf(
        "uri"
    )

    private lateinit var remoteRecipe: RemoteRecipe
    private lateinit var remoteRecipes: List<RemoteRecipe>
    private lateinit var favoriteRemoteRecipeList: List<RemoteRecipe>

    private val mealType = mutableListOf<MealType>()

    @MockK
    private lateinit var localDataSource: LocalDataSource

    @MockK
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setUp() {
        localDataSource = mockk()
        remoteDataSource = mockk()
        recipeRepositoryImpl = RecipeRepositoryImpl(localDataSource, remoteDataSource)

        localRecipe = LocalRecipe(
            name = recipeName,
            description = recipeDescription,
            time = recipeTime,
            type = recipeTypes,
            image = recipeImage
        ).also { recipe ->
            recipe.setSteps(recipeSteps)
        }

        remoteRecipe = RemoteRecipe(
            recipeId = "uri",
            name = "Salted Pecan Caramel Corn",
            image = "image",
            source = "source",
            url = "url",
            type = listOf(RecipeType.Vegetarian, RecipeType.GlutenFree),
            ingredients = listOf("3 cups Pecan Halves, Roughly Chopped"),
            time = 75
        )

        remoteRecipes = listOf(remoteRecipe)
        favoriteRemoteRecipeList = remoteRecipes.onEach { it.toggleFavorite() }
    }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error then OtherError state is loaded`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipes()
            } throws CommonException.OtherError(msg)

            recipeRepositoryImpl.getAllLocalRecipes()
        }

    @Test
    fun `when the recipes are found in the database then the recipe list is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipes()
            } returns recipes

            val recipeList = recipeRepositoryImpl.getAllLocalRecipes()
            assertThat(recipeList, `is`(recipes))
        }

    @Test(expected = Exception::class)
    fun `when getting all ingredients there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllIngredients()
            } throws Exception(msg)

            recipeRepositoryImpl.getAllIngredients()
        }

    @Test
    fun `when getting all ingredients there is no error then a list of ingredients is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllIngredients()
            } returns ingredientEntityList

            val result = recipeRepositoryImpl.getAllIngredients()
            assertThat(result.size, isEqualTo(ingredientEntityList.size))
        }

    @Test(expected = Exception::class)
    fun `when creating a recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.insertRecipe(any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any())
            } throws Exception(msg)

            recipeRepositoryImpl.createRecipe(
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage,
                uid
            )
        }

    @Test
    fun `when creating a recipe there is no error then the recipe is created`() =
        runBlockingTest {
            coEvery {
                localDataSource.insertRecipe(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns localRecipe

            coEvery {
                remoteDataSource.insertRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                localDataSource.addIngredients(any(), any())
            } returns Unit

            recipeRepositoryImpl.createRecipe(
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage,
                uid
            )

            coVerify {
                localDataSource.addIngredients(localRecipe, any())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection it should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRemoteFavorites()
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getRecipes(recipeName, mealType)
            } throws CommonException.NoInternetException

            recipeRepositoryImpl.getRemoteRecipes(recipeName, mealType)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error it should throw OtherError`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRemoteFavorites()
            } throws CommonException.OtherError(msg)

            coEvery {
                remoteDataSource.getRecipes(recipeName, mealType)
            } throws CommonException.OtherError(msg)

            recipeRepositoryImpl.getRemoteRecipes(recipeName, mealType)
        }

    @Test
    fun `when there is no error then it should return a list of recipes`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRemoteFavorites()
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getRecipes(recipeName, mealType)
            } returns remoteRecipes

            val result = recipeRepositoryImpl.getRemoteRecipes(recipeName, mealType)

            assertThat(result, isEqualTo(favoriteRemoteRecipeList))
        }

    @Test(expected = Exception::class)
    fun `when deleting the recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.deleteRecipe(any())
            } throws Exception()

            recipeRepositoryImpl.deleteRecipe(recipeId)
        }

    @Test
    fun `when deleting the recipe there is no error then the delete recipe method is called`() =
        runBlockingTest {
            coEvery {
                localDataSource.deleteRecipe(any())
            } returns Unit

            recipeRepositoryImpl.deleteRecipe(recipeId)

            coVerify {
                localDataSource.deleteRecipe(any())
            }
        }

    @Test(expected = Exception::class)
    fun `when updating the recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.updateRecipe(any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any())
            } throws Exception()

            recipeRepositoryImpl.updateRecipe(
                recipeId,
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage,
                uid
            )
        }

    @Test
    fun `when updating the recipe there is no error then the recipe is updated`() =
        runBlockingTest {
            coEvery {
                localDataSource.updateRecipe(any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any())
            } returns localRecipe

            coEvery {
                localDataSource.updateIngredients(localRecipe, any())
            } returns Unit

            coEvery {
                localDataSource.getRecipeById(any())
            } returns localRecipe

            coEvery {
                remoteDataSource.updateRecipe(any(), any(), any(), any())
            } returns Unit

            recipeRepositoryImpl.updateRecipe(
                recipeId,
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeIngredients,
                recipeSteps,
                recipeImage,
                uid
            )

            coVerify {
                localDataSource.updateRecipe(any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any())
                localDataSource.updateIngredients(localRecipe, any())
            }
        }

    @Test(expected = Exception::class)
    fun `when getting a recipe there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getRecipeById(any())
            } throws Exception()

            recipeRepositoryImpl.getRecipeById(recipeId)
        }

    @Test
    fun `when getting a recipe there is no error then the recipe is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getRecipeById(any())
            } returns localRecipe

            val result = recipeRepositoryImpl.getRecipeById(recipeId)
            assertThat(result.recipeId, isEqualTo(localRecipe.recipeId))
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding remote recipe in local there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding remote recipe in remote there is a network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } throws CommonException.NoInternetException

            remoteRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding remote recipe in remote there is an other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)
        }

    @Test
    fun `when adding favorite remote recipe there is no error then addFavoriteRecipe is called`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            remoteRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)

            coVerify {
                localDataSource.addFavoriteRemoteRecipe(remoteRecipeId, uid, any())
                remoteDataSource.addFavoriteRemoteRecipe(remoteRecipeId, uid, any())
            }
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing remote recipe in local there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when removing remote recipe in remote there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } throws CommonException.NoInternetException

            remoteRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing remote recipe in remote there is an other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)
        }

    @Test
    fun `when removing remote recipe there is no error then removeFavoriteRecipe is called`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.removeFavoriteRemoteRecipe(any(), any(), any())
            } returns Unit

            remoteRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteRemoteRecipe(remoteRecipe, uid)

            coVerify {
                localDataSource.removeFavoriteRemoteRecipe(remoteRecipeId, uid, any())
                remoteDataSource.removeFavoriteRemoteRecipe(remoteRecipeId, uid, any())
            }
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when favorite local recipe there is a local error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            localRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when favorite local recipe there is a remote network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } throws CommonException.NoInternetException

            localRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when favorite local recipe there is a remote other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            localRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)
        }

    @Test
    fun `when adding favorite local recipe then addFavoriteLocalRecipe is called`() =
        runBlockingTest {
            coEvery {
                localDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            localRecipe.setFavorite(false)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)

            coVerify {
                localDataSource.addFavoriteLocalRecipe(any(), uid, any())
                remoteDataSource.addFavoriteLocalRecipe(any(), uid, any())
            }

            coVerify(exactly = 0) {
                localDataSource.removeFavoriteLocalRecipe(any(), uid, any())
                remoteDataSource.removeFavoriteLocalRecipe(any(), uid, any())
            }
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite local recipe in local there is error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            localRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when removing favorite recipe in remote network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } throws CommonException.NoInternetException

            localRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe in remote other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            localRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)
        }

    @Test
    fun `when removing favorite local recipe then removeFavoriteLocalRecipe is called`() =
        runBlockingTest {
            coEvery {
                localDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.removeFavoriteLocalRecipe(any(), any(), any())
            } returns Unit

            localRecipe.setFavorite(true)
            recipeRepositoryImpl.favoriteLocalRecipe(localRecipe, uid)

            coVerify {
                localDataSource.removeFavoriteLocalRecipe(any(), uid, any())
                remoteDataSource.removeFavoriteLocalRecipe(recipeName, uid, any())
            }

            coVerify(exactly = 0) {
                localDataSource.addFavoriteLocalRecipe(recipeId, uid, any())
                remoteDataSource.addFavoriteLocalRecipe(recipeName, uid, any())
            }
        }

    @Test(expected = Exception::class)
    fun `when getting local favorites recipes there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getFavoriteRecipes()
            } throws Exception()

            recipeRepositoryImpl.getFavoriteRecipes()
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting remote favorites recipes list there is error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getFavoriteRecipes()
            } returns emptyList()

            coEvery {
                localDataSource.getAllRemoteFavorites()
            } throws CommonException.OtherError("Other error")

            recipeRepositoryImpl.getFavoriteRecipes()
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when getting remote favorites recipes there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getFavoriteRecipes()
            } returns emptyList()

            coEvery {
                localDataSource.getAllRemoteFavorites()
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } throws CommonException.NoInternetException

            recipeRepositoryImpl.getFavoriteRecipes()
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting remote favorites recipes there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getFavoriteRecipes()
            } returns emptyList()

            coEvery {
                localDataSource.getAllRemoteFavorites()
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } throws CommonException.OtherError("Other error")

            recipeRepositoryImpl.getFavoriteRecipes()
        }

    @Test
    fun `when there are no error then the list of favorites recipes is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getFavoriteRecipes()
            } returns localFavorites

            coEvery {
                localDataSource.getAllRemoteFavorites()
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns remoteFavorites

            val recipes = recipeRepositoryImpl.getFavoriteRecipes()
            assertThat(recipes, isEqualTo(localFavorites.union(remoteFavorites).toList()))
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting all recipes from user in local there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } throws CommonException.OtherError("Other error")

            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting user remote recipes in local there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } throws CommonException.OtherError("Other error")

            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when getting favorite remote recipes there is a network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } throws CommonException.NoInternetException

            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting favorite remote recipes there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } throws CommonException.OtherError("Other error")

            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when getting user from remote source there is a network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } throws CommonException.NoInternetException

            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting user from remote source there is an other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } throws CommonException.OtherError("Other error")

            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when local is updated and there is a network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } throws CommonException.NoInternetException

            localUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when local is updated and there is an internet error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } throws CommonException.NoInternetException

            localUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when local is updated and there is an other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            localUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when local is updated and there is a favorite internet error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteRemoteRecipesToRemoteDatabaseUser(any(), any())
            } throws CommonException.NoInternetException

            localUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when local is updated and there is a favorite other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteRemoteRecipesToRemoteDatabaseUser(any(), any())
            } throws CommonException.OtherError("Other error")

            localUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test
    fun `when local is updated and there is not any error then remote recipes are updated`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } returns Unit

            coEvery {
                remoteDataSource.addFavoriteRemoteRecipesToRemoteDatabaseUser(any(), any())
            } returns Unit

            localUser.lastUpdate = newLastUpdate

            val result = recipeRepositoryImpl.getAllRecipesFromUser(localUser)
            assertThat(result, isEqualTo(recipes.unionList(favoriteRemoteRecipeList)))

            coVerify {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(uid, newLastUpdate, recipes)
                remoteDataSource.addFavoriteRemoteRecipesToRemoteDatabaseUser(
                    uid,
                    favoriteRemoteRecipes
                )
            }

            coVerify(exactly = 0) {
                localDataSource.addRemoteDatabaseRecipesToUser(uid, newLastUpdate, recipes)
                localDataSource.addFavoriteRemoteRecipesToUser(uid, favoriteRemoteRecipeList)
            }
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when remote is updated and there is an other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                localDataSource.addRemoteDatabaseRecipesToUser(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when remote is updated and there is a favorite error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns favoriteRemoteRecipeList

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                localDataSource.addRemoteDatabaseRecipesToUser(any(), any(), any())
            } returns Unit

            coEvery {
                localDataSource.addFavoriteRemoteRecipesToUser(any(), any())
            } throws CommonException.OtherError("Other error")

            remoteUser.lastUpdate = newLastUpdate
            recipeRepositoryImpl.getAllRecipesFromUser(localUser)
        }

    @Test
    fun `when remote is updated and there is no error then local recipes are updated`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns remoteRecipes

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                localDataSource.addRemoteDatabaseRecipesToUser(any(), any(), any())
            } returns Unit

            coEvery {
                localDataSource.addFavoriteRemoteRecipesToUser(any(), any())
            } returns Unit

            remoteUser.lastUpdate = newLastUpdate
            remoteUser.addRecipes(recipes)
            remoteUser.addRecipes(favoriteRemoteRecipeList)

            val result = recipeRepositoryImpl.getAllRecipesFromUser(localUser)
            assertThat(result, isEqualTo(recipes.unionList(favoriteRemoteRecipeList)))

            coVerify {
                localDataSource.addRemoteDatabaseRecipesToUser(uid, any(), recipes)
                localDataSource.addFavoriteRemoteRecipesToUser(uid, any())
            }

            coVerify(exactly = 0) {
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(uid, newLastUpdate, recipes)
                remoteDataSource.addFavoriteRemoteRecipesToRemoteDatabaseUser(
                    uid,
                    favoriteRemoteRecipes
                )
            }
        }

    @Test
    fun `when local and remote are updated then list of recipes is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getAllRecipesFromUser(any())
            } returns recipes

            coEvery {
                localDataSource.getUserRemoteFavoriteRecipes(any())
            } returns favoriteRemoteRecipes

            coEvery {
                remoteDataSource.getFavoriteRecipes(any())
            } returns remoteRecipes

            coEvery {
                remoteDataSource.getUser(any())
            } returns remoteUser

            coEvery {
                localDataSource.addRemoteDatabaseRecipesToUser(any(), any(), any())
            } returns Unit

            val result = recipeRepositoryImpl.getAllRecipesFromUser(localUser)
            assertThat(result, isEqualTo(recipes.unionList(favoriteRemoteRecipeList)))

            coVerify(exactly = 0) {
                localDataSource.addRemoteDatabaseRecipesToUser(uid, newLastUpdate, recipes)
                localDataSource.addFavoriteRemoteRecipesToUser(uid, favoriteRemoteRecipeList)
                remoteDataSource.addLocalRecipesToRemoteDataBaseUser(uid, newLastUpdate, recipes)
                remoteDataSource.addFavoriteRemoteRecipesToRemoteDatabaseUser(
                    uid,
                    favoriteRemoteRecipes
                )
            }
        }
}
