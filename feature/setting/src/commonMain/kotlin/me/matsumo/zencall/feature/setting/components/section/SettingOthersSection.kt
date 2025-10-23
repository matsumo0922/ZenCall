package me.matsumo.zencall.feature.setting.components.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import me.matsumo.zencall.core.model.AppSetting
import me.matsumo.zencall.core.resource.Res
import me.matsumo.zencall.core.resource.setting_other
import me.matsumo.zencall.core.resource.setting_other_developer_mode
import me.matsumo.zencall.core.resource.setting_other_developer_mode_description
import me.matsumo.zencall.core.resource.setting_other_open_source_license
import me.matsumo.zencall.core.resource.setting_other_open_source_license_description
import me.matsumo.zencall.core.resource.setting_other_privacy_policy
import me.matsumo.zencall.core.resource.setting_other_team_of_service
import me.matsumo.zencall.feature.setting.components.SettingDeveloperModeDialog
import me.matsumo.zencall.feature.setting.components.SettingSwitchItem
import me.matsumo.zencall.feature.setting.components.SettingTextItem
import me.matsumo.zencall.feature.setting.components.SettingTitleItem
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingOthersSection(
    setting: AppSetting,
    onTeamsOfServiceClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
    onOpenSourceLicenseClicked: () -> Unit,
    onDeveloperModeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowDeveloperModeDialog by remember { mutableStateOf(false) }

    Column(modifier) {
        SettingTitleItem(
            modifier = Modifier.fillMaxWidth(),
            text = Res.string.setting_other,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(Res.string.setting_other_team_of_service),
            onClick = onTeamsOfServiceClicked,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(Res.string.setting_other_privacy_policy),
            onClick = onPrivacyPolicyClicked,
        )

        SettingTextItem(
            modifier = Modifier.fillMaxWidth(),
            title = Res.string.setting_other_open_source_license,
            description = Res.string.setting_other_open_source_license_description,
            onClick = { onOpenSourceLicenseClicked.invoke() },
        )

        SettingSwitchItem(
            modifier = Modifier.fillMaxWidth(),
            title = Res.string.setting_other_developer_mode,
            description = Res.string.setting_other_developer_mode_description,
            value = setting.developerMode,
            onValueChanged = {
                if (it) {
                    isShowDeveloperModeDialog = true
                } else {
                    onDeveloperModeChanged.invoke(false)
                }
            },
        )
    }

    if (isShowDeveloperModeDialog) {
        SettingDeveloperModeDialog(
            onDeveloperModeEnabled = {
                onDeveloperModeChanged.invoke(true)
                isShowDeveloperModeDialog = false
            },
            onDismissRequest = {
                isShowDeveloperModeDialog = false
            },
        )
    }
}
