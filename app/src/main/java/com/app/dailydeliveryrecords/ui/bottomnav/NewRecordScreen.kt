package com.app.dailydeliveryrecords.ui.bottomnav

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.app.dailydeliveryrecords.R


@Composable
fun NewRecordScreen(){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("Add/Update Record", color = colorResource(id = R.color.tab_color))
    }

}