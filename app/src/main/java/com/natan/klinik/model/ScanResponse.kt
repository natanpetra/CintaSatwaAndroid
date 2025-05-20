package com.natan.klinik.model

data class ScanResponse(
    val success: Boolean,
    val data: List<Scan>
)