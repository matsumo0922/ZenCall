package me.matsumo.zencall.feature.home.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navOptions
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RootBottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentHierarchy = navBackStackEntry?.destination?.hierarchy

    NavigationBar(modifier) {
        HomeDestinationData.entries.forEach { destination ->
            val isSelected by remember(destination) {
                derivedStateOf { currentHierarchy?.any { it.hasRoute(destination.route::class) } ?: false }
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { navController.navigateToHomeDestination(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.deselectedIcon,
                        contentDescription = stringResource(destination.title),
                    )
                },
                label = {
                    Text(
                        text = stringResource(destination.title),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        }
    }
}

fun NavController.navigateToHomeDestination(destination: HomeDestinationData) {
    val navOption = navOptions {
        popUpTo(graph.findStartDestination().route.orEmpty()) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    navigate(destination.route, navOption)
}