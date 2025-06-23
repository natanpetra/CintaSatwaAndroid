package com.natan.klinik.utils

import com.natan.klinik.model.ProductItem

object CartManager {
    private val cartItems = mutableListOf<ProductItem>()

    fun addToCart(product: ProductItem) {
        val existingItem = cartItems.find { it.id == product.id }
        if (existingItem != null) {
            existingItem.quantity = (existingItem.quantity ?: 0) + (product.quantity ?: 1)
        } else {
            cartItems.add(product.copy(quantity = product.quantity ?: 1))
        }
    }

    fun getCartItems(): List<ProductItem> {
        return cartItems.toList()
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf {
            (it.price?.toDoubleOrNull() ?: 0.0) * (it.quantity ?: 0)
        }
    }

    // ✅ PERBAIKAN: Remove item yang lebih robust
    fun removeItem(productId: Int): Boolean {
        val initialSize = cartItems.size
        cartItems.removeAll { it.id == productId }
        return cartItems.size < initialSize // Return true jika ada item yang dihapus
    }

    // ✅ PERBAIKAN: Update quantity
    fun updateQuantity(productId: Int, newQuantity: Int): Boolean {
        val item = cartItems.find { it.id == productId }
        return if (item != null) {
            item.quantity = if (newQuantity > 0) newQuantity else 1
            true
        } else {
            false
        }
    }

    fun getItemCount(): Int {
        return cartItems.sumOf { it.quantity ?: 0 }
    }

    // ✅ Method untuk debug
    fun printCartItems() {
        cartItems.forEachIndexed { index, item ->
            println("Item $index: ${item.name} - Qty: ${item.quantity} - Price: ${item.price}")
        }
    }
}