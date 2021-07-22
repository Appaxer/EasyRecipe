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
import org.easyrecipe.model.Ingredient
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.User
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateRecipeImplTest {
    private lateinit var createRecipeImpl: CreateRecipeImpl

    private val uid = "1"
    private val lastUpdate = 0L
    private val user = User(uid, lastUpdate)

    private val name = "Fish and chips"
    private val description = "Delicious"
    private val time = 10
    private val types = listOf(RecipeType.Hot, RecipeType.Fish)
    private val ingredients = mutableMapOf("Fish" to "1", "Potato" to "2")
    private val stepList = listOf("First", "Second")
    private val imageUri = ""

    private val request = CreateRecipe.Request(
        name = name,
        description = description,
        time = time,
        types = types,
        ingredients = ingredients,
        stepList = stepList,
        imageUri = imageUri,
        user = user
    )

    private val localRecipe = LocalRecipe(
        name = name,
        description = description,
        time = time,
        type = types,
        image = imageUri
    ).also { recipe ->
        ingredients.forEach { (name, quantity) ->
            recipe.addIngredient(Ingredient(name), quantity)
        }

        recipe.setSteps(stepList)
    }

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
            recipeRepository.createRecipe(any(), any(), any(), any(), any(), any(), any(), any())
        } throws Exception("")

        val result = createRecipeImpl.execute(request)
        assertThat(result, isResultError())
    }

    @Test
    fun `when there is no error then the recipe is created`() = runBlockingTest {
        coEvery {
            recipeRepository.createRecipe(any(), any(), any(), any(), any(), any(), any(), any())
        } returns localRecipe

        val result = createRecipeImpl.execute(request)
        assertThat(result, isResultSuccess())
    }
}
