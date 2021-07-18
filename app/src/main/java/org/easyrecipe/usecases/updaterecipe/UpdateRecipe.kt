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

import org.easyrecipe.common.usecases.UseCase
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType

interface UpdateRecipe : UseCase<UpdateRecipe.Request, UpdateRecipe.Response> {

    data class Request(
        val recipe: LocalRecipe,
        val name: String,
        val description: String,
        val time: Int,
        val type: List<RecipeType>,
        val ingredients: Map<String, String>,
        val stepList: List<String>,
        val imageUri: String,
        val uid: String,
    ) : UseCase.UseCaseRequest

    data class Response(
        val recipe: LocalRecipe,
    ) : UseCase.UseCaseResponse
}
