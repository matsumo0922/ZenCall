package me.matsumo.zencall.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.matsumo.zencall.core.model.AppConfig
import me.matsumo.zencall.core.model.AppSetting
import me.matsumo.zencall.core.model.Theme
import me.matsumo.zencall.core.ui.utils.rememberColorScheme
import org.koin.compose.koinInject

@Suppress("ModifierMissing")
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ZenCallTheme(
    appSetting: AppSetting = AppSetting.DEFAULT,
    navController: NavHostController = rememberNavController(),
    appConfig: AppConfig = koinInject(),
    content: @Composable () -> Unit,
) {
    val colorScheme = rememberColorScheme(
        useDynamicColor = appSetting.useDynamicColor,
        seedColor = appSetting.seedColor,
        isDark = shouldUseDarkTheme(appSetting.theme),
    )

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalAppSetting provides appSetting,
        LocalAppConfig provides appConfig,
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                content = content,
            )
        }
    }
}

@Composable
fun shouldUseDarkTheme(theme: Theme): Boolean {
    return when (theme) {
        Theme.System -> isSystemInDarkTheme()
        Theme.Light -> false
        Theme.Dark -> true
    }
}
