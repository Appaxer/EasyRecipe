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

package org.easyrecipe.usecases.createrecipe

import org.easyrecipe.common.usecases.UseCase
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.User

interface CreateRecipe : UseCase<CreateRecipe.Request, CreateRecipe.Response> {

    data class Request(
        val name: String,
        val description: String,
        val time: Int,
        val types: List<RecipeType>,
        val ingredients: Map<String, String>,
        val stepList: List<String>,
        val imageUri: String,
        val user: User,
    ) : UseCase.UseCaseRequest

    class Response : UseCase.UseCaseResponse
}
