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

package org.easyrecipe.data.dao

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await
import org.easyrecipe.common.CommonException
import javax.inject.Inject

class FirebaseAuthDaoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : FirebaseAuthDao {
    override suspend fun doLogin(email: String, password: String): String {
        val task = firebaseAuth.signInWithEmailAndPassword(email, password)
        task.await()
        if (!task.isSuccessful) {
            throw task.exception?.let { exception ->
                if (exception is FirebaseNetworkException) {
                    CommonException.NoInternetException
                } else {
                    exception
                }
            } ?: Exception()
        }
        return task.result?.let { authResult ->
            authResult.user?.uid
        } ?: ""
    }

    override suspend fun doSignup(email: String, password: String): String {
        val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
        task.await()
        if (!task.isSuccessful) {
            throw task.exception?.let { exception ->
                when (exception) {
                    is FirebaseNetworkException -> CommonException.NoInternetException
                    is FirebaseAuthWeakPasswordException -> CommonException.OtherError(exception.stackTraceToString())
                    is FirebaseAuthInvalidCredentialsException -> CommonException.OtherError(
                        exception.stackTraceToString())
                    else -> exception
                }
            } ?: Exception()

        }
        return task.result?.let { authResult ->
            authResult.user?.uid
        } ?: ""
    }
}
