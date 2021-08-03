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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.data.dao.RemoteDataBaseDao
import org.easyrecipe.data.dao.RemoteRecipeDao
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteDataSourceImplTest {
    private lateinit var remoteDataSourceImpl: RemoteDataSourceImpl

    private val msg = "There was an unexpected error"
    private val name = "name"
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

    private val mealType = mutableListOf(MealType.Teatime)
    private val recipeIds = listOf("uri")

    private val uid = "1"
    private val lastUpdate = 0L
    private val user = User(uid, lastUpdate)

    private val recipeId = 1L
    private val recipeName = "Fish and chips"
    private val recipeDescription = "Delicious"
    private val recipeTime = 10
    private val recipeTypes = listOf(RecipeType.Hot, RecipeType.Fish)
    private val recipeIngredients = mutableMapOf("Fish" to "1", "Potato" to "2")
    private val recipeSteps = listOf("First", "Second")
    private val recipeImage = ""

    private val localRecipe = LocalRecipe(
        recipeId = recipeId,
        name = recipeName,
        description = recipeDescription,
        time = recipeTime,
        type = recipeTypes,
        image = ""
    ).also {
        it.setSteps(recipeSteps)
    }

    private val recipes = listOf(localRecipe)

    @MockK
    private lateinit var remoteRecipeDao: RemoteRecipeDao

    @MockK
    private lateinit var remoteDataBaseDao: RemoteDataBaseDao

    @Before
    fun setUp() {
        remoteRecipeDao = mockk()
        remoteDataBaseDao = mockk()
        remoteDataSourceImpl = RemoteDataSourceImpl(remoteRecipeDao, remoteDataBaseDao)
    }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection then getRecipes should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipes(any()) } throws CommonException.NoInternetException
            remoteDataSourceImpl.getRecipes(name, emptyList())
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error then getRecipes should throw OtherError`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipes(any()) } throws CommonException.OtherError(msg)
            remoteDataSourceImpl.getRecipes(name, emptyList())
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection then getFavoriteRecipes should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getFavoriteRecipes(any()) } throws CommonException.NoInternetException
            remoteDataSourceImpl.getFavoriteRecipes(emptyList())
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error then getFavoriteRecipes should throw OtherError`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getFavoriteRecipes(any()) } throws CommonException.OtherError(
                msg)
            remoteDataSourceImpl.getFavoriteRecipes(emptyList())
        }

    @Test
    fun `when there is no error and the meal type list is not empty then getRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipes(any()) } returns remoteRecipes
            val result = remoteDataSourceImpl.getRecipes(name, emptyList())

            coVerify(exactly = 0) { remoteRecipeDao.getRecipesByMealType(any(), any()) }
            assertThat(result, isEqualTo(remoteRecipes))
        }

    @Test
    fun `when there is no error and the meal type list is empty then getRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipesByMealType(any(), any()) } returns remoteRecipes
            val result = remoteDataSourceImpl.getRecipes(name, mealType)

            coVerify(exactly = 0) { remoteRecipeDao.getRecipes(any()) }
            assertThat(result, isEqualTo(remoteRecipes))
        }

    @Test
    fun `when there is no error the getFavoriteRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getFavoriteRecipes(any()) } returns remoteRecipes
            val result = remoteDataSourceImpl.getFavoriteRecipes(recipeIds)

            assertThat(result, isEqualTo(remoteRecipes))
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when creating user and check it exists there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.isUserExisting(any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.createUserIfNotExisting(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when creating user and check it exists there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.isUserExisting(any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.createUserIfNotExisting(uid)
        }

    @Test
    fun `when creating user and exists then it is not created`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.isUserExisting(any())
            } returns true

            remoteDataSourceImpl.createUserIfNotExisting(uid)

            coVerify(exactly = 0) {
                remoteDataBaseDao.createUser(any())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when creating user and not exists but there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.isUserExisting(any())
            } returns false

            coEvery {
                remoteDataBaseDao.createUser(any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.createUserIfNotExisting(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when creating user and do not exist but there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.isUserExisting(any())
            } returns false

            coEvery {
                remoteDataBaseDao.createUser(any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.createUserIfNotExisting(uid)
        }

    @Test
    fun `when creating user and do not exist then it is created`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.isUserExisting(any())
            } returns false

            coEvery {
                remoteDataBaseDao.createUser(any())
            } returns Unit

            remoteDataSourceImpl.createUserIfNotExisting(uid)

            coVerify {
                remoteDataBaseDao.createUser(any())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when getting user there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.getUser(any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.getUser(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting user there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.getUser(any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.getUser(uid)
        }

    @Test
    fun `when getting user there is no error then it is returned`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.getUser(any())
            } returns user

            val result = remoteDataSourceImpl.getUser(uid)
            assertThat(result, isEqualTo(user))
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding local recipes to remote there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, recipes)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding local recipes to remote there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, recipes)
        }

    @Test
    fun `when adding local recipes to remote there is no error then it is returned`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.addLocalRecipesToRemoteDataBaseUser(any(), any(), any())
            } returns Unit

            remoteDataSourceImpl.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, recipes)

            coVerify {
                remoteDataBaseDao.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, recipes)
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding recipe there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.insertRecipe(any(), any(), any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.insertRecipe(uid, localRecipe, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding recipe there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.insertRecipe(any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.insertRecipe(uid, localRecipe, lastUpdate)
        }

    @Test
    fun `when adding recipe there is no error then it is returned`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.insertRecipe(any(), any(), any())
            } returns Unit

            remoteDataSourceImpl.insertRecipe(uid, localRecipe, lastUpdate)

            coVerify {
                remoteDataBaseDao.insertRecipe(uid, localRecipe, lastUpdate)
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when updating recipe there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.updateRecipe(any(), any(), any(), any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.updateRecipe(uid, recipeName, localRecipe, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when updating recipe there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.updateRecipe(any(), any(), any(), any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.updateRecipe(uid, recipeName, localRecipe, lastUpdate)
        }

    @Test
    fun `when updating recipe there is no error then it is returned`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.updateRecipe(any(), any(), any(), any())
            } returns Unit

            remoteDataSourceImpl.updateRecipe(uid, recipeName, localRecipe, lastUpdate)

            coVerify {
                remoteDataBaseDao.updateRecipe(uid, recipeName, localRecipe, lastUpdate)
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding favorite recipe there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.addFavoriteLocalRecipe(any(), any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding favorite recipe there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.addFavoriteLocalRecipe(any(), any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test
    fun `when adding favorite recipe there is no error then it is returned`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.addFavoriteLocalRecipe(any(), any())
            } returns Unit

            remoteDataSourceImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)

            coVerify {
                remoteDataBaseDao.addFavoriteLocalRecipe(recipeName, uid)
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when removing favorite recipe there is network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.removeFavoriteLocalRecipe(any(), any())
            } throws CommonException.NoInternetException

            remoteDataSourceImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe there is other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.removeFavoriteLocalRecipe(any(), any())
            } throws CommonException.OtherError("Other error")

            remoteDataSourceImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test
    fun `when removing favorite recipe there is no error then it is returned`() =
        runBlockingTest {
            coEvery {
                remoteDataBaseDao.removeFavoriteLocalRecipe(any(), any())
            } returns Unit

            remoteDataSourceImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)

            coVerify {
                remoteDataBaseDao.removeFavoriteLocalRecipe(recipeName, uid)
            }
        }
}
