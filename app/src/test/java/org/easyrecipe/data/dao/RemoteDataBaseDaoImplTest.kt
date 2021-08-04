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

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.easyrecipe.common.CommonException
import org.easyrecipe.data.firebase.FirebaseRecipe
import org.easyrecipe.data.firebase.FirebaseUser
import org.easyrecipe.isEqualTo
import org.easyrecipe.isFalse
import org.easyrecipe.isTrue
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteDataBaseDaoImplTest {
    private lateinit var remoteDataBaseDaoImpl: RemoteDataBaseDaoImpl

    private val recipeId = 1L
    private val recipeName = "Fish and chips"
    private val recipeDescription = "Delicious"
    private val recipeTime = 10
    private val recipeTypes = listOf(RecipeType.Hot, RecipeType.Fish)
    private val recipeSteps = listOf("First", "Second")
    private val recipeImage = ""

    private val localRecipes = listOf(
        LocalRecipe(
            recipeId = recipeId,
            name = recipeName,
            description = recipeDescription,
            time = recipeTime,
            type = recipeTypes,
            image = recipeImage
        ).also {
            it.setSteps(recipeSteps)
        }
    )

    private val uid = "1"
    private val lastUpdate = 1L
    private val firebaseRecipe = FirebaseRecipe(recipeName)
    private val firebaseUser = FirebaseUser(
        recipes = mutableListOf(
            firebaseRecipe
        )
    )

    private val notExistingRecipeName = "Pizza"

    @MockK
    private lateinit var firestore: FirebaseFirestore

    @MockK
    private lateinit var collection: CollectionReference

    @MockK
    private lateinit var document: DocumentReference

    @MockK
    private lateinit var documentSnapshot: DocumentSnapshot

    @MockK
    private lateinit var documentTask: Task<DocumentSnapshot>

    @MockK
    private lateinit var voidTask: Task<Void>

    @Before
    fun setUp() {
        firestore = mockk()
        collection = mockk()
        every { firestore.collection(any()) } returns collection

        document = mockk()
        every { collection.document(any()) } returns document

        documentTask = mockk()
        every { document.get() } returns documentTask

        voidTask = mockk()
        every { document.set(any()) } returns voidTask

        documentSnapshot = mockk()

        remoteDataBaseDaoImpl = RemoteDataBaseDaoImpl(firestore)
    }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when checking if user exists there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.isUserExisting(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when checking if user exists there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.isUserExisting(uid)
        }

    @Test
    fun `when checking if user exists document is null then result is false`() =
        runBlockingTest {
            setUpMockTask(documentTask)

            val result = remoteDataBaseDaoImpl.isUserExisting(uid)
            assertThat(result, isFalse())
        }

    @Test
    fun `when checking if user exists document it does not exist then result is false`() =
        runBlockingTest {
            every { documentSnapshot.exists() } returns false

            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            val result = remoteDataBaseDaoImpl.isUserExisting(uid)
            assertThat(result, isFalse())
        }

    @Test
    fun `when checking if user exists document it exists then result is false`() =
        runBlockingTest {
            every { documentSnapshot.exists() } returns true

            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            val result = remoteDataBaseDaoImpl.isUserExisting(uid)
            assertThat(result, isTrue())
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when creating user there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.createUser(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when creating user there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.createUser(uid)
        }

    @Test
    fun `when creating user there is no error then it is created`() =
        runBlockingTest {
            setUpMockTask(voidTask)

            remoteDataBaseDaoImpl.createUser(uid)

            verify {
                document.set(any<FirebaseUser>())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when getting user there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.getUser(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting user there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.getUser(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting user document is null then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(documentTask)
            remoteDataBaseDaoImpl.getUser(uid)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when getting user document there is a parsing error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns null

            remoteDataBaseDaoImpl.getUser(uid)
        }

    @Test
    fun `when getting user document there is no error then it is returned`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            val user = remoteDataBaseDaoImpl.getUser(uid)
            assertThat(user.uid, isEqualTo(uid))
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding local recipes there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, localRecipes)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding local recipes there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, localRecipes)
        }

    @Test
    fun `when adding local recipes there is no error then it is created`() =
        runBlockingTest {
            setUpMockTask(voidTask)

            remoteDataBaseDaoImpl.addLocalRecipesToRemoteDataBaseUser(uid, lastUpdate, localRecipes)

            verify {
                document.set(any<FirebaseUser>())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when inserting recipe there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when inserting recipe there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when inserting recipe document is null then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(documentTask)
            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when inserting recipe there is a parsing error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns null

            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when updating user after insert there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when updating user after insert there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot,
                exception = Exception("Other error")
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)
        }

    @Test
    fun `when updating user after insert there is no error then it is updated`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(voidTask)

            remoteDataBaseDaoImpl.insertRecipe(uid, localRecipes.first(), lastUpdate)

            verify {
                document.set(any<FirebaseUser>())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when updating recipe there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when updating recipe there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when updating recipe document is null then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(documentTask)
            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when updating recipe there is a parsing error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns null

            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when updating user after update there is a network error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("No internet")
            )

            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when updating user after update there is an other error then an exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate)
        }

    @Test
    fun `when updating user after update there is no error then it is updated`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(voidTask)

            remoteDataBaseDaoImpl.updateRecipe(uid,
                localRecipes.first().name,
                localRecipes.first(),
                lastUpdate
            )

            verify {
                document.set(any<FirebaseUser>())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when removing favorite recipe there is a user network error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("Internet error")
            )

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe there is an user other error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe document is null then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(documentTask)
            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe there is a parsing error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns null

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe it does not exist then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(notExistingRecipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when removing favorite recipe and updating user network error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("Internet error")
            )

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when removing favorite recipe and updating user other error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test
    fun `when removing favorite recipe there is no error then recipe is marked as not favorite`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(voidTask)

            remoteDataBaseDaoImpl.removeFavoriteLocalRecipe(recipeName, uid, lastUpdate)

            assertThat(firebaseRecipe.favorite, isFalse())

            verify {
                document.set(any())
            }
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding favorite recipe there is a user network error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("Internet error")
            )

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding favorite recipe there is an user other error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding favorite recipe document is null then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(documentTask)
            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding favorite recipe there is a parsing error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns null

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding favorite recipe it does not exist then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(notExistingRecipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.NoInternetException::class)
    fun `when adding favorite recipe and updating user network error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = FirebaseNetworkException("Internet error")
            )

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test(expected = CommonException.OtherError::class)
    fun `when adding favorite recipe and updating user other error then exception is thrown`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(
                task = voidTask,
                isSuccessful = false,
                exception = Exception("Other error")
            )

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)
        }

    @Test
    fun `when adding favorite recipe there is no error then recipe is marked as not favorite`() =
        runBlockingTest {
            setUpMockTask(
                task = documentTask,
                result = documentSnapshot
            )

            every {
                documentSnapshot.toObject(FirebaseUser::class.java)
            } returns firebaseUser

            setUpMockTask(voidTask)

            remoteDataBaseDaoImpl.addFavoriteLocalRecipe(recipeName, uid, lastUpdate)

            assertThat(firebaseRecipe.favorite, isTrue())

            verify {
                document.set(any())
            }
        }

    private fun <T> setUpMockTask(
        task: Task<T>,
        isComplete: Boolean = true,
        isSuccessful: Boolean = true,
        isCanceled: Boolean = false,
        result: T? = null,
        exception: Exception? = null,
    ) {
        every { task.isComplete } returns isComplete
        every { task.isSuccessful } returns isSuccessful
        every { task.isCanceled } returns isCanceled
        every { task.result } returns result
        every { task.exception } returns exception
    }
}
