package com.app.dailydeliveryrecords.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.app.dailydeliveryrecords.R

@Composable
fun SimpleAlertDialog(message:String?=null,onConfirm: () -> Unit,onCancel:() -> Unit) {
    AlertDialog(
        backgroundColor = colorResource(id = R.color.beige),
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            })
            { Text(text = "OK") }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
            })
            { Text(text = "Cancel") }
        },
        title = { Text(text = "Please confirm", color = colorResource(id = R.color.tab_color)) },
        text = { Text(text = message?: "Are you sure you want to logout?",color = colorResource(id = R.color.tab_color)) }
    )
}