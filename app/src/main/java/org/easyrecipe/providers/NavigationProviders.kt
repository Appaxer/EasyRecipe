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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.easyrecipe.features.createrecipe.navigation.CreateRecipeNavigation
import org.easyrecipe.features.createrecipe.navigation.CreateRecipeNavigationImpl
import org.easyrecipe.features.recipes.navigation.RecipesNavigation
import org.easyrecipe.features.recipes.navigation.RecipesNavigationImpl
import org.easyrecipe.features.search.navigation.SearchNavigation
import org.easyrecipe.features.search.navigation.SearchNavigationImpl

@Module
@InstallIn(SingletonComponent::class)
class NavigationProviders {

    @Provides
    fun provideSearchNavigation(): SearchNavigation = SearchNavigationImpl()

    @Provides
    fun provideRecipesNavigation(
        @ApplicationContext context: Context,
    ): RecipesNavigation = RecipesNavigationImpl(context)

    @Provides
    fun provideCreateRecipeNavigation(): CreateRecipeNavigation = CreateRecipeNavigationImpl()
}
