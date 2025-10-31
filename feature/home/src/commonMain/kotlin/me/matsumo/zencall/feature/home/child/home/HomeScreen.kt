package me.matsumo.zencall.feature.home.child.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.model.call.ContactInfo
import me.matsumo.zencall.core.ui.screen.AsyncLoadContents
import me.matsumo.zencall.core.ui.screen.LazyPagingItemsLoadContents
import me.matsumo.zencall.feature.home.child.home.components.HomeCallLogItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val screenState by viewModel.screenState.collectAsState()

    AsyncLoadContents(
        modifier = modifier,
        screenState = screenState,
    ) {
        HomeScreen(
            modifier = Modifier.fillMaxSize(),
            contacts = it.contacts,
            callLogsPager = it.callLogPager,
        )
    }
}

@Composable
private fun HomeScreen(
    contacts: ImmutableList<ContactInfo>,
    callLogsPager: Flow<PagingData<CallLog>>,
    modifier: Modifier = Modifier,
) {
    val callLogsPagingAdapter = callLogsPager.collectAsLazyPagingItems()

    LazyPagingItemsLoadContents(
        modifier = modifier,
        lazyPagingItems = callLogsPagingAdapter,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = callLogsPagingAdapter.itemCount,
                key = callLogsPagingAdapter.itemKey { it.id },
                contentType = callLogsPagingAdapter.itemContentType(),
            ) { index ->
                callLogsPagingAdapter[index]?.let { callLog ->
                    val contact = remember(contacts) { matchContact(contacts, callLog.number.orEmpty()) }

                    HomeCallLogItem(
                        modifier = Modifier.fillMaxWidth(),
                        callLog = callLog,
                        contactInfo = contact
                    )
                }
            }
        }
    }
}

private fun matchContact(contacts: ImmutableList<ContactInfo>, phoneNumber: String): ContactInfo? {
    if (phoneNumber.isEmpty()) return null

    return contacts.find { it ->
        val normalizedNumbers = it.phoneNumbers.map { it.replace(Regex("[^0-9]"), "") }
        val normalizedPhoneNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        normalizedNumbers.contains(normalizedPhoneNumber)
    }
}