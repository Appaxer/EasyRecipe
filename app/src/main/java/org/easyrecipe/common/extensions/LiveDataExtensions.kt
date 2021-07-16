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
import com.google.android.material.textfield.TextInputLayout
import org.easyrecipe.common.CombinedLiveData
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.handlers.InvalidFieldHandler
import org.easyrecipe.common.handlers.ScreenStateHandler

/**
 * Observes the [LiveData] that contains the [ScreenState] of the fragment. It requires a
 * [ScreenStateHandler] to control its behaviour. This method should be called only inside the
 * ViewModel.observe() extension method, defined in each fragment.
 *
 * @param T The state assigned to the fragment
 * @param lifecycleOwner The [LifecycleOwner] of the fragment
 * @param handler The handler that performs some actions depending on the [ScreenState]
 */
@Deprecated("The use of states is deprecated, you should use managers instead")
fun <T : ScreenState> LiveData<ScreenState>.observeScreenState(
    lifecycleOwner: LifecycleOwner,
    handler: ScreenStateHandler<T>,
) {
    removeObservers(lifecycleOwner)
    observe(lifecycleOwner) {
        handler.executeStateAction(it)
    }
}

/**
 * Observes the [LiveData] that contains the [Boolean] that represents the invalidity of a field
 * from the ui. The field is modified in [textInputLayout] and if there is an error the
 * [errorMessage] is displayed in it. The [checkFunction] is used to verify if the error should
 * continue be displaying. This method should be called only inside the ViewModel.observe()
 * extension method, defined in each fragment.
 *
 * @param lifecycleOwner The [LifecycleOwner] of the fragment
 * @param textInputLayout The [TextInputLayout] where the field is modified
 * @param errorMessage The message that should be displayed in [textInputLayout] if there is an
 * error
 * @param checkFunction The function used to verify if the error should continue being displayed
 */
fun LiveData<Boolean>.observeInvalidField(
    lifecycleOwner: LifecycleOwner,
    textInputLayout: TextInputLayout,
    errorMessage: String,
    checkFunction: (String) -> Unit,
) {
    val handler = InvalidFieldHandler(textInputLayout, checkFunction)
    observe(lifecycleOwner) { isInvalid ->
        handler.isBeingChecked = isInvalid
        textInputLayout.showErrorIf(isInvalid, errorMessage)
    }
}

/**
 * Combines the values of the implicit [LiveData] with the parameter one and updates it whenever
 * any of those are updated.
 *
 * @param T The type of the first [LiveData]
 * @param K The type of the second [LiveData]
 * @param S The type of the resulting [LiveData]
 * @param liveData The second [LiveData]
 * @param combine The method that generates the value of the [CombinedLiveData]
 */
fun <T, K, S> LiveData<T>.combine(liveData: LiveData<K>, combine: (T?, K?) -> S) =
    CombinedLiveData(this, liveData, combine)

/**
 * Get the value of a [LiveData] or throw an exception if it is not set.
 *
 * @param T The type of the [LiveData]
 * @return The value of the [LiveData]
 * @throws Exception The live data value is not set
 */
fun <T> LiveData<T>.requireValue(): T = value ?: throw Exception("Live data value is not set")
