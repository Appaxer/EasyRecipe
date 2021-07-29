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

package org.easyrecipe.features.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.BuildConfig
import org.easyrecipe.R
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.databinding.FragmentMainBinding

@AndroidEntryPoint
class MainFragment : BaseFragment() {
    private val UID = BuildConfig.USER_UID
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    override val viewModel: MainViewModel by activityViewModels()

    private val args: MainFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bind()

        viewModel.onGetCurrentUser(UID) // This value should be changed for each new user
    }

    private fun FragmentMainBinding.bind() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.mainFragmentNavGraph)
            as NavHostFragment
        navController = navHostFragment.findNavController()
        bottomNavigationView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(
                R.id.recipesFragment,
                R.id.searchFragment,
                R.id.favoriteFragment,
                R.id.settingsFragment
            )
        )

        (requireActivity() as AppCompatActivity).let {
            it.setSupportActionBar(topBar)
            it.setupActionBarWithNavController(navController, appBarConfiguration)
        }

        topBar.setNavigationOnClickListener {
            viewModel.onNavigateUp()
        }
    }
}
