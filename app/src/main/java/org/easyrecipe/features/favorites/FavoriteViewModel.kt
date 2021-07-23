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

package org.easyrecipe.features.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.extensions.combine
import org.easyrecipe.common.extensions.navigateMainFragment
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.features.favorites.navigation.FavoriteNavigation
import org.easyrecipe.model.Recipe
import org.easyrecipe.usecases.getfavoriterecipes.GetFavoriteRecipes
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteRecipes: GetFavoriteRecipes,
    private val navManager: NavManager,
    private val favoriteNavigation: FavoriteNavigation,
    private val dialogManager: DialogManager,
) : BaseViewModel() {
    val recipeList = MutableLiveData<List<Recipe>>(emptyList())
    val search = MutableLiveData("")
    val recipesDisplayed: LiveData<List<Recipe>> = recipeList.combine(search) { recipes, search ->
        if (recipes != null && search != null) {
            recipes.filter { it.name.startsWith(search, ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    val isDisplayedRecipeListEmpty = recipesDisplayed.map { it.isEmpty() }

    fun onShowRecipeDetail(recipe: Recipe) {
        val action = favoriteNavigation.navigateToRecipeDetail(recipe)
        navManager.navigateMainFragment(action)
    }

    fun onGetFavoriteRecipes() = launch {
        executeUseCase(
            useCase = getFavoriteRecipes,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = {
                GetFavoriteRecipes.Request()
            }
        ).onSuccess { result ->
            recipeList.value = result.recipes.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )
        }
    }
}
