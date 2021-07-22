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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.R
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.common.extensions.observeEnable
import org.easyrecipe.common.extensions.observeError
import org.easyrecipe.common.extensions.observeText
import org.easyrecipe.databinding.FragmentSignupBinding

@AndroidEntryPoint
class SignupFragment : BaseFragment() {
    private lateinit var binding: FragmentSignupBinding
    override val viewModel: SignupViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bind()
    }

    private fun FragmentSignupBinding.bind() {
        txtEmail.observeText(viewModel.email)
        txtPassword.observeText(viewModel.password)
        txtPasswordRepeat.observeText(viewModel.repeatPassword)
        txtEmail.observeError(
            viewLifecycleOwner,
            getString(R.string.invalid_email),
            viewModel.isEmailInvalid,
        )
        txtPassword.observeError(
            viewLifecycleOwner,
            getString(R.string.invalid_password),
            viewModel.isPasswordInvalid,
        )
        txtPasswordRepeat.observeError(
            viewLifecycleOwner,
            getString(R.string.password_repeat_different),
            viewModel.isRepeatPasswordDifferent
        )
        btnSignup.observeEnable(
            viewLifecycleOwner,
            viewModel.isButtonEnabled
        )
        btnSignup.setOnClickListener {
            viewModel.onDoSignup()
        }
    }
}
