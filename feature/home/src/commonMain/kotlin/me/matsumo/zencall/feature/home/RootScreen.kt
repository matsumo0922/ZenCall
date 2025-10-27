package me.matsumo.zencall.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.matsumo.zencall.feature.home.child.account.accountScreen
import me.matsumo.zencall.feature.home.child.home.homeScreen
import me.matsumo.zencall.feature.home.components.HomeDestination
import me.matsumo.zencall.feature.home.components.RootBottomNavigationBar
import me.matsumo.zencall.feature.home.components.RootTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RootRoute(
    modifier: Modifier = Modifier,
    viewModel: RootViewModel = koinViewModel(),
) {
    RootScreen(
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootScreen(
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val navHostController = rememberNavController()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {

        }
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                RootTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    scrollBehavior = scrollBehavior,
                    drawerState = drawerState,
                )
            },
            bottomBar = {
                RootBottomNavigationBar(
                    modifier = Modifier.fillMaxWidth(),
                    navController = navHostController,
                )
            }
        ) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                navController = navHostController,
                startDestination = HomeDestination.Home
            ) {
                homeScreen()
                accountScreen()
            }
        }
    }
}