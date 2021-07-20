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

package org.easyrecipe.data.remotedatabase

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RemoteDataBaseImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : RemoteDataBase {
    override fun isUserExisting(): Boolean {
        return true
    }

    override fun createUser(uid: String) {

    }
}
