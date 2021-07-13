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

package org.easyrecipe.common.handlers

import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout

/**
 * Handler for displaying an error if the input of the [TextInputLayout] is invalid. It uses a
 * check function to verify if the input continues to be invalid. This function should update the
 * value of the LiveData that controls the validity of the field.
 *
 * @constructor The constructor requires the [TextInputLayout] in which the field is modified and
 * the check function to verify if it continues to be invalid.
 */
class InvalidFieldHandler(
    textInputLayout: TextInputLayout,
    checkFunction: (String) -> Unit,
) {
    var isBeingChecked = false

    init {
        textInputLayout.editText?.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                if (isBeingChecked) {
                    checkFunction(text.toString())
                }
            }
        )
    }
}
