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

package org.easyrecipe.common.validation

import java.util.regex.Pattern

class FieldValidatorImpl : FieldValidator {
    private val emailAddressPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    )

    override fun isEmailValid(email: String): Boolean {
        return emailAddressPattern.matcher(email).matches()
    }

    override fun isPasswordValid(password: String): Boolean {
        val regex = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[-+_!@#\$%^&€*.,?()/\\\\|¿¡=~ñç¬{}])"
        return password.length >= 8 && password.contains(Regex(regex))
    }
}
