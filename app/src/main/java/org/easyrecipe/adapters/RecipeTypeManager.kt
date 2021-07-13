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

package org.easyrecipe.adapters

import org.easyrecipe.R
import org.easyrecipe.model.RecipeType

class RecipeTypeManager : IconManager<RecipeType> {
    override fun getIcon(value: RecipeType): Int {
        return when (value) {
            RecipeType.Meat -> R.drawable.ic_meat
            RecipeType.Fish -> R.drawable.ic_fish
            RecipeType.Vegetarian -> R.drawable.ic_vegetarian
            RecipeType.Vegan -> R.drawable.ic_vegan
            RecipeType.GlutenFree -> R.drawable.ic_gluten_free
            RecipeType.Hot -> R.drawable.ic_hot
            RecipeType.Cold -> R.drawable.ic_cold
            RecipeType.Spicy -> R.drawable.ic_spicy
            RecipeType.Sweet -> R.drawable.ic_sweet
            RecipeType.SweetAndSour -> R.drawable.ic_sweet_and_sour
        }
    }
}
