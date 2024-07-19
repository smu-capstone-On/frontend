package com.example.team_on

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.ItemViewProductBinding
import java.util.Date

class AdapterProduct(private val products: MutableList<Retrofit.Product>,
                     private val onItemClick: (Retrofit.Product) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Retrofit.Product) {
            binding.productName.text = product.title
            binding.productPrice.text = product.price.toString() + "원"
            binding.productDate.text = product.createdTime.toString()
            //binding.productImage = product.postImage

            if (product.isPreorder) {
                binding.productPreorder.visibility = View.VISIBLE
            }

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


    fun filterList(filteredProducts: List<Retrofit.Product>) {
        val oldSize = products.size
        products.clear()
        notifyItemRangeRemoved(0, oldSize)
        products.addAll(filteredProducts)
        notifyItemRangeInserted(0, filteredProducts.size)
    }

}