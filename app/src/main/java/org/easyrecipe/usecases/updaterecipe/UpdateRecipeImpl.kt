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

package org.easyrecipe.usecases.updaterecipe

import org.easyrecipe.common.usecases.runUseCase
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import javax.inject.Inject

class UpdateRecipeImpl @Inject constructor(
    private val recipeRepository: RecipeRepository,
) : UpdateRecipe {
    override suspend fun execute(request: UpdateRecipe.Request) = runUseCase {
        recipeRepository.updateRecipe(
            request.recipe.recipeId,
            request.name,
            request.description,
            request.time,
            request.type,
            request.ingredients,
            request.stepList,
            request.imageUri,
            request.uid
        )

        val recipe = recipeRepository.getRecipeById(request.recipe.recipeId)
        UpdateRecipe.Response(recipe)
    }
}
