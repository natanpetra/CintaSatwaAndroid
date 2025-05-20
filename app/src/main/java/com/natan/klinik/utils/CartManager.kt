package com.natan.klinik.utils

import com.natan.klinik.model.ProductItem

object CartManager {
    private val cartItems = mutableListOf<ProductItem>()

    fun addToCart(product: ProductItem) {
        val existingItem = cartItems.find { it.id == product.id }
        if (existingItem != null) {
            product.stock = product.stock!! + product.stock!!
        } else {
            cartItems.add(product)
        }
    }

    fun getCartItems(): List<ProductItem> {
        return cartItems.toList()
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double {
        var total = 0.0
        for (item in cartItems) {
            total += item.price!!.toDouble() * item.stock!!.toDouble()
        }
        return total
    }

    fun removeItem(productId: Int) {
        cartItems.removeAll { it.id == productId }
    }
}