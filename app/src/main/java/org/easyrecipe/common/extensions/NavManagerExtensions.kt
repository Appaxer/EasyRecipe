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

import androidx.navigation.NavDirections
import org.easyrecipe.R
import org.easyrecipe.common.managers.NavManager

private const val NAV_HOST_FRAGMENT = R.id.nav_host_fragment
private const val MAIN_FRAGMENT_NAV_GRAPH = R.id.mainFragmentNavGraph

fun NavManager.navigate(action: NavDirections) {
    navigate(NAV_HOST_FRAGMENT, action)
}

fun NavManager.navigateUp() {
    navigateUp(NAV_HOST_FRAGMENT)
}

fun NavManager.navigateMainFragment(action: NavDirections) {
    navigate(MAIN_FRAGMENT_NAV_GRAPH, action)
}

fun NavManager.navigateUpMainFragment() {
    navigateUp(MAIN_FRAGMENT_NAV_GRAPH)
}
