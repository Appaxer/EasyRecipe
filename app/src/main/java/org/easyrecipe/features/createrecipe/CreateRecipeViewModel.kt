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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.easyrecipe.R
import org.easyrecipe.common.BaseViewModel
import org.easyrecipe.common.CombinedLiveData
import org.easyrecipe.common.MultipleCombinedLiveData
import org.easyrecipe.common.ScreenState
import org.easyrecipe.common.extensions.notify
import org.easyrecipe.common.extensions.requireValue
import org.easyrecipe.common.handlers.UseCaseResultHandler
import org.easyrecipe.model.Ingredient
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.usecases.createrecipe.CreateRecipe
import org.easyrecipe.usecases.getallingredients.GetAllIngredients
import org.easyrecipe.usecases.updaterecipe.UpdateRecipe
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val getAllIngredients: GetAllIngredients,
    private val createRecipe: CreateRecipe,
    private val updateRecipe: UpdateRecipe,
) : BaseViewModel() {
    val name = MutableLiveData("")
    val description = MutableLiveData("")
    val time = MutableLiveData("")

    val imageUri = MutableLiveData("")
    val isRecipeImageVisible = imageUri.map { it.isNotEmpty() }
    val isBtnRecipeImageVisible = imageUri.map { it.isEmpty() }

    private val types = MutableLiveData<MutableSet<RecipeType>>(mutableSetOf())

    val ingredientName = MutableLiveData("")
    val ingredientQuantity = MutableLiveData("")
    private val predefinedIngredients = MutableLiveData<List<Ingredient>>()
    val predefinedIngredientsNames = predefinedIngredients.map { ingredients ->
        ingredients.map { it.name.capitalize(Locale.getDefault()) }
    }
    val isIngredientInfoFilled =
        CombinedLiveData(ingredientName, ingredientQuantity) { name, quantity ->
            !name.isNullOrEmpty() && !quantity.isNullOrEmpty()
        }

    private val ingredientBeingEdited = MutableLiveData("")

    private val ingredients = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val ingredientsList = ingredients.map { ingredientsMap ->
        ingredientsMap.entries.toList().map { it.key to it.value }
    }
    val addIngredientIconResource =
        CombinedLiveData(ingredientName, ingredients) { name, ingredients ->
            if (name != null && ingredients != null && ingredients.containsKey(name)) {
                R.drawable.ic_update
            } else {
                R.drawable.ic_add
            }
        }

    val step = MutableLiveData("")
    private val currentPosition = MutableLiveData<Int?>(null)
    private val _stepList = MutableLiveData<MutableList<String>>(mutableListOf())
    val stepList = _stepList.map { list ->
        list.indices.map { it + 1 to list[it] }
    }
    val isStepFilled = step.map { it.isNotEmpty() }
    val addStepIconResource = currentPosition.map {
        if (it == null) R.drawable.ic_add else R.drawable.ic_update
    }

    val isCreateRecipeEnabled = MultipleCombinedLiveData(
        name, description, time, types, ingredients, stepList
    ) {
        !name.value.isNullOrEmpty() && !description.value.isNullOrEmpty()
            && !time.value.isNullOrEmpty() && !types.value.isNullOrEmpty()
            && !ingredients.value.isNullOrEmpty() && !stepList.value.isNullOrEmpty()
    }

    private val getAllIngredientsHandler = UseCaseResultHandler<GetAllIngredients.Response>(
        onSuccess = {
            predefinedIngredients.value = it.ingredients
            ScreenState.Nothing
        },
        onError = { ScreenState.Nothing }
    )

    private val createRecipeHandler = UseCaseResultHandler<CreateRecipe.Response>(
        onSuccess = { CreateRecipeState.RecipeCreated },
        onError = { ScreenState.Nothing }
    )

    private val updateRecipeHandler = UseCaseResultHandler<UpdateRecipe.Response>(
        onSuccess = { CreateRecipeState.RecipeUpdated(it.recipe) },
        onError = { ScreenState.Nothing }
    )

    fun onAddRecipeType(recipeType: RecipeType) {
        types.value?.add(recipeType)
        types.notify()
    }

    fun onRemoveRecipeType(recipeType: RecipeType) {
        types.value?.remove(recipeType)
        types.notify()
    }

    fun onAddIngredient() {
        val name = ingredientName.value
        val quantity = ingredientQuantity.value
        if (!name.isNullOrEmpty() && !quantity.isNullOrEmpty()) {
            if (!ingredientBeingEdited.value.isNullOrEmpty()) {
                ingredients.value?.remove(ingredientBeingEdited.value)
                ingredientBeingEdited.value = ""
            }

            ingredients.value?.put(name, quantity)
            ingredients.notify()

            ingredientName.value = ""
            ingredientQuantity.value = ""
        }
    }

    fun onRemoveIngredient(ingredientName: String) {
        ingredients.value?.remove(ingredientName)
        ingredients.notify()
    }

    fun onEditIngredient(ingredientName: String) {
        val quantity = ingredients.value?.getOrDefault(ingredientName, "")
        if (!quantity.isNullOrEmpty()) {
            ingredientBeingEdited.value = ingredientName
            loadState(CreateRecipeState.EditIngredient(ingredientName, quantity))
        }
    }

    fun onAddStep() {
        val currentStep = step.value
        val position = currentPosition.value
        if (!currentStep.isNullOrEmpty()) {
            if (position == null) {
                _stepList.value?.add(currentStep)
            } else {
                _stepList.value?.removeAt(position)
                _stepList.value?.add(position, currentStep)
                currentPosition.value = null
            }

            _stepList.notify()
        }
    }

    fun onDeleteStep(position: Int) {
        if (position - 1 >= 0) {
            _stepList.value?.removeAt(position - 1)
            _stepList.notify()
        }
    }

    fun onEditStep(position: Int) {
        val steps = stepList.value
        if (position - 1 >= 0 && steps != null) {
            currentPosition.value = position - 1
            loadState(CreateRecipeState.EditStep(position, steps[position - 1].second))
        }
    }

    fun onGetAllIngredients() {
        viewModelScope.launch {
            executeUseCase(getAllIngredients, getAllIngredientsHandler, false) {
                GetAllIngredients.Request()
            }
        }
    }

    fun onCreateRecipe() {
        viewModelScope.launch {
            executeUseCase(createRecipe, createRecipeHandler) {
                CreateRecipe.Request(
                    name = name.requireValue(),
                    description = description.requireValue(),
                    time = time.requireValue().toInt(),
                    types = types.requireValue().toList(),
                    ingredients = ingredients.requireValue(),
                    stepList = _stepList.requireValue(),
                    imageUri = imageUri.requireValue()
                )
            }
        }
    }

    fun onUpdateRecipe(recipe: LocalRecipe) {
        viewModelScope.launch {
            executeUseCase(updateRecipe, updateRecipeHandler) {
                UpdateRecipe.Request(
                    recipe = recipe,
                    name = name.requireValue(),
                    description = description.requireValue(),
                    time = time.requireValue().toInt(),
                    type = types.requireValue().toList(),
                    ingredients = ingredients.requireValue(),
                    stepList = _stepList.requireValue(),
                    imageUri = imageUri.requireValue()
                )
            }
        }
    }
}
