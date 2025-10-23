package me.matsumo.zencall.core.ui.utils

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.rememberDynamicColorScheme

@Composable
actual fun rememberColorScheme(
    useDynamicColor: Boolean,
    seedColor: Color,
    isDark: Boolean,
): ColorScheme {
    val context = LocalContext.current

    return if (useDynamicColor && isSupportDynamicColor) {
        if (isDark) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        rememberDynamicColorScheme(
            seedColor = seedColor,
            isDark = isDark,
        )
    }
}

actual val isSupportDynamicColor: Boolean = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
