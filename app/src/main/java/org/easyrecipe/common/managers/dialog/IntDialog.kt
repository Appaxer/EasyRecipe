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

package org.easyrecipe.common.managers.dialog

import android.content.DialogInterface
import org.easyrecipe.R

data class IntDialog(
    val title: Int,
    val message: Int,
    val positiveButtonText: Int = R.string.ok,
    val positiveButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    val negativeButtonText: Int? = null,
    val negativeButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    val neutralButtonText: Int? = null,
    val neutralButtonAction: (DialogInterface, Int) -> Unit = { dialog, _ -> dialog.dismiss() },
    val isCancelable: Boolean = true,
)
