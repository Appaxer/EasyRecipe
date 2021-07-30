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

package org.easyrecipe.usecases.favoriteremoterecipe

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.isResultError
import org.easyrecipe.isResultSuccess
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import org.easyrecipe.model.User
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteRemoteRecipeImplTest {
    private lateinit var favoriteRemoteRecipe: FavoriteRemoteRecipe

    private val uid = "1"
    private val lastUpdate = 0L
    private val user = User(uid, lastUpdate)

    private val recipeName = "Fish and chips"
    private val remoteRecipeId = "uri"
    private val remoteRecipe = RemoteRecipe(
        name = recipeName,
        type = listOf(RecipeType.Hot, RecipeType.Fish),
        ingredients = emptyList(),
        time = 10,
        image = "",
        recipeId = remoteRecipeId,
        source = "",
        url = ""
    )

    private val request = FavoriteRemoteRecipe.Request(user, remoteRecipe)

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        recipeRepository = mockk()
        favoriteRemoteRecipe = FavoriteRemoteRecipeImpl(recipeRepository)
    }

    @Test
    fun `when there is an unexpected error then the result is an error`() = runBlockingTest {
        coEvery {
            recipeRepository.favoriteRemoteRecipe(any(), any())
        } throws Exception()

        val result = favoriteRemoteRecipe.execute(request)
        assertThat(result, isResultError())

        val error = result as UseCaseResult.Error
        assertThat(error.exception, instanceOf(Exception::class.java))
    }

    @Test
    fun `when there is no error then the recipe is marked as favorite`() = runBlockingTest {
        coEvery {
            recipeRepository.favoriteRemoteRecipe(any(), any())
        } returns Unit

        val result = favoriteRemoteRecipe.execute(request)
        assertThat(result, isResultSuccess())
    }
}
