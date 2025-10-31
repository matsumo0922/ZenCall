package me.matsumo.zencall.feature.home.child.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import me.matsumo.zencall.core.model.call.CallLog
import me.matsumo.zencall.core.model.call.ContactInfo
import me.matsumo.zencall.core.resource.Res
import me.matsumo.zencall.core.resource.common_unknown
import me.matsumo.zencall.core.ui.utils.formatDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HomeCallLogItem(
    callLog: CallLog,
    contactInfo: ContactInfo?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ContactIcon(
            modifier = Modifier.size(40.dp),
            name = callLog.cachedName ?: contactInfo?.displayName.orEmpty(),
            icon = contactInfo?.photo,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TitleText(
                modifier = Modifier.fillMaxWidth(),
                callLog = callLog,
                contactInfo = contactInfo,
            )

            CallType(
                modifier = Modifier.fillMaxWidth(),
                callLog = callLog,
            )
        }
    }
}

@Composable
private fun TitleText(
    callLog: CallLog,
    contactInfo: ContactInfo?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = contactInfo?.displayName?.takeIf { it.isNotBlank() }
                ?: callLog.cachedName?.takeIf { it.isNotBlank() }
                ?: callLog.number?.takeIf { it.isNotBlank() }
                ?: stringResource(Res.string.common_unknown),
            style = MaterialTheme.typography.bodyLarge,
        )

        if (callLog.groupedCount > 1) {
            Text(
                text = "(${callLog.groupedCount})",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CallType(
    callLog: CallLog,
    modifier: Modifier = Modifier,
) {
    val color = when (callLog.type) {
        CallLog.Type.REJECTED,
        CallLog.Type.MISSED,
        CallLog.Type.BLOCKED -> MaterialTheme.colorScheme.error

        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = when (callLog.type) {
                CallLog.Type.INCOMING -> Icons.AutoMirrored.Filled.CallReceived
                CallLog.Type.OUTGOING -> Icons.AutoMirrored.Filled.CallMade
                CallLog.Type.REJECTED -> Icons.AutoMirrored.Filled.CallMissed
                CallLog.Type.MISSED -> Icons.AutoMirrored.Filled.CallMissed
                CallLog.Type.BLOCKED -> Icons.Default.Block
                CallLog.Type.VOICEMAIL -> Icons.Default.Voicemail
                CallLog.Type.ANSWERED_EXTERNALLY -> Icons.AutoMirrored.Filled.CallReceived
            },
            contentDescription = null,
            tint = color,
        )

        Text(
            modifier = Modifier.weight(1f),
            text = callLog.location?.takeIf { it.isNotBlank() }?.let { "$it・" }.orEmpty() + formatDateTime(callLog.dateMillis),
            style = MaterialTheme.typography.bodyMedium,
            color = color,
        )
    }
}

@Composable
private fun ContactIcon(
    name: String,
    icon: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = icon,
                contentDescription = null,
            )
        } else if (name.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    modifier = Modifier.padding(4.dp),
                    text = name.first().uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    color = ColorProducer { Color.White },
                    autoSize = TextAutoSize.StepBased(),
                    maxLines = 1,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.padding(4.dp),
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
