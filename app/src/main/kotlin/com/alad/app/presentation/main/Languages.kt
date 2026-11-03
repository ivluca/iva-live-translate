package com.alad.app.presentation.main

val supportedLanguages = listOf(
    "vi" to "Tiếng Việt"
)

data class DialectOption(
    val code: String,
    val displayName: String,
    val promptInstruction: String
)

val supportedDialects: Map<String, List<DialectOption>> = mapOf(
    "vi" to listOf(
        DialectOption("vi-VN-N", "Giọng Miền Bắc", "Nói bằng tiếng Việt giọng Bắc (Hà Nội) tự nhiên, rõ ràng, phát âm chuẩn.")
    )
)
