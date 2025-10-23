package me.matsumo.zencall.core.model

expect val currentPlatform: Platform

enum class Platform {
    Android,
    IOS,
}
