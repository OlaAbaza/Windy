package com.example.windy.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.windy.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*


fun dateFormat(milliSeconds: Int): String {
    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds.toLong() * 1000
    val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val year = calendar.get(Calendar.YEAR).toString()
    return day + month + year

}

@SuppressLint("SimpleDateFormat")
fun timeFormat(millisSeconds: Int): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = (millisSeconds * 1000).toLong()
    val format = SimpleDateFormat("hh:00 aaa")
    return format.format(calendar.time)
}

fun isConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }

}

fun cancelAlarm(alarmId: Int, context: Context) {
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}

fun swipeToDeleteFunction(
    onSwipe: (position: Int) -> Unit,
    getSwipeDirs: ((position: Int) -> Boolean?)? = null
): ItemTouchHelper {
    val itemTouchHelperCallback =
        object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                getSwipeDirs?.let {
                    if (getSwipeDirs(viewHolder.adapterPosition) == true) {
                        return 0
                    }
                }
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onSwipe(viewHolder.adapterPosition)
            }

        }
    return ItemTouchHelper(itemTouchHelperCallback)
}


fun getImgUrl(iconName: String): String =
    "https://openweathermap.org/img/wn/$iconName@2x.png"
