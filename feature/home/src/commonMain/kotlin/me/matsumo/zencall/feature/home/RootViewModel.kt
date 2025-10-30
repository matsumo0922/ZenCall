package me.matsumo.zencall.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.matsumo.zencall.core.repository.PhoneNumberRepository

internal class RootViewModel(
    private val phoneNumberRepository: PhoneNumberRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            phoneNumberRepository.getDetail("0120964886")
        }
    }
}
