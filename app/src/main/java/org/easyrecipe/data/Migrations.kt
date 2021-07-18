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

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.easyrecipe.common.extensions.hash
import org.easyrecipe.data.sources.LocalDataSourceImpl

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table recipes add column image TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table recipes add column is_favorite INT default 0")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val defaultLastUpdate = System.currentTimeMillis()
        database.execSQL("alter table users add column last_update INT default $defaultLastUpdate")

        val defaultUid = "0".hash(LocalDataSourceImpl.HASH_ALGORITHM)
        database.execSQL("alter table users add column uid TEXT default $defaultUid")
    }
}
