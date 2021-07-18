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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

/**
 * Contract to open an url when registering an activity for result.
 */
class OpenUrlContract : ActivityResultContract<String, Unit>() {
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {
        // The result of this contract is not used
    }
}
