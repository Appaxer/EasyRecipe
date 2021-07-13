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

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

/**
 * Observe if the [View] has been enabled.
 *
 * @param lifecycleOwner The lifecycle of the fragment
 * @param liveData The [LiveData] to observe the changes from
 */
fun View.observeEnable(lifecycleOwner: LifecycleOwner, liveData: LiveData<Boolean>) {
    liveData.observe(lifecycleOwner) {
        isEnabled = it
    }
}

/**
 * Observe if the [View] is visible.
 *
 * @param lifecycleOwner The lifecycle of the fragment
 * @param liveData The [LiveData] to observe the changes from
 */
fun View.observeVisibility(lifecycleOwner: LifecycleOwner, liveData: LiveData<Boolean>) {
    liveData.observe(lifecycleOwner) { isVisible ->
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}
