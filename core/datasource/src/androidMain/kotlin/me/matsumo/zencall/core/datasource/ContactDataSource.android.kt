package me.matsumo.zencall.core.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.matsumo.zencall.core.model.call.ContactInfo

class AndroidContactsDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) : ContactsDataSource {

    override suspend fun getContacts(): List<ContactInfo> = withContext(ioDispatcher) {
        val resolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_URI,
        )
        val queryArgs = Bundle().apply {
            putStringArray(
                android.content.ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
            )
            putInt(
                android.content.ContentResolver.QUERY_ARG_SORT_DIRECTION,
                android.content.ContentResolver.QUERY_SORT_DIRECTION_ASCENDING,
            )
        }
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            queryArgs,
            null,
        )

        cursor?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val nameIndex = c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val hasNumberIndex = c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
            val photoUriIndex = c.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)

            buildList {
                while (c.moveToNext()) {
                    val contactId = c.getLong(idIndex)
                    val displayName = c.getString(nameIndex)?.takeIf { it.isNotBlank() } ?: ""
                    val hasPhoneNumber = c.getInt(hasNumberIndex) > 0
                    val photoUri = c.getString(photoUriIndex)

                    val phoneNumbers = if (hasPhoneNumber) {
                        loadPhoneNumbers(resolver, contactId)
                    } else {
                        emptyList()
                    }

                    add(
                        ContactInfo(
                            id = contactId,
                            displayName = displayName,
                            phoneNumbers = phoneNumbers,
                            photo = loadPhoto(resolver, photoUri)?.asImageBitmap(),
                        ),
                    )
                }
            }
        } ?: emptyList()
    }

    override suspend fun getContactByPhoneNumber(phoneNumber: String): ContactInfo? = withContext(ioDispatcher) {
        val normalized = phoneNumber.trim()
        if (normalized.isEmpty()) return@withContext null

        val resolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.PHOTO_URI,
        )
        val uri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(normalized),
        )

        resolver.query(
            uri,
            projection,
            null,
            null,
            null,
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val photoIndex = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_URI)

            if (cursor.moveToFirst()) {
                val contactId = cursor.getLong(idIndex)
                val displayName = cursor.getString(nameIndex)?.takeIf { it.isNotBlank() } ?: ""
                val numbers = loadPhoneNumbers(resolver, contactId)
                val photoUri = cursor.getString(photoIndex)

                return@withContext ContactInfo(
                    id = contactId,
                    displayName = displayName,
                    phoneNumbers = numbers.ifEmpty { listOf(normalized) },
                    photo = loadPhoto(resolver, photoUri)?.asImageBitmap(),
                )
            }
        }

        null
    }

    private fun loadPhoneNumbers(
        resolver: android.content.ContentResolver,
        contactId: Long,
    ): List<String> {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)

        resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
        )?.use { phoneCursor ->
            val numberIndex = phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)

            return buildSet {
                while (phoneCursor.moveToNext()) {
                    val number = phoneCursor.getString(numberIndex)?.trim()
                    if (!number.isNullOrEmpty()) {
                        add(number)
                    }
                }
            }.toList()
        }

        return emptyList()
    }

    private fun loadPhoto(
        resolver: android.content.ContentResolver,
        uri: String?,
    ): Bitmap? {
        if (uri == null) return null

        return runCatching {
            resolver.openInputStream(uri.toUri()).use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        }.getOrNull()
    }
}
