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

package org.easyrecipe.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.R
import org.easyrecipe.adapters.ViewPagerAdapter
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.databinding.FragmentAuthBinding
import org.easyrecipe.features.login.LoginFragment
import org.easyrecipe.features.signup.SignupFragment

@AndroidEntryPoint
class AuthFragment : BaseFragment() {
    private lateinit var binding: FragmentAuthBinding
    override val viewModel: AuthViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val fragmentsText = listOf(R.string.login_fragment, R.string.signup_fragment)
    private val fragmentsIcon = listOf(R.drawable.ic_login, R.drawable.ic_sign_up)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bind()
    }

    private fun FragmentAuthBinding.bind() {
        viewPagerAdapter =
            ViewPagerAdapter(this@AuthFragment, listOf(LoginFragment(), SignupFragment()))
        authFragmentNavGraph.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, authFragmentNavGraph) { tab, position ->
            tab.setText(fragmentsText[position])
            tab.setIcon(fragmentsIcon[position])
        }.attach()
    }
}
