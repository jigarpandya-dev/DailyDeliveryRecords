package com.app.dailydeliveryrecords.model

import kotlinx.serialization.Serializable

@Serializable
data class DeliveryItem (val label:String,val code:Int,val unit:Double)