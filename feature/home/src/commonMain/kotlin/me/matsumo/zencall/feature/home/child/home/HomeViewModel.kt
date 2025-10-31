package me.matsumo.zencall.feature.home.child.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.model.call.ContactInfo
import me.matsumo.zencall.core.repository.ContactsRepository
import me.matsumo.zencall.core.ui.screen.ScreenState

class HomeViewModel(
    private val contactsRepository: ContactsRepository,
): ViewModel() {
    val screenState: StateFlow<ScreenState<HomeUiState>>
        private field: MutableStateFlow<ScreenState<HomeUiState>> = MutableStateFlow(ScreenState.Loading)

    init {
        viewModelScope.launch {
            screenState.update {
                ScreenState.Idle(
                    HomeUiState(
                        contacts = contactsRepository.getContacts().toImmutableList(),
                        callLogPager = contactsRepository.getCallLogsPager()
                    )
                )
            }
        }
    }
}

@Stable
data class HomeUiState(
    val contacts: ImmutableList<ContactInfo>,
    val callLogPager: Flow<PagingData<CallLog>>
)