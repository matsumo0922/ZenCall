package me.matsumo.zencall.core.datasource.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import me.matsumo.zencall.core.common.formatter
import me.matsumo.zencall.core.datasource.AppSettingDataSource
import me.matsumo.zencall.core.model.AppConfig
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataSourceModule = module {
    single {
        val appConfig = get<AppConfig>()

        createSupabaseClient(
            supabaseUrl = appConfig.supabaseUrl,
            supabaseKey = appConfig.supabaseKey,
        ) {
            defaultLogLevel = LogLevel.DEBUG
            defaultSerializer = KotlinXSerializer(formatter)

            install(Postgrest)
            install(Auth) {
                flowType = FlowType.PKCE
            }
            install(ComposeAuth) {
                googleNativeLogin(appConfig.googleClientId)
                appleNativeLogin()
            }
        }
    }

    single {
        get<SupabaseClient>().composeAuth
    }

    singleOf(::AppSettingDataSource)

    includes(dataSourcePlatformModule)
}

internal expect val dataSourcePlatformModule: Module
