package com.app.dailydeliveryrecords.ui.bottomnav

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.dailydeliveryrecords.R
import com.app.dailydeliveryrecords.model.DeliveryItem
import com.app.dailydeliveryrecords.viewmodel.HomeViewModel
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*


@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.height(100.dp))
        HomeUI(viewModel)
        ProgressUI(viewModel)
        viewModel.fetchDelivery()
    }
}

@Composable
fun HomeUI(viewModel: HomeViewModel) {

    // Create a list of items
    val mDeliveryItems = arrayListOf<DeliveryItem>()

    val deliveryValueList: List<DocumentSnapshot> by viewModel.deliveryValueList.observeAsState(emptyList())
    var selectedDelivery by remember { mutableStateOf("") }
    var mExpanded by remember { mutableStateOf(false) }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }
    val todayDelivery: Int by viewModel.todayDelivery.observeAsState(0)
    val calendar = viewModel.calendar

    for (delivery in deliveryValueList) {
        mDeliveryItems.add(
            DeliveryItem(
                delivery.get("label").toString(),
                (delivery.get("code") as Long).toInt(),
                delivery.get("unit").toString().toDouble()
            )
        )
    }

    DateUI(viewModel, calendar)
    if (todayDelivery == 0) {

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Enter your today's delivery",
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.tab_color),
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

        Box() {
            OutlinedTextField(
                value = selectedDelivery,
                onValueChange = { selectedDelivery = it },
                label = { Text("Select Delivery") },
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        "",
                        tint = colorResource(id = R.color.tab_color)
                    )
                },
                enabled = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = MaterialTheme.colors.primary,
                    focusedLabelColor = MaterialTheme.colors.primary,
                    unfocusedLabelColor = MaterialTheme.colors.primary,
                    textColor = MaterialTheme.colors.primary
                ),
                modifier = Modifier
                    .clickable {
                        mExpanded = true
                    }
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textfieldSize = coordinates.size.toSize()
                    }
            )

            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .background(colorResource(id = R.color.beige))
                    .width(with(LocalDensity.current) { textfieldSize.width.toDp() })

            ) {
                mDeliveryItems.forEach { deliveryItem ->
                    DropdownMenuItem(onClick = {
                        mExpanded = false
                        selectedDelivery = deliveryItem.label
                        viewModel.setDelivery(deliveryItem.code,deliveryItem.unit)
                    }) {
                        Text(
                            text = deliveryItem.label,
                            color = colorResource(id = R.color.tab_color)
                        )
                    }

                }
            }
        }
//        appButton(viewModel, text = "No delivery", 1)
//        appButton(viewModel, text = "Half litre", 2)
//        appButton(viewModel, text = "One litre", 3)
    } else {
        var successMsg = ""
        when (todayDelivery) {
            1 -> {
                successMsg = "No delivery"
            }
            2 -> {
                successMsg = "Half litre"
            }
            3 -> {
                successMsg = "One litre"
            }
        }

        val currentDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${
            calendar.get(Calendar.MONTH) + 1
        }/${calendar.get(Calendar.YEAR)}"
        Text(
            text = "You have entered $currentDate delivery!\n\n$successMsg",
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.tab_color),
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
    }
}

@Composable
fun DateUI(viewModel: HomeViewModel, c: Calendar) {

    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    var currentMonthLabel by remember { mutableStateOf("$day/${month + 1}/$year") }

    val datePickerDialog = DatePickerDialog(
        LocalContext.current, DatePickerDialog.OnDateSetListener
        { _, year: Int, month: Int, day: Int ->
            //onUpdateMonth(month+1)
            c.set(Calendar.DAY_OF_MONTH, day)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.YEAR, year)
            currentMonthLabel = "$day/${month + 1}/$year"
            viewModel.fetchDelivery(currentMonthLabel)
        }, year, month, day
    )

    Button(
        onClick = {
            datePickerDialog.show()
        },
        modifier = Modifier
            .width(200.dp)
            .padding(10.dp),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 12.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterEnd),
                contentDescription = "date picker",
                tint = Color.White
            )
            Text(currentMonthLabel, color = Color.White)
        }

    }
}

@Composable
fun appButton(
    viewModel: HomeViewModel,
    text: String,
    value: Int
) {
    Button(
        onClick = {
            viewModel.setDelivery(value,0.0)
        },
        modifier = Modifier
            .width(200.dp)
            .padding(10.dp),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 12.dp,
            end = 20.dp,
            bottom = 12.dp
        )
    ) {
        Text(text, color = Color.White)
    }

}

@Composable
fun ProgressUI(viewModel: HomeViewModel) {
    val showDialog: Boolean by viewModel.showDialog.observeAsState(false)
    if (showDialog)
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
        )
}


