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

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.easyrecipe.features.login.navigation.LoginNavigation
import org.easyrecipe.features.login.navigation.LoginNavigationImpl
import org.easyrecipe.features.search.navigation.SearchNavigation
import org.easyrecipe.features.search.navigation.SearchNavigationImpl
import org.easyrecipe.features.signup.navigation.SignupNavigation
import org.easyrecipe.features.signup.navigation.SignupNavigationImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NavigationProviders {

    @Provides
    @Singleton
    fun provideSearchNavigation(): SearchNavigation = SearchNavigationImpl()

    @Provides
    @Singleton
    fun provideLoginNavigation(): LoginNavigation = LoginNavigationImpl()

    @Provides
    @Singleton
    fun provideSignupNavigation(): SignupNavigation = SignupNavigationImpl()
}
