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
    val lastUpdate: Long,
) : Serializable {
    private val _recipes: MutableList<Recipe> = mutableListOf()
    val recipes: List<Recipe>
        get() = _recipes

    fun addRecipe(recipe: Recipe) {
        _recipes.add(recipe)
    }

    fun addRecipes(recipes: List<Recipe>) {
        _recipes.addAll(recipes)
    }

    companion object {
        @JvmStatic
        fun fromEntity(userEntity: UserEntity) = User(
            uid = userEntity.uid ?: "",
            lastUpdate = userEntity.lastUpdate ?: 0
        )
    }
}
