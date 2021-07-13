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

package org.easyrecipe.usecases.searchrandomrecipes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.isEqualTo
import org.easyrecipe.isResultError
import org.easyrecipe.isResultSuccess
import org.easyrecipe.model.MealType
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchRecipesImplTest {
    private lateinit var searchRecipes: SearchRecipes

    private val msg = "There was an unexpected error"
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
    private val name = "name"
    private val mealType = mutableListOf<MealType>()

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        recipeRepository = mockk()
        searchRecipes = SearchRecipesImpl(recipeRepository)
    }

    @Test
    fun `when there is no internet connection it should return NoInternet`() =
        runBlockingTest {
            coEvery {
                recipeRepository.getRemoteRecipes(any(),
                    any())
            } throws CommonException.NoInternetException
            val result = searchRecipes.execute(SearchRecipes.Request(name, mealType))

            assertThat(result, isResultError())
            val error = result as UseCaseResult.Error
            assertThat(error.exception, instanceOf(CommonException.NoInternetException::class.java))
        }

    @Test
    fun `when there is an unexpected error it should return OtherError`() = runBlockingTest {
        coEvery {
            recipeRepository.getRemoteRecipes(any(),
                any())
        } throws CommonException.OtherError(msg)
        val result = searchRecipes.execute(SearchRecipes.Request(name, mealType))

        assertThat(result, isResultError())
        val error = result as UseCaseResult.Error
        assertThat(error.exception, instanceOf(CommonException.OtherError::class.java))
    }

    @Test
    fun `when there is no error then it should return a list of recipes`() = runBlockingTest {
        coEvery { recipeRepository.getRemoteRecipes(any(), any()) } returns recipeList
        val result = searchRecipes.execute(SearchRecipes.Request(name, mealType))

        assertThat(result, isResultSuccess())
        val success = result as UseCaseResult.Success
        assertThat(success.result.recipes, isEqualTo(recipeList))
    }
}
