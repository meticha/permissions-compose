package com.meticha.permissions_compose


import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ShowSettings(
    message: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(text = "Permission")
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(text = "OK")
            }
        }
    )
}

@Composable
fun ShowPopup(
    message: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(text = "Permission")
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(text = "OK")
            }
        }
    )
}
