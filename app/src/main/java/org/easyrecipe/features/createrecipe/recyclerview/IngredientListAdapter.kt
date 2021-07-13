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

package org.easyrecipe.features.createrecipe.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.easyrecipe.databinding.IngredientBinding
import java.util.*

class IngredientListAdapter(
    private val onEditIngredient: (String) -> Unit,
    private val onDeleteIngredient: (String) -> Unit,
) : ListAdapter<Pair<String, String>, IngredientListAdapter.IngredientViewHolder>(
    IngredientDiffCallback
) {

    inner class IngredientViewHolder(
        private val binding: IngredientBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun render(ingredientQuantity: Pair<String, String>) {
            binding.bind(ingredientQuantity)
        }

        private fun IngredientBinding.bind(ingredientQuantity: Pair<String, String>) {
            txtIngredientName.text = ingredientQuantity.first.capitalize(Locale.getDefault())
            txtIngredientQuantity.text = ingredientQuantity.second
            btnEditIngredient.setOnClickListener {
                onEditIngredient(ingredientQuantity.first)
            }
            btnDeleteIngredient.setOnClickListener {
                onDeleteIngredient(ingredientQuantity.first)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = IngredientBinding.inflate(inflater, parent, false)
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.render(getItem(position))
    }

    object IngredientDiffCallback : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
