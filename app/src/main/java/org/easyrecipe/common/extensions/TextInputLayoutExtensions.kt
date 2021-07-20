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
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout
import org.easyrecipe.R

/**
 * Shows an error in the [TextInputLayout].
 *
 * @param errorMsg The error to be displayed
 */
fun TextInputLayout.showError(errorMsg: String?) {
    isErrorEnabled = errorMsg != null
    error = errorMsg
}

/**
 * Shows an error in the [TextInputLayout] if [isError] is true.
 *
 * @param isError It is true if the error has to be shown in the [TextInputLayout]
 * @param errorMsg The error to be displayed
 */
fun TextInputLayout.showErrorIf(isError: Boolean, errorMsg: String) {
    val msg = if (isError) errorMsg else null
    showError(msg)
}

/**
 * Checks whether the [TextInputLayout] is empty.
 */
fun TextInputLayout.isEmpty() = editText?.text?.isEmpty() ?: false

fun TextInputLayout.setText(text: String) {
    editText?.setText(text)
}

/**
 * Observe the text into the LiveData.
 *
 * @param mutableLiveData The live data to store the text
 */
fun TextInputLayout.observeText(mutableLiveData: MutableLiveData<String>) {
    editText?.addTextChangedListener(
        onTextChanged = { text, _, _, _ -> mutableLiveData.value = text.toString() }
    )
}

/**
 * Observe the exposed menu for changes.
 *
 * @param context The [Context] of the application
 * @param lifecycleOwner The lifecycle of the fragment
 * @param liveData The [LiveData] to observe the changes from
 */
fun TextInputLayout.observeExposedMenu(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    liveData: LiveData<List<String>>,
) {
    liveData.observe(lifecycleOwner) { items ->
        val adapter = ArrayAdapter(context, R.layout.list_item, items.toList())
        (editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}

fun TextInputLayout.observeError(
    lifecycleOwner: LifecycleOwner,
    errorMsg: String,
    liveData: LiveData<Boolean>,
) {
    liveData.observe(lifecycleOwner) {
        showErrorIf(it, errorMsg)
    }
}
