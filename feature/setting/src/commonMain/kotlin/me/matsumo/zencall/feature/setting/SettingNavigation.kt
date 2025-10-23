package me.matsumo.zencall.feature.setting

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.matsumo.zencall.core.model.Destination

fun NavGraphBuilder.settingScreen() {
    composable<Destination.Setting.Root> {
        SettingScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}
