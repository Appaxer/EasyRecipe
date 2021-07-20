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
import org.easyrecipe.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bind()
    }

    private fun FragmentLoginBinding.bind() {
        txtEmail.observeText(viewModel.email)
        txtEmail.observeError(viewLifecycleOwner,
            getString(R.string.invalid_email),
            viewModel.isEmailInvalid)
        txtPassword.observeText(viewModel.password)
        txtPassword.observeError(viewLifecycleOwner,
            getString(R.string.invalid_password),
            viewModel.isPasswordInvalid)
        btnLogin.observeEnable(viewLifecycleOwner, viewModel.isButtonEnabled)
        btnLogin.setOnClickListener {
            viewModel.onDoLogin()
        }
    }
}
