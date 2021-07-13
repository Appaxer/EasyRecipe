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

package org.easyrecipe.usecases.deleterecipe

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.isResultError
import org.easyrecipe.isResultSuccess
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteRecipeImplTest {
    private lateinit var deleteRecipeImpl: DeleteRecipeImpl
    private val request = DeleteRecipe.Request(
        recipeId = 1L
    )

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        recipeRepository = mockk()
        deleteRecipeImpl = DeleteRecipeImpl(recipeRepository)
    }

    @Test
    fun `when there is an unexpected error then the result is an error`() = runBlockingTest {
        coEvery {
            recipeRepository.deleteRecipe(any())
        } throws Exception()

        val result = deleteRecipeImpl.execute(request)
        assertThat(result, isResultError())

        val error = result as UseCaseResult.Error
        assertThat(error.exception, instanceOf(Exception::class.java))
    }

    @Test
    fun `when there is no error then the recipe is deleted`() = runBlockingTest {
        coEvery {
            recipeRepository.deleteRecipe(any())
        } returns Unit

        val result = deleteRecipeImpl.execute(request)
        assertThat(result, isResultSuccess())
    }
}
