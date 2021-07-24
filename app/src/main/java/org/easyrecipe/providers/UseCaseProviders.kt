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
import org.easyrecipe.data.repositories.recipe.RecipeRepository
import org.easyrecipe.data.repositories.user.UserRepository
import org.easyrecipe.usecases.createrecipe.CreateRecipe
import org.easyrecipe.usecases.createrecipe.CreateRecipeImpl
import org.easyrecipe.usecases.deleterecipe.DeleteRecipe
import org.easyrecipe.usecases.deleterecipe.DeleteRecipeImpl
import org.easyrecipe.usecases.favoritelocalrecipe.FavoriteLocalRecipe
import org.easyrecipe.usecases.favoritelocalrecipe.FavoriteLocalRecipeImpl
import org.easyrecipe.usecases.favoriteremoterecipe.FavoriteRemoteRecipe
import org.easyrecipe.usecases.favoriteremoterecipe.FavoriteRemoteRecipeImpl
import org.easyrecipe.usecases.getallingredients.GetAllIngredients
import org.easyrecipe.usecases.getallingredients.GetAllIngredientsImpl
import org.easyrecipe.usecases.getallrecipes.GetAllRecipes
import org.easyrecipe.usecases.getallrecipes.GetAllRecipesImpl
import org.easyrecipe.usecases.getfavoriterecipes.GetFavoriteRecipes
import org.easyrecipe.usecases.getfavoriterecipes.GetFavoriteRecipesImpl
import org.easyrecipe.usecases.getorcreateuser.GetOrCreateUser
import org.easyrecipe.usecases.getorcreateuser.GetOrCreateUserImpl
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipes
import org.easyrecipe.usecases.searchrandomrecipes.SearchRecipesImpl
import org.easyrecipe.usecases.updaterecipe.UpdateRecipe
import org.easyrecipe.usecases.updaterecipe.UpdateRecipeImpl
import javax.inject.Singleton

/**
 * Providers of the use cases of the view models.
 */
@Module(includes = [DataProviders::class])
@InstallIn(SingletonComponent::class)
class UseCaseProviders {

    @Provides
    @Singleton
    fun provideGetAllRecipes(
        recipeRepository: RecipeRepository,
    ): GetAllRecipes = GetAllRecipesImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideGetAllIngredients(
        recipeRepository: RecipeRepository,
    ): GetAllIngredients = GetAllIngredientsImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideSearchRandomRecipes(
        recipeRepository: RecipeRepository,
    ): SearchRecipes = SearchRecipesImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideCreateRecipe(
        recipeRepository: RecipeRepository,
    ): CreateRecipe = CreateRecipeImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideDeleteRecipe(
        recipeRepository: RecipeRepository,
    ): DeleteRecipe = DeleteRecipeImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideUpdateRecipe(
        recipeRepository: RecipeRepository,
    ): UpdateRecipe = UpdateRecipeImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideFavoriteRemoteRecipe(
        recipeRepository: RecipeRepository,
    ): FavoriteRemoteRecipe = FavoriteRemoteRecipeImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideFavoriteLocalRecipe(
        recipeRepository: RecipeRepository,
    ): FavoriteLocalRecipe = FavoriteLocalRecipeImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideGetFavoriteRecipes(
        recipeRepository: RecipeRepository,
    ): GetFavoriteRecipes = GetFavoriteRecipesImpl(recipeRepository)

    @Provides
    @Singleton
    fun provideGetOrCreateUser(
        userRepository: UserRepository,
        recipeRepository: RecipeRepository,
    ): GetOrCreateUser = GetOrCreateUserImpl(userRepository, recipeRepository)
}
