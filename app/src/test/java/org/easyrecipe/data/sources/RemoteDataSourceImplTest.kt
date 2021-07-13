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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.data.dao.RemoteRecipeDao
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.MealType
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteDataSourceImplTest {
    private lateinit var remoteDataSource: RemoteDataSource

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

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var remoteRecipeDao: RemoteRecipeDao

    @Before
    fun setUp() {
        remoteRecipeDao = mockk()
        remoteDataSource = RemoteDataSourceImpl(remoteRecipeDao)
    }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection then getRecipes should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipes(any()) } throws CommonException.NoInternetException
            remoteDataSource.getRecipes(name, emptyList())
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error then getRecipes should throw OtherError`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipes(any()) } throws CommonException.OtherError(msg)
            remoteDataSource.getRecipes(name, emptyList())
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection then getFavoriteRecipes should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getFavoriteRecipes(any()) } throws CommonException.NoInternetException
            remoteDataSource.getFavoriteRecipes(emptyList())
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error then getFavoriteRecipes should throw OtherError`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getFavoriteRecipes(any()) } throws CommonException.OtherError(
                msg)
            remoteDataSource.getFavoriteRecipes(emptyList())
        }

    @Test
    fun `when there is no error and the meal type list is not empty then getRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipes(any()) } returns remoteRecipes
            val result = remoteDataSource.getRecipes(name, emptyList())

            coVerify(exactly = 0) { remoteRecipeDao.getRecipesByMealType(any(), any()) }
            assertThat(result, isEqualTo(remoteRecipes))
        }

    @Test
    fun `when there is no error and the meal type list is empty then getRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getRecipesByMealType(any(), any()) } returns remoteRecipes
            val result = remoteDataSource.getRecipes(name, mealType)

            coVerify(exactly = 0) { remoteRecipeDao.getRecipes(any()) }
            assertThat(result, isEqualTo(remoteRecipes))
        }

    @Test
    fun `when there is no error the getFavoriteRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { remoteRecipeDao.getFavoriteRecipes(any()) } returns remoteRecipes
            val result = remoteDataSource.getFavoriteRecipes(recipeIds)

            assertThat(result, isEqualTo(remoteRecipes))
        }
}
