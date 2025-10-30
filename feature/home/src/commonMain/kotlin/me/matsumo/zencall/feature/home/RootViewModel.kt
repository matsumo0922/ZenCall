package me.matsumo.zencall.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import me.matsumo.zencall.core.repository.PhoneNumberRepository

internal class RootViewModel(
    private val phoneNumberRepository: PhoneNumberRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            val result = phoneNumberRepository.parse("0120964886")
            Napier.d { "result: $result" }
        }
    }
}
