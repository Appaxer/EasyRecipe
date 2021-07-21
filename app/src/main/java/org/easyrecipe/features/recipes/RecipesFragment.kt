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

package org.easyrecipe.features.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.adapters.RecipeAdapter
import org.easyrecipe.adapters.RecipeTypeManager
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.common.extensions.observeList
import org.easyrecipe.common.extensions.observeText
import org.easyrecipe.common.extensions.observeVisibility
import org.easyrecipe.databinding.FragmentRecipesBinding
import org.easyrecipe.features.main.MainViewModel

@AndroidEntryPoint
class RecipesFragment : BaseFragment() {
    private lateinit var binding: FragmentRecipesBinding
    private lateinit var adapter: RecipeAdapter

    override val viewModel: RecipesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runWithImagePermissions { isGranted ->
            adapter = RecipeAdapter(requireContext(), isGranted, RecipeTypeManager()) { recipe ->
                viewModel.onShowRecipeDetail(recipe)
            }
            binding.bind()
            viewModel.setUpObservers()
            mainViewModel.setUpObservers()
        }
    }

    private fun FragmentRecipesBinding.bind() {
        txtRecipeSearch.observeText(viewModel.search)

        recipesRecyclerView.adapter = adapter
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesRecyclerView.observeList(viewLifecycleOwner, viewModel.recipesDisplayed)
        layoutRecipesNotFound.observeVisibility(
            viewLifecycleOwner,
            viewModel.isDisplayedRecipeListEmpty
        )

        flBtnCreateRecipe.setOnClickListener {
            viewModel.onCreateRecipe()
        }
    }

    private fun RecipesViewModel.setUpObservers() {
        recipesDisplayed.observe(viewLifecycleOwner) {
            (binding.recipesRecyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
        }
    }

    private fun MainViewModel.setUpObservers() {
        user.observe { user ->
            viewModel.onGetAllRecipes()
        }
    }
}
