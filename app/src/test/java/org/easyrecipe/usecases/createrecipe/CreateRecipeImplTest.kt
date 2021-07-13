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

package org.easyrecipe.usecases.createrecipe

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.isResultError
import org.easyrecipe.isResultSuccess
import org.easyrecipe.model.RecipeType
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateRecipeImplTest {
    private lateinit var createRecipeImpl: CreateRecipeImpl

    private val request = CreateRecipe.Request(
        name = "Fish and chips",
        description = "Delicious",
        time = 10,
        types = listOf(RecipeType.Hot, RecipeType.Fish),
        ingredients = mutableMapOf("Fish" to "1", "Potato" to "2"),
        stepList = listOf("First", "Second"),
        imageUri = ""
    )

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        recipeRepository = mockk()
        createRecipeImpl = CreateRecipeImpl(recipeRepository)
    }

    @Test
    fun `when there is an error then the result is an error`() = runBlockingTest {
        coEvery {
            recipeRepository.createRecipe(any(), any(), any(), any(), any(), any(), any())
        } throws Exception("")

        val result = createRecipeImpl.execute(request)
        assertThat(result, isResultError())
    }

    @Test
    fun `when there is no error then the recipe is created`() = runBlockingTest {
        coEvery {
            recipeRepository.createRecipe(any(), any(), any(), any(), any(), any(), any())
        } returns Unit

        val result = createRecipeImpl.execute(request)
        assertThat(result, isResultSuccess())
    }
}
