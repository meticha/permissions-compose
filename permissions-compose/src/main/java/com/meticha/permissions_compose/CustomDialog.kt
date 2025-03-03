package com.meticha.permissions_compose

import androidx.compose.runtime.Composable

/**
 * Custom Rationale UI type
 */
typealias CustomRationaleUI = @Composable (
    permission: AppPermission,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) -> Unit

/**
 * Custom Settings UI type
 */
typealias CustomSettingsUI = @Composable (
    permission: AppPermission,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) -> Unit

/**
 * Permission manager singleton to hold global configurations
 */
object PermissionManagerConfig {
    private var _customRationaleUI: CustomRationaleUI? = null
    val customRationaleUI: CustomRationaleUI?
        get() = _customRationaleUI

    private var _customSettingsUI: CustomSettingsUI? = null
    val customSettingsUI: CustomSettingsUI?
        get() = _customSettingsUI

    /**
     * Set a custom UI for permission rationale dialogs
     */
    fun setCustomRationaleUI(rationaleUI: CustomRationaleUI) {
        _customRationaleUI = rationaleUI
    }

    fun setCustomSettingsUI(settingsUI: CustomSettingsUI) {
        _customSettingsUI = settingsUI
    }

    /**
     * Reset the custom UI
     */
    fun resetCustomRationaleUI() {
        _customRationaleUI = null
    }

    fun resetCustomSettingUI() {
        _customSettingsUI = null
    }
}




