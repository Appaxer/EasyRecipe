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

package org.easyrecipe.utils

import org.easyrecipe.R
import org.easyrecipe.model.RecipeType

class RecipeTypeConversionImpl : RecipeTypeConversion {

    override fun convertToStringRes(recipeType: RecipeType): Int {
        return when (recipeType) {
            RecipeType.Meat -> R.string.recipe_type_meat
            RecipeType.Fish -> R.string.recipe_type_fish
            RecipeType.Vegetarian -> R.string.recipe_type_vegetarian
            RecipeType.Vegan -> R.string.recipe_type_vegan
            RecipeType.GlutenFree -> R.string.recipe_type_gluten_free
            RecipeType.Hot -> R.string.recipe_type_hot
            RecipeType.Cold -> R.string.recipe_type_cold
            RecipeType.Spicy -> R.string.recipe_type_spicy
            RecipeType.Sweet -> R.string.recipe_type_sweet
            RecipeType.SweetAndSour -> R.string.recipe_type_sweet_and_sour
        }
    }
}
