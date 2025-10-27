package me.matsumo.zencall.core.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination {
    @Serializable
    data object Root : Destination

    @Serializable
    data object About: Destination

    @Serializable
    sealed interface Setting : Destination {
        @Serializable
        data object Root : Setting

        @Serializable
        data object License : Setting
    }
}
