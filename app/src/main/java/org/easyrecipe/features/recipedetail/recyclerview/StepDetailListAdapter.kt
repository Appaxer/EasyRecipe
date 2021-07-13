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
import org.easyrecipe.databinding.StepDetailBinding

class StepDetailListAdapter :
    ListAdapter<Pair<Int, String>, StepDetailListAdapter.StepDetailViewHolder>(
        StepsDiffCallback
    ) {

    inner class StepDetailViewHolder(
        private val binding: StepDetailBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun render(step: Pair<Int, String>) {
            binding.bind(step)
        }

        private fun StepDetailBinding.bind(step: Pair<Int, String>) {
            txtStepNumber.text = step.first.toString()
            txtStepText.text = step.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepDetailBinding.inflate(inflater, parent, false)
        return StepDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepDetailViewHolder, position: Int) {
        holder.render(getItem(position))
    }

    object StepsDiffCallback : DiffUtil.ItemCallback<Pair<Int, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Int, String>,
            newItem: Pair<Int, String>,
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Pair<Int, String>,
            newItem: Pair<Int, String>,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
