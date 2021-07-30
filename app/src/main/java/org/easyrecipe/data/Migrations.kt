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

/**
 * Create a migration between [startVersion] version to [endVersion] version using [onMigrate].
 *
 * @param startVersion The start version
 * @param endVersion The end version
 * @param onMigrate The migration that will be applied
 * @return The [Migration] that has to be applied to database
 * @throws IllegalArgumentException When [startVersion] is greater or equal to [endVersion]
 */
private fun createMigration(
    startVersion: Int,
    endVersion: Int,
    onMigrate: SupportSQLiteDatabase.() -> Unit,
): Migration {
    require(startVersion < endVersion) {
        "The startVersion should be lesser than endVersion ($startVersion >= $endVersion)"
    }

    return object : Migration(startVersion, endVersion) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.onMigrate()
        }
    }
}

val MIGRATION_1_2 = createMigration(1, 2) {
    execSQL("alter table recipes add column image TEXT")
}

val MIGRATION_2_3 = createMigration(2, 3) {
    execSQL("alter table recipes add column is_favorite INT default 0")
}

val MIGRATION_3_4 = createMigration(3, 4) {
    val lastUpdate = System.currentTimeMillis()
    execSQL("alter table users add column last_update INTEGER default $lastUpdate")
    execSQL("alter table users add column uid TEXT")
}

val MIGRATION_4_5 = createMigration(4, 5) {
    execSQL("alter table favorite rename to user_recipes")
}

val MIGRATION_5_6 = createMigration(5, 6) {
    execSQL("alter table user_recipes add column is_favourite INT default 0")
}

val MIGRATION_6_7 = createMigration(6, 7) {
    execSQL("delete from user_favorite_remote_recipes")
}
