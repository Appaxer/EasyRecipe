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
 * Class to represent the combinations of multiple [LiveData]. Whenever any of them is updated,
 * then the [MultipleCombinedLiveData] is also updated.
 *
 * @param T The type of the [MultipleCombinedLiveData]
 * @property combine The method that produces the combined result
 * @constructor The constructor requires the array of [LiveData] that will be combined and the
 * [combine] method
 *
 * @param liveDataList The array of [LiveData] to be combined
 * @param combine The method to combine the [LiveData]
 */
class MultipleCombinedLiveData<T>(
    vararg liveDataList: LiveData<*>,
    private val combine: () -> T,
) : MediatorLiveData<T>() {

    init {
        liveDataList.forEach { liveData ->
            super.addSource(liveData) {
                value = combine()
            }
        }
    }

    override fun <T : Any?> addSource(source: LiveData<T>, onChanged: Observer<in T>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}
