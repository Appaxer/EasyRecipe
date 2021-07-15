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
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.extensions.navigateMainFragment
import org.easyrecipe.common.extensions.requireValue
import org.easyrecipe.common.handlers.UseCaseResultHandler
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
) : BaseViewModel() {
    val recipeList = MutableLiveData<List<Recipe>>(mutableListOf())
    val mealType = MutableLiveData<MutableList<MealType>>(mutableListOf())
    val search = MutableLiveData(listOf("a", "e", "i", "o", "u").random())

    val isDisplayedRecipeListEmpty = recipeList.map { it.isEmpty() }

    private val searchRandomRecipesResultHandler = UseCaseResultHandler<SearchRecipes.Response>(
        onSuccess = { result ->
            recipeList.value = result.recipes
            ScreenState.Nothing
        },
        onError = { ScreenState.OtherError }
    )

    fun onSearchRecipes() {
        viewModelScope.launch {
            executeUseCase(searchRecipes, searchRandomRecipesResultHandler) {
                SearchRecipes.Request(search.requireValue(), mealType.requireValue())
            }
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
