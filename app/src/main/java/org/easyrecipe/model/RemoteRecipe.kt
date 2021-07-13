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

import java.io.Serializable

class RemoteRecipe(
    name: String,
    type: List<RecipeType>,
    time: Int,
    image: String,
    val recipeId: String,
    val source: String,
    val url: String,
    val ingredients: List<String>,
) : Recipe(name, type, time, image), Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoteRecipe

        if (recipeId != other.recipeId) return false

        return true
    }

    override fun hashCode(): Int {
        return recipeId.hashCode()
    }
}
