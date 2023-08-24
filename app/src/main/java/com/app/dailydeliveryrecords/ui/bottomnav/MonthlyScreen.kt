package com.app.dailydeliveryrecords.ui.bottomnav

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.app.dailydeliveryrecords.R
import com.app.dailydeliveryrecords.model.DeliveryItem
import com.app.dailydeliveryrecords.ui.common.MonthPickerDialog
import com.app.dailydeliveryrecords.viewmodel.HomeViewModel
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonthlyScreen(viewModel: HomeViewModel) {
    val c = Calendar.getInstance()
    val month = c.get(Calendar.MONTH) + 1
    val year = c.get(Calendar.YEAR)

    val mDeliveryItems = arrayListOf<DeliveryItem>()
    val deliveryValueList: List<DocumentSnapshot> by viewModel.deliveryValueList.observeAsState(
        emptyList(),
    )

    for (delivery in deliveryValueList) {
        mDeliveryItems.add(
            DeliveryItem(
                delivery.get("label").toString(),
                (delivery.get("code") as Long).toInt(),
                delivery.get("unit").toString().toDouble(),
            ),
        )
    }

    viewModel.fetchMonthlyDelivery(month, year)
    MonthlyUI(viewModel, c, mDeliveryItems) {
        viewModel.fetchMonthlyDelivery(it, year)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun MonthlyUI(
    viewModel: HomeViewModel,
    c: Calendar,
    mDeliveryItems: List<DeliveryItem>,
    onUpdateMonth: (Int) -> Unit,
) {
    val deliveryList: List<DocumentSnapshot> by viewModel.deliveryList.observeAsState(emptyList())
    val year = c.get(Calendar.YEAR)
    var currentMonthLabel by remember { mutableStateOf("${SimpleDateFormat("MMM").format(c.time)} $year") }
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        MonthPickerDialog(onCancel = {
            showDialog.value = false
        }, onUpdateMonth = { month: Int, year: Int ->
            showDialog.value = false
            c.set(Calendar.MONTH, month - 1)
            c.set(Calendar.YEAR, year)
            currentMonthLabel = "${SimpleDateFormat("MMM").format(c.time)} $year"
            viewModel.fetchMonthlyDelivery(month, year)
        })
    }

    /* val datePickerDialog = DatePickerDialog(
         LocalContext.current, DatePickerDialog.OnDateSetListener
         { _: DatePicker, year: Int, month: Int, day: Int ->
             onUpdateMonth(month + 1)
             c.set(Calendar.DAY_OF_MONTH, day)
             c.set(Calendar.MONTH, month)
             c.set(Calendar.YEAR, year)
             currentMonthLabel = "${SimpleDateFormat("MMM").format(c.time)} $year"
         }, year, month, day
     )*/

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = {
                    // datePickerDialog.show()
                    showDialog.value = true
                },
                modifier = Modifier
                    .width(200.dp)
                    .padding(10.dp),
                contentPadding = PaddingValues(
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
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterEnd),
                        contentDescription = "date picker",
                        tint = Color.White,
                    )
                    Text(currentMonthLabel, color = Color.White)
                }
            }

            val context = LocalContext.current
            Image(
                painter = painterResource(id = R.drawable.ic_share),
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        if (deliveryList.isNotEmpty()) {
                            var report = ""
                            for (delivery in deliveryList) {
                                report += delivery
                                    .get("date")
                                    .toString() + "   " + mDeliveryItems.find {
                                    it.code == (
                                        delivery.get(
                                            "delivery",
                                        ) as Long
                                        ).toInt()
                                } + "\n"
                            }

                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Report for $currentMonthLabel\n\n$report",
                                )
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            startActivity(context, shareIntent, null)
                        }
                    },
                contentDescription = "share report",
            )
        }

        if (deliveryList.isEmpty()) {
            Text(
                text = "No records found.",
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.tab_color),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
        }

        AnimatedVisibility(visible = deliveryList.isNotEmpty(), enter = scaleIn(), exit = scaleOut()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(deliveryList.size, key = {
                    it
                }) { index ->
                    Box(
                        modifier = Modifier
                            .animateItemPlacement(
                                tween(
                                    durationMillis = 1000,
                                    easing = LinearEasing,
                                ),
                            )
                            .fillMaxWidth()
                            .padding(10.dp)
                            .background(
                                color = colorResource(id = R.color.tab_color),
                                shape = RoundedCornerShape(4.dp),
                            ),
                    ) {
                        Text(
                            text = deliveryList[index].get("date").toString(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            color = Color.White,
                            textAlign = TextAlign.Start,
                            fontSize = 20.sp,
                        )

                        Text(
                            text = mDeliveryItems.find { it.code == (deliveryList[index].get("delivery") as Long).toInt() }?.label
                                ?: "",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            color = Color.White,
                            textAlign = TextAlign.End,
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        }
    }
}
