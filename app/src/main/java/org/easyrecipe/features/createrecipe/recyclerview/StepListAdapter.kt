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
import org.easyrecipe.databinding.StepBinding

class StepListAdapter(
    private val onEditStep: (Int) -> Unit,
    private val onDeleteStep: (Int) -> Unit,
) : ListAdapter<Pair<Int, String>, StepListAdapter.StepViewHolder>(
    StepsDiffCallback
) {

    inner class StepViewHolder(
        private val binding: StepBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun render(step: Pair<Int, String>) {
            binding.bind(step)
        }

        private fun StepBinding.bind(step: Pair<Int, String>) {
            txtStepNumber.text = step.first.toString()
            txtStepText.text = step.second
            btnEditStep.setOnClickListener { onEditStep(step.first) }
            btnDeleteStep.setOnClickListener { onDeleteStep(step.first) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepBinding.inflate(inflater, parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
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
