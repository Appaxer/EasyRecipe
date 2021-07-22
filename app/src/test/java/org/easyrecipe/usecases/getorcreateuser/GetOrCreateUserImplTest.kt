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

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.*
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.data.repositories.user.UserRepository
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.User
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetOrCreateUserImplTest {
    private lateinit var getOrCreateUserImpl: GetOrCreateUserImpl

    private val uid = "1"
    private val lastUpdate = 0L
    private val request = GetOrCreateUser.Request(uid)

    private val user = User(uid, lastUpdate)
    private val recipes = listOf(
        LocalRecipe(
            name = "Fish and chips",
            description = "Delicious",
            type = listOf(RecipeType.Hot, RecipeType.Fish),
            time = 10,
            image = ""
        )
    )

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        userRepository = mockk()
        recipeRepository = mockk()

        getOrCreateUserImpl = GetOrCreateUserImpl(userRepository, recipeRepository)
    }

    @Test
    fun `when getting current user there is a network error then the use case fails`() =
        runBlockingTest {
            coEvery {
                userRepository.getOrCreateUser(any())
            } throws CommonException.NoInternetException

            val result = getOrCreateUserImpl.execute(request)
            assertThat(result, isResultError())

            val exception = (result as UseCaseResult.Error).exception
            assertThat(exception, isNoInternetError())
        }

    @Test
    fun `when getting current user there is an unexpected error then the use case fails`() =
        runBlockingTest {
            coEvery {
                userRepository.getOrCreateUser(any())
            } throws CommonException.OtherError("Other error")

            val result = getOrCreateUserImpl.execute(request)
            assertThat(result, isResultError())

            val exception = (result as UseCaseResult.Error).exception
            assertThat(exception, isOtherError())
        }

    @Test
    fun `when getting current user recipes there is a network error then the use case fails`() =
        runBlockingTest {
            coEvery {
                userRepository.getOrCreateUser(any())
            } returns user

            coEvery {
                recipeRepository.getAllRecipesFromUser(user)
            } throws CommonException.NoInternetException

            val result = getOrCreateUserImpl.execute(request)
            assertThat(result, isResultError())

            val exception = (result as UseCaseResult.Error).exception
            assertThat(exception, isNoInternetError())
        }

    @Test
    fun `when getting current user recipes there is an unexpected error then the use case fails`() =
        runBlockingTest {
            coEvery {
                userRepository.getOrCreateUser(any())
            } returns user

            coEvery {
                recipeRepository.getAllRecipesFromUser(user)
            } throws CommonException.OtherError("Other error")

            val result = getOrCreateUserImpl.execute(request)
            assertThat(result, isResultError())

            val exception = (result as UseCaseResult.Error).exception
            assertThat(exception, isOtherError())
        }

    @Test
    fun `when getting current user there is not any error then the use case succeed`() =
        runBlockingTest {
            coEvery {
                userRepository.getOrCreateUser(any())
            } returns user

            coEvery {
                recipeRepository.getAllRecipesFromUser(user)
            } returns recipes

            val result = getOrCreateUserImpl.execute(request)
            assertThat(result, isResultSuccess())

            val success = (result as UseCaseResult.Success).result
            assertThat(success.user, isEqualTo(user))

            assertThat(user.recipes, isEqualTo(recipes))
        }
}
