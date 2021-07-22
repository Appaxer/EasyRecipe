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

package org.easyrecipe.features.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import org.easyrecipe.R
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.MultipleCombinedLiveData
import org.easyrecipe.common.extensions.navigate
import org.easyrecipe.common.extensions.requireValue
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.dialog.IntDialog
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.common.validation.FieldValidator
import org.easyrecipe.features.signup.navigation.SignupNavigation
import org.easyrecipe.usecases.signup.Signup
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val navManager: NavManager,
    private val signupNavigation: SignupNavigation,
    private val fieldValidator: FieldValidator,
    private val dialogManager: DialogManager,
    private val signup: Signup,
) : BaseViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val repeatPassword = MutableLiveData("")
    val isEmailInvalid = email.map { it.isNotEmpty() && !fieldValidator.isEmailValid(it) }
    val isPasswordInvalid = password.map { it.isNotEmpty() && !fieldValidator.isPasswordValid(it) }
    val isRepeatPasswordDifferent =
        repeatPassword.map {
            it.isNotEmpty() && password.requireValue().isNotEmpty() && it != password.requireValue()
        }
    val isButtonEnabled = MultipleCombinedLiveData(isEmailInvalid,
        isPasswordInvalid,
        isRepeatPasswordDifferent) {
        !isEmailInvalid.requireValue() && !isPasswordInvalid.requireValue() && !isRepeatPasswordDifferent.requireValue()
    }

    fun onDoSignup() = launch {
        executeUseCase(
            useCase = signup,
            onBefore = { dialogManager.showLoadingDialog() },
            onAfter = { dialogManager.cancelLoadingDialog() },
            onPrepareInput = {
                Signup.Request(email.requireValue(), password.requireValue())
            }
        ).onSuccess { result ->
            val action = signupNavigation.navigateToMainFragment(result.uid)
            navManager.navigate(action)
        }.onError { exception ->
            if (exception is FirebaseAuthUserCollisionException) {
                dialogManager.showDialog(IntDialog(
                    title = R.string.repeated_email,
                    message = R.string.repeated_email_explanation,
                ))
            }
        }
    }
}
