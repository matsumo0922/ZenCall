package me.matsumo.zencall.core.datasource

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.matsumo.zencall.core.model.call.ContactInfo

class AndroidContactsDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
) : ContactsDataSource {

    override suspend fun getContacts(limit: Int, offset: Int): List<ContactInfo> = withContext(ioDispatcher) {
        if (limit <= 0) return@withContext emptyList()

        val resolver = context.contentResolver
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
        )
        val appliedOffset = offset.coerceAtLeast(0)
        val sortOrder = buildString {
            append("${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} ASC LIMIT $limit")
            if (appliedOffset > 0) {
                append(" OFFSET ")
                append(appliedOffset)
            }
        }

        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            sortOrder,
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val hasNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)

            buildList {
                while (cursor.moveToNext()) {
                    val contactId = cursor.getLong(idIndex)
                    val displayName = cursor.getString(nameIndex)?.takeIf { it.isNotBlank() } ?: ""
                    val hasPhoneNumber = cursor.getInt(hasNumberIndex) > 0

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

            if (cursor.moveToFirst()) {
                val contactId = cursor.getLong(idIndex)
                val displayName = cursor.getString(nameIndex)?.takeIf { it.isNotBlank() } ?: ""
                val numbers = loadPhoneNumbers(resolver, contactId)

                return@withContext ContactInfo(
                    id = contactId,
                    displayName = displayName,
                    phoneNumbers = numbers.ifEmpty { listOf(normalized) },
                )
            }
        }

        null
    }

    private fun loadPhoneNumbers(resolver: android.content.ContentResolver, contactId: Long): List<String> {
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
}
