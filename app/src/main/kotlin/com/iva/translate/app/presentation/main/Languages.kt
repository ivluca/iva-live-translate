package com.iva.translate.app.presentation.main

val supportedLanguages = listOf(
    "vi" to "Tiếng Việt (Vietnamese)",
    "en" to "English (Tiếng Anh)",
    "de" to "Deutsch (Tiếng Đức)",
    "ja" to "日本語 (Tiếng Nhật)"
)

data class DialectOption(
    val code: String,
    val displayName: String,
    val promptInstruction: String
)

/**
 * Automatic standard accent instructions per target language.
 * Users do not need to manually select a dialect/accent; it is automatically matched to the target language.
 */
val autoAccentInstructions: Map<String, String> = mapOf(
    "vi" to "Speak in a natural, clear, standard Vietnamese accent (giọng chuẩn, rõ ràng, tự nhiên).",
    "en" to "Speak in a natural, clear, standard English accent.",
    "de" to "Speak in a natural, clear, standard High German (Hochdeutsch) accent.",
    "ja" to "Speak in a natural, clear, standard Japanese (Hyōjungo / 標準語) accent."
)

val supportedDialects: Map<String, List<DialectOption>> = emptyMap()
