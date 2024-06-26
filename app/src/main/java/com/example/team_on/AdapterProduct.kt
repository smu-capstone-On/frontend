package com.example.team_on

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.ItemViewProductBinding

data class Product(val title: String, val price: String, val createdTime: String,
                   val content: String? = null, val tags: List<String>? = null, val isPreorder: Boolean? = null)

class AdapterProduct(private val products: MutableList<Product>,
                     private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.productName.text = product.title
            binding.productPrice.text = product.price
            binding.productDate.text = product.createdTime

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size


    fun filterList(filteredProducts: List<Product>) {
        val oldSize = products.size
        products.clear()
        notifyItemRangeRemoved(0, oldSize)
        products.addAll(filteredProducts)
        notifyItemRangeInserted(0, filteredProducts.size)
    }

}