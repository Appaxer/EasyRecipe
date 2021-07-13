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

package org.easyrecipe.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * Class to represent a combination of two [LiveData]. Whenever any [LiveData] is updated, then
 * the [CombinedLiveData] is also updated.
 *
 * @param T The type of the first [LiveData]
 * @param K The type of the second [LiveData]
 * @param S The type of the [CombinedLiveData]
 * @property combine The method that produces the combined result.
 * @constructor The constructor requires both [LiveData] and the [combine] method.
 *
 * @param first The first [LiveData]
 * @param second The second [LiveData]
 * @param combine The method to combine the [LiveData]
 */
class CombinedLiveData<T, K, S>(
    first: LiveData<T>,
    second: LiveData<K>,
    private val combine: (T?, K?) -> S,
) : MediatorLiveData<S>() {

    private var firstData: T? = null
    private var secondData: K? = null

    init {
        super.addSource(first) {
            firstData = it
            value = combine(firstData, secondData)
        }

        super.addSource(second) {
            secondData = it
            value = combine(firstData, secondData)
        }
    }

    override fun <T : Any?> addSource(source: LiveData<T>, onChanged: Observer<in T>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}
