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
import androidx.compose.ui.graphics.RectangleShape
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
import me.matsumo.zencall.core.resource.Res
import me.matsumo.zencall.core.resource.unit_last_month_and_more
import me.matsumo.zencall.core.resource.unit_last_week
import me.matsumo.zencall.core.resource.unit_this_month
import me.matsumo.zencall.core.resource.unit_this_week
import me.matsumo.zencall.core.resource.unit_today
import me.matsumo.zencall.core.resource.unit_yesterday
import me.matsumo.zencall.core.ui.screen.AsyncLoadContents
import me.matsumo.zencall.core.ui.screen.LazyPagingItemsLoadContents
import me.matsumo.zencall.feature.home.child.home.components.HomeCallLogItem
import org.jetbrains.compose.resources.stringResource
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

                    val isSectionStart = shouldShowDateHeader
                    val isSectionEnd = run {
                        val nextDateMillis = if (index + 1 < callLogsPagingAdapter.itemCount) {
                            callLogsPagingAdapter.peek(index + 1)?.dateMillis
                        } else {
                            null
                        }
                        val nextSection = nextDateMillis?.let { resolveSectionLabel(it) }
                        nextSection == null || nextSection != sectionLabel
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isSectionStart) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 6.dp)
                                    .padding(horizontal = 32.dp),
                                text = sectionLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        val shape = if (isSectionStart || isSectionEnd) {
                            RoundedCornerShape(
                                topStart = if (isSectionStart) 16.dp else 0.dp,
                                topEnd = if (isSectionStart) 16.dp else 0.dp,
                                bottomStart = if (isSectionEnd) 16.dp else 0.dp,
                                bottomEnd = if (isSectionEnd) 16.dp else 0.dp,
                            )
                        } else {
                            RectangleShape
                        }

                        HomeCallLogItem(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clip(shape)
                                .fillMaxWidth(),
                            callLog = callLog,
                            contactInfo = contact,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun resolveSectionLabel(dateMillis: Long): String {
    val timeZone = TimeZone.currentSystemDefault()
    val todayDate = Clock.System.now().toLocalDateTime(timeZone).date
    val callDate = Instant.fromEpochMilliseconds(dateMillis).toLocalDateTime(timeZone).date

    val yesterday = todayDate - DatePeriod(days = 1)
    val startOfThisWeek = todayDate.startOfWeek()
    val startOfLastWeek = startOfThisWeek - DatePeriod(days = 7)
    val startOfThisMonth = LocalDate(todayDate.year, todayDate.monthNumber, 1)

    return when {
        callDate == todayDate -> stringResource(Res.string.unit_today)
        callDate == yesterday -> stringResource(Res.string.unit_yesterday)
        callDate >= startOfThisWeek -> stringResource(Res.string.unit_this_week)
        callDate >= startOfLastWeek -> stringResource(Res.string.unit_last_week)
        callDate >= startOfThisMonth -> stringResource(Res.string.unit_this_month)
        else -> stringResource(Res.string.unit_last_month_and_more)
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
