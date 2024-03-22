package com.app.dailydeliveryrecords.ui.bottomnav

import android.os.Handler
import android.os.Looper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.dailydeliveryrecords.R
import com.app.dailydeliveryrecords.viewmodel.HomeViewModel
import java.text.DecimalFormat
import java.util.Calendar

@Composable
fun ReceiptScreen(viewModel: HomeViewModel) {
    val c = Calendar.getInstance()
    val month = c.get(Calendar.MONTH) + 1
    val year = c.get(Calendar.YEAR)
    viewModel.fetchMonthlyDelivery(month, year)

    ReceiptsUI(viewModel)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReceiptsUI(viewModel: HomeViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var total = 0.0
    for (item in uiState.deliveryList) {
        total += uiState.price * item.get("unit").toString().toDouble()
    }

    var rotate by remember { mutableStateOf(0f) }
    val flip by animateFloatAsState(rotate, animationSpec = tween(800))

    LaunchedEffect(key1 = true, block = {
        rotate += 180
        Handler(Looper.getMainLooper()).postDelayed({
            rotate += 180
        }, 1000)
    })

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        AnimatedVisibility(visible = true, enter = scaleIn()) {
            Card(
                Modifier.graphicsLayer {
                    rotationX = flip
                },
                shape = RoundedCornerShape(10.dp),
                backgroundColor = colorResource(id = R.color.tab_color),
                elevation = 10.dp,

            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_receipt),
                        contentDescription = stringResource(id = R.string.app_name),
                        colorFilter = ColorFilter.tint(colorResource(id = R.color.beige)),
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .align(Alignment.CenterHorizontally),
                    )

                    Text(
                        text = "Your bill for this month is ${DecimalFormat("#.##").format(total)}/-",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.beige),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}
