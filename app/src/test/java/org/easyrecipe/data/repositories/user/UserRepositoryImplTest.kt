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

package org.easyrecipe.data.repositories.user

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.data.sources.LocalDataSource
import org.easyrecipe.data.sources.RemoteDataSource
import org.easyrecipe.isEqualTo
import org.easyrecipe.model.User
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryImplTest {
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    private val uid = "1"
    private val lastUpdate = 0L
    private val user = User(uid, lastUpdate)

    @MockK
    private lateinit var localDataSource: LocalDataSource

    @MockK
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setUp() {
        localDataSource = mockk()
        remoteDataSource = mockk()

        userRepositoryImpl = UserRepositoryImpl(localDataSource, remoteDataSource)
    }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting or creating user in local there is an error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getOrCreateUser(any())
            } throws CommonException.OtherError("Other error")

            userRepositoryImpl.getOrCreateUser(uid)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when creating user in remote source there is a network error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getOrCreateUser(any())
            } returns user

            coEvery {
                remoteDataSource.createUserIfNotExisting(any())
            } throws CommonException.NoInternetException

            userRepositoryImpl.getOrCreateUser(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when creating user in remote source there is an other error then exception is thrown`() =
        runBlockingTest {
            coEvery {
                localDataSource.getOrCreateUser(any())
            } returns user

            coEvery {
                remoteDataSource.createUserIfNotExisting(any())
            } throws CommonException.OtherError("Other error")

            userRepositoryImpl.getOrCreateUser(uid)
        }

    @Test
    fun `when getting or creating user there is not any error then user is returned`() =
        runBlockingTest {
            coEvery {
                localDataSource.getOrCreateUser(any())
            } returns user

            coEvery {
                remoteDataSource.createUserIfNotExisting(any())
            } returns Unit

            val result = userRepositoryImpl.getOrCreateUser(uid)
            assertThat(result, isEqualTo(user))
        }
}
