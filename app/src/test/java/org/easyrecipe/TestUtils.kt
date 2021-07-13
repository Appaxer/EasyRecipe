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

import org.easyrecipe.common.usecases.UseCaseResult
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matcher

fun <T> isEqualTo(value: T): Matcher<T?> = `is`(value)

fun isTrue(): Matcher<Boolean?> = isEqualTo(true)

fun isFalse(): Matcher<Boolean?> = isEqualTo(false)

fun isEmpty(): Matcher<String?> = isEqualTo("")

fun <T> isResultSuccess(): Matcher<T> = instanceOf(UseCaseResult.Success::class.java)

fun <T> isResultError(): Matcher<T> = instanceOf(UseCaseResult.Error::class.java)
