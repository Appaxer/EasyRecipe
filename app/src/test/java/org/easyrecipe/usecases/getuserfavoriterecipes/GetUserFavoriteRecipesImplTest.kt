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

package org.easyrecipe.usecases.getuserfavoriterecipes

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.isEqualTo
import org.easyrecipe.isResultSuccess
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.User
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetUserFavoriteRecipesImplTest {
    private lateinit var getUserFavoriteRecipesImpl: GetUserFavoriteRecipesImpl

    val recipe = LocalRecipe(
        recipeId = 1,
        name = "Chicken Fried",
        type = listOf(RecipeType.Meat),
        description = "It's delicious!",
        time = 20,
        image = ""
    ).also { recipe ->
        recipe.setFavorite(true)

    }

    private val uid = "1"
    private val lastUpdate = 0L
    private val user = User(uid, lastUpdate).also { user ->
        user.addRecipe(recipe)
    }

    private val request = GetUserFavoriteRecipes.Request(user)

    @Before
    fun setUp() {
        getUserFavoriteRecipesImpl = GetUserFavoriteRecipesImpl()
    }

    @Test
    fun `when getting user favorite recipes when there is not any favorite then list is empty`() =
        runBlockingTest {
            recipe.setFavorite(false)

            val result = getUserFavoriteRecipesImpl.execute(request)
            assertThat(result, isResultSuccess())

            val success = (result as? UseCaseResult.Success)?.result
            assertThat(success?.favoriteRecipes?.size, isEqualTo(0))
        }

    @Test
    fun `when getting user favorite recipes when there is favorites then list is not empty`() =
        runBlockingTest {
            val result = getUserFavoriteRecipesImpl.execute(request)
            assertThat(result, isResultSuccess())

            val success = (result as? UseCaseResult.Success)?.result
            assertThat(success?.favoriteRecipes?.size, not(isEqualTo(0)))
        }
}
