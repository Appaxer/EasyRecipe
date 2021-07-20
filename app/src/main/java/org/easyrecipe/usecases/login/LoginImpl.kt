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

package org.easyrecipe.usecases.login

import org.easyrecipe.common.usecases.runUseCase
import org.easyrecipe.data.repositories.user.UserRepository
import javax.inject.Inject

class LoginImpl @Inject constructor(
    private val userRepository: UserRepository,
) : Login {
    override suspend fun execute(request: Login.Request) = runUseCase {
        val uid = userRepository.doLogin(request.email, request.password)
        Login.Response(uid)
    }

}
