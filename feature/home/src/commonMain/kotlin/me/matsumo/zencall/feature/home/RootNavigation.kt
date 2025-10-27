package me.matsumo.zencall.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.matsumo.zencall.core.model.Destination

fun NavGraphBuilder.rootScreen() {
    composable<Destination.Root> {
        RootRoute(
            modifier = Modifier.fillMaxSize(),
        )
    }
}
