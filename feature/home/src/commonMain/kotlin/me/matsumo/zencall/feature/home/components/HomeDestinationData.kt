package me.matsumo.zencall.feature.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import me.matsumo.zencall.core.resource.Res
import me.matsumo.zencall.core.resource.home_menu_account
import me.matsumo.zencall.core.resource.home_menu_home
import org.jetbrains.compose.resources.StringResource
import kotlin.reflect.KClass

@Serializable
sealed interface HomeDestination {
    @Serializable
    data object Home : HomeDestination

    @Serializable
    data object Account : HomeDestination
}

enum class HomeDestinationData(
    val selectedIcon: ImageVector,
    val deselectedIcon: ImageVector,
    val title: StringResource,
    val route: KClass<out HomeDestination>,
) {
    Home(
        selectedIcon = Icons.Default.Home,
        deselectedIcon = Icons.Outlined.Home,
        title = Res.string.home_menu_home,
        route = HomeDestination.Home::class,
    ),
    Account(
        selectedIcon = Icons.Default.AccountCircle,
        deselectedIcon = Icons.Outlined.AccountCircle,
        title = Res.string.home_menu_account,
        route = HomeDestination.Account::class,
    )
}
