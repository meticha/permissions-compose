package com.meticha.permissions_compose

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner


/**
 * Monitors lifecycle events to handle permission state after returning from Settings
 */
@Composable
fun PermissionLifeCycleCheckEffect(
    permissionState: PermissionState,
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_RESUME
) {

    val observer = LifecycleEventObserver { _, event ->
        Log.e("COMPOSE PERMISSION", "Event: $event")
        if (event == lifecycleEvent) {
            if (permissionState.resumedFromSettings) {
                permissionState.requestPermission()
                permissionState.resumedFromSettings = false
            }
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
