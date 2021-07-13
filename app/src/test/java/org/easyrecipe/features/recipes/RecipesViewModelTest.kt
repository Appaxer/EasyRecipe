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

package org.easyrecipe.features.recipes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.easyrecipe.*
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.usecases.getallrecipes.GetAllRecipes
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipesViewModelTest {
    private lateinit var viewModel: RecipesViewModel

    private var msg = "There was an unexpected error"
    private var recipes = listOf(
        LocalRecipe(recipeId = 1,
            name = "Chicken Fried",
            type = listOf(RecipeType.Meat),
            description = "It's delicious!",
            time = 20,
            image = ""),
        LocalRecipe(recipeId = 2,
            name = "Fried Chicken",
            type = listOf(RecipeType.Hot, RecipeType.Meat),
            description = "Yummmmmmm",
            time = 30,
            image = ""),
        LocalRecipe(recipeId = 3,
            name = "Fried Chicken",
            type = listOf(RecipeType.Vegan),
            description = "This is not fried chicken",
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
    private lateinit var getAllRecipes: GetAllRecipes

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        getAllRecipes = mockk()
        viewModel = RecipesViewModel(getAllRecipes)
    }

    @Test
    fun `when there is an unexpected error then OtherError state is loaded`() {
        coEvery { getAllRecipes.execute(any()) } returns
            UseCaseResult.Error(CommonException.OtherError(msg))

        viewModel.onGetAllRecipes()
        val state = viewModel.screenState.getAfterLoading()
        assertThat(state, instanceOf(ScreenState.OtherError::class.java))
    }

    @Test
    fun `when the recipes are found in the database then they are stored in the ViewModel`() {
        coEvery { getAllRecipes.execute(any()) } returns
            UseCaseResult.Success(GetAllRecipes.Response(recipes))

        viewModel.onGetAllRecipes()
        val state = viewModel.screenState.getAfterLoading()
        assertThat(state, instanceOf(ScreenState.Nothing::class.java))

        assertThat(
            viewModel.recipesDisplayed.getOrAwaitValueExceptDefault(default = emptyList()),
            isEqualTo(recipes)
        )
    }

    @Test
    fun `when the recipes are found and filter by name but not matching then list is empty`() {
        coEvery { getAllRecipes.execute(any()) } returns
            UseCaseResult.Success(GetAllRecipes.Response(recipes))

        viewModel.search.value = "Not Existing"
        viewModel.onGetAllRecipes()
        val state = viewModel.screenState.getAfterLoading()
        assertThat(state, instanceOf(ScreenState.Nothing::class.java))

        assertThat(
            viewModel.recipesDisplayed.getOrAwaitValue(),
            isEqualTo(emptyList())
        )

        assertThat(
            viewModel.isDisplayedRecipeListEmpty.getOrAwaitValueExceptDefault(default = false),
            isTrue()
        )
    }

    @Test
    fun `when the recipes are found and filter by name then only selected are shown`() {
        coEvery { getAllRecipes.execute(any()) } returns
            UseCaseResult.Success(GetAllRecipes.Response(recipes))

        viewModel.search.value = "Spicy"
        viewModel.onGetAllRecipes()
        val state = viewModel.screenState.getAfterLoading()
        assertThat(state, instanceOf(ScreenState.Nothing::class.java))

        assertThat(
            viewModel.recipesDisplayed.getOrAwaitValueExceptDefault(default = emptyList()),
            isEqualTo(listOf(recipes[4]))
        )
    }
}
