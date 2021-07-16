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

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.easyrecipe.common.extensions.*
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.dialog.DialogState
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.common.managers.navigation.NavState
import org.easyrecipe.databinding.ActivityMainBinding
import org.easyrecipe.features.settings.SettingsFragment.Companion.APP_LANGUAGE
import org.easyrecipe.features.settings.SettingsFragment.Companion.ENABLE_DARK_THEME
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navManager: NavManager

    @Inject
    lateinit var dialogManager: DialogManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            setUpNavManager()
            setUpDialogManager()
        }
    }

    private fun setUpNavManager() {
        navManager.action.asLiveData().observe(this) { navState ->
            val navController = findNavController(navState.navHostFragment)
            when (navState) {
                is NavState.Navigate -> navController.navigate(navState.action)
                else -> navController.navigateUp()
            }
        }
    }

    private fun setUpDialogManager() {
        dialogManager.dialog.asLiveData().observe(this) { dialogState ->
            when (dialogState) {
                is DialogState.ShowIntDialog -> {
                    showIntDialog(dialogState.data)
                }
                is DialogState.ShowLambdaDialog -> {
                    showLambdaDialog(dialogState.data)
                }
                DialogState.ShowLoadingDialog -> {
                    showLoadingDialog()
                }
                DialogState.CancelLoadingDialog -> {
                    cancelLoadingDialog()
                }
                is DialogState.ShowStringToast -> {
                    toast(dialogState.data, dialogState.duration)
                }
                is DialogState.ShowIntToast -> {
                    toast(dialogState.data, dialogState.duration)
                }
                is DialogState.ShowLambdaToast -> {
                    toast(dialogState.msg(this), dialogState.duration)
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRec = Rect()
                view.getGlobalVisibleRect(outRec)

                if (!outRec.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    view.clearFocus()
                    hideSoftKeyboard(view)
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun attachBaseContext(base: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(base)
        val langCode = getLanguage(sharedPreferences) ?: Locale.getDefault().language
        super.attachBaseContext(base.updateBaseContextLocale(langCode))

        setUpUiMode(sharedPreferences)
    }

    private fun getLanguage(sharedPreferences: SharedPreferences): String? {
        return sharedPreferences.getString(APP_LANGUAGE, null)
    }

    private fun Context.updateBaseContextLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        return createConfigurationContext(configuration)
    }

    private fun setUpUiMode(sharedPreferences: SharedPreferences) {
        if (sharedPreferences.contains(ENABLE_DARK_THEME)) {
            enableDarkTheme(sharedPreferences.getBoolean(ENABLE_DARK_THEME, false))
        }
    }
}
