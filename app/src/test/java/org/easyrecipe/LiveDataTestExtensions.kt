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

package org.easyrecipe

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.easyrecipe.common.ScreenState
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 5,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {},
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun LiveData<ScreenState>.getAfterLoading(
    time: Long = 5,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
): ScreenState {
    var data: ScreenState? = null
    val latch = CountDownLatch(1)
    val observer: Observer<ScreenState> = object : Observer<ScreenState> {
        override fun onChanged(value: ScreenState?) {
            if (value !is ScreenState.Loading && value !is ScreenState.ExecutingUseCase) {
                data = value
                latch.countDown()
                this@getAfterLoading.removeObserver(this)
            }
        }
    }

    this.observeForever(observer)

    try {
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set or it always was Loading")
        }
    } finally {
        this.removeObserver(observer)
    }

    return data!!
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValueExceptDefault(
    time: Long = 5,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    default: T? = null,
    afterObserve: () -> Unit = {},
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            if (o != null && o != default) {
                data = o
                latch.countDown()
                this@getOrAwaitValueExceptDefault.removeObserver(this)
            }
        }
    }

    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}
