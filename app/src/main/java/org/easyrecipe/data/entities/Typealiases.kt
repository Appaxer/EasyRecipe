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

package org.easyrecipe.data.entities

import org.easyrecipe.data.entities.relations.RecipeIngredientCrossRef
import org.easyrecipe.data.entities.relations.UserFavoriteRemoteRecipeCrossRef
import org.easyrecipe.data.entities.relations.UserRecipeCrossRef

typealias UserRecipe = UserRecipeCrossRef
typealias RecipeIngredient = RecipeIngredientCrossRef
typealias UserFavoriteRemoteRecipe = UserFavoriteRemoteRecipeCrossRef
