package me.matsumo.zencall.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import me.matsumo.zencall.core.model.Destination
import me.matsumo.zencall.core.resource.Res
import me.matsumo.zencall.core.resource.home_menu_about
import me.matsumo.zencall.core.resource.home_menu_account
import me.matsumo.zencall.core.resource.home_menu_setting
import me.matsumo.zencall.core.ui.theme.LocalNavController
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RootDrawer(
    state: DrawerState,
    localNavController: NavController,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier,
    ) {
        DrawerContent(
            state = state,
            localNavController = localNavController,
        )
    }
}

@Composable
private fun DrawerContent(
    state: DrawerState,
    localNavController: NavController,
    modifier: Modifier = Modifier,
) {
    val navController = LocalNavController.current
    val navBackStackEntry by localNavController.currentBackStackEntryAsState()

    Column(
        modifier = modifier
            .width(256.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        NavigationDrawerItem(
            modifier = Modifier.padding(top = 8.dp),
            state = state,
            isSelected = navBackStackEntry.isCurrentRoute(HomeDestination.Home::class),
            label = stringResource(Res.string.home_menu_setting),
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Default.Home,
            onClick = { localNavController.navigateToHomeDestination(HomeDestinationData.Home) },
        )

        NavigationDrawerItem(
            state = state,
            isSelected = navBackStackEntry.isCurrentRoute(HomeDestination.Account::class),
            label = stringResource(Res.string.home_menu_account),
            icon = Icons.Outlined.AccountCircle,
            selectedIcon = Icons.Default.AccountCircle,
            onClick = { localNavController.navigateToHomeDestination(HomeDestinationData.Account) },
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
        )

        NavigationDrawerItem(
            state = state,
            label = stringResource(Res.string.home_menu_setting),
            icon = Icons.Outlined.Settings,
            onClick = { navController.navigate(Destination.Setting.Root) },
        )

        NavigationDrawerItem(
            state = state,
            label = stringResource(Res.string.home_menu_about),
            icon = Icons.Outlined.Info,
            onClick = { navController.navigate(Destination.About) },
        )
    }
}

@Composable
private fun NavigationDrawerItem(
    state: DrawerState?,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedIcon: ImageVector = icon,
) {
    val scope = rememberCoroutineScope()
    val containerColor: Color
    val contentColor: Color

    if (isSelected) {
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        contentColor = MaterialTheme.colorScheme.primary
    } else {
        containerColor = Color.Transparent
        contentColor = MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .padding(end = 16.dp)
            .clip(
                RoundedCornerShape(
                    topEnd = 32.dp,
                    bottomEnd = 32.dp,
                ),
            )
            .background(containerColor)
            .clickable {
                scope.launch {
                    state?.close()
                    onClick.invoke()
                }
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = if (isSelected) selectedIcon else icon,
            contentDescription = null,
            tint = contentColor,
        )

        Text(
            modifier = Modifier.weight(1f),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
        )
    }
}