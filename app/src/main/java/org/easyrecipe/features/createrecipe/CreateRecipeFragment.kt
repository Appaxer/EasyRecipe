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

package org.easyrecipe.features.createrecipe

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import org.easyrecipe.R
import org.easyrecipe.common.BaseFragment
import org.easyrecipe.common.extensions.*
import org.easyrecipe.databinding.FragmentCreateRecipeBinding
import org.easyrecipe.features.createrecipe.recyclerview.IngredientListAdapter
import org.easyrecipe.features.createrecipe.recyclerview.StepListAdapter
import org.easyrecipe.features.main.MainViewModel
import org.easyrecipe.model.RecipeType
import org.easyrecipe.utils.RecipeTypeConversion
import javax.inject.Inject

@AndroidEntryPoint
class CreateRecipeFragment : BaseFragment() {
    private lateinit var binding: FragmentCreateRecipeBinding
    private lateinit var ingredientListAdapter: IngredientListAdapter
    private lateinit var stepListAdapter: StepListAdapter

    private lateinit var imagePicker: ActivityResultLauncher<String>
    private lateinit var requestPermissions: ActivityResultLauncher<Array<String>>

    private val args: CreateRecipeFragmentArgs by navArgs()

    override val viewModel: CreateRecipeViewModel by viewModels()

    private val mainViewModel: MainViewModel by activityViewModels()

    @Inject
    lateinit var recipeTypeConversion: RecipeTypeConversion

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCreateRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bind()
        viewModel.setUpObservers()

        viewModel.onGetAllIngredients()
    }

    private fun FragmentCreateRecipeBinding.bind() {
        setUpBasicInformation()
        setUpTypeChips()
        setUpIngredients()
        setUpSteps()
        setUpCreateRecipeButton()
        setUpImagePicker()
    }

    private fun FragmentCreateRecipeBinding.setUpBasicInformation() {
        txtRecipeName.observeText(viewModel.name)
        txtRecipeDescription.observeText(viewModel.description)
        txtTime.observeText(viewModel.time)

        args.recipe?.let { localRecipe ->
            txtRecipeName.setText(localRecipe.name)
            txtRecipeDescription.setText(localRecipe.description)
            txtTime.setText(localRecipe.time.toString())
        }
    }

    private fun FragmentCreateRecipeBinding.setUpTypeChips() {
        val typeChips = RecipeType.values().getChips()
        typeChips.forEach { recipeTypes.addView(it) }
    }

    private fun FragmentCreateRecipeBinding.setUpIngredients() {
        txtSearchIngredients.observeExposedMenu(
            requireContext(),
            viewLifecycleOwner,
            viewModel.predefinedIngredientsNames
        )
        txtSearchIngredients.observeText(viewModel.ingredientName)
        txtIngredientQuantity.observeText(viewModel.ingredientQuantity)
        btnAddIngredient.observeEnable(viewLifecycleOwner, viewModel.isIngredientInfoFilled)
        btnAddIngredient.observeIcon(viewLifecycleOwner, viewModel.addIngredientIconResource)
        btnAddIngredient.setOnClickListener {
            addIngredient()
        }
        setUpIngredientsRecycleView()

        args.recipe?.let { localRecipe ->
            localRecipe.ingredients.forEach { (ingredientName, quantity) ->
                txtSearchIngredients.setText(ingredientName.name)
                txtIngredientQuantity.setText(quantity)

                addIngredient()
            }
        }
    }

    private fun FragmentCreateRecipeBinding.addIngredient() {
        viewModel.onAddIngredient()
        txtSearchIngredients.setText("")
        txtIngredientQuantity.setText("")
    }

    private fun FragmentCreateRecipeBinding.setUpIngredientsRecycleView() {
        ingredientListAdapter = IngredientListAdapter(
            onEditIngredient = { ingredientName -> viewModel.onEditIngredient(ingredientName) },
            onDeleteIngredient = { ingredientName -> viewModel.onRemoveIngredient(ingredientName) }
        )

        ingredientsList.adapter = ingredientListAdapter
        ingredientsList.layoutManager = LinearLayoutManager(requireContext())
        ingredientsList.observeList(viewLifecycleOwner, viewModel.ingredientsList)

        addDivider(ingredientsList)
    }

    private fun FragmentCreateRecipeBinding.setUpSteps() {
        txtStep.observeText(viewModel.step)
        btnAddStep.setOnClickListener {
            viewModel.onAddStep()
            txtStep.setText("")
            txtStep.helperText = null
        }
        btnAddStep.observeEnable(viewLifecycleOwner, viewModel.isStepFilled)
        btnAddStep.observeIcon(viewLifecycleOwner, viewModel.addStepIconResource)

        setUpStepsRecycleView()

        args.recipe?.let { localRecipe ->
            localRecipe.steps.forEach { step ->
                txtStep.setText(step)
                addStep()
            }
        }
    }

    private fun FragmentCreateRecipeBinding.addStep() {
        viewModel.onAddStep()
        txtStep.setText("")
        txtStep.helperText = null
    }

    private fun FragmentCreateRecipeBinding.setUpStepsRecycleView() {
        stepListAdapter = StepListAdapter(
            onEditStep = { position -> viewModel.onEditStep(position) },
            onDeleteStep = { position -> viewModel.onDeleteStep(position) }
        )

        stepsList.adapter = stepListAdapter
        stepsList.layoutManager = LinearLayoutManager(requireContext())
        stepsList.observeList(viewLifecycleOwner, viewModel.stepList)

        addDivider(stepsList)
    }

    private fun FragmentCreateRecipeBinding.setUpCreateRecipeButton() {
        btnCreateRecipe.observeEnable(viewLifecycleOwner, viewModel.isCreateRecipeEnabled)

        val createRecipeText = if (args.isEditing) R.string.edit_recipe else R.string.recipe_create
        btnCreateRecipe.text = getString(createRecipeText)

        btnCreateRecipe.setOnClickListener {
            if (args.isEditing) {
                args.recipe?.let { localRecipe ->
                    viewModel.onUpdateRecipe(localRecipe, mainViewModel.user.requireValue().uid)
                }
            } else {
                viewModel.onCreateRecipe(mainViewModel.user.requireValue().uid)
            }
        }
    }

    private fun FragmentCreateRecipeBinding.setUpImagePicker() {
        recipeImage.observeVisibility(viewLifecycleOwner, viewModel.isRecipeImageVisible)
        btnRecipeImage.observeVisibility(viewLifecycleOwner, viewModel.isBtnRecipeImageVisible)
        imagePicker =
            registerForActivityResult(ActivityResultContracts.GetContent()) { selectedUri ->
                selectedUri?.let { uri -> loadRecipeImage(uri) }
            }

        requestPermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                imagePicker.launch("image/*")
            }
        }

        btnRecipeImage.setOnClickListener {
            requestImagePermissions()
        }

        recipeImage.setOnClickListener {
            requestImagePermissions()
        }

        args.recipe?.let { localRecipe ->
            if (localRecipe.imageLocation.isNotEmpty()) {
                val uri = Uri.parse(localRecipe.imageLocation)
                loadRecipeImage(uri)
            }
        }
    }

    private fun FragmentCreateRecipeBinding.loadRecipeImage(uri: Uri) {
        viewModel.imageUri.value = uri.toString()
        Glide.with(requireContext()).load(uri).into(recipeImage)
    }

    private fun CreateRecipeViewModel.setUpObservers() {
        editIngredient.observe(viewLifecycleOwner) { ingredient ->
            with(binding) {
                txtSearchIngredients.editText?.setText(ingredient.first)
                txtIngredientQuantity.editText?.setText(ingredient.second)
            }
        }

        editStep.observe(viewLifecycleOwner) { step ->
            with(binding) {
                txtStep.helperText = getString(
                    R.string.recipe_step_modifying,
                    step.first
                )
                txtStep.editText?.setText(step.second)
            }
        }
    }

    private fun requestImagePermissions() {
        requestPermissions.launch(imagePermissions)
    }

    private fun addDivider(recyclerView: RecyclerView) {
        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(divider)
    }

    private fun Array<RecipeType>.getChips(): List<Chip> = sortedBy { it.name.length }
        .map { type ->
            val typeNameId = recipeTypeConversion.convertToStringRes(type)
            val typeName = getString(typeNameId)

            Chip(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                isCheckable = true
                text = typeName
            }.also { chip ->
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.onAddRecipeType(type)
                    } else {
                        viewModel.onRemoveRecipeType(type)
                    }
                }

                args.recipe?.let { localRecipe ->
                    chip.isChecked = localRecipe.hasType(type)
                }
            }
        }
}
