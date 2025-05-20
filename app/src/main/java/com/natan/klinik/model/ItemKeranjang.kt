package com.natan.klinik.model

data class ItemKeranjang(
    val obat: ProductItem,
    var qty: Int = 1
)