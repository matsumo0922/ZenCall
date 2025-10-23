package me.matsumo.zencall

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import me.matsumo.zencall.core.model.Destination
import me.matsumo.zencall.core.ui.theme.LocalNavController
import me.matsumo.zencall.feature.home.homeScreen
import me.matsumo.zencall.feature.setting.oss.settingLicenseScreen
import me.matsumo.zencall.feature.setting.settingScreen

@Composable
internal fun AppNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destination.Home,
    ) {
        homeScreen()
        settingScreen()
        settingLicenseScreen()
    }
}
