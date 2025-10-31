package me.matsumo.zencall.core.datasource.di

import me.matsumo.zencall.core.datasource.CallLogDataSource
import me.matsumo.zencall.core.datasource.IosCallLogDataSource
import me.matsumo.zencall.core.datasource.helper.PreferenceHelper
import me.matsumo.zencall.core.datasource.helper.PreferenceHelperImpl
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val dataSourcePlatformModule: Module = module {
    single<PreferenceHelper> {
        PreferenceHelperImpl(
            ioDispatcher = get(),
        )
    }

    single<CallLogDataSource> {
        IosCallLogDataSource()
    }
}
