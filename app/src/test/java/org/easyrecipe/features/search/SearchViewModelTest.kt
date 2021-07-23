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

package org.easyrecipe.features.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavDirections
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.easyrecipe.MainCoroutineRule
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.features.main.MainViewModel
import org.easyrecipe.features.search.navigation.SearchNavigation
import org.easyrecipe.getOrAwaitValueExceptDefault
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SearchViewModelTest {
    private lateinit var viewModel: SearchViewModel

    private var msg = "There was an unexpected error"
    private val recipes = listOf(RemoteRecipe(
        recipeId = "uri",
        name = "Salted Pecan Caramel Corn",
        image = "image",
        source = "source",
        url = "url",
        type = listOf(RecipeType.Vegetarian, RecipeType.GlutenFree),
        ingredients = listOf("3 cups Pecan Halves, Roughly Chopped"),
        time = 75
    ))

    @MockK
    private lateinit var mainViewModel: MainViewModel

    @MockK
    private lateinit var searchRecipes: SearchRecipes

    @MockK
    private lateinit var navManager: NavManager

    @MockK
    private lateinit var searchNavigation: SearchNavigation

    @MockK
    private lateinit var navDirections: NavDirections

    @MockK
    private lateinit var dialogManager: DialogManager

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mainViewModel = mockk()
        searchRecipes = mockk()
        navManager = mockk()
        every { navManager.navigate(any(), any()) } returns Unit

        navDirections = mockk()
        searchNavigation = mockk()
        every { searchNavigation.navigateToRecipeDetail(any()) } returns navDirections

        dialogManager = mockk()
        every { dialogManager.showLoadingDialog() } returns Unit
        every { dialogManager.cancelLoadingDialog() } returns Unit

        viewModel = SearchViewModel(searchRecipes, navManager, searchNavigation, dialogManager)
    }

    @Test
    fun `when there is no internet then NoInternet message is shown`() {
        coEvery { searchRecipes.execute(any()) } returns
            UseCaseResult.Error(CommonException.NoInternetException)

        viewModel.onSearchRecipes(mainViewModel)
        val state = viewModel.displayCommonError.getOrAwaitValueExceptDefault(default = null)
        assertThat(state, instanceOf(CommonException.NoInternetException::class.java))
    }

    @Test
    fun `when there is an unexpected error OtherError message is shown`() {
        coEvery { searchRecipes.execute(any()) } returns
            UseCaseResult.Error(CommonException.OtherError(msg))

        viewModel.onSearchRecipes(mainViewModel)
        val state = viewModel.displayCommonError.getOrAwaitValueExceptDefault(default = null)
        assertThat(state, instanceOf(CommonException.OtherError::class.java))
    }

    @Test
    fun `when the recipes are found in the remote database then they are stored in the ViewModel`() {
        coEvery { searchRecipes.execute(any()) } returns
            UseCaseResult.Success(SearchRecipes.Response(recipes))

        viewModel.onSearchRecipes(mainViewModel)

        assertThat(
            viewModel.recipeList.getOrAwaitValueExceptDefault(default = emptyList()),
            isEqualTo(recipes)
        )
    }

    @Test
    fun `when loading recipe then we navigate to RecipeDetail`() {
        val recipe = recipes.first()
        viewModel.onShowRecipeDetail(recipe)

        verify {
            searchNavigation.navigateToRecipeDetail(recipe)
            navManager.navigate(any(), navDirections)
        }
    }
}
