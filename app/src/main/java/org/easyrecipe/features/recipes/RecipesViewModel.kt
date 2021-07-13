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

package org.easyrecipe.features.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.extensions.combine
import org.easyrecipe.common.handlers.UseCaseResultHandler
import org.easyrecipe.model.Recipe
import org.easyrecipe.usecases.getallrecipes.GetAllRecipes
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val getAllRecipes: GetAllRecipes,
) : BaseViewModel() {
    private val recipeList = MutableLiveData<List<Recipe>>(mutableListOf())
    val search = MutableLiveData("")
    val recipesDisplayed: LiveData<List<Recipe>> = recipeList.combine(search) { recipes, search ->
        if (recipes != null && search != null) {
            recipes.filter { it.name.startsWith(search, ignoreCase = true) }
        } else {
            emptyList()
        }
    }

    val isDisplayedRecipeListEmpty = recipesDisplayed.map { it.isEmpty() }

    private val getAllRecipesResultHandler = UseCaseResultHandler<GetAllRecipes.Response>(
        onSuccess = { result ->
            recipeList.value = result.recipes.sortedWith(
                compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
            )
            ScreenState.Nothing
        },
        onError = { ScreenState.OtherError }
    )

    fun onCreateRecipe() {
        loadState(RecipesState.CreateRecipe)
    }

    fun onShowRecipeDetail(recipe: Recipe) {
        loadState(RecipesState.ShowRecipeDetail(recipe))
    }

    fun onGetAllRecipes() {
        viewModelScope.launch {
            executeUseCase(getAllRecipes, getAllRecipesResultHandler) {
                GetAllRecipes.Request()
            }
        }
    }
}
