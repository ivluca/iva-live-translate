package com.alad.app.presentation.main

val supportedLanguages = listOf(
    "vi" to "🇻🇳 Tiếng Việt",
    "en" to "🇺🇸 English",
    "ja" to "🇯🇵 日本語"
)

data class DialectOption(
    val code: String,
    val displayName: String,
    val promptInstruction: String
)

val supportedDialects: Map<String, List<DialectOption>> = mapOf(
    "vi" to listOf(
        DialectOption("vi-VN-N", "🇻🇳 Giọng Miền Bắc (Hà Nội)", "Nói bằng tiếng Việt giọng Bắc (Hà Nội) tự nhiên, rõ ràng, phát âm chuẩn."),
        DialectOption("vi-VN-S", "🇻🇳 Giọng Miền Nam (Sài Gòn)", "Nói bằng tiếng Việt giọng Nam Bộ tự nhiên, thân thiện, truyền cảm.")
    ),
    "en" to listOf(
        DialectOption("en-US", "🇺🇸 American", "Speak in natural General American English pronunciation and vocabulary"),
        DialectOption("en-GB", "🇬🇧 British", "Speak in British English (Received Pronunciation)"),
        DialectOption("en-AU", "🇦🇺 Australian", "Speak in natural Australian English accent and terminology"),
        DialectOption("en-IN", "🇮🇳 Indian", "Speak in clear Indian English accent")
    ),
    "ja" to listOf(
        DialectOption("ja-JP", "🇯🇵 Standard Japanese (標準語)", "Speak in natural standard Japanese (Hyojungo/Tokyo dialect) with polite and natural intonation")
    )
)
