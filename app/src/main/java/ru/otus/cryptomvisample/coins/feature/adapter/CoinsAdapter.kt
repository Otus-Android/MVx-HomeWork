package ru.otus.cryptomvisample.coins.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cryptomvisample.coins.feature.CoinCategoryState
import ru.otus.cryptomvisample.coins.feature.CoinState
import ru.otus.cryptomvisample.databinding.ItemCategoryHeaderBinding
import ru.otus.cryptomvisample.databinding.ItemCoinBinding
import ru.otus.cryptomvisample.databinding.ItemHorizontalCoinListBinding

class CoinsAdapter : ListAdapter<CoinsAdapterItem, RecyclerView.ViewHolder>(CoinsAdapterDiffCallback()) {

    private class CoinsAdapterDiffCallback : DiffUtil.ItemCallback<CoinsAdapterItem>() {
        override fun areItemsTheSame(oldItem: CoinsAdapterItem, newItem: CoinsAdapterItem): Boolean {
            return when {
                oldItem is CoinsAdapterItem.CategoryHeader && newItem is CoinsAdapterItem.CategoryHeader ->
                    oldItem.categoryName == newItem.categoryName
                oldItem is CoinsAdapterItem.CoinItem && newItem is CoinsAdapterItem.CoinItem ->
                    oldItem.coin.id == newItem.coin.id
                oldItem is CoinsAdapterItem.HorizontalCoinList && newItem is CoinsAdapterItem.HorizontalCoinList ->
                    oldItem.coins.map { it.id } == newItem.coins.map { it.id }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CoinsAdapterItem, newItem: CoinsAdapterItem): Boolean {
            return oldItem == newItem
        }
    }

    private class CoinStateDiffCallback : DiffUtil.ItemCallback<CoinState>() {
        override fun areItemsTheSame(oldItem: CoinState, newItem: CoinState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CoinState, newItem: CoinState): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        const val VIEW_TYPE_CATEGORY = 0
        const val VIEW_TYPE_COIN = 1
        const val VIEW_TYPE_HORIZONTAL_COIN_LIST = 2
        const val COINS_THRESHOLD = 10
    }

    fun setData(categories: List<CoinCategoryState>) {
        val adapterItems = mutableListOf<CoinsAdapterItem>()

        categories.forEach { category ->
            adapterItems.add(CoinsAdapterItem.CategoryHeader(category.name))
            if (category.coins.size >= COINS_THRESHOLD) {
                adapterItems.add(CoinsAdapterItem.HorizontalCoinList(category.coins))
            } else {
                category.coins.forEach { coin ->
                    adapterItems.add(CoinsAdapterItem.CoinItem(coin))
                }
            }
        }
        submitList(adapterItems)
    }

    override fun getItemCount(): Int = currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is CoinsAdapterItem.CategoryHeader -> VIEW_TYPE_CATEGORY
            is CoinsAdapterItem.CoinItem -> VIEW_TYPE_COIN
            is CoinsAdapterItem.HorizontalCoinList -> VIEW_TYPE_HORIZONTAL_COIN_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> CategoryHeaderViewHolder(
                ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_COIN -> {
                val displayMetrics = parent.context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                val halfScreenWidth = (screenWidth / 2)
                CoinViewHolder(
                    ItemCoinBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                )
            }
            VIEW_TYPE_HORIZONTAL_COIN_LIST -> HorizontalCoinListViewHolder(
                ItemHorizontalCoinListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = currentList[position]) {
            is CoinsAdapterItem.CategoryHeader -> {
                (holder as CategoryHeaderViewHolder).bind(item.categoryName)
            }
            is CoinsAdapterItem.CoinItem -> {
                (holder as CoinViewHolder).bind(item.coin)
            }
            is CoinsAdapterItem.HorizontalCoinList -> {
                (holder as HorizontalCoinListViewHolder).bind(item.coins)
            }
        }
    }

    // Nested Adapter for horizontal RecyclerView
    class HorizontalCoinsAdapter : ListAdapter<CoinState, CoinViewHolder>(CoinStateDiffCallback()) {

        fun setData(coins: List<CoinState>) {
            submitList(coins)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
            val displayMetrics = parent.context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val halfScreenWidth = (0.8 * screenWidth / 2).toInt()
            return CoinViewHolder(
                ItemCoinBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                halfScreenWidth
            )
        }

        override fun getItemCount(): Int = currentList.size

        override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
            holder.bind(currentList[position])
        }
    }

    // ViewHolder for the horizontal RecyclerView
    class HorizontalCoinListViewHolder(
        private val binding: ItemHorizontalCoinListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val horizontalCoinsAdapter = HorizontalCoinsAdapter()

        init {
            binding.horizontalRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = horizontalCoinsAdapter
            }
        }

        fun bind(coins: List<CoinState>) {
            horizontalCoinsAdapter.setData(coins)
        }
    }
}
