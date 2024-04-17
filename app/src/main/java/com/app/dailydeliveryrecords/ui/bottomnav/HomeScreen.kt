package com.app.dailydeliveryrecords.ui.bottomnav

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.dailydeliveryrecords.R
import com.app.dailydeliveryrecords.model.DeliveryItem
import com.app.dailydeliveryrecords.viewmodel.HomeViewModel
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        HomeUI(viewModel)
        // ProgressUI(viewModel)
    }
}

@Composable
fun HomeUI(viewModel: HomeViewModel) {
    // Create a list of items
    var selectedDelivery by remember { mutableStateOf("") }
    var mExpanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val calendar = viewModel.calendar


    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DateUI(viewModel, calendar)

        Icon(
            modifier =
            Modifier
                .padding(start = 10.dp)
                .alpha(if (uiState.todayDelivery.delivery != 0) 1.0f else 0.0f)
                .clickable {
                    viewModel.setDelivery(0, 0.0)
                },
            imageVector = Icons.Default.Edit,
            contentDescription = "Update Delivery",
            tint =
                colorResource(
                    id = R.color.tab_color,
                ),
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    AnimatedVisibility(
        visible = uiState.todayDelivery.delivery != 0,
        enter =
            expandHorizontally(
                animationSpec = tween(500, 500),
                expandFrom = Alignment.CenterHorizontally,
            ),
        exit = fadeOut(),
    ) {
        val currentDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${
            calendar.get(Calendar.MONTH) + 1
        }/${calendar.get(Calendar.YEAR)}"

        Text(
            text = "You have entered $currentDate delivery!\n\n${uiState.deliveryValueList.find { it.code == uiState.todayDelivery.delivery }?.label ?: ""}",
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.tab_color),
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
        )
    }

    AnimatedVisibility(
        visible = uiState.todayDelivery.delivery == 0,
        enter = fadeIn(tween(500, 500)),
        exit = fadeOut(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Enter your today's delivery",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.tab_color),
                modifier =
                    Modifier
                        .padding(10.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )

            Box {
                OutlinedTextField(
                    value = selectedDelivery,
                    onValueChange = { selectedDelivery = it },
                    label = { Text("Select Delivery") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            "",
                            tint = colorResource(id = R.color.tab_color),
                        )
                    },
                    enabled = false,
                    colors =
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            unfocusedBorderColor = MaterialTheme.colors.primary,
                            focusedLabelColor = MaterialTheme.colors.primary,
                            unfocusedLabelColor = MaterialTheme.colors.primary,
                            textColor = MaterialTheme.colors.primary,
                        ),
                    modifier =
                    Modifier
                        .clickable {
                            mExpanded = true
                        }
                        .onGloballyPositioned { coordinates ->
                            // This value is used to assign to the DropDown the same width
                            textFieldSize = coordinates.size.toSize()
                        },
                )

                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = { mExpanded = false },
                    modifier =
                    Modifier
                        .background(colorResource(id = R.color.beige))
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                ) {
                    uiState.deliveryValueList.forEach { deliveryItem ->
                        DropdownMenuItem(onClick = {
                            mExpanded = false
                            selectedDelivery = deliveryItem.label
                            viewModel.setDelivery(deliveryItem.code, deliveryItem.unit)
                        }) {
                            Text(
                                text = deliveryItem.label,
                                color = colorResource(id = R.color.tab_color),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DateUI(
    viewModel: HomeViewModel,
    c: Calendar,
) {
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    var currentMonthLabel by remember { mutableStateOf("$day/${month + 1}/$year") }

    val datePickerDialog =
        DatePickerDialog(
            LocalContext.current,
            DatePickerDialog.OnDateSetListener
                { _, year: Int, month: Int, day: Int ->
                    // onUpdateMonth(month+1)
                    c.set(Calendar.DAY_OF_MONTH, day)
                    c.set(Calendar.MONTH, month)
                    c.set(Calendar.YEAR, year)
                    currentMonthLabel = "$day/${month + 1}/$year"
                    viewModel.fetchDelivery(currentMonthLabel)
                },
            year,
            month,
            day,
        )

    Button(
        onClick = {
            datePickerDialog.show()
        },
        modifier =
        Modifier
            .width(200.dp)
            .padding(10.dp),
        contentPadding =
            PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp,
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                modifier =
                Modifier
                    .size(18.dp)
                    .align(Alignment.CenterEnd),
                contentDescription = "date picker",
                tint = Color.White,
            )
            Text(currentMonthLabel, color = Color.White)
        }
    }
}

@Composable
fun ProgressUI(viewModel: HomeViewModel) {
    val showDialog: Boolean by viewModel.showDialog.observeAsState(false)
    if (showDialog) {
        CircularProgressIndicator(
            modifier =
            Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center),
        )
    }
}
