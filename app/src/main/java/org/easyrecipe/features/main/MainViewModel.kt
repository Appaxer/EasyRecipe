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

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.extensions.navigateUpMainFragment
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.model.Recipe
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchRecipes: SearchRecipes,
    private val navManager: NavManager,
    private val dialogManager: DialogManager,
) : BaseViewModel() {
    val recipeList = MutableLiveData<List<Recipe>>(mutableListOf())
    val searchResultList = MutableLiveData<List<Recipe>>(mutableListOf())
    val comesFromDetail = MutableLiveData(false)

    fun onSearchRecipes() = launch {
        executeUseCase(
            useCase = searchRecipes,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = {
                SearchRecipes.Request("", emptyList())
            }
        ).onSuccess { result ->
            recipeList.value = result.recipes
        }
    }

    fun onNavigateUp() {
        navManager.navigateUpMainFragment()
    }
}
