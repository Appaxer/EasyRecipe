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

package org.easyrecipe.common

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import org.easyrecipe.R
import org.easyrecipe.common.extensions.observeScreenState
import org.easyrecipe.common.extensions.showIntDialog
import org.easyrecipe.common.handlers.ScreenStateHandler
import org.easyrecipe.common.managers.dialog.IntDialog

/**
 * Class from which all fragments must extend from. It is a subclass of [Fragment] but it adds a
 * [BaseViewModel] to implement the logic and a [ScreenStateHandler] to handle the events.
 */
abstract class BaseFragment : Fragment() {
    abstract val viewModel: BaseViewModel

    @Deprecated("The use of states is deprecated, you should use managers instead")
    open val screenStateHandler: ScreenStateHandler<*>? = null

    protected val imagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private var menuRes: Int? = null
    private var setFavIcon: Boolean = false
    private var favIconRes: Int? = null
    private var onIconSelected: (MenuItem) -> Boolean = { false }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        screenStateHandler?.context = requireContext()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenStateHandler?.let { screenStateHandler ->
            viewModel.screenState.observeScreenState(viewLifecycleOwner, screenStateHandler)
        }

        viewModel.displayCommonError.observe(viewLifecycleOwner) { exception ->
            exception?.let { currentException ->
                val (title, message) = when (currentException) {
                    CommonException.NoInternetException ->
                        R.string.no_internet_title to R.string.no_internet_message
                    else ->
                        R.string.other_error_title to R.string.other_error_message
                }

                requireContext().showIntDialog(IntDialog(title, message))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuRes?.let { menuResource ->
            inflater.inflate(menuResource, menu)
        }

        if (setFavIcon && favIconRes != null) {
            menu.findItem(favIconRes!!).setIcon(R.drawable.ic_favourite)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onIconSelected(item) || super.onOptionsItemSelected(item)
    }

    /**
     * Navigates to the given directions.
     *
     * @param direction The directions to which the fragment needs to navigate
     * @param navController The [NavController] that will be used to navigate
     */
    @Deprecated("Navigation between fragments should be done from ViewModels using NavManager")
    protected fun navigate(
        direction: NavDirections,
        navController: NavController = findNavController(),
    ) {
        navController.navigate(direction)
        viewModel.onLoadNothing()
    }

    /**
     * Navigates up in the navigation stack.
     *
     * @param navController The [NavController] that will be used to navigate up
     */
    @Deprecated("Navigation between fragments should be done from ViewModels using NavManager")
    protected fun navigateUp(navController: NavController = findNavController()) {
        navController.navigateUp()
        viewModel.onLoadNothing()
    }

    /**
     * Set up the tool bar menu for each [Fragment] that needs it.
     *
     * @param menuRes The menu resource that has to be loaded
     * @param setFavIcon The favorite icon has to be set
     * @param favIconRes The resource of the favorite icon if it is set
     * @param onIconSelected The listener that is called whenever an option is selected
     */
    protected fun setUpToolBarMenu(
        @MenuRes menuRes: Int,
        setFavIcon: Boolean,
        @IdRes favIconRes: Int,
        onIconSelected: (MenuItem) -> Boolean,
    ) {
        this.menuRes = menuRes
        this.setFavIcon = setFavIcon
        this.favIconRes = favIconRes
        this.onIconSelected = onIconSelected

        setHasOptionsMenu(true)
    }

    /**
     * Request for image permissions and run a code.
     *
     * @param action The function to be run as soon as the permission are requests
     */
    protected fun runWithImagePermissions(action: (Boolean) -> Unit) {
        val requestImagePermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val isGranted = result.values.all { it }
            action(isGranted)
        }

        requestImagePermissions.launch(imagePermissions)
    }
}
