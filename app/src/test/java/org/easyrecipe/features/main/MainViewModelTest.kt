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

package org.easyrecipe.features.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.easyrecipe.*
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import org.easyrecipe.usecases.getorcreateuser.GetOrCreateUser
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel

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

    private val uid = "1"

    @MockK
    private lateinit var searchRecipes: SearchRecipes

    @MockK
    private lateinit var getOrCreateUser: GetOrCreateUser

    @MockK
    private lateinit var navManager: NavManager

    @MockK
    private lateinit var dialogManager: DialogManager

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        searchRecipes = mockk()
        getOrCreateUser = mockk()

        navManager = mockk()
        every { navManager.navigateUp(any()) } returns Unit

        dialogManager = mockk()
        every { dialogManager.showLoadingDialog() } returns Unit
        every { dialogManager.cancelLoadingDialog() } returns Unit

        viewModel = MainViewModel(searchRecipes, getOrCreateUser, navManager, dialogManager)
    }

    @Test
    fun `when there is no internet then NoInternet is shown`() {
        coEvery { searchRecipes.execute(any()) } returns
            UseCaseResult.Error(CommonException.NoInternetException)

        viewModel.onSearchRecipes()

        val exception = viewModel.displayCommonError.getOrAwaitValue()
        assertThat(exception, isNoInternetError())
    }

    @Test
    fun `when there is an unexpected error then OtherError is shown`() {
        coEvery { searchRecipes.execute(any()) } returns
            UseCaseResult.Error(CommonException.OtherError(msg))

        viewModel.onSearchRecipes()
        val exception = viewModel.displayCommonError.getOrAwaitValue()
        assertThat(exception, isOtherError())
    }

    @Test
    fun `when the recipes are found in the remote database then they are stored`() {
        coEvery { searchRecipes.execute(any()) } returns
            UseCaseResult.Success(SearchRecipes.Response(recipes))

        viewModel.onSearchRecipes()

        assertThat(
            viewModel.recipeList.getOrAwaitValueExceptDefault(default = emptyList()),
            isEqualTo(recipes)
        )
    }

    @Test
    fun `when navigating to previous fragment the NavManager is called`() {
        viewModel.onNavigateUp()
        verify {
            navManager.navigateUp(any())
        }
    }

    @Test
    fun `when getting current user there is an unexpected error then OtherError is shown`() {
        coEvery {
            getOrCreateUser.execute(any())
        } returns UseCaseResult.Error(CommonException.OtherError("Other error"))
        viewModel.onGetCurrentUser(uid)

        val exception = viewModel.displayCommonError.getOrAwaitValue()
        assertThat(exception, isOtherError())
    }
}
