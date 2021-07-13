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
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.easyrecipe.data.LocalDatabase
import org.easyrecipe.data.MIGRATION_1_2
import org.easyrecipe.data.MIGRATION_2_3
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
        localDataSource: LocalDataSource,
    ): UserRepository = UserRepositoryImpl(localDataSource)

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
    ): RemoteDataSource = RemoteDataSourceImpl(remoteRecipeDao)

    @Provides
    @Singleton
    fun provideRemoteRecipeDao(
        sharedPreferences: SharedPreferences,
        gson: Gson,
    ): RemoteRecipeDao = RemoteRecipeDaoImpl(sharedPreferences, gson)

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideLocalDatabase(
        @ApplicationContext context: Context,
    ): LocalDatabase = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        LocalDatabase.NAME
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class LocalData

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MockLocalData
}
