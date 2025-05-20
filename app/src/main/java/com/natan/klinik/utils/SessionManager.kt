package com.natan.klinik.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.natan.klinik.model.ItemKeranjang
import com.natan.klinik.model.ProductItem

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("keranjang", Context.MODE_PRIVATE)

    fun tambahKeKeranjang(obat: ProductItem, qty: Int) {
        val keranjang = getKeranjang().toMutableList()
        val existing = keranjang.find { it.obat.id == obat.id }
        if (existing != null) {
            existing.qty += qty
        } else {
            keranjang.add(ItemKeranjang(obat, qty))
        }
        val json = Gson().toJson(keranjang)
        prefs.edit().putString("data", json).apply()
    }


    fun getKeranjang(): List<ItemKeranjang> {
        val json = prefs.getString("data", "[]")
        val type = object : TypeToken<List<ItemKeranjang>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearKeranjang() {
        prefs.edit().remove("data").apply()
    }
}
