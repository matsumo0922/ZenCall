package me.matsumo.zencall.core.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import me.matsumo.zencall.core.model.AppConfig
import me.matsumo.zencall.core.model.AppSetting

val LocalAppSetting = staticCompositionLocalOf {
    AppSetting.DEFAULT
}

val LocalAppConfig = staticCompositionLocalOf<AppConfig> {
    error("No AppConfig provided")
}
