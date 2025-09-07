package ru.otus.cryptomvisample.coins.feature.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptomvisample.databinding.ItemCategoryHeaderBinding

class CategoryHeaderViewHolder(
    private val binding: ItemCategoryHeaderBinding
) : RecyclerView.ViewHolder(binding.root) {
    
    fun bind(categoryName: String) {
        binding.categoryTitle.text = categoryName
    }
}