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

package org.easyrecipe.features.recipedetail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.R
import org.easyrecipe.adapters.IconManager
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.common.OpenUrlContract
import org.easyrecipe.common.extensions.dpToPixels
import org.easyrecipe.common.handlers.ScreenStateHandler
import org.easyrecipe.databinding.FragmentRecipeDetailBinding
import org.easyrecipe.features.recipedetail.recyclerview.IngredientDetailListAdapter
import org.easyrecipe.features.recipedetail.recyclerview.StepDetailListAdapter
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe
import javax.inject.Inject

@AndroidEntryPoint
class RecipeDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentRecipeDetailBinding
    private lateinit var ingredientDetailListAdapter: IngredientDetailListAdapter
    private lateinit var stepDetailListAdapter: StepDetailListAdapter
    private lateinit var checkStepsLauncher: ActivityResultLauncher<String>

    private val args: RecipeDetailFragmentArgs by navArgs()

    @Inject
    lateinit var iconManager: IconManager<RecipeType>

    override val viewModel: RecipeDetailViewModel by viewModels()
    override val screenStateHandler = ScreenStateHandler<RecipeDetailState> { context, state ->
        when (state) {
            RecipeDetailState.RecipeDeleted -> navigateUp()
            is RecipeDetailState.EditLocalRecipe -> {
                val screenTitle = getString(R.string.editing_recipe, args.recipeName)
                val action = RecipeDetailFragmentDirections
                    .actionRecipeDetailToCreateRecipeFragment(
                        title = screenTitle,
                        isEditing = true,
                        recipe = state.localRecipe
                    )
                navigate(action)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bind()

        checkStepsLauncher = registerForActivityResult(OpenUrlContract()) {}
        setUpLocalRecipeToolBarMenu()
        setUpRemoteRecipeToolBarMenu()
    }

    private fun FragmentRecipeDetailBinding.bind() {
        val recipe = args.recipe

        if (recipe.imageLocation.isNotEmpty()) {
            val uri = Uri.parse(recipe.imageLocation)
            Glide.with(requireContext()).load(uri).into(recipeDetailImage)
        }

        val typeIcons = recipe.type.getImageTypes()

        typeDetailList.removeAllViews()
        typeIcons.forEach { icon ->
            typeDetailList.addView(icon)
        }

        typeDetailList.bringToFront()

        recipeDetailTime.text = getString(R.string.recipe_detail_time, recipe.time)

        ingredientDetailListAdapter = IngredientDetailListAdapter()
        ingredientDetailList.adapter = ingredientDetailListAdapter
        ingredientDetailList.layoutManager = LinearLayoutManager(requireContext())

        stepDetailListAdapter = StepDetailListAdapter()
        stepsDetailList.adapter = stepDetailListAdapter
        stepsDetailList.layoutManager = LinearLayoutManager(requireContext())

        if (recipe is LocalRecipe) {
            setUpLocalRecipe(recipe)
        } else {
            setUpRemoteRecipe(recipe as RemoteRecipe)
        }
    }

    private fun setUpLocalRecipeToolBarMenu() {
        (args.recipe as? LocalRecipe)?.let { localRecipe ->
            setUpToolBarMenu(
                R.menu.recipe_detail_top_bar_menu,
                localRecipe.favorite,
                R.id.favoriteRecipe
            ) { item ->
                when (item.itemId) {
                    R.id.favoriteRecipe -> {
                        viewModel.onFavoriteLocalRecipe(localRecipe.recipeId, localRecipe.favorite)
                        when (localRecipe.favorite) {
                            true -> item.setIcon(R.drawable.ic_not_favorite)
                            false -> item.setIcon(R.drawable.ic_favourite)
                        }
                        localRecipe.toggleFavorite()
                        true
                    }
                    R.id.deleteRecipe -> {
                        viewModel.onDeleteRecipe(localRecipe.recipeId)
                        true
                    }
                    R.id.editRecipe -> {
                        viewModel.onEditRecipe(localRecipe)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setUpRemoteRecipeToolBarMenu() {
        (args.recipe as? RemoteRecipe)?.let { remoteRecipe ->
            setUpToolBarMenu(
                R.menu.remote_recipe_detail_top_bar_menu,
                remoteRecipe.favorite,
                R.id.favorite
            ) { item ->
                when (item.itemId) {
                    R.id.favorite -> {
                        viewModel.onFavoriteRecipe(remoteRecipe.recipeId, remoteRecipe.favorite)
                        when (remoteRecipe.favorite) {
                            true -> item.setIcon(R.drawable.ic_not_favorite)
                            false -> item.setIcon(R.drawable.ic_favourite)
                        }
                        remoteRecipe.toggleFavorite()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun List<RecipeType>.getImageTypes() = map { type ->
        val drawableId = iconManager.getIcon(type)
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
        ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
            val passingSizeInPixels = requireContext().dpToPixels(1)
            setPadding(passingSizeInPixels, 0, passingSizeInPixels, 0)
            setImageDrawable(drawable)
        }
    }

    private fun FragmentRecipeDetailBinding.setUpLocalRecipe(localRecipe: LocalRecipe) {
        recipeDetailDescription.text = localRecipe.description
        edamamImage.visibility = View.GONE

        val ingredientList = localRecipe.ingredients.map { (ingredient, quantity) ->
            "${ingredient.name.replaceFirstChar { it.uppercase() }}: $quantity"
        }
        ingredientDetailListAdapter.submitList(ingredientList)

        val steps = localRecipe.steps.withIndex().map { indexedValue ->
            indexedValue.index + 1 to indexedValue.value
        }
        stepDetailListAdapter.submitList(steps)
    }

    private fun FragmentRecipeDetailBinding.setUpRemoteRecipe(remoteRecipe: RemoteRecipe) {
        recipeDetailDescription.visibility = View.GONE
        ingredientDetailListAdapter.submitList(remoteRecipe.ingredients)

        btnCheckSteps.visibility = View.VISIBLE
        btnCheckSteps.setOnClickListener {
            checkStepsLauncher.launch(remoteRecipe.url)
        }
    }
}
