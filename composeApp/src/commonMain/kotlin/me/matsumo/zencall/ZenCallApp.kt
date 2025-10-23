package me.matsumo.zencall

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import me.matsumo.zencall.core.model.AppSetting
import me.matsumo.zencall.core.ui.theme.ZenCallTheme

@Composable
internal fun ZenCallApp(
    setting: AppSetting,
    modifier: Modifier = Modifier,
) {
    SetupCoil()

    ZenCallTheme(setting) {
        AppNavHost(modifier)
    }
}

@Composable
private fun SetupCoil() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
            }
            .build()
    }
}
