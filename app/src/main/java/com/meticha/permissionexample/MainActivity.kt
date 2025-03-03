package com.meticha.permissionexample

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meticha.permissionexample.ui.theme.PermissionExampleJetpackComposeTheme
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.PermissionManagerConfig
import com.meticha.permissions_compose.rememberAppPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set a custom rationale UI globally (Optional)
        PermissionManagerConfig.setCustomRationaleUI { permission, onDismiss, onConfirm ->
            CustomRationaleDialog(
                description = permission.description,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }

        setContent {
            PermissionExampleJetpackComposeTheme {
                PermissionScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen() {
    val permissions = rememberAppPermissionState(
        permissions = listOf(
            AppPermission(
                permission = Manifest.permission.CAMERA,
                description = "Camera access is needed to take photos. Please grant this permission.",
                isRequired = true
            ),
            AppPermission(
                permission = Manifest.permission.RECORD_AUDIO,
                description = "Microphone access is needed for voice recording. Please grant this permission.",
                isRequired = false
            ),
            AppPermission(
                permission = Manifest.permission.READ_CONTACTS,
                description = "Contact access is needed to show the contacts in the App. Please grant this permission",
                isRequired = true
            ),
        )
    )


    Scaffold(
        topBar = { TopAppBar(title = { Text("Permission Example") }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show current permission status
            Text("Camera Permission: ${if (permissions.isGranted(Manifest.permission.CAMERA)) "Granted" else "Not Granted"}")

            Spacer(modifier = Modifier.height(10.dp))

            Text("Audio Permission: ${if (permissions.isGranted(Manifest.permission.RECORD_AUDIO)) "Granted" else "Not Granted"}")

            Spacer(modifier = Modifier.height(10.dp))

            Text("Contact Permission: ${if (permissions.isGranted(Manifest.permission.READ_CONTACTS)) "Granted" else "Not Granted"}")

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { permissions.requestPermission() },
            ) {
                Text("Request Permissions")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conditionally show content based on permission state
            if (permissions.allRequiredGranted()) {
                Text("All required permissions granted! You can now use the app.")
            }
        }
    }
}
