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

package org.easyrecipe.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.easyrecipe.model.RecipeType

class Converters {

    @TypeConverter
    fun toRecipeType(value: String) = enumValueOf<RecipeType>(value)

    @TypeConverter
    fun fromRecipeType(value: RecipeType) = value.name

    @TypeConverter
    fun toRecypeTypeList(value: String) = Gson().fromJson<List<RecipeType>>(value)

    @TypeConverter
    fun fromRecipeTypeList(value: List<RecipeType>) = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String) = Gson().fromJson<List<String>>(value)

    @TypeConverter
    fun fromStringList(value: List<String>) = Gson().toJson(value)

    private inline fun <reified T> Gson.fromJson(json: String) =
        fromJson<T>(json, object : TypeToken<T>() {}.type)
}
