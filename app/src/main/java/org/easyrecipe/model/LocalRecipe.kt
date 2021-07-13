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

import org.easyrecipe.data.entities.RecipeEntity
import java.io.Serializable

class LocalRecipe(
    name: String,
    type: List<RecipeType>,
    time: Int,
    image: String,
    var recipeId: Long = 0L,
    var description: String,
) : Recipe(name, type, time, image), Serializable {
    private val _steps: MutableList<String> = mutableListOf()
    val steps: List<String>
        get() = _steps

    private val _ingredients: MutableMap<Ingredient, String> = mutableMapOf()
    val ingredients: Map<Ingredient, String>
        get() = _ingredients

    fun setSteps(steps: List<String>) {
        _steps.addAll(steps)
    }

    fun addIngredient(ingredient: Ingredient, quantity: String) {
        _ingredients[ingredient] = quantity
    }

    fun hasType(type: RecipeType): Boolean = this.type.contains(type)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalRecipe

        if (recipeId != other.recipeId) return false

        return true
    }

    override fun hashCode(): Int {
        return recipeId.hashCode()
    }

    fun removeAllIngredients() {
        _ingredients.clear()
    }

    companion object {
        @JvmStatic
        fun fromEntity(entity: RecipeEntity): LocalRecipe {
            return LocalRecipe(
                recipeId = entity.recipeId,
                name = entity.name,
                type = entity.type,
                description = entity.description,
                time = entity.time,
                image = entity.image ?: ""
            ).also {
                it.setSteps(entity.steps)

                if (entity.isFavorite == 1) {
                    it.toggleFavorite()
                }
            }
        }
    }
}
