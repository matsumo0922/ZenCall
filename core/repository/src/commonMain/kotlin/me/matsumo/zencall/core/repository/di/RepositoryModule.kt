package me.matsumo.zencall.core.repository.di

import me.matsumo.zencall.core.repository.AppSettingRepository
import me.matsumo.zencall.core.repository.PhoneNumberRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AppSettingRepository)
    singleOf(::PhoneNumberRepository)
}
