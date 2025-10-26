package me.matsumo.zencall.core.datasource.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class PreferenceHelperImpl(
    private val ioDispatcher: CoroutineDispatcher,
    baseDirectory: Path? = null,
) : PreferenceHelper {

    private val dataStoreDirectory: Path = baseDirectory ?: defaultDirectory()

    override fun create(name: String): DataStore<Preferences> {
        ensureDirectoryExists()
        val file = dataStoreDirectory / "$name.preferences_pb"

        return PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            scope = CoroutineScope(ioDispatcher),
            produceFile = {
                ensureDirectoryExists()
                file
            },
        )
    }

    override fun delete(name: String) {
        val file = dataStoreDirectory / "$name.preferences_pb"
        if (FileSystem.SYSTEM.exists(file)) {
            FileSystem.SYSTEM.delete(file, mustExist = false)
        }
    }

    private fun ensureDirectoryExists() {
        if (!FileSystem.SYSTEM.exists(dataStoreDirectory)) {
            FileSystem.SYSTEM.createDirectories(dataStoreDirectory, mustCreate = false)
        }
    }

    private fun defaultDirectory(): Path {
        val userHome = System.getProperty("user.home").orEmpty()
        val basePath = if (userHome.isNotBlank()) {
            "$userHome/.zencall/datastore"
        } else {
            ".zencall/datastore"
        }
        return basePath.toPath().also { parent ->
            if (!FileSystem.SYSTEM.exists(parent)) {
                FileSystem.SYSTEM.createDirectories(parent, mustCreate = false)
            }
        }
    }
}
