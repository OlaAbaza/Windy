package com.example.windy.ui.fragment

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.windy.R
import com.example.windy.adapter.AlarmListAdapter
import com.example.windy.database.Alarm
import com.example.windy.database.WeatherDatabase
import com.example.windy.databinding.AlarmDialogBinding
import com.example.windy.databinding.AlarmFragmentBinding
import com.example.windy.receiver.AlarmReceiver
import com.example.windy.util.Constant.ALARM_END_TIME
import com.example.windy.util.Constant.ALARM_ID
import com.example.windy.util.Constant.EVENT
import com.example.windy.util.Constant.SOUND
import com.example.windy.util.cancelAlarm
import com.example.windy.ui.viewModel.AlarmViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class AlarmFragment : Fragment() {

    private lateinit var binding: AlarmFragmentBinding
    private lateinit var bindingDialog: AlarmDialogBinding
    private lateinit var alarmListAdapter: AlarmListAdapter
    private var alarmStartTime = Calendar.getInstance()
    private var alarmEndTime = Calendar.getInstance()
    private var alarmDate = Calendar.getInstance()
    private var alarmId: Int? = null
    private val viewModel by lazy {
        val application = requireNotNull(activity).application
        val weatherDatabase = WeatherDatabase.getInstance(application)
        ViewModelProvider(this, AlarmViewModel.Factory(weatherDatabase)).get(
            AlarmViewModel::class.java
        )
    }
    private val isAlarmTimeValid by lazy {
        alarmStartTime.timeInMillis < alarmEndTime.timeInMillis
                && alarmDate.timeInMillis > Calendar.getInstance().timeInMillis
    }
    private val itemTouchHelper by lazy {
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

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = alarmListAdapter.currentList[position]
                    viewModel.deleteAlarmItem(item.id)
                    showSnackBar(item, position)

                }

            }
        ItemTouchHelper(itemTouchHelperCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AlarmFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        alarmListAdapter = AlarmListAdapter(AlarmListAdapter.AlarmListener { alarmItem ->
            viewModel.updateAlarmDetails(alarmItem)
        })
        binding.apply {
            lifecycleOwner = this@AlarmFragment
            alarmViewModel = viewModel
            itemTouchHelper.attachToRecyclerView(rvAlarms)
            rvAlarms.adapter = alarmListAdapter
            addBtn.setOnClickListener {
                showAlarmDialog(null)
            }
        }

        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, { alarmItem ->
            alarmItem?.let {
                alarmId = alarmItem.id
                showAlarmDialog(alarmItem)
            }
        })
        viewModel.getAlarmItem.observe(viewLifecycleOwner, { alarm ->
            context?.let { _ ->
                setAlarm(
                    alarm.id,
                    alarmStartTime,
                    alarmEndTime,
                    alarm.event,
                    alarm.sound
                )
            }
        })
    }

    private fun showAlarmDialog(alarm: Alarm?) {
        val dialog = context?.let { Dialog(it) }
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val layoutParams = WindowManager.LayoutParams()
        // change dialog size
        layoutParams.copyFrom(dialog?.window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        // init views
        bindingDialog = AlarmDialogBinding.inflate(layoutInflater)
        bindingDialog.alarm = alarm
        dialog?.setContentView(bindingDialog.root)

        alarmDate = bindingDialog.calenderBtn.showDatePicker()
        alarmStartTime = bindingDialog.startTimeBtn.showTimePicker()
        alarmEndTime = bindingDialog.endTimeBtn.showTimePicker()
        setAlarmDate()
        bindingDialog.closeBtn.setOnClickListener {
            dialog?.dismiss()
        }
        bindingDialog.addAlarmBtn.setOnClickListener {
            if (validateDialogFields()) {
                if (isAlarmTimeValid) {
                    insertAlarmItem()
                    dialog?.dismiss()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.validate_time_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        dialog?.show()
        dialog?.window?.attributes = layoutParams
    }

    private fun setAlarmDate() {
        alarmStartTime.set(Calendar.YEAR, alarmDate.get(Calendar.YEAR))
        alarmStartTime.set(Calendar.MONTH, alarmDate.get(Calendar.MONTH))
        alarmStartTime.set(Calendar.DAY_OF_MONTH, alarmDate.get(Calendar.DAY_OF_MONTH))

        alarmEndTime.set(Calendar.YEAR, alarmDate.get(Calendar.YEAR))
        alarmEndTime.set(Calendar.MONTH, alarmDate.get(Calendar.MONTH))
        alarmEndTime.set(Calendar.DAY_OF_MONTH, alarmDate.get(Calendar.DAY_OF_MONTH))

    }

    private fun insertAlarmItem() {
        val alarmItem = Alarm(
            getAlarmEvent(),
            bindingDialog.calenderBtn.text.toString(),
            bindingDialog.startTimeBtn.text.toString(),
            bindingDialog.endTimeBtn.text.toString(),
            getAlarmSound(),
            bindingDialog.DescTv.text.toString()
        )

        viewModel.insertAlarmItem(alarmItem.apply {
            alarmId?.let {
                id = it
            }
        })
    }

    private fun showSnackBar(alarm: Alarm, position: Int) {
        Snackbar.make(
            binding.alarmLayout,
            getString(R.string.deleted),
            Snackbar.LENGTH_LONG
        ).apply {
            setAction(getString(R.string.undo)) {
                viewModel.insertAlarmItem(alarm)
                binding.rvAlarms.scrollToPosition(position)
            }
            addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        cancelAlarm(alarm.id, context)
                    }

                }
            })

            setTextColor(Color.parseColor(R.color.white.toString()))
            setActionTextColor(Color.parseColor(R.color.purple_200.toString()))
            setBackgroundTint(Color.parseColor(R.color.gray.toString()))
            duration.minus(1)
        }.show()
    }


    @SuppressLint("SimpleDateFormat")
    private fun TextView.showTimePicker(): Calendar {
        val calendar = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            this.text =
                SimpleDateFormat(context.getString(R.string.time_format)).format(calendar.time)
        }

        this.setOnClickListener {
            TimePickerDialog(
                context,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context)
            ).show()
        }

        return calendar
    }

    @SuppressLint("SimpleDateFormat")
    private fun TextView.showDatePicker(): Calendar {
        val calendar = Calendar.getInstance()

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                this.text =
                    SimpleDateFormat(context.getString(R.string.date_format)).format(calendar.time)

            }

        this.setOnClickListener {
            DatePickerDialog(
                context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        return calendar
    }

    @SuppressLint("ShortAlarm")
    private fun setAlarm(
        alarmId: Int,
        startTime: Calendar,
        endTime: Calendar,
        event: String,
        isAlarmSound: Boolean
    ) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(ALARM_END_TIME, endTime.timeInMillis)
            putExtra(ALARM_ID, alarmId)
            putExtra(EVENT, event)
            putExtra(SOUND, isAlarmSound)
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, startTime.timeInMillis,
            (2 * 1000).toLong(), pendingIntent
        )
    }

    private fun getAlarmEvent(): String {
        val events = this.resources.getStringArray(R.array.event_options)
        return events[bindingDialog.eventSpinner.selectedItemPosition]

    }

    private fun getAlarmSound(): Boolean {
        return when (bindingDialog.soundSpinner.selectedItemPosition) {
            0 -> false// notification
            1 -> true // alarm loopSound
            else -> false
        }
    }

    private fun validateDialogFields(): Boolean {
        when {
            bindingDialog.toTime.text.isEmpty() ||
                    bindingDialog.fromTime.text.isEmpty() -> Toast.makeText(
                context,
                getString(R.string.empty_time),
                Toast.LENGTH_SHORT
            ).show()
            bindingDialog.calenderBtn.text.isEmpty() -> Toast.makeText(
                context,
                getString(R.string.empty_date),
                Toast.LENGTH_SHORT
            ).show()
            else -> return true
        }
        return false
    }

}