package com.example.windy.extensions

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.windy.R
import com.example.windy.models.Alarm
import com.example.windy.models.domain.Daily
import com.example.windy.models.domain.Hourly
import com.example.windy.models.domain.WeatherConditions
import com.example.windy.ui.adapter.AlarmListAdapter
import com.example.windy.ui.adapter.DailyListAdapter
import com.example.windy.ui.adapter.FavoriteListAdapter
import com.example.windy.ui.adapter.HourlyListAdapter
import com.example.windy.util.getImgUrl


@BindingAdapter(value = ["stringToSplit", "delimiter"], requireAll = true)
fun TextView.splitText(stringToSplit: String, delimiter: String) {
    val arrayOfStrings = stringToSplit.split(delimiter)
    if (arrayOfStrings.isNotEmpty())
        (arrayOfStrings[0] + "\n" + arrayOfStrings[1]).also { text = it }
}

@BindingAdapter("setSrcImage")
fun ImageView.setSrcImage(icon: String?) {
    icon?.let {
        Glide.with(this.context)
            .load(getImgUrl(icon))
            .placeholder(R.drawable.ic_baseline_wb_sunny_24).into(this)
    }
}

@BindingAdapter("dailyMaxTempString")
fun TextView.setDailyMaxTempString(item: Daily?) {
    item?.let {
        text = context.resources.getString(
            R.string.max_min_temp,
            item.temp?.max?.toInt().toString(),
            item.temp?.min?.toInt().toString()
        )
    }
}

@BindingAdapter("setDateAndTime")
fun TextView.setAlarmDateTimeString(item: Alarm?) {
    item?.let {
        text = context.resources.getString(
            R.string.date_time,
            item.Date,
            item.startTime,
            item.endTime
        )
    }
}


@BindingAdapter("descImageSrc")
fun ImageView.setWeatherImage(iconName: String?) {
    when {
        iconName?.contains(
            "02d" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.cloud_sun)
        iconName?.contains(
            "09" + "",
            ignoreCase = true
        ) == true || iconName?.contains(
            "10" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.rain)
        iconName?.contains(
            "13" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.snow)
        iconName?.contains(
            "02n" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.cloud_night)
        iconName?.contains(
            "01d" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.sun)
        iconName?.contains(
            "01n" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.night_icon)
        iconName?.contains(
            "11" + "",
            ignoreCase = true
        ) == true -> setImageResource(R.drawable.ic_wind)
        else -> setImageResource(R.drawable.ic_cloud1)
    }
}

@BindingAdapter("favListData")
fun RecyclerView.bindFavRecyclerView(data: List<WeatherConditions>?) {
    val adapter = this.adapter as FavoriteListAdapter
    data?.let {
        adapter.submitList(it)
    }
}

@BindingAdapter("hourListData")
fun RecyclerView.bindHourRecyclerView(data: List<Hourly>?) {
    val adapter = this.adapter as HourlyListAdapter
    data?.let {
        adapter.submitList(it)
    }
}

@BindingAdapter("dayListData")
fun RecyclerView.bindDaysRecyclerView(data: List<Daily>?) {
    val adapter = this.adapter as DailyListAdapter
    data?.let {
        adapter.submitList(it)
    }
}

@BindingAdapter("alarmListData")
fun RecyclerView.bindAlarmRecyclerView(data: List<Alarm>?) {
    val adapter = this.adapter as AlarmListAdapter
    data?.let {
        adapter.submitList(it)
    }
}

@BindingAdapter("setGroupVisibility")
fun Group.bindGroupVisibility(data: List<Alarm>?) {
    if (data.isNullOrEmpty())
        this.makeVisible()
    else
        this.makeGone()

}
