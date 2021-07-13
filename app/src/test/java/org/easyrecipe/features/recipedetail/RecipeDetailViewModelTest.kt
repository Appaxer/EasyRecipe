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

package org.easyrecipe.features.recipedetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.easyrecipe.MainCoroutineRule
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.getAfterLoading
import org.easyrecipe.usecases.deleterecipe.DeleteRecipe
import org.easyrecipe.usecases.favoritelocalrecipe.FavoriteLocalRecipe
import org.easyrecipe.usecases.favoriteremoterecipe.FavoriteRemoteRecipe
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipeDetailViewModelTest {
    private lateinit var viewModel: RecipeDetailViewModel
    private val recipeId = 1L
    private val remoteRecipeId = "uri"
    private val isFavorite = true

    @MockK
    private lateinit var deleteRecipe: DeleteRecipe

    @MockK
    private lateinit var favoriteRemoteRecipe: FavoriteRemoteRecipe

    @MockK
    private lateinit var favoriteLocalRecipe: FavoriteLocalRecipe

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        deleteRecipe = mockk()
        favoriteRemoteRecipe = mockk()
        favoriteLocalRecipe = mockk()
        viewModel = RecipeDetailViewModel(deleteRecipe, favoriteRemoteRecipe, favoriteLocalRecipe)
    }

    @Test
    fun `when deleting the recipe there is an error then the OtherError state is loaded`() {
        coEvery {
            deleteRecipe.execute(any())
        } returns UseCaseResult.Error(Exception())

        viewModel.onDeleteRecipe(recipeId)
        assertThat(
            viewModel.screenState.getAfterLoading(),
            instanceOf(ScreenState.OtherError::class.java)
        )
    }

    @Test
    fun `when deleting the recipe if there is no error then the RecipeDeleted state is loaded`() {
        coEvery {
            deleteRecipe.execute(any())
        } returns UseCaseResult.Success(DeleteRecipe.Response())

        viewModel.onDeleteRecipe(recipeId)
        assertThat(
            viewModel.screenState.getAfterLoading(),
            instanceOf(RecipeDetailState.RecipeDeleted::class.java)
        )
    }

    @Test
    fun `when favorite a remote recipe and there is an error then OtherError state is loaded`() {
        coEvery {
            favoriteRemoteRecipe.execute(any())
        } returns UseCaseResult.Error(Exception())

        viewModel.onFavoriteRecipe(remoteRecipeId, isFavorite)
        assertThat(
            viewModel.screenState.getAfterLoading(),
            instanceOf(ScreenState.OtherError::class.java)
        )
    }

    @Test
    fun `when favorite a remote recipe if there is no error then Nothing state is loaded`() {
        coEvery {
            favoriteRemoteRecipe.execute(any())
        } returns UseCaseResult.Success(FavoriteRemoteRecipe.Response())

        viewModel.onFavoriteRecipe(remoteRecipeId, isFavorite)
        assertThat(
            viewModel.screenState.getAfterLoading(),
            instanceOf(ScreenState.Nothing::class.java)
        )
    }

    @Test
    fun `when favorite local recipe there is an error then OtherError state is loaded`() {
        coEvery {
            favoriteLocalRecipe.execute(any())
        } returns UseCaseResult.Error(Exception())

        viewModel.onFavoriteLocalRecipe(recipeId, isFavorite)
        assertThat(
            viewModel.screenState.getAfterLoading(),
            instanceOf(ScreenState.OtherError::class.java)
        )
    }

    @Test
    fun `when favorite local recipe there is no error then Nothing state is loaded`() {
        coEvery {
            favoriteLocalRecipe.execute(any())
        } returns UseCaseResult.Success(FavoriteLocalRecipe.Response())

        viewModel.onFavoriteLocalRecipe(recipeId, isFavorite)
        assertThat(
            viewModel.screenState.getAfterLoading(),
            instanceOf(ScreenState.Nothing::class.java)
        )
    }
}
