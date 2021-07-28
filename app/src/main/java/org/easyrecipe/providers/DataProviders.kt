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

package org.easyrecipe.providers

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.easyrecipe.data.*
import org.easyrecipe.data.dao.RemoteDataBaseDao
import org.easyrecipe.data.dao.RemoteDataBaseDaoImpl
import org.easyrecipe.data.dao.RemoteRecipeDao
import org.easyrecipe.data.dao.RemoteRecipeDaoImpl
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.data.repositories.recipe.RecipeRepositoryImpl
import org.easyrecipe.data.repositories.user.UserRepository
import org.easyrecipe.data.repositories.user.UserRepositoryImpl
import org.easyrecipe.data.sources.*
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Providers of the repositories and the data.
 */
@Module
@InstallIn(SingletonComponent::class)
class DataProviders {

    @Provides
    @Singleton
    fun provideUserRepository(
        @LocalData localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
    ): UserRepository = UserRepositoryImpl(localDataSource, remoteDataSource)

    @Provides
    @Singleton
    fun provideRecipeRepository(
        @LocalData localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
    ): RecipeRepository = RecipeRepositoryImpl(localDataSource, remoteDataSource)

    @Provides
    @Singleton
    @LocalData
    fun provideLocalDataSource(
        localDatabase: LocalDatabase,
    ): LocalDataSource = LocalDataSourceImpl(localDatabase)

    @Provides
    @Singleton
    @MockLocalData
    fun provideMockDataSource(): LocalDataSource = MockLocalDataSource()

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        remoteRecipeDao: RemoteRecipeDao,
        remoteDataBaseDao: RemoteDataBaseDao,
    ): RemoteDataSource = RemoteDataSourceImpl(remoteRecipeDao, remoteDataBaseDao)

    @Provides
    @Singleton
    fun provideRemoteRecipeDao(
        sharedPreferences: SharedPreferences,
        gson: Gson,
    ): RemoteRecipeDao = RemoteRecipeDaoImpl(sharedPreferences, gson)

    @Provides
    @Singleton
    fun provideRemoteDataBaseDao(
        firestore: FirebaseFirestore,
    ): RemoteDataBaseDao = RemoteDataBaseDaoImpl(firestore)

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context,
    ): LocalDatabase = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        LocalDatabase.NAME
    ).addMigrations(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
    ).build()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalData

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MockLocalData
}
