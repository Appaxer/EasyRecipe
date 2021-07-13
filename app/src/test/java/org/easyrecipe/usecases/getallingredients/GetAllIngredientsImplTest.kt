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

package org.easyrecipe.usecases.getallingredients

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.isEqualTo
import org.easyrecipe.isResultError
import org.easyrecipe.isResultSuccess
import org.easyrecipe.model.Ingredient
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetAllIngredientsImplTest {
    private lateinit var getAllIngredientsImpl: GetAllIngredientsImpl
    private val ingredientList = listOf(
        Ingredient("Fish"),
        Ingredient("Potato")
    )

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        recipeRepository = mockk()
        getAllIngredientsImpl = GetAllIngredientsImpl(recipeRepository)
    }

    @Test
    fun `when there is an error then the result is an error`() = runBlockingTest {
        coEvery {
            recipeRepository.getAllIngredients()
        } throws Exception("")

        val result = getAllIngredientsImpl.execute(GetAllIngredients.Request())
        assertThat(result, isResultError())

        val error = result as UseCaseResult.Error
        assertThat(error.exception, instanceOf(Exception::class.java))
    }

    @Test
    fun `when there is no error then a list of ingredients is returned`() = runBlockingTest {
        coEvery {
            recipeRepository.getAllIngredients()
        } returns ingredientList

        val result = getAllIngredientsImpl.execute(GetAllIngredients.Request())
        assertThat(result, isResultSuccess())

        val success = result as UseCaseResult.Success
        assertThat(success.result.ingredients, isEqualTo(ingredientList))
    }
}
