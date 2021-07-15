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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class NavManagerImpl : NavManager() {
    private val _action = MutableSharedFlow<NavState>(replay = 1)
    override val action: SharedFlow<NavState>
        get() = _action

    override fun navigate(@IdRes navHostFragment: Int, action: NavDirections) {
        _action.tryEmit(NavState.Navigate(navHostFragment, action))
    }

    override fun navigateUp(@IdRes navHostFragment: Int) {
        _action.tryEmit(NavState.NavigateUp(navHostFragment))
    }
}
