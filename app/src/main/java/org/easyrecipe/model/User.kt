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

import org.easyrecipe.data.entities.UserEntity
import java.io.Serializable

class User(
    val uid: String,
    var lastUpdate: Long,
) : Serializable {
    private val _recipes: MutableList<Recipe> = mutableListOf()
    val recipes: List<Recipe>
        get() = _recipes

    val favoriteRecipes: List<Recipe>
        get() = recipes.filter { recipe -> recipe.favorite }

    fun addRecipe(recipe: Recipe) {
        _recipes.add(recipe)
    }

    fun addRecipes(recipes: List<Recipe>) {
        _recipes.addAll(recipes)
    }

    fun updateRecipe(name: String, recipe: LocalRecipe) {
        val index = _recipes.indexOfFirst { currentRecipe -> currentRecipe.name == name }
        if (index >= 0) {
            _recipes[index] = recipe
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
