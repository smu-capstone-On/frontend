package com.example.team_on

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.ItemViewProductBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterProduct(
    private var products: MutableList<Retrofit.Product>,
    private val onItemClick: (Retrofit.Product) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    private var originalProducts: MutableList<Retrofit.Product> = products.toMutableList()

    inner class ProductViewHolder(private val binding: ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Retrofit.Product) {
            binding.productName.text = product.title
            binding.productPrice.text = product.price.toString() + "원"
            binding.productDate.text = formatPostTime(product.time)
            binding.productImage.setImageResource(0)

            product.imgUrl?.let { url ->
                Glide.with(binding.productImage.context)
                    .load(url.toUri())
                    .error(R.drawable.svg_camera_error)
                    .into(binding.productImage)
            }

            Log.d("AdapterPost", "product: ${product}")

            if (product.reservationStatus) {
                binding.productPreorder.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                onItemClick(product)
            }
        }

        private fun formatPostTime(dateString: String): String {
            val dateTime = LocalDateTime.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd  HH:mm")
            return dateTime.format(formatter)
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
        products = if (filteredProducts.isEmpty()) {
            originalProducts.toMutableList()
        } else {
            filteredProducts.toMutableList()
        }
        notifyDataSetChanged()
    }
}