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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.data.LocalDatabase
import org.easyrecipe.data.dao.RecipeDao
import org.easyrecipe.data.dao.UserDao
import org.easyrecipe.data.entities.FavoriteRemoteRecipeEntity
import org.easyrecipe.data.entities.IngredientEntity
import org.easyrecipe.data.entities.RecipeEntity
import org.easyrecipe.data.entities.RecipeIngredient
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocalDataSourceImplTest {
    private lateinit var localDataSourceImpl: LocalDataSourceImpl

    private val recipeName = "Fish and chips"
    private val recipeDescription = "Delicious"
    private val recipeTime = 10
    private val recipeTypes = listOf(RecipeType.Hot, RecipeType.Fish)
    private val recipeIngredients = mutableMapOf("Fish" to "1", "Potato" to "2")
    private val recipeSteps = listOf("First", "Second")
    private val recipeImage = ""
    private val remoteRecipeId = "uri"

    private val recipeId = 1L
    private val ingredientEntity = IngredientEntity("Salt")

    private val ingredients = listOf(
        IngredientEntity("Fish"),
        IngredientEntity("Potato")
    )

    private val recipe = LocalRecipe(
        recipeId = recipeId,
        name = recipeName,
        description = recipeDescription,
        time = recipeTime,
        type = recipeTypes,
        image = ""
    ).also {
        it.setSteps(recipeSteps)
    }

    private val recipeEntity = RecipeEntity(
        recipeId = recipeId,
        name = recipeName,
        description = recipeDescription,
        time = recipeTime,
        type = recipeTypes,
        steps = recipeSteps,
        image = null
    )

    private val localRecipe: LocalRecipe
        get() = LocalRecipe.fromEntity(recipeEntity)

    private val recipeIngredientEntities = ingredients.map { ingredient ->
        RecipeIngredient(recipeId, ingredient.name, "1")
    }

    private val remoteFavoriteEntity = FavoriteRemoteRecipeEntity(remoteRecipeId)
    private val remoteFavoriteEntities = listOf(remoteFavoriteEntity)

    @MockK
    private lateinit var userDao: UserDao

    @MockK
    private lateinit var recipeDao: RecipeDao

    @MockK
    private lateinit var localDatabase: LocalDatabase

    @Before
    fun setUp() {
        userDao = mockk()
        recipeDao = mockk()

        localDatabase = mockk()
        every { localDatabase.userDao() } returns userDao
        every { localDatabase.recipeDao() } returns recipeDao

        localDataSourceImpl = LocalDataSourceImpl(localDatabase)
    }

    @Test(expected = Exception::class)
    fun `when getting all ingredients there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.getAllIngredients() } throws Exception("Database error")
            localDataSourceImpl.getAllIngredients()
        }

    @Test
    fun `when getting all ingredients then the recipeDao returns a list of ingredients`() =
        runBlockingTest {
            coEvery { recipeDao.getAllIngredients() } returns ingredients

            val result = localDataSourceImpl.getAllIngredients()
            assertThat(result.size, not(isEqualTo(0)))

            coVerify {
                recipeDao.getAllIngredients()
            }
        }

    @Test(expected = Exception::class)
    fun `when creating recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.insertRecipe(any()) } throws Exception("Database error")
            localDataSourceImpl.insertRecipe(
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeSteps,
                recipeImage
            )
        }

    @Test
    fun `when creating recipe the new id is returned and assigned to the entity`() =
        runBlockingTest {
            coEvery { recipeDao.insertRecipe(any()) } returns recipeId

            val result = localDataSourceImpl.insertRecipe(
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeSteps,
                recipeImage
            )

            assertThat(result.recipeId, isEqualTo(recipeId))
        }

    @Test(expected = Exception::class)
    fun `when adding ingredients there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.getIngredient(any()) } throws Exception("Database error")
            localDataSourceImpl.addIngredients(recipe, recipeIngredients)
        }

    @Test
    fun `when ingredient does not exist then it is created before the recipe`() =
        runBlockingTest {
            coEvery { recipeDao.getIngredient(any()) } returns null
            coEvery { recipeDao.insertIngredient(any()) } returns Unit
            coEvery { recipeDao.insertRecipeIngredient(any()) } returns Unit

            localDataSourceImpl.addIngredients(recipe, recipeIngredients)

            coVerify {
                recipeDao.getIngredient(any())
                recipeDao.insertIngredient(any())
                recipeDao.insertRecipeIngredient(any())
            }
        }

    @Test
    fun `when ingredient exists then it is not created before the recipe`() =
        runBlockingTest {
            coEvery { recipeDao.getIngredient(any()) } returns ingredientEntity
            coEvery { recipeDao.insertIngredient(any()) } returns Unit
            coEvery { recipeDao.insertRecipeIngredient(any()) } returns Unit

            localDataSourceImpl.addIngredients(recipe, recipeIngredients)

            coVerify {
                recipeDao.getIngredient(any())
                recipeDao.insertRecipeIngredient(any())
            }

            coVerify(exactly = 0) {
                recipeDao.insertIngredient(any())
            }
        }

    @Test(expected = Exception::class)
    fun `when deleting the recipe if it does not exist then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.deleteRecipe(any()) } throws Exception()
            localDataSourceImpl.deleteRecipe(recipeId)
        }

    @Test
    fun `when deleting the recipe if it exists then it is deleted`() =
        runBlockingTest {
            coEvery { recipeDao.deleteRecipeIngredients(any()) } returns Unit

            coEvery { recipeDao.deleteRecipe(any()) } returns Unit

            localDataSourceImpl.deleteRecipe(recipeId)

            coVerify {
                recipeDao.deleteRecipeIngredients(any())
                recipeDao.deleteRecipe(any())
            }
        }

    @Test(expected = Exception::class)
    fun `when updating the recipe there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.getRecipe(any()) } throws Exception()

            localDataSourceImpl.updateRecipe(
                recipeId,
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeSteps,
                recipeImage
            )
        }

    @Test
    fun `when updating the recipe there is no error then the recipe is updated`() =
        runBlockingTest {
            coEvery { recipeDao.getRecipe(any()) } returns recipeEntity

            coEvery { recipeDao.updateRecipe(any()) } returns Unit

            localDataSourceImpl.updateRecipe(
                recipeId,
                recipeName,
                recipeDescription,
                recipeTime,
                recipeTypes,
                recipeSteps,
                recipeImage
            )

            coVerify {
                recipeDao.getRecipe(recipeId)
                recipeDao.updateRecipe(recipeEntity)
            }
        }

    @Test(expected = Exception::class)
    fun `when updating the ingredients there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.deleteRecipeIngredients(any()) } throws Exception()

            localDataSourceImpl.updateIngredients(localRecipe, recipeIngredients)
        }

    @Test
    fun `when updating the ingredients there is no error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.deleteRecipeIngredients(any()) } returns Unit

            coEvery { recipeDao.getIngredient(any()) } returns ingredientEntity
            coEvery { recipeDao.insertIngredient(any()) } returns Unit
            coEvery { recipeDao.insertRecipeIngredient(any()) } returns Unit

            localDataSourceImpl.updateIngredients(localRecipe, recipeIngredients)

            coVerify {
                recipeDao.deleteRecipeIngredients(recipeId)
            }
        }

    @Test(expected = Exception::class)
    fun `when getting a recipe by id there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { recipeDao.getRecipe(any()) } throws Exception()
            localDataSourceImpl.getRecipeById(recipeId)
        }

    @Test
    fun `when getting a recipe by id there is no error then it is returned`() =
        runBlockingTest {
            coEvery { recipeDao.getRecipe(any()) } returns recipeEntity

            localDataSourceImpl.getRecipeById(recipeId)

            coVerify {
                recipeDao.getRecipe(recipeId)
            }
        }

    @Test(expected = Exception::class)
    fun `when getting all favorite remote recipes there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { userDao.getAllFavoriteRemoteRecipes() } throws Exception()
            localDataSourceImpl.getAllRemoteFavorites()
        }

    @Test
    fun `when getting all favorite remote recipes there is no error then return all of them`() =
        runBlockingTest {
            coEvery {
                userDao.getAllFavoriteRemoteRecipes()
            } returns remoteFavoriteEntities

            val result = localDataSourceImpl.getAllRemoteFavorites()

            assertThat(result, isEqualTo(remoteFavoriteEntities.map { it.remoteRecipeId }))
        }

    @Test(expected = Exception::class)
    fun `when inserting a favorite remote recipe if there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery { userDao.insertFavoriteRemoteRecipe(any()) } throws Exception()
            localDataSourceImpl.addFavoriteRemoteRecipe(remoteRecipeId)
        }

    @Test
    fun `when inserting a favorite remote recipe then it is created`() =
        runBlockingTest {
            coEvery {
                userDao.insertFavoriteRemoteRecipe(any())
            } returns Unit

            localDataSourceImpl.addFavoriteRemoteRecipe(remoteRecipeId)

            coVerify { userDao.insertFavoriteRemoteRecipe(remoteFavoriteEntity) }
        }

    @Test(expected = Exception::class)
    fun `when deleting a favorite remote recipe if it does not exist then an exception is thrown`() =
        runBlockingTest {
            coEvery { userDao.deleteFavoriteRemoteRecipe(any()) } throws Exception()
            localDataSourceImpl.removeFavoriteRemoteRecipe(remoteRecipeId)
        }

    @Test
    fun `when deleting a favorite remote recipe if it exists then it is deleted`() =
        runBlockingTest {
            coEvery {
                userDao.deleteFavoriteRemoteRecipe(any())
            } returns Unit

            localDataSourceImpl.removeFavoriteRemoteRecipe(remoteRecipeId)

            coVerify { userDao.deleteFavoriteRemoteRecipe(remoteFavoriteEntity) }
        }

    @Test(expected = Exception::class)
    fun `when adding favorite local recipe there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                userDao.updateFavoriteLocalRecipe(any(), any())
            } throws Exception()

            localDataSourceImpl.addFavoriteLocalRecipe(recipeId)
        }

    @Test
    fun `when adding favorite local recipe there is no error then it is set as favorite`() =
        runBlockingTest {
            coEvery {
                userDao.updateFavoriteLocalRecipe(any(), any())
            } returns Unit

            localDataSourceImpl.addFavoriteLocalRecipe(recipeId)

            coVerify {
                userDao.updateFavoriteLocalRecipe(recipeId, 1)
            }
        }

    @Test(expected = Exception::class)
    fun `when removing favorite local recipe there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                userDao.updateFavoriteLocalRecipe(any(), any())
            } throws Exception()

            localDataSourceImpl.removeFavoriteLocalRecipe(recipeId)
        }

    @Test
    fun `when removing favorite local recipe there is no error then it is set as not favorite`() =
        runBlockingTest {
            coEvery {
                userDao.updateFavoriteLocalRecipe(any(), any())
            } returns Unit

            localDataSourceImpl.removeFavoriteLocalRecipe(recipeId)

            coVerify {
                userDao.updateFavoriteLocalRecipe(recipeId, 0)
            }
        }

    @Test(expected = Exception::class)
    fun `when getting favorite local recipes there is an error then an exception is thrown`() =
        runBlockingTest {
            coEvery {
                userDao.getFavoriteLocalRecipes()
            } throws Exception()

            localDataSourceImpl.getFavoriteRecipes()
        }

    @Test
    fun `when getting favorite local recipes there is no error then the recipes are returned`() =
        runBlockingTest {
            coEvery {
                userDao.getFavoriteLocalRecipes()
            } returns listOf(recipeEntity)

            localDataSourceImpl.getFavoriteRecipes()
        }
}
