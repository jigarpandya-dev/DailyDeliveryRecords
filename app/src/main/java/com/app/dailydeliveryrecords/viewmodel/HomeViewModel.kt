package com.app.dailydeliveryrecords.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeViewModel : ViewModel() {

    val db = Firebase.firestore
    val calendar = Calendar.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser?.uid

    /* private val today = "${calendar.get(Calendar.DATE)}/${
         calendar.get(Calendar.MONTH) + 1
     }/${calendar.get(Calendar.YEAR)}"*/

    private val _todayDelivery: MutableLiveData<TodayDelivery> = MutableLiveData(TodayDelivery())
    val todayDelivery: LiveData<TodayDelivery> = _todayDelivery

    private val _price: MutableLiveData<Double> = MutableLiveData()
    val price: LiveData<Double> = _price

    private val _notify: MutableLiveData<Boolean> = MutableLiveData()
    val notify: LiveData<Boolean> = _notify

    private val _showDialog: MutableLiveData<Boolean> = MutableLiveData()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _deliveryList: MutableLiveData<List<DocumentSnapshot>> = MutableLiveData()
    val deliveryList: LiveData<List<DocumentSnapshot>> = _deliveryList

    private val _deliveryValueList: MutableLiveData<List<DocumentSnapshot>> =
        MutableLiveData(emptyList())
    val deliveryValueList: LiveData<List<DocumentSnapshot>> = _deliveryValueList


    fun fetchPrice() {
        db.collection("settings")
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data.containsKey("price"))
                        _price.value = (document.data["price"] as Double).toDouble()
                }

            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }

    fun setPrice(price: String) {
        val record = hashMapOf(
            "price" to if (price.isEmpty()) 0 else price.toDouble(),
            "user" to user
        )
        // Add a new document with a generated ID
        db
            .collection("settings")
            .document("$user")
            .set(record, SetOptions.merge())
            .addOnSuccessListener { _ ->
//                coroutineScope.launch {
//                    scaffoldState.snackbarHostState.showSnackbar(
//                        message = "Price updated!"
//                    )
//                }
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error adding document", e)
            }
    }

    fun fetchNotify() {
        db.collection("settings")
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.data.containsKey("notify"))
                        _notify.value = (document.data["notify"] as Boolean)
                }

            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }

    fun setNotify(flag: Boolean) {
        val record = hashMapOf(
            "notify" to flag,
            "user" to user
        )
        // Add a new document with a generated ID
        db
            .collection("settings")
            .document("$user")
            .set(record, SetOptions.merge())
            .addOnSuccessListener { _ ->
                _notify.value = flag
//                coroutineScope.launch {
//                    scaffoldState.snackbarHostState.showSnackbar(
//                        message = "Price updated!"
//                    )
//                }
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error adding document", e)
            }
    }

    fun fetchMonthlyDelivery(month: Int, year: Int) {
        db.collection("records")
            .whereEqualTo("user", user)
            .whereEqualTo("month", month)
            .whereEqualTo("year", year)
            .orderBy("date")
            .get()
            .addOnSuccessListener { documents ->
                _deliveryList.value = documents.documents
                fetchPrice()

            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }

    fun fetchDelivery(todayDate: String? = null) {

        val today = "${calendar.get(Calendar.DATE)}/${
            calendar.get(Calendar.MONTH) + 1
        }/${calendar.get(Calendar.YEAR)}"

        _showDialog.value = true
        db.collection("records")
            .whereEqualTo("date", todayDate ?: today)
            .whereEqualTo("user", user)
            .get()
            .addOnSuccessListener { documents ->
                _showDialog.value = false
                if (documents.isEmpty) {
                    _todayDelivery.value = TodayDelivery(todayDate,0)
                    //if(deliveryValueList.value.isNullOrEmpty())

                }

                for (document in documents) {
                    Log.d("MainActivity", "${document.id} => ${document.data}")
                    _todayDelivery.value = TodayDelivery(todayDate,(document.data["delivery"] as Long).toInt())
                }
                fetchDeliveryValues()

            }
            .addOnFailureListener { exception ->
                _showDialog.value = false
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }

    fun setDelivery(code: Int, unit: Double) {

        if (code != 0) {
            val today = "${calendar.get(Calendar.DATE)}/${
                calendar.get(Calendar.MONTH) + 1
            }/${calendar.get(Calendar.YEAR)}"

            val record = hashMapOf(
                "delivery" to code,
                "unit" to unit,
                "date" to today,
                "day" to calendar.get(Calendar.DAY_OF_MONTH),
                "month" to calendar.get(Calendar.MONTH) + 1,
                "year" to calendar.get(Calendar.YEAR),
                "user" to user
            )
            // Add a new document with a generated ID
            db.collection("records").document("$user${today.replace("/", "")}")
                .set(record)
                .addOnSuccessListener { _ ->
                    _todayDelivery.value = TodayDelivery(null,code)
                }
                .addOnFailureListener { e ->
                    Log.w("MainActivity", "Error adding document", e)
                }
        } else
            _todayDelivery.value = TodayDelivery(null,code)
    }

    private fun fetchDeliveryValues() {
        _showDialog.value = true
        db.collection("delivery_values")
            .get()
            .addOnSuccessListener { documents ->
                _deliveryValueList.value = documents.documents
                _showDialog.value = false

            }
            .addOnFailureListener { exception ->
                _showDialog.value = false
                Log.w("MainActivity", "Error getting documents: ", exception)
            }
    }

    data class TodayDelivery(val today:String?=null,val delivery:Int=0)
}