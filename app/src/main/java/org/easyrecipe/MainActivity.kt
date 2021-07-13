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
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.common.extensions.enableDarkTheme
import org.easyrecipe.databinding.ActivityMainBinding
import org.easyrecipe.features.settings.SettingsFragment.Companion.APP_LANGUAGE
import org.easyrecipe.features.settings.SettingsFragment.Companion.ENABLE_DARK_THEME
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
