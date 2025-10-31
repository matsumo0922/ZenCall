package me.matsumo.zencall.feature.home

import androidx.lifecycle.ViewModel
import me.matsumo.zencall.core.repository.PhoneNumberRepository

internal class RootViewModel(
    private val phoneNumberRepository: PhoneNumberRepository,
) : ViewModel() {

}
