package me.matsumo.zencall.core.model.call

import androidx.compose.ui.graphics.ImageBitmap

data class ContactInfo(
    val id: Long,
    val displayName: String,
    val phoneNumbers: List<String>,
    val photo: ImageBitmap? = null,
)
