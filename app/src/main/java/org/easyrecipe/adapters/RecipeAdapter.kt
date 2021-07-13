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

package org.easyrecipe.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import org.easyrecipe.common.extensions.dpToPixels
import org.easyrecipe.databinding.RecipeCardBinding
import org.easyrecipe.model.LocalRecipe
import org.easyrecipe.model.Recipe
import org.easyrecipe.model.RecipeType
import org.easyrecipe.model.RemoteRecipe

@Suppress("USELESS_CAST")
class RecipeAdapter(
    private val context: Context,
    private val isImagePermissionGranted: Boolean,
    iconManager: IconManager<RecipeType>,
    private val onRecipeSelected: (Recipe) -> Unit = {},
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeAdapterDiff),
    IconManager<RecipeType> by iconManager {

    inner class RecipeViewHolder(
        private val binding: RecipeCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun render(recipe: Recipe) {
            binding.bind(recipe)
        }

        private fun RecipeCardBinding.bind(recipe: Recipe) {
            recipeName.text = recipe.name
            val icons = recipe.type.getImageTypes()

            typeList.removeAllViews()
            icons.forEach { icon ->
                typeList.addView(icon)
            }

            if (recipe.imageLocation.isNotEmpty()) {
                when (recipe) {
                    is LocalRecipe -> loadFromFile(recipeIcon, recipe.imageLocation)
                    is RemoteRecipe -> loadFromUrl(recipeIcon, recipe.imageLocation)
                }
            }

            root.setOnClickListener { onRecipeSelected(recipe) }
        }

        private fun List<RecipeType>.getImageTypes() = map { type ->
            val drawableId = getIcon(type)
            val drawable = ContextCompat.getDrawable(context, drawableId)
            ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                val passingSizeInPixels = context.dpToPixels(1)
                setPadding(passingSizeInPixels, 0, passingSizeInPixels, 0)
                setImageDrawable(drawable)
            }
        }

        private fun loadFromFile(recipeIcon: ShapeableImageView, path: String) {
            if (isImagePermissionGranted) {
                val uri = Uri.parse(path)
                Glide.with(context).load(uri).into(recipeIcon)
            }
        }

        private fun loadFromUrl(recipeIcon: ShapeableImageView, url: String) {
            Glide.with(context).load(url).into(recipeIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RecipeCardBinding.inflate(layoutInflater, parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.render(getItem(position))
    }

    object RecipeAdapterDiff : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            if (oldItem is LocalRecipe && newItem is LocalRecipe) {
                return oldItem as LocalRecipe == newItem as LocalRecipe
            } else if (oldItem is RemoteRecipe && newItem is RemoteRecipe) {
                return oldItem as RemoteRecipe == newItem as RemoteRecipe
            }
            return false
        }
    }
}
