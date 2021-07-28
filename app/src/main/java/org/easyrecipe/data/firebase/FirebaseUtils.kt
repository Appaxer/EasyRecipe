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

package org.easyrecipe.data.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await
import org.easyrecipe.common.CommonException
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

/**
 * Run a firebase task.
 *
 * @param T The type returned by the task
 * @param task The task that has to be executed
 * @param skipExceptions The exceptions that has to be thrown
 */
suspend fun <T> runFirebaseTask(
    task: Task<T>,
    skipExceptions: List<KClass<*>> = emptyList(),
) {
    executeFirebaseTask(task, skipExceptions)
}

/**
 * Run a firebase task and deal with its result.
 *
 * @param T The type of the result returned by the task
 * @param O The type of the method returned after dealing with task result
 * @param task The task that has to be executed
 * @param skipExceptions The exceptions that has to be thrown
 * @param onSuccess The action performed on task result if success
 * @return The result of dealing with task result
 */
suspend fun <T, O> runFirebaseTask(
    task: Task<T>,
    skipExceptions: List<KClass<*>> = emptyList(),
    onSuccess: suspend (T?) -> O,
): O {
    val currentTask = executeFirebaseTask(task, skipExceptions)
    return onSuccess(currentTask.result)
}

private suspend fun <I> executeFirebaseTask(
    task: Task<I>,
    skipExceptions: List<KClass<*>>,
): Task<I> {
    try {
        task.await()
    } catch (e: Exception) {
        // The exception should be ignored
    }

    if (!task.isSuccessful) {
        throw task.exception?.let { exception ->
            val exceptionClass = exception::class
            when {
                exceptionClass in skipExceptions -> exception
                exceptionClass.isFirebaseNetworkError() -> CommonException.NoInternetException
                else -> CommonException.OtherError(exception.stackTraceToString())
            }
        } ?: CommonException.OtherError("Firebase error")
    }

    return task
}

private fun <T : Any> KClass<T>.isFirebaseNetworkError(): Boolean =
    this == FirebaseNetworkException::class || FirebaseNetworkException::class in allSuperclasses
