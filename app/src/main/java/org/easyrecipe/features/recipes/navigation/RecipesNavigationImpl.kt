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

package org.easyrecipe.features.recipes.navigation

import android.content.Context
import org.easyrecipe.R
import org.easyrecipe.features.recipes.RecipesFragmentDirections
import org.easyrecipe.model.Recipe
import javax.inject.Inject

class RecipesNavigationImpl @Inject constructor(
    private val context: Context,
) : RecipesNavigation {
    override fun navigateToCreateRecipe() =
        RecipesFragmentDirections.actionRecipesFragmentToCreateRecipeFragment(
            context.getString(R.string.create_recipe_fragment)
        )

    override fun navigateToShowRecipeDetail(recipe: Recipe) =
        RecipesFragmentDirections.actionRecipesFragmentToRecipeDetail(
            recipeName = recipe.name,
            recipe = recipe
        )
}
