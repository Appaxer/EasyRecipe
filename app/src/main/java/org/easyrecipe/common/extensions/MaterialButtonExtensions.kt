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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.google.android.material.button.MaterialButton

/**
 * Observe the icon to be displayed in a [MaterialButton].
 *
 * @param lifecycleOwner The lifecycle of the fragment.
 * @param liveData The [LiveData] to observe the changes from
 */
fun MaterialButton.observeIcon(lifecycleOwner: LifecycleOwner, liveData: LiveData<Int>) {
    liveData.observe(lifecycleOwner) { iconRes ->
        setIconResource(iconRes)
    }
}
