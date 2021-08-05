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

package org.easyrecipe.model

import org.easyrecipe.common.extensions.unionList
import org.easyrecipe.data.entities.UserEntity
import java.io.Serializable

class User(
    val uid: String,
    var lastUpdate: Long,
) : Serializable {
    private val _localRecipes: MutableList<LocalRecipe> = mutableListOf()
    val localRecipes: List<LocalRecipe>
        get() = _localRecipes

    private val _remoteRecipes: MutableList<RemoteRecipe> = mutableListOf()
    val remoteRecipes: List<RemoteRecipe>
        get() = _remoteRecipes

    val recipes: List<Recipe>
        get() = localRecipes.unionList(remoteRecipes)

    val favoriteRecipes: List<Recipe>
        get() = _localRecipes.union(_remoteRecipes).filter { recipe -> recipe.favorite }

    fun addRecipe(recipe: Recipe) {
        (recipe as? LocalRecipe)?.let { localRecipe ->
            _localRecipes.add(localRecipe)
        }

        (recipe as? RemoteRecipe)?.let { remoteRecipe ->
            _remoteRecipes.add(remoteRecipe)
        }
    }

    fun addRecipes(recipes: List<Recipe>) {
        recipes.forEach { recipe ->
            addRecipe(recipe)
        }
    }

    fun updateRecipe(name: String, recipe: LocalRecipe) {
        val index = _localRecipes.indexOfFirst { currentRecipe -> currentRecipe.name == name }
        if (index >= 0) {
            _localRecipes[index] = recipe
        }
    }

    fun toggleRecipeFavorite(recipeId: Long) {
        recipes.find { recipe ->
            (recipe as? LocalRecipe)?.recipeId == recipeId
        }?.toggleFavorite()
    }

    companion object {
        @JvmStatic
        fun fromEntity(userEntity: UserEntity, uid: String) = User(
            uid = uid,
            lastUpdate = userEntity.lastUpdate ?: 0
        )
    }
}
