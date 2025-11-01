package me.matsumo.zencall.feature.home.child.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.model.call.ContactInfo
import me.matsumo.zencall.core.ui.screen.AsyncLoadContents
import me.matsumo.zencall.core.ui.screen.LazyPagingItemsLoadContents
import me.matsumo.zencall.feature.home.child.home.components.HomeCallLogItem
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(
                count = callLogsPagingAdapter.itemCount,
                key = callLogsPagingAdapter.itemKey { it.id },
                contentType = callLogsPagingAdapter.itemContentType(),
            ) { index ->
                callLogsPagingAdapter[index]?.let { callLog ->
                    val contact = remember(contacts, callLog.number) {
                        matchContact(contacts, callLog.number.orEmpty())
                    }

                    val sectionLabel = resolveSectionLabel(callLog.dateMillis)

                    val shouldShowDateHeader = run {
                        val previousDateMillis = if (index > 0) callLogsPagingAdapter.peek(index - 1)?.dateMillis else null
                        val previousSection = previousDateMillis?.let { resolveSectionLabel(it) }
                        previousSection == null || previousSection != sectionLabel
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (shouldShowDateHeader) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 6.dp)
                                    .padding(horizontal = 16.dp),
                                text = sectionLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        HomeCallLogItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .then(
                                    if (shouldShowDateHeader) {
                                        Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .fillMaxWidth(),
                            callLog = callLog,
                            contactInfo = contact
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun resolveSectionLabel(dateMillis: Long): String {
    val timeZone = TimeZone.currentSystemDefault()
    val todayDate = Clock.System.now().toLocalDateTime(timeZone).date
    val callDate = Instant.fromEpochMilliseconds(dateMillis).toLocalDateTime(timeZone).date

    val yesterday = todayDate - DatePeriod(days = 1)
    val startOfThisWeek = todayDate.startOfWeek()
    val startOfLastWeek = startOfThisWeek - DatePeriod(days = 7)
    val startOfThisMonth = LocalDate(todayDate.year, todayDate.monthNumber, 1)

    return when {
        callDate == todayDate -> "今日"
        callDate == yesterday -> "昨日"
        callDate >= startOfThisWeek -> "今週"
        callDate >= startOfLastWeek -> "先週"
        callDate >= startOfThisMonth -> "今月"
        else -> "先月以前"
    }
}

private fun LocalDate.startOfWeek(): LocalDate {
    val daysFromMonday = dayOfWeek.isoDayNumber - 1
    return this - DatePeriod(days = daysFromMonday)
}

private fun matchContact(contacts: ImmutableList<ContactInfo>, phoneNumber: String): ContactInfo? {
    if (phoneNumber.isEmpty()) return null

    return contacts.find { it ->
        val normalizedNumbers = it.phoneNumbers.map { it.replace(Regex("[^0-9]"), "") }
        val normalizedPhoneNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        normalizedNumbers.contains(normalizedPhoneNumber)
    }
}
