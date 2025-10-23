package me.matsumo.zencall

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.matsumo.zencall.di.applyModules
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        applyModules()
    }
}

fun initNapier() {
    Napier.base(DebugAntilog())
}
