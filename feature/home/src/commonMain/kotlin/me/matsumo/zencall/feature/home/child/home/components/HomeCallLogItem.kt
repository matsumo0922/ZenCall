package me.matsumo.zencall.feature.home.child.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
            Text(
                text = contactInfo?.displayName?.takeIf { it.isNotBlank() }
                    ?: callLog.cachedName?.takeIf { it.isNotBlank() }
                    ?: callLog.number?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.common_unknown),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
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