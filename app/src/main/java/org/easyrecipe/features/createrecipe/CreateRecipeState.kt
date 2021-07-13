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

package org.easyrecipe.features.createrecipe

import org.easyrecipe.common.ScreenState
import org.easyrecipe.model.LocalRecipe

sealed class CreateRecipeState : ScreenState() {
    class EditIngredient(val name: String, val quantity: String) : CreateRecipeState()
    class EditStep(val position: Int, val step: String) : CreateRecipeState()
    object RecipeCreated : CreateRecipeState()
    class RecipeUpdated(val recipe: LocalRecipe) : CreateRecipeState()
}
