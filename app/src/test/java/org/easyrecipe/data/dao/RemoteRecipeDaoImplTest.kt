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

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.extensions.fromJson
import org.easyrecipe.data.edamam.EdamamHit
import org.easyrecipe.data.edamam.EdamamRecipe
import org.easyrecipe.data.edamam.EdamamResponse
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.MealType
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteRecipeDaoImplTest {
    private lateinit var remoteRecipeDaoImpl: RemoteRecipeDao

    private val recipeList = listOf(RemoteRecipe(
        recipeId = "uri",
        name = "Salted Pecan Caramel Corn",
        image = "image",
        source = "source",
        url = "url",
        type = listOf(RecipeType.Vegetarian, RecipeType.GlutenFree),
        ingredients = listOf("3 cups Pecan Halves, Roughly Chopped"),
        time = 75
    ))

    private val edamamRecipe = EdamamRecipe(
        id = "uri",
        name = "Salted Pecan Caramel Corn",
        image = "image",
        source = "source",
        url = "url",
        types = listOf("Vegetarian", "Gluten-Free"),
        ingredients = listOf("3 cups Pecan Halves, Roughly Chopped"),
        totalTime = 75.0F
    )

    private val edamamHit = EdamamHit(edamamRecipe)

    private val edamamRecipeList = listOf(edamamHit)

    private val recipeIds = listOf("uri")

    private val edamamResponse = EdamamResponse(hits = edamamRecipeList)
    private val name = "name"
    private val mealTypes = mutableListOf<MealType>()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var sharedPreferences: SharedPreferences

    @MockK
    private lateinit var gson: Gson

    @MockK
    private lateinit var client: Client

    @Before
    fun setUp() {
        sharedPreferences = mockk()
        every { sharedPreferences.getString(any(), any()) } returns "en"
        gson = mockk()
        client = mockk()
        FuelManager.instance.client = client

        remoteRecipeDaoImpl = RemoteRecipeDaoImpl(sharedPreferences, gson)
    }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection getRecipes should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns -1
            remoteRecipeDaoImpl.getRecipes(name)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection getRecipesByMealType should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns -1
            remoteRecipeDaoImpl.getRecipesByMealType(name, mealTypes)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when there is no internet connection getFavoriteRecipes should throw NoInternetConnectionException`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns -1
            remoteRecipeDaoImpl.getFavoriteRecipes(recipeIds)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error getRecipes should throw OtherError`() = runBlockingTest {
        coEvery { client.executeRequest(any()).statusCode } returns 500
        remoteRecipeDaoImpl.getRecipes(name)
    }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error getRecipesByMealType should throw OtherError`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns 500
            remoteRecipeDaoImpl.getRecipesByMealType(name, mealTypes)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when there is an unexpected error getFavoriteRecipes should throw OtherError`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns 500
            remoteRecipeDaoImpl.getFavoriteRecipes(recipeIds)
        }

    @Test
    fun `when there is no error then getRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns 200
            coEvery { client.executeRequest(any()).data } returns "".toByteArray()
            coEvery { gson.fromJson<EdamamResponse>(any()) } returns edamamResponse

            val result = remoteRecipeDaoImpl.getRecipes(name)

            assertThat(result, isEqualTo(recipeList))
        }

    @Test
    fun `when there is no error then getRecipesByMealType should return a list of recipes`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns 200
            coEvery { client.executeRequest(any()).data } returns "".toByteArray()
            coEvery { gson.fromJson<EdamamResponse>(any()) } returns edamamResponse

            val result = remoteRecipeDaoImpl.getRecipesByMealType(name, mealTypes)

            assertThat(result, isEqualTo(recipeList))
        }

    @Test
    fun `when there is no error then getFavoriteRecipes should return a list of recipes`() =
        runBlockingTest {
            coEvery { client.executeRequest(any()).statusCode } returns 200
            coEvery { client.executeRequest(any()).data } returns "".toByteArray()
            coEvery { gson.fromJson<EdamamHit>(any()) } returns edamamHit

            val result = remoteRecipeDaoImpl.getFavoriteRecipes(recipeIds)

            assertThat(result, isEqualTo(recipeList))
        }

}
