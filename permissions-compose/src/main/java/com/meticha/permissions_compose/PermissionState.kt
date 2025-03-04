package com.meticha.permissions_compose


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference


@SuppressLint("ComposableNaming")
@Composable
fun rememberAppPermissionState(permissions: List<AppPermission>): PermissionState {
    val context = LocalContext.current
    val activity = requireNotNull(LocalActivity.current)

    val permissionState = remember(permissions) { PermissionState(permissions) }

    // Provide context access through composable scope
    permissionState.contextRef = WeakReference(context)

    /**
     * Check if the permissions are added in the manifest file
     */
    permissions.forEach {
        if (!checkPermissionAddedInManifest(it, context)) {
            throw PermissionNotAddedException(it.permission)
        }
    }

    // Handle lifecycle events
    PermissionLifeCycleCheckEffect(
        permissionState = permissionState
    )

    // Set up permission launcher
    permissionState.launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        when {
            isGranted -> permissionState.next()
            else -> handlePermissionDenial(permissionState, activity)
        }
    }

    // Display permission rationale popup if needed
    permissionState.currentPermission?.let { permission ->
        when {
            permissionState.showRationalePopUp -> {
                if (PermissionManagerConfig.customRationaleUI != null) {
                    PermissionManagerConfig.customRationaleUI!!.invoke(
                        permission,
                        {},
                        {
                            permissionState.showRationalePopUp = false
                            permissionState.requestPermission()
                        }
                    )

                } else {
                    ShowPopup(
                        message = permission.description,
                        onConfirm = {
                            permissionState.showRationalePopUp = false
                            permissionState.requestPermission()
                        }
                    )
                }
            }

            permissionState.showSettingsPopUp -> {
                if (PermissionManagerConfig.customSettingsUI != null) {
                    PermissionManagerConfig.customSettingsUI!!.invoke(
                        permission,
                        {},
                        {
                            permissionState.showSettingsPopUp = false
                            permissionState.requestPermission()
                        }
                    )

                } else {
                    ShowSettings(
                        message = permission.description,
                        onConfirm = {
                            permissionState.showSettingsPopUp = false
                            permissionState.resumedFromSettings = true
                            openAppSettings(context)
                        }
                    )
                }
            }
        }
    }

    return permissionState
}


/**
 * Handles permission denial, showing appropriate UI based on denial context
 */
private fun handlePermissionDenial(permissionState: PermissionState, activity: Activity) {
    permissionState.currentPermission?.let { currentPermission ->
        when {
            currentPermission.isRequired &&
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        currentPermission.permission
                    ) ->
                permissionState.showRationalePopUp = true

            currentPermission.isRequired ->
                permissionState.showSettingsPopUp = true

            else ->
                permissionState.next()
        }
    }
}

/**
 * Opens application settings screen
 */
private fun openAppSettings(context: Context) {
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(this)
    }
}

/**
 * Manages the state of permission requests and their UI flows
 */
class PermissionState(
    permissionList: List<AppPermission>,
) {
    // WeakReference to context to avoid memory leaks
    lateinit var contextRef: WeakReference<Context>

    // All permissions that need to be handled
    private val allPermissions = mutableStateListOf<AppPermission>()

    // Permissions waiting to be processed
    private var pendingPermissions = mutableStateListOf<AppPermission>()

    // Currently processing permission
    internal var currentPermission by mutableStateOf<AppPermission?>(null)

    // UI state
    internal var showRationalePopUp by mutableStateOf(false)
    internal var showSettingsPopUp by mutableStateOf(false)
    internal var resumedFromSettings by mutableStateOf(false)

    // Permission states
    private var isRequiredPermissionGranted by mutableStateOf(false)

    // Permission request launcher
    internal var launcher: ManagedActivityResultLauncher<String, Boolean>? = null

    init {
        allPermissions.addAll(permissionList)
        pendingPermissions.addAll(permissionList)
    }

    /**
     * Checks if all required permissions are actually granted
     */
    private fun allRequiredGranted(): Boolean {
        isRequiredPermissionGranted = allPermissions
            .filter { it.isRequired }
            .all { isGranted(it.permission) }
        return isRequiredPermissionGranted
    }

    /**
     * Checks if a specific permission is granted
     */
    private fun isGranted(permission: String): Boolean {
        val context = requireNotNull(contextRef.get())
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Starts or continues the permission request flow
     */
    fun requestPermission() {
        if (pendingPermissions.isNotEmpty()) {
            currentPermission = pendingPermissions.first()
            currentPermission?.let { permission ->
                if (isGranted(permission.permission)) {
                    next() // Permission already granted, move to next
                } else {
                    launcher?.launch(permission.permission)
                }
            }
        }
    }

    /**
     * Moves to the next permission in the queue
     */
    internal fun next() {
        if (pendingPermissions.isNotEmpty()) {
            pendingPermissions.removeAt(0)
        }
        requestPermission()
    }
}