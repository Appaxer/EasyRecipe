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

package org.easyrecipe.usecases.getfavoriterecipes

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
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class GetFavoriteRecipesImplTest {
    private lateinit var getAllFavoriteRecipesImpl: GetFavoriteRecipesImpl

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

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        recipeRepository = mockk()
        getAllFavoriteRecipesImpl = GetFavoriteRecipesImpl(recipeRepository)
    }

    @Test
    fun `when there is an unexpected error then Error result is returned with the OtherError exception`() =
        runBlockingTest {
            coEvery { recipeRepository.getFavoriteRecipes() } throws CommonException.OtherError(msg)

            val result = getAllFavoriteRecipesImpl.execute(GetFavoriteRecipes.Request())
            assertThat(result, isResultError())

            val exception = (result as UseCaseResult.Error).exception
            assertThat(exception, instanceOf(CommonException.OtherError::class.java))
        }

    @Test
    fun `when the recipes are found in the database then Success result is returned with the recipe list`() =
        runBlockingTest {
            coEvery { recipeRepository.getFavoriteRecipes() } returns recipes

            val result = getAllFavoriteRecipesImpl.execute(GetFavoriteRecipes.Request())
            assertThat(result, isResultSuccess())

            val recipeList = (result as UseCaseResult.Success).result.recipes
            assertThat(recipeList, isEqualTo(recipes))
        }
}
