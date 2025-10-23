package me.matsumo.zencall.core.repository.di

import me.matsumo.zencall.core.repository.AppSettingRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AppSettingRepository)
}
