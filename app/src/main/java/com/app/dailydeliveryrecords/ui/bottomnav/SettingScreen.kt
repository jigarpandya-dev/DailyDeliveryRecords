package com.app.dailydeliveryrecords.ui.bottomnav

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.MutableLiveData
import com.app.dailydeliveryrecords.LoginActivity
import com.app.dailydeliveryrecords.R
import com.app.dailydeliveryrecords.viewmodel.HomeViewModel
import com.app.dailydeliveryrecords.ui.common.SimpleAlertDialog
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingScreen(
    viewModel: HomeViewModel, activity: Activity, showRationale: Boolean,
    showRationaleLiveData: MutableLiveData<Boolean>
) {

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    SettingsUI(viewModel, activity, showRationaleLiveData, requestPermissionLauncher)
    showPermissionRationale(showRationale, showRationaleLiveData, requestPermissionLauncher)
    viewModel.fetchPrice()
    viewModel.fetchNotify()

}


@Composable
fun showPermissionRationale(
    showRationale: Boolean,
    showRationaleLiveData: MutableLiveData<Boolean>,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {
    if (showRationale)
        SimpleAlertDialog(
            message = "DDR App needs notification permission to show important notifications.",
            onConfirm = {
                showRationaleLiveData.value = false
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            },
            onCancel = {
                showRationaleLiveData.value = false
            })
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsUI(
    viewModel: HomeViewModel,
    activity: Activity,
    showRationaleLiveData: MutableLiveData<Boolean>,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
) {

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val user = FirebaseAuth.getInstance().currentUser
    val name = if (!user?.displayName.isNullOrEmpty() && user?.displayName != "null")
        user?.displayName
    else
        user?.phoneNumber ?: ""


    val priceValue by viewModel.price.observeAsState(0.0f)
    val notify by viewModel.notify.observeAsState(false)

    var price by remember { mutableStateOf(priceValue.toString()) }
    price = priceValue.toString()

    Scaffold(scaffoldState = scaffoldState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.beige))

        ) {
            val context = LocalContext.current
            var showDialog = remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "Hi, $name!",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.tab_color),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    color = colorResource(id = R.color.tab_color)
                )

                Text(
                    text = "Set your price here",
                    color = colorResource(id = R.color.tab_color),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    fontSize = 16.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {

                    var readOnly by remember { mutableStateOf(true) }

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Price") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.primary,
                            focusedLabelColor = MaterialTheme.colors.primary,
                            unfocusedLabelColor = MaterialTheme.colors.primary,
                            textColor = MaterialTheme.colors.primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        readOnly = readOnly,
                        enabled = !readOnly
                    )

                    Icon(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                readOnly = !readOnly
                                if (readOnly) {
                                    viewModel.setPrice(price)
                                }

                            },
                        imageVector = if (readOnly) Icons.Default.Edit else Icons.Filled.Check,
                        contentDescription = "Edit Price",
                        tint = colorResource(
                            id = R.color.tab_color
                        )
                    )
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    color = colorResource(id = R.color.tab_color)
                )

                Text(
                    text = "Notifications",
                    color = colorResource(id = R.color.tab_color),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    fontSize = 16.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "Daily Reminders",
                        color = colorResource(id = R.color.tab_color),
                        modifier = Modifier
                            .weight(1f),
                        fontSize = 14.sp
                    )

//                    // Declaring a boolean value for storing checked state
//                    val mCheckedState = remember { mutableStateOf(false) }

                    // Creating a Switch, when value changes,
                    // it updates mCheckedState value

                    val currentContext = LocalContext.current
                    Switch(
                        checked = notify,
                        onCheckedChange = {
                            if (ContextCompat.checkSelfPermission(
                                    currentContext,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                // Permission is granted,no action needed.
                                viewModel.setNotify(it)
                                if (it)
                                    FirebaseMessaging.getInstance()
                                        .subscribeToTopic("daily-reminders")
                                else
                                    FirebaseMessaging.getInstance()
                                        .unsubscribeFromTopic("daily-reminders")
                            } else if (shouldShowRequestPermissionRationale(
                                    activity,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                )
                            ) {
                                showRationaleLiveData.value = true
                            } else {
                                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }

                        })
                }

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    color = colorResource(id = R.color.tab_color)
                )

            }

            Button(
                onClick = {
                    showDialog.value = true
                },
                modifier = Modifier
                    .width(200.dp)
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 12.dp
                )
            ) {
                Text("Log out ?", color = Color.White)
            }

            if (showDialog.value) {
                SimpleAlertDialog(onConfirm = {
                    AuthUI.getInstance()
                        .signOut(context)
                        .addOnCompleteListener {
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            if (context is Activity)
                                context.finish()
                        }

                }, onCancel = {
                    showDialog.value = false
                })
            }
        }
    }
}