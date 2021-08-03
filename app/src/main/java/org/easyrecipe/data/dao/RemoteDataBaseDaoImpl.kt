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

import com.google.firebase.firestore.FirebaseFirestore
import org.easyrecipe.common.CommonException
import org.easyrecipe.data.firebase.FirebaseRecipe
import org.easyrecipe.data.firebase.FirebaseUser
import org.easyrecipe.data.firebase.runFirebaseTask
import org.easyrecipe.model.Ingredient
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.User
import javax.inject.Inject

class RemoteDataBaseDaoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : RemoteDataBaseDao {

    override suspend fun isUserExisting(uid: String): Boolean {
        val task = firestore.collection(COLLECTION_USERS).document(uid).get()
        return runFirebaseTask(task) { document ->
            document?.exists() ?: false
        }
    }

    override suspend fun createUser(uid: String) {
        val task = firestore.collection(COLLECTION_USERS).document(uid).set(FirebaseUser())
        runFirebaseTask(task)
    }

    override suspend fun getUser(uid: String): User {
        val task = firestore.collection(COLLECTION_USERS).document(uid).get()
        return runFirebaseTask(task) { document ->
            document?.toObject(FirebaseUser::class.java)?.let { firebaseUser ->
                with(firebaseUser) {
                    User(uid = uid, lastUpdate = lastUpdate).also { user ->
                        user.addRecipes(recipes.toLocalRecipes())
                    }
                }
            } ?: throw CommonException.OtherError("Error parsing document: $COLLECTION_USERS/$uid")
        }
    }

    override suspend fun addLocalRecipesToRemoteDataBaseUser(
        uid: String,
        lastUpdate: Long,
        localRecipes: List<LocalRecipe>,
    ) {
        val firebaseUser = FirebaseUser(
            lastUpdate = lastUpdate,
            recipes = localRecipes.toFirebaseRecipes()
        )
        val task = firestore.collection(COLLECTION_USERS).document(uid).set(firebaseUser)
        runFirebaseTask(task)
    }

    override suspend fun insertRecipe(uid: String, localRecipe: LocalRecipe, lastUpdate: Long) {
        val getUserTask = firestore.collection(COLLECTION_USERS).document(uid).get()
        runFirebaseTask(getUserTask) { document ->
            document?.toObject(FirebaseUser::class.java)?.let { firebaseUser ->
                firebaseUser.lastUpdate = lastUpdate
                firebaseUser.recipes.add(localRecipe.toFirebaseRecipe())

                val updateUserTask =
                    firestore.collection(COLLECTION_USERS).document(uid).set(firebaseUser)

                runFirebaseTask(updateUserTask)
            } ?: throw CommonException.OtherError("Error parsing document: $COLLECTION_USERS/$uid")
        }
    }

    override suspend fun updateRecipe(
        uid: String,
        originalName: String,
        localRecipe: LocalRecipe,
        lastUpdate: Long,
    ) {
        val getUserTask = firestore.collection(COLLECTION_USERS).document(uid).get()
        runFirebaseTask(getUserTask) { document ->
            document?.toObject(FirebaseUser::class.java)?.let { firebaseUser ->
                firebaseUser.lastUpdate = lastUpdate

                val firebaseRecipe = localRecipe.toFirebaseRecipe()
                val index = firebaseUser.recipes.indexOfFirst { recipe ->
                    recipe.name == originalName
                }

                firebaseUser.recipes[index] = firebaseRecipe

                val updateUserTask =
                    firestore.collection(COLLECTION_USERS).document(uid).set(firebaseUser)

                runFirebaseTask(updateUserTask)
            } ?: throw CommonException.OtherError("Error parsing document: $COLLECTION_USERS/$uid")
        }
    }

    override suspend fun removeFavoriteLocalRecipe(name: String, uid: String, lastUpdate: Long) {
        updateRecipeFavorite(name, uid, lastUpdate, false)
    }

    override suspend fun addFavoriteLocalRecipe(name: String, uid: String, lastUpdate: Long) {
        updateRecipeFavorite(name, uid, lastUpdate, true)
    }

    override suspend fun addFavoriteRemoteRecipesToRemoteDatabaseUser(
        uid: String,
        favoriteRemoteRecipesIds: List<String>,
    ) {
        updateFavoriteRemoteRecipe(uid) { firebaseUser ->
            firebaseUser.favoriteRemoteRecipes.addAll(favoriteRemoteRecipesIds)
        }
    }

    override suspend fun getUserFavoriteRemoteRecipes(uid: String): List<String> {
        val task = firestore.collection(COLLECTION_USERS).document(uid).get()
        return runFirebaseTask(task) { document ->
            document?.toObject(FirebaseUser::class.java)?.favoriteRemoteRecipes
                ?: throw CommonException.OtherError(
                    "Error parsing document: $COLLECTION_USERS/$uid"
                )
        }
    }

    override suspend fun removeFavoriteRemoteRecipe(
        recipeId: String,
        uid: String,
        lastUpdate: Long,
    ) {
        updateFavoriteRemoteRecipe(uid) { firebaseUser ->
            firebaseUser.lastUpdate = lastUpdate
            firebaseUser.favoriteRemoteRecipes.remove(recipeId)
        }
    }

    override suspend fun addFavoriteRemoteRecipe(recipeId: String, uid: String, lastUpdate: Long) {
        updateFavoriteRemoteRecipe(uid) { firebaseUser ->
            firebaseUser.lastUpdate = lastUpdate
            firebaseUser.favoriteRemoteRecipes.add(recipeId)
        }
    }

    private suspend fun updateRecipeFavorite(
        name: String,
        uid: String,
        lastUpdate: Long,
        isFavorite: Boolean,
    ) {
        val userTask = firestore.collection(COLLECTION_USERS).document(uid).get()
        runFirebaseTask(userTask) { document ->
            document?.toObject(FirebaseUser::class.java)?.let { firebaseUser ->
                firebaseUser.lastUpdate = lastUpdate
                firebaseUser.recipes.find { recipe -> recipe.name == name }?.let { firebaseRecipe ->
                    firebaseRecipe.favorite = isFavorite

                    val updateTask = firestore.collection(COLLECTION_USERS).document(uid)
                        .set(firebaseUser)
                    runFirebaseTask(updateTask)
                }
            } ?: throw CommonException.OtherError("Error parsing document: $COLLECTION_USERS/$uid")
        }
    }

    private suspend fun updateFavoriteRemoteRecipe(uid: String, onUpdate: (FirebaseUser) -> Unit) {
        val userTask = firestore.collection(COLLECTION_USERS).document(uid).get()
        runFirebaseTask(userTask) { document ->
            document?.toObject(FirebaseUser::class.java)?.let { firebaseUser ->
                onUpdate(firebaseUser)

                val updateTask =
                    firestore.collection(COLLECTION_USERS).document(uid).set(firebaseUser)
                runFirebaseTask(updateTask)
            } ?: throw CommonException.OtherError("Error parsing document: $COLLECTION_USERS/$uid")
        }
    }

    private fun List<FirebaseRecipe>.toLocalRecipes(): List<LocalRecipe> =
        map { firebaseRecipe ->
            with(firebaseRecipe) {
                LocalRecipe(
                    name = name,
                    type = types.map { type -> RecipeType.valueOf(type) },
                    time = time,
                    image = image,
                    description = description
                ).also { localRecipe ->
                    ingredients.forEach { (name, quantity) ->
                        localRecipe.addIngredient(Ingredient(name), quantity)
                    }

                    localRecipe.setSteps(steps)
                    localRecipe.setFavorite(favorite)
                }
            }
        }

    private fun List<LocalRecipe>.toFirebaseRecipes(): MutableList<FirebaseRecipe> =
        map { localRecipe ->
            localRecipe.toFirebaseRecipe()
        }.toMutableList()

    private fun LocalRecipe.toFirebaseRecipe(): FirebaseRecipe =
        FirebaseRecipe(
            name = name,
            types = type.map { type -> type.name },
            time = time,
            image = imageLocation,
            description = description,
            ingredients = ingredients.mapKeys { entry -> entry.key.name },
            steps = steps,
            favorite = favorite
        )

    companion object {
        private const val COLLECTION_USERS = "users"
    }
}
