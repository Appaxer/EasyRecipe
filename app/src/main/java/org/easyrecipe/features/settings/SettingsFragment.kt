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

package org.easyrecipe.features.settings

import android.content.res.Configuration
import android.os.Bundle
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.R
import org.easyrecipe.common.extensions.enableDarkTheme

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<DropDownPreference>(APP_LANGUAGE)?.onPreferenceChangeListener = this
        findPreference<SwitchPreference>(ENABLE_DARK_THEME)?.apply {
            onPreferenceChangeListener = this@SettingsFragment
            isChecked = getUiMode() == Configuration.UI_MODE_NIGHT_YES
        }

        val context = requireContext()
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        findPreference<Preference>(VERSION)?.summary = versionName
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            APP_LANGUAGE -> requireActivity().recreate()
            ENABLE_DARK_THEME -> enableDarkTheme(newValue as Boolean)
        }
        return true
    }

    private fun getUiMode() =
        requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

    companion object {
        const val APP_LANGUAGE = "app_language"
        const val ENABLE_DARK_THEME = "enable_dark_theme"
        const val VERSION = "version"
    }
}
