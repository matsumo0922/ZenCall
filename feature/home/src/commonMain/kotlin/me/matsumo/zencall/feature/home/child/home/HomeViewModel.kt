package me.matsumo.zencall.feature.home.child.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.repository.ContactsRepository

class HomeViewModel(
    private val contactsRepository: ContactsRepository,
): ViewModel() {
    val uiState = HomeUiState(
        callLogPager = contactsRepository.getCallLogsPager(),
    )
}

@Stable
data class HomeUiState(
    val callLogPager: Flow<PagingData<CallLog>>
)