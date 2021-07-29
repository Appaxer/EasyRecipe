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

package org.easyrecipe.features.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.adapters.RecipeAdapter
import org.easyrecipe.adapters.RecipeTypeManager
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.common.extensions.*
import org.easyrecipe.databinding.FragmentSearchBinding
import org.easyrecipe.features.main.MainViewModel
import org.easyrecipe.model.MealType
import org.easyrecipe.utils.MealTypeConversion
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: RecipeAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    override val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var mealTypeConversion: MealTypeConversion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.setUpObservers()

        runWithImagePermissions { isGranted ->
            adapter = RecipeAdapter(requireContext(), isGranted, RecipeTypeManager()) { recipe ->
                mainViewModel.comesFromDetail.value = true
                viewModel.onShowRecipeDetail(recipe)
            }
            binding.bind()
            viewModel.setUpObservers()

            mainViewModel.onSearchRecipes()
        }
    }

    private fun FragmentSearchBinding.bind() {
        setUpBasicInformation()
        setUpTypeChips()
        setUpRecyclerView()
    }

    private fun FragmentSearchBinding.setUpBasicInformation() {
        txtRecipeSearch.observeText(viewModel.search)
        txtRecipeSearch.setEndIconOnClickListener {
            viewModel.onSearchRecipes()
        }
        txtRecipeSearch.editText?.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    viewModel.onSearchRecipes()
                    true
                }
                else -> false
            }
        }
    }

    private fun FragmentSearchBinding.setUpTypeChips() {
        val typeChips = MealType.values().getChips()
        typeChips.forEach { mealTypes.addView(it) }
    }

    private fun FragmentSearchBinding.setUpRecyclerView() {
        recipesRecyclerView.adapter = adapter
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesRecyclerView.observeList(viewLifecycleOwner, viewModel.recipeList)
        layoutRecipesNotFound.observeVisibility(
            viewLifecycleOwner,
            viewModel.isDisplayedRecipeListEmpty
        )
    }

    private fun SearchViewModel.setUpObservers() {
        recipeList.observe(viewLifecycleOwner) {
            (binding.recipesRecyclerView.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
        }
        searchRecipeList.observe(viewLifecycleOwner) {
            mainViewModel.searchResultList.value = it
            mainViewModel.searchResultList.notify()
        }
    }

    private fun MainViewModel.setUpObservers() {
        if (comesFromDetail.requireValue() && !searchResultList.value.isNullOrEmpty()) {
            searchResultList.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    viewModel.recipeList.value = it
                    viewModel.recipeList.notify()
                }
            }
            comesFromDetail.value = false
        } else {
            recipeList.observe(viewLifecycleOwner) {
                viewModel.recipeList.value = it
                viewModel.recipeList.notify()
            }
            searchResultList.value = null
        }
    }

    private fun Array<MealType>.getChips(): List<Chip> = sortedBy { it.name.length }
        .map { type ->
            val typeNameId = mealTypeConversion.convertToStringRes(type)
            val typeName = getString(typeNameId)

            Chip(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                isCheckable = true
                text = typeName
            }.also {
                it.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.onAddMealType(type)
                    } else {
                        viewModel.onRemoveMealType(type)
                    }

                    if (viewModel.mealType.requireValue().isNotEmpty()) {
                        viewModel.onSearchRecipes()
                    } else {
                        viewModel.recipeList.value = mainViewModel.recipeList.value
                    }
                }
            }
        }
}
