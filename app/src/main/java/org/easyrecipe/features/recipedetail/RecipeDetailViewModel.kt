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

import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.extensions.navigateMainFragment
import org.easyrecipe.common.extensions.navigateUpMainFragment
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.features.recipedetail.navigation.RecipeDetailNavigation
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.User
import org.easyrecipe.usecases.deleterecipe.DeleteRecipe
import org.easyrecipe.usecases.favoritelocalrecipe.FavoriteLocalRecipe
import org.easyrecipe.usecases.favoriteremoterecipe.FavoriteRemoteRecipe
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val deleteRecipe: DeleteRecipe,
    private val favoriteRemoteRecipe: FavoriteRemoteRecipe,
    private val favoriteLocalRecipe: FavoriteLocalRecipe,
    private val navManager: NavManager,
    private val recipeDetailNavigation: RecipeDetailNavigation,
    private val dialogManager: DialogManager,
) : BaseViewModel() {
    private var _isLocalRecipeFavorite = false
    val isLocalRecipeFavorite: Boolean
        get() = _isLocalRecipeFavorite

    fun onDeleteRecipe(recipeId: Long) = launch {
        executeUseCase(
            useCase = deleteRecipe,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = { DeleteRecipe.Request(recipeId) }
        ).onSuccess { navManager.navigateUpMainFragment() }
    }

    fun onEditRecipe(localRecipe: LocalRecipe, screenTitle: String) {
        val action = recipeDetailNavigation.navigateToCreateRecipe(localRecipe, screenTitle)
        navManager.navigateMainFragment(action)
    }

    fun onFavoriteRemoteRecipe(user: User, recipeId: String, isFavorite: Boolean) = launch {
        executeUseCase(
            useCase = favoriteRemoteRecipe,
            onPrepareInput = { FavoriteRemoteRecipe.Request(recipeId, isFavorite) }
        )
    }

    fun onFavoriteLocalRecipe(
        user: User,
        localRecipe: LocalRecipe,
        onChangeFavorite: (LocalRecipe) -> Unit,
    ) = launch {
        executeUseCase(
            useCase = favoriteLocalRecipe,
            onPrepareInput = { FavoriteLocalRecipe.Request(user, localRecipe) }
        ).onSuccess {
            onChangeFavorite(localRecipe)
        }
    }
}
