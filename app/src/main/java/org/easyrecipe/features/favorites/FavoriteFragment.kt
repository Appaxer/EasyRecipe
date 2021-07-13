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

package org.easyrecipe.features.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.adapters.RecipeAdapter
import org.easyrecipe.adapters.RecipeTypeManager
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.common.extensions.observeList
import org.easyrecipe.common.extensions.observeText
import org.easyrecipe.common.extensions.observeVisibility
import org.easyrecipe.common.handlers.ScreenStateHandler
import org.easyrecipe.databinding.FragmentFavoriteBinding

@AndroidEntryPoint
class FavoriteFragment : BaseFragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: RecipeAdapter

    override val viewModel: FavoriteViewModel by viewModels()
    override val screenStateHandler = ScreenStateHandler<FavoriteState> { context, state ->
        when (state) {
            is FavoriteState.ShowRecipeDetail -> {
                val action = FavoriteFragmentDirections
                    .actionFavoriteFragmentToRecipeDetail(state.recipe.name, state.recipe)
                navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
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

            viewModel.onGetFavoriteRecipes()
        }
    }

    private fun FragmentFavoriteBinding.bind() {
        txtRecipeSearch.observeText(viewModel.search)

        recipesRecyclerView.adapter = adapter
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesRecyclerView.observeList(viewLifecycleOwner, viewModel.recipesDisplayed)
        layoutRecipesNotFound.observeVisibility(
            viewLifecycleOwner,
            viewModel.isDisplayedRecipeListEmpty
        )
    }

    private fun FavoriteViewModel.setUpObservers() {
        recipesDisplayed.observe(viewLifecycleOwner) {
            (binding.recipesRecyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
        }
    }
}
