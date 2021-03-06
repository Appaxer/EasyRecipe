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

package org.easyrecipe.common.managers.navigation

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.SharedFlow

abstract class NavManager {
    abstract val action: SharedFlow<NavState>

    /**
     * Navigate within the [navHostFragment] to the destination specified in [action].
     *
     * @param navHostFragment The fragment that contains the navigation
     * @param action The action that indicates the destination
     */
    abstract fun navigate(@IdRes navHostFragment: Int, action: NavDirections)

    /**
     * Navigates up within the [navHostFragment].
     *
     * @param navHostFragment The fragment that contains the navigation
     */
    abstract fun navigateUp(@IdRes navHostFragment: Int)
}
