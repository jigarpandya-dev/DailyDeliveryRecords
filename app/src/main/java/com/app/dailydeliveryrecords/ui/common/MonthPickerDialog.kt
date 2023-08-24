package com.app.dailydeliveryrecords.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.dailydeliveryrecords.R
import com.app.dailydeliveryrecords.ui.theme.Shapes

@Composable
fun MonthPickerDialog(onCancel: () -> Unit, onUpdateMonth: (Int, Int) -> Unit) {
    val monthList = listOf(
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC",
    )
    val currentMonth = remember {
        mutableStateOf(0)
    }

    val currentYear = remember {
        mutableStateOf(2023)
    }

    Dialog(onDismissRequest = { onCancel() }) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .background(colorResource(id = R.color.beige), Shapes.medium)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

        ) {
            Text(text = "Select month and year", modifier = Modifier.padding(vertical = 10.dp), fontWeight = (FontWeight.Bold), color = colorResource(id = R.color.tab_color))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_prev),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            if (currentMonth.value > 0) {
                                currentMonth.value--
                            }
                        },
                    contentDescription = "previous month",
                )
                Text(
                    modifier = Modifier.width(100.dp),
                    textAlign = TextAlign.Center,
                    text = monthList[currentMonth.value],
                    color = colorResource(id = R.color.tab_color),
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_next),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            if (currentMonth.value < monthList.size - 1) {
                                currentMonth.value++
                            }
                        },
                    contentDescription = "next month",
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_prev),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            currentYear.value--
                        },
                    contentDescription = "previous year",
                )
                Text(
                    modifier = Modifier.width(100.dp),
                    textAlign = TextAlign.Center,
                    text = currentYear.value.toString(),
                    color = colorResource(id = R.color.tab_color),
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_next),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            currentYear.value++
                        },
                    contentDescription = "next year",
                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp),
            ) {
                Button(
                    onClick = {
                        onCancel()
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp),
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onUpdateMonth(currentMonth.value + 1, currentYear.value)
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp),
                ) {
                    Text("OK")
                }
            }
        }
    }
}
