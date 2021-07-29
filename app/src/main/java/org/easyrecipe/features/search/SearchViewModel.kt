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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.extensions.navigateMainFragment
import org.easyrecipe.common.extensions.requireValue
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.features.search.navigation.SearchNavigation
import org.easyrecipe.model.MealType
import org.easyrecipe.model.Recipe
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRecipes: SearchRecipes,
    private val navManager: NavManager,
    private val searchNavigation: SearchNavigation,
    private val dialogManager: DialogManager,
) : BaseViewModel() {
    val recipeList = MutableLiveData<List<Recipe>>(mutableListOf())
    val searchRecipeList = MutableLiveData<List<Recipe>>(mutableListOf())
    val mealType = MutableLiveData<MutableList<MealType>>(mutableListOf())
    val search = MutableLiveData(listOf("a", "e", "i", "o", "u").random())

    val isDisplayedRecipeListEmpty = recipeList.map { it.isEmpty() }

    fun onSearchRecipes() = launch {
        executeUseCase(
            useCase = searchRecipes,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = {
                SearchRecipes.Request(search.requireValue(), mealType.requireValue())
            }
        ).onSuccess { result ->
            recipeList.value = result.recipes
            searchRecipeList.value = result.recipes
        }
    }

    fun onShowRecipeDetail(recipe: Recipe) {
        val action = searchNavigation.navigateToRecipeDetail(recipe)
        navManager.navigateMainFragment(action)
    }

    fun onAddMealType(type: MealType) {
        mealType.value?.add(type)
    }

    fun onRemoveMealType(type: MealType) {
        mealType.value?.remove(type)
    }
}
