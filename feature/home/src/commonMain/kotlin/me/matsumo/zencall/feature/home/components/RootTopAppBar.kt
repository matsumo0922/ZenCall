package me.matsumo.zencall.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.matsumo.zencall.core.resource.Res
import me.matsumo.zencall.core.resource.home_search_bar_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RootTopAppBar(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()

    fun openDrawer() {
        scope.launch {
            drawerState.open()
        }
    }

    Column(
        modifier = modifier.padding(bottom = 8.dp),
    ) {
        AppBarWithSearch(
            state = searchBarState,
            navigationIcon = {
                IconButton(::openDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(::openDrawer) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                    )
                }
            },
            inputField = {
                InputField(
                    searchBarState = searchBarState,
                    textFieldState = textFieldState,
                )
            },
        )

        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = {
                InputField(
                    searchBarState = searchBarState,
                    textFieldState = textFieldState,
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(100) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        text = "Item #$it",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InputField(
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    SearchBarDefaults.InputField(
        modifier = modifier,
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = {
            scope.launch {
                searchBarState.animateToCollapsed()
            }
        },
        placeholder = {
            if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.home_search_bar_placeholder),
                    textAlign = TextAlign.Center,
                )
            }
        },
    )
}