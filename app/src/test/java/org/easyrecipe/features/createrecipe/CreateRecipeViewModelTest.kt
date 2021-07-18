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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavDirections
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.easyrecipe.*
import org.easyrecipe.common.CommonException
import org.easyrecipe.common.managers.dialog.DialogManager
import org.easyrecipe.common.managers.navigation.NavManager
import org.easyrecipe.common.usecases.UseCaseResult
import org.easyrecipe.features.createrecipe.navigation.CreateRecipeNavigation
import org.easyrecipe.model.Ingredient
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.usecases.createrecipe.CreateRecipe
import org.easyrecipe.usecases.getallingredients.GetAllIngredients
import org.easyrecipe.usecases.updaterecipe.UpdateRecipe
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateRecipeViewModelTest {
    private lateinit var viewModel: CreateRecipeViewModel

    private val recipeName = "Fish and chips"
    private val recipeDescription = "Delicious"
    private val recipeTime = "10"
    private val recipeTypes = listOf(RecipeType.Hot, RecipeType.Fish)
    private val recipeIngredients = mutableMapOf("Fish" to "1", "Potato" to "2")
    private val recipeSteps = listOf(1 to "First", 2 to "Second")

    private val ingredientName = "Potato"
    private val ingredientQuantity = "1"
    private val step = "First step"

    private val ingredientList = listOf(
        Ingredient("Potato"),
        Ingredient("Fish")
    )

    private val uid = "1"

    private val localRecipe = LocalRecipe(
        name = recipeName,
        description = recipeDescription,
        time = recipeTime.toInt(),
        type = recipeTypes,
        image = ""
    ).also {
        it.setSteps(recipeSteps.unzip().second)
    }

    @MockK
    private lateinit var getAllIngredients: GetAllIngredients

    @MockK
    private lateinit var createRecipe: CreateRecipe

    @MockK
    private lateinit var updateRecipe: UpdateRecipe

    @MockK
    private lateinit var navManager: NavManager

    @MockK
    private lateinit var createRecipeNavigation: CreateRecipeNavigation

    @MockK
    private lateinit var navDirections: NavDirections

    @MockK
    private lateinit var dialogManager: DialogManager

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        getAllIngredients = mockk()
        createRecipe = mockk()
        updateRecipe = mockk()

        navManager = mockk()
        every { navManager.navigate(any(), any()) } returns Unit
        every { navManager.navigateUp(any()) } returns Unit

        navDirections = mockk()
        createRecipeNavigation = mockk()
        every { createRecipeNavigation.navigateToRecipeDetail(any()) } returns navDirections

        dialogManager = mockk()
        every { dialogManager.showLoadingDialog() } returns Unit
        every { dialogManager.cancelLoadingDialog() } returns Unit

        viewModel = CreateRecipeViewModel(
            getAllIngredients,
            createRecipe,
            updateRecipe,
            navManager,
            createRecipeNavigation,
            dialogManager
        )
    }

    @Test
    fun `when ingredient name and quantity are empty then isIngredientInfoFilled is false`() {
        assertThat(viewModel.isIngredientInfoFilled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when ingredient quantity is empty but not name then isIngredientInfoFilled is false`() {
        viewModel.ingredientName.value = ingredientName
        assertThat(viewModel.isIngredientInfoFilled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when ingredient name is empty but not quantity then isIngredientInfoFilled is false`() {
        viewModel.ingredientQuantity.value = ingredientQuantity
        assertThat(viewModel.isIngredientInfoFilled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when ingredient name and quantity are not empty then isIngredientInfoFilled is true`() {
        viewModel.ingredientName.value = ingredientName
        viewModel.ingredientQuantity.value = ingredientQuantity
        assertThat(viewModel.isIngredientInfoFilled.getOrAwaitValueExceptDefault(default = false),
            isTrue())
    }

    @Test
    fun `when step is empty then isCreateRecipeEnabled is false`() {
        assertThat(viewModel.isStepFilled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when step is not empty then isCreateRecipeEnabled is true`() {
        viewModel.step.value = step
        assertThat(viewModel.isStepFilled.getOrAwaitValueExceptDefault(default = false), isTrue())
    }

    @Test
    fun `when all recipe fields are empty then isCreateRecipeEnabled is false`() {
        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when only name is empty then isCreateRecipeEnabled is false`() {
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when only description is empty then isCreateRecipeEnabled is false`() {
        viewModel.name.value = recipeName
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when only time is empty then isCreateRecipeEnabled is false`() {
        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when only types is empty then isCreateRecipeEnabled is false`() {
        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when only ingredients is empty then isCreateRecipeEnabled is false`() {
        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when only steps is empty then isCreateRecipeEnabled is false`() {
        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }

        assertThat(viewModel.isCreateRecipeEnabled.getOrAwaitValue(), isFalse())
    }

    @Test
    fun `when all fields are not empty then isCreateRecipeEnabled is true`() {
        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        assertThat(
            viewModel.isCreateRecipeEnabled.getOrAwaitValueExceptDefault(default = false),
            isTrue()
        )
    }

    @Test
    fun `when ingredient name and quantity are not empty then onAddIngredient makes them empty`() {
        viewModel.ingredientName.value = ingredientName
        viewModel.ingredientQuantity.value = ingredientQuantity

        viewModel.onAddIngredient()

        assertThat(viewModel.ingredientName.getOrAwaitValue(), isEmpty())
        assertThat(viewModel.ingredientQuantity.getOrAwaitValue(), isEmpty())
    }

    @Test
    fun `when ingredient exists and onEditIngredient then we edit the ingredient`() {
        viewModel.ingredientName.value = ingredientName
        viewModel.ingredientQuantity.value = ingredientQuantity
        viewModel.onAddIngredient()

        viewModel.onEditIngredient(ingredientName)

        val editIngredient = viewModel.editIngredient.getOrAwaitValue()
        assertThat(editIngredient.first, isEqualTo(ingredientName))
        assertThat(editIngredient.second, isEqualTo(ingredientQuantity))
    }

    @Test
    fun `when step exists and onEditStep then we edit the step`() {
        viewModel.step.value = step
        viewModel.onAddStep()

        viewModel.stepList.getOrAwaitValue()

        val position = 1
        viewModel.onEditStep(position)

        val editStep = viewModel.editStep.getOrAwaitValue()
        assertThat(editStep.first, isEqualTo(position))
        assertThat(editStep.second, isEqualTo(step))
    }

    @Test
    fun `when getAllIngredients is executed but there is an error the OtherError is shown`() {
        coEvery { getAllIngredients.execute(any()) } returns
            UseCaseResult.Error(CommonException.OtherError("error"))

        viewModel.onGetAllIngredients()

        assertThat(
            viewModel.displayCommonError.getOrAwaitValue(),
            instanceOf(CommonException.OtherError::class.java)
        )
    }

    @Test
    fun `when getAllIngredients is executed and there is no error then ingredients are stored`() {
        coEvery { getAllIngredients.execute(any()) } returns
            UseCaseResult.Success(GetAllIngredients.Response(ingredientList))

        viewModel.onGetAllIngredients()

        assertThat(
            viewModel.predefinedIngredientsNames.getOrAwaitValue(),
            isEqualTo(ingredientList.map { it.name })
        )
    }

    @Test
    fun `when createRecipe is executed but there is an error then OtherError is shown`() {
        coEvery { createRecipe.execute(any()) } returns
            UseCaseResult.Error(CommonException.OtherError("error"))

        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        viewModel.onCreateRecipe(uid)

        assertThat(
            viewModel.displayCommonError.getOrAwaitValue(),
            instanceOf(CommonException.OtherError::class.java)
        )
    }

    @Test
    fun `when createRecipe is executed and there is no error then we navigate up`() {
        coEvery { createRecipe.execute(any()) } returns
            UseCaseResult.Success(CreateRecipe.Response())

        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        viewModel.onCreateRecipe(uid)

        verify {
            navManager.navigateUp(any())
        }
    }

    @Test
    fun `when updateRecipe is executed but there is an error then OtherError is shown`() {
        coEvery { updateRecipe.execute(any()) } returns
            UseCaseResult.Error(CommonException.OtherError("error"))

        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        viewModel.onUpdateRecipe(localRecipe, uid)

        assertThat(
            viewModel.displayCommonError.getOrAwaitValue(),
            instanceOf(CommonException.OtherError::class.java)
        )
    }

    @Test
    fun `when updateRecipe is executed and there is no error then we navigate to RecipeDetail`() {
        coEvery { updateRecipe.execute(any()) } returns
            UseCaseResult.Success(UpdateRecipe.Response(localRecipe))

        viewModel.name.value = recipeName
        viewModel.description.value = recipeDescription
        viewModel.time.value = recipeTime
        recipeTypes.forEach { viewModel.onAddRecipeType(it) }
        recipeIngredients.forEach { (ingredient, quantity) ->
            viewModel.ingredientName.value = ingredient
            viewModel.ingredientQuantity.value = quantity
            viewModel.onAddIngredient()
        }
        recipeSteps.forEach { (_, step) ->
            viewModel.step.value = step
            viewModel.onAddStep()
        }

        viewModel.onUpdateRecipe(localRecipe, uid)

        verify {
            createRecipeNavigation.navigateToRecipeDetail(localRecipe)
            navManager.navigate(any(), navDirections)
        }
    }
}
