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

package org.easyrecipe.data.entities.relations

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "user_favorite_remote_recipes",
    primaryKeys = ["user_id", "favorite_remote_recipe_id"]
)
data class UserFavoriteRemoteRecipeCrossRef(
    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "favorite_remote_recipe_id")
    val favoriteRemoteRecipeId: Long = 0L,
)
