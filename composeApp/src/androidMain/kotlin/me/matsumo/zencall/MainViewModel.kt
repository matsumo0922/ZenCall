package me.matsumo.zencall

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.matsumo.zencall.core.repository.AppSettingRepository

class MainViewModel(
    private val settingRepository: AppSettingRepository,
) : ViewModel() {

    val setting = settingRepository.setting

    private val _isAdsSdkInitialized = MutableStateFlow(false)
    val isAdsSdkInitialized = _isAdsSdkInitialized.asStateFlow()

    fun setAdsSdkInitialized() {
        _isAdsSdkInitialized.value = true
    }
}
