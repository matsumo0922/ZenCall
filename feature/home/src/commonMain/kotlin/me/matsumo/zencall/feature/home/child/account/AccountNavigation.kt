package me.matsumo.zencall.feature.home.child.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.matsumo.zencall.feature.home.components.HomeDestination

internal fun NavGraphBuilder.accountScreen() {
    composable<HomeDestination.Account> {
        AccountScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}