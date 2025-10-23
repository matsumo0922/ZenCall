package me.matsumo.zencall.di

import me.matsumo.zencall.core.common.di.commonModule
import me.matsumo.zencall.core.datasource.di.dataSourceModule
import me.matsumo.zencall.core.repository.di.repositoryModule
import me.matsumo.zencall.feature.home.di.homeModule
import me.matsumo.zencall.feature.setting.di.settingModule
import org.koin.core.KoinApplication

fun KoinApplication.applyModules() {
    modules(appModule)

    modules(commonModule)
    modules(dataSourceModule)
    modules(repositoryModule)

    modules(homeModule)
    modules(settingModule)
}
