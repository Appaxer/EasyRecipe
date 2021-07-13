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
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.handlers.UseCaseResultHandler
import org.easyrecipe.model.Recipe
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchRecipes: SearchRecipes,
) : BaseViewModel() {
    val recipeList = MutableLiveData<List<Recipe>>(mutableListOf())

    private val searchRandomRecipesResultHandler = UseCaseResultHandler<SearchRecipes.Response>(
        onSuccess = { result ->
            recipeList.value = result.recipes
            ScreenState.Nothing
        },
        onError = { ScreenState.OtherError }
    )

    fun onSearchRecipes() {
        viewModelScope.launch {
            if (recipeList.value.isNullOrEmpty()) {
                executeUseCase(searchRecipes, searchRandomRecipesResultHandler) {
                    SearchRecipes.Request("", emptyList())
                }
            }
        }
    }

}
