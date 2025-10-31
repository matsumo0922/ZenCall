package me.matsumo.zencall.feature.home.di

import me.matsumo.zencall.feature.home.RootViewModel
import me.matsumo.zencall.feature.home.child.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::RootViewModel)
    viewModelOf(::HomeViewModel)
}
