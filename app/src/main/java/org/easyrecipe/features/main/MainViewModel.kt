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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.extensions.navigateUpMainFragment
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.model.Recipe
import org.easyrecipe.model.User
import org.easyrecipe.usecases.getorcreateuser.GetOrCreateUser
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchRecipes: SearchRecipes,
    private val getOrCreateUser: GetOrCreateUser,
    private val navManager: NavManager,
    private val dialogManager: DialogManager,
) : BaseViewModel() {
    private val _recipeList = MutableLiveData<List<Recipe>>(mutableListOf())
    val recipeList: LiveData<List<Recipe>>
        get() = _recipeList

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun onSearchRecipes() = launch {
        executeUseCase(
            useCase = searchRecipes,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = { SearchRecipes.Request("", emptyList()) }
        ).onSuccess { result ->
            _recipeList.value = result.recipes
        }
    }

    fun onNavigateUp() {
        navManager.navigateUpMainFragment()
    }

    fun onGetCurrentUser(uid: String) = launch {
        executeUseCase(
            useCase = getOrCreateUser,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = { GetOrCreateUser.Request(uid) }
        ).onSuccess { result ->
            Log.i("Test", "onGetCurrentUser: ${result.user}")
            _user.value = result.user
        }
    }
}
