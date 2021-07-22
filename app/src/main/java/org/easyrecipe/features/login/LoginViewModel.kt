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

package org.easyrecipe.features.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.R
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.extensions.combine
import org.easyrecipe.common.extensions.navigate
import org.easyrecipe.common.extensions.requireValue
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.dialog.IntDialog
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.common.validation.FieldValidator
import org.easyrecipe.features.login.navigation.LoginNavigation
import org.easyrecipe.usecases.login.Login
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navManager: NavManager,
    private val loginNavigation: LoginNavigation,
    private val fieldValidator: FieldValidator,
    private val dialogManager: DialogManager,
    private val login: Login,
) : BaseViewModel() {
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val isEmailInvalid = email.map { it.isNotEmpty() && !fieldValidator.isEmailValid(it) }
    val isPasswordInvalid = password.map { it.isNotEmpty() && !fieldValidator.isPasswordValid(it) }
    val isButtonEnabled =
        isEmailInvalid.combine(isPasswordInvalid) { isInvalidEmail, isInvalidPassword ->
            isInvalidEmail != null && isInvalidPassword != null && !isInvalidEmail && !isInvalidPassword && email.requireValue()
                .isNotEmpty() && password.requireValue().isNotEmpty()
        }

    fun onDoLogin() = launch {
        executeUseCase(
            useCase = login,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = {
                Login.Request(email.requireValue(), password.requireValue())
            }
        ).onSuccess { result ->
            val action = loginNavigation.navigateToMainFragment(result.uid)
            navManager.navigate(action)
        }.onError { exception ->
            when (exception) {
                is FirebaseAuthInvalidUserException -> dialogManager.showDialog(IntDialog(
                    title = R.string.wrong_email,
                    message = R.string.wrong_email_explanation
                ))
                is FirebaseAuthInvalidCredentialsException -> dialogManager.showDialog(IntDialog(
                    title = R.string.wrong_password,
                    message = R.string.wrong_password_explanation
                ))
            }
        }
    }
}
