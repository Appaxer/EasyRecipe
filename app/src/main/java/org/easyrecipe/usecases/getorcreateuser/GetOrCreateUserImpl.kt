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

package org.easyrecipe.usecases.getorcreateuser

import org.easyrecipe.common.usecases.runUseCase
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.data.repositories.user.UserRepository
import javax.inject.Inject

class GetOrCreateUserImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
) : GetOrCreateUser {

    override suspend fun execute(request: GetOrCreateUser.Request) = runUseCase {
        val user = userRepository.getOrCreateUser(request.uid)
        val recipes = recipeRepository.getAllRecipesFromUser(user)

        user.addRecipes(recipes)
        GetOrCreateUser.Response(user)
    }
}
