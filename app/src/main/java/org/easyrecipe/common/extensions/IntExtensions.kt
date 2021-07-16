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

package org.easyrecipe.common.extensions

import android.content.Context
import org.easyrecipe.R

/**
 * Returns the [Int] conversion from minutes to an hours and minutes string.
 */
fun Int.toDurationString(context: Context): String {
    val hours = div(60)
    val minutes = mod(60)

    return when {
        hours < 1 -> context.getString(R.string.minute_time, minutes)
        minutes > 1 -> context.getString(R.string.hour_minute_time, hours, minutes)
        else -> context.getString(R.string.hour_time, hours)
    }
}

