package me.matsumo.zencall

import android.Manifest
import android.app.role.RoleManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.MobileAds
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import me.matsumo.zencall.core.model.Theme
import me.matsumo.zencall.core.ui.theme.shouldUseDarkTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<MainViewModel>()

    private val roleLauncher = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        callback = {},
    )

    private val permissionLauncher = registerForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        callback = {},
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userData by viewModel.setting.collectAsStateWithLifecycle(null)
            val isSystemInDarkTheme = shouldUseDarkTheme(userData?.theme ?: Theme.System)

            val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
            val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

            DisposableEffect(isSystemInDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isSystemInDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(lightScrim, darkScrim) { isSystemInDarkTheme },
                )
                onDispose {}
            }

            userData?.let {
                ZenCallApp(
                    modifier = Modifier.fillMaxSize(),
                    setting = it,
                )
            }

            splashScreen.setKeepOnScreenCondition { userData == null }
        }

        if (!hasPermission()) {
            permissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            roleLauncher.launch(intent)
        }

        FileKit.init(this)
        initAdsSdk()
    }

    private fun hasPermission(): Boolean {
        val contacts = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        val callLog = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED

        return contacts && callLog
    }

    private fun initAdsSdk() {
        if (viewModel.isAdsSdkInitialized.value) {
            return
        }

        MobileAds.initialize(this)
        viewModel.setAdsSdkInitialized()
    }
}
