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

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.handlers.UseCaseResultHandler
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.usecases.deleterecipe.DeleteRecipe
import org.easyrecipe.usecases.favoritelocalrecipe.FavoriteLocalRecipe
import org.easyrecipe.usecases.favoriteremoterecipe.FavoriteRemoteRecipe
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val deleteRecipe: DeleteRecipe,
    private val favoriteRemoteRecipe: FavoriteRemoteRecipe,
    private val favoriteLocalRecipe: FavoriteLocalRecipe,
) : BaseViewModel() {
    private var _isLocalRecipeFavorite = false
    val isLocalRecipeFavorite: Boolean
        get() = _isLocalRecipeFavorite

    private val deleteRecipeHandler = UseCaseResultHandler<DeleteRecipe.Response>(
        onSuccess = { RecipeDetailState.RecipeDeleted },
        onError = { ScreenState.OtherError }
    )

    private val favoriteRecipeHandler = UseCaseResultHandler<FavoriteRemoteRecipe.Response>(
        onSuccess = { ScreenState.Nothing },
        onError = { ScreenState.OtherError }
    )

    private val favoriteLocalRecipeHandler = UseCaseResultHandler<FavoriteLocalRecipe.Response>(
        onSuccess = { ScreenState.Nothing },
        onError = { ScreenState.OtherError }
    )

    fun onDeleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            executeUseCase(deleteRecipe, deleteRecipeHandler) {
                DeleteRecipe.Request(recipeId)
            }
        }
    }

    fun onEditRecipe(localRecipe: LocalRecipe) {
        loadState(RecipeDetailState.EditLocalRecipe(localRecipe))
    }

    fun onFavoriteRecipe(recipeId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            executeUseCase(
                favoriteRemoteRecipe,
                favoriteRecipeHandler,
                isExecutingUseCaseStateLoaded = false
            ) {
                FavoriteRemoteRecipe.Request(recipeId, isFavorite)
            }
        }
    }

    fun onFavoriteLocalRecipe(localRecipeId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            executeUseCase(favoriteLocalRecipe,
                favoriteLocalRecipeHandler,
                isExecutingUseCaseStateLoaded = false
            ) {
                FavoriteLocalRecipe.Request(localRecipeId, isFavorite)
            }
        }
    }
}
