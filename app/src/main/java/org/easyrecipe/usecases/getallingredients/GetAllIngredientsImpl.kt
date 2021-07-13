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

package org.easyrecipe.usecases.getallingredients

import org.easyrecipe.common.usecases.runUseCase
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import javax.inject.Inject

class GetAllIngredientsImpl @Inject constructor(
    private val recipeRepository: RecipeRepository,
) : GetAllIngredients {

    override suspend fun execute(request: GetAllIngredients.Request) = runUseCase {
        val ingredients = recipeRepository.getAllIngredients()
        GetAllIngredients.Response(ingredients)
    }
}
