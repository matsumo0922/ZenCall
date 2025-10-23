package me.matsumo.zencall.core.datasource.di

import me.matsumo.zencall.core.datasource.AppSettingDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataSourceModule = module {
    singleOf(::AppSettingDataSource)

    includes(dataSourcePlatformModule)
}

internal expect val dataSourcePlatformModule: Module
