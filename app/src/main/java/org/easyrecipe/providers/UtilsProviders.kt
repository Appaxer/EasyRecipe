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
import org.easyrecipe.adapters.IconManager
import org.easyrecipe.adapters.RecipeTypeManager
import org.easyrecipe.model.RecipeType
import org.easyrecipe.utils.MealTypeConversion
import org.easyrecipe.utils.MealTypeConversionImpl
import org.easyrecipe.utils.RecipeTypeConversion
import org.easyrecipe.utils.RecipeTypeConversionImpl
import javax.inject.Singleton

@Module(includes = [DataProviders::class])
@InstallIn(SingletonComponent::class)
class UtilsProviders {

    @Provides
    @Singleton
    fun provideRecipeTypeConversion(): RecipeTypeConversion = RecipeTypeConversionImpl()

    @Provides
    @Singleton
    fun provideMealTypeConversion(): MealTypeConversion = MealTypeConversionImpl()

    @Provides
    @Singleton
    fun provideRecipeRecipeTypeIconManager(): IconManager<RecipeType> = RecipeTypeManager()
}
