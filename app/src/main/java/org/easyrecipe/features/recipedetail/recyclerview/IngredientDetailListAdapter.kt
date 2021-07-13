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

package org.easyrecipe.features.recipedetail.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.easyrecipe.databinding.IngredientDetailBinding

class IngredientDetailListAdapter :
    ListAdapter<String, IngredientDetailListAdapter.IngredientDetailViewHolder>(
        IngredientDetailDiffCallback
    ) {

    inner class IngredientDetailViewHolder(
        private val binding: IngredientDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun render(ingredient: String) {
            binding.bind(ingredient)
        }

        fun IngredientDetailBinding.bind(ingredient: String) {
            txtIngredient.text = ingredient
        }
    }

    object IngredientDetailDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = IngredientDetailBinding.inflate(inflater, parent, false)
        return IngredientDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientDetailViewHolder, position: Int) {
        holder.render(getItem(position))
    }
}
