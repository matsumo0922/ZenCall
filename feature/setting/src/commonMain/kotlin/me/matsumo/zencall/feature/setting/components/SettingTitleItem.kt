package me.matsumo.zencall.feature.setting.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.matsumo.zencall.core.ui.theme.bold
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingTitleItem(
    text: StringResource,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(
            top = 24.dp,
            bottom = 12.dp,
            start = 24.dp,
            end = 24.dp,
        ),
        text = stringResource(text).uppercase(),
        style = MaterialTheme.typography.bodyMedium.bold(),
        color = MaterialTheme.colorScheme.primary,
    )
}
