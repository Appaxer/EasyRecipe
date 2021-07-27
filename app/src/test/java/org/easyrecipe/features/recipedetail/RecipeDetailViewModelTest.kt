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
import androidx.navigation.NavDirections
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
import org.easyrecipe.features.recipedetail.navigation.RecipeDetailNavigation
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.User
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

    private val uid = "1"
    private val lastUpdate = 0L
    private val user = User(uid, lastUpdate)

    private val recipeName = "Fish and chips"

    private val localRecipe = LocalRecipe(
        name = recipeName,
        description = "Delicious",
        type = listOf(RecipeType.Hot, RecipeType.Fish),
        time = 10,
        image = "",
        recipeId = recipeId
    ).also { recipe ->
        recipe.setFavorite(isFavorite)
    }

    private val screenTitle: String
        get() = "Editing $recipeName"

    @MockK
    private lateinit var navManager: NavManager

    @MockK
    private lateinit var recipeDetailNavigation: RecipeDetailNavigation

    @MockK
    private lateinit var navDirections: NavDirections

    @MockK
    private lateinit var dialogManager: DialogManager

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
        navManager = mockk()
        every { navManager.navigate(any(), any()) } returns Unit
        every { navManager.navigateUp(any()) } returns Unit

        navDirections = mockk()
        recipeDetailNavigation = mockk()
        every { recipeDetailNavigation.navigateToCreateRecipe(any(), any()) } returns navDirections

        dialogManager = mockk()
        every { dialogManager.showLoadingDialog() } returns Unit
        every { dialogManager.cancelLoadingDialog() } returns Unit

        deleteRecipe = mockk()
        favoriteRemoteRecipe = mockk()
        favoriteLocalRecipe = mockk()
        viewModel = RecipeDetailViewModel(
            deleteRecipe,
            favoriteRemoteRecipe,
            favoriteLocalRecipe,
            navManager,
            recipeDetailNavigation,
            dialogManager
        )
    }

    @Test
    fun `when checking if local recipe is favorite then the default value is false`() {
        assertThat(viewModel.isLocalRecipeFavorite, isFalse())
    }

    @Test
    fun `when deleting the recipe there is an error then OtherError is shown`() {
        coEvery {
            deleteRecipe.execute(any())
        } returns UseCaseResult.Error(CommonException.OtherError("Other error"))

        viewModel.onDeleteRecipe(recipeId)

        assertThat(
            viewModel.displayCommonError.getOrAwaitValue(),
            instanceOf(CommonException.OtherError::class.java)
        )
    }

    @Test
    fun `when deleting the recipe if there is no error then we navigate up`() {
        coEvery {
            deleteRecipe.execute(any())
        } returns UseCaseResult.Success(DeleteRecipe.Response())

        viewModel.onDeleteRecipe(recipeId)

        await(4)
        verify {
            navManager.navigateUp(any())
        }
    }

    @Test
    fun `when editing recipe then we navigate to create recipe`() {
        viewModel.onEditRecipe(localRecipe, screenTitle)

        verify {
            recipeDetailNavigation.navigateToCreateRecipe(localRecipe, screenTitle)
            navManager.navigate(any(), navDirections)
        }
    }

    @Test
    fun `when favorite a remote recipe and there is an error then OtherError is shown`() {
        coEvery {
            favoriteRemoteRecipe.execute(any())
        } returns UseCaseResult.Error(CommonException.OtherError("Other error"))

        viewModel.onFavoriteRemoteRecipe(user, remoteRecipeId, isFavorite)

        assertThat(
            viewModel.displayCommonError.getOrAwaitValue(),
            instanceOf(CommonException.OtherError::class.java)
        )
    }

    @Test
    fun `when favorite a remote recipe if there is no error then it is marked as favorite`() {
        coEvery {
            favoriteRemoteRecipe.execute(any())
        } returns UseCaseResult.Success(FavoriteRemoteRecipe.Response())

        viewModel.onFavoriteRemoteRecipe(user, remoteRecipeId, isFavorite)
    }

    @Test
    fun `when favorite local recipe there is an error then OtherError state is shown`() {
        coEvery {
            favoriteLocalRecipe.execute(any())
        } returns UseCaseResult.Error(CommonException.OtherError("Other error"))

        viewModel.onFavoriteLocalRecipe(user, localRecipe) {}

        assertThat(
            viewModel.displayCommonError.getOrAwaitValue(),
            instanceOf(CommonException.OtherError::class.java)
        )
    }

    @Test
    fun `when favorite local recipe there is no error then it is marked as favorite`() {
        coEvery {
            favoriteLocalRecipe.execute(any())
        } returns UseCaseResult.Success(FavoriteLocalRecipe.Response())

        viewModel.onFavoriteLocalRecipe(user, localRecipe) {}
        assertThat(localRecipe.favorite, isTrue())
    }
}
