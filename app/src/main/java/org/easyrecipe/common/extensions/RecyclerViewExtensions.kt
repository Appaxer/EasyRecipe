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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Observe the changes to the values that are being displayed in the [RecyclerView].
 *
 * @param T The type of the elements displayed in the [RecyclerView]
 * @param lifecycleOwner The lifecycle of the fragment
 * @param liveData The [LiveData] to observe the values from.
 */
@Suppress("UNCHECKED_CAST")
fun <T> RecyclerView.observeList(lifecycleOwner: LifecycleOwner, liveData: LiveData<List<T>>) {
    liveData.observe(lifecycleOwner) {
        (adapter as? ListAdapter<T, *>)?.submitList(it)
    }
}
