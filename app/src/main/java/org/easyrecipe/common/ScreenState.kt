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

/**
 * Screen state of the fragments of the application. Each one is equivalent to an event that
 * takes place in the fragment.
 */
abstract class ScreenState {
    object Loading : ScreenState()
    object NoInternet : ScreenState()
    object OtherError : ScreenState()
    object ExecutingUseCase : ScreenState()
    object Nothing : ScreenState()
}
