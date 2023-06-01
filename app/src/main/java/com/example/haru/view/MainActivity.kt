package com.example.haru.view

import BaseActivity
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.view.calendar.CalendarFragment
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.databinding.ActivityMainBinding
import com.example.haru.utils.User
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.view.etc.AlarmWorker
import com.example.haru.view.etc.EtcFragment
import com.example.haru.view.sns.SnsFragment
import com.example.haru.view.timetable.TimetableFragment
import com.example.haru.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity(){
    private val fragments = arrayOfNulls<Fragment>(5)

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: Editor

    companion object {
        private lateinit var binding: ActivityMainBinding
        fun hideNavi(state: Boolean) {
            if (state)
                binding.bottomNav.visibility = View.GONE
            else binding.bottomNav.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        editor.putBoolean("todoApply", calendarMainData.todoApply)
        editor.putBoolean("scheduleApply", calendarMainData.scheduleApply)
        editor.putBoolean("unclassifiedCategory", calendarMainData.unclassifiedCategory)
        editor.putBoolean("todoComplete", calendarMainData.todoComplete)
        editor.putBoolean("todoInComplete", calendarMainData.todoInComplete)
        editor.putString("userId", User.id)
        editor.putBoolean("alarmAprove", User.alarmAprove)
        editor.apply()

        initAlarm()

        super.onPause()
    }

    fun initAlarm(){
        if(User.alarmAprove) {
            Log.d("알람", "알람 설정")
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            val intent = Intent(this, AlarmWorker::class.java)
            if (User.id != "") {
                intent.putExtra("userId", User.id)
                val pendingIntent = PendingIntent.getBroadcast(
                    this, 0, intent,
                    PendingIntent.FLAG_MUTABLE
                )

                val calendar = Calendar.getInstance()
//                val todaytime = calendar.time
//
//                todaytime.hours = 9
//                todaytime.minutes = 0
//                todaytime.seconds = 0
//
//                if (calendar.time.after(todaytime)){
//                    calendar.add(Calendar.DATE, 1)
//                }
//
//                calendar.apply {
//                    set(Calendar.HOUR_OF_DAY, 9)
//                    set(Calendar.MINUTE, 0)
//                    set(Calendar.SECOND, 0)
//                }

                calendar.add(Calendar.SECOND, 20)

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(baseContext, AlarmWorker::class.java)

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                PendingIntent.getBroadcast(baseContext,0,intent,PendingIntent.FLAG_IMMUTABLE)
            }else{
                PendingIntent.getBroadcast(baseContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
            }

            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragments()
        setDefaultFragment()

        binding.bottomNav.itemIconTintList = null

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            handleNavigation(menuItem.itemId)
        }

        sharedPreference = getSharedPreferences("ApplyData", 0)
        editor = sharedPreference.edit()

        calendarMainData.todoApply = sharedPreference.getBoolean("todoApply", true)
        calendarMainData.scheduleApply = sharedPreference.getBoolean("scheduleApply", true)
        calendarMainData.unclassifiedCategory =
            sharedPreference.getBoolean("unclassifiedCategory", true)
        calendarMainData.todoComplete = sharedPreference.getBoolean("todoComplete", true)
        calendarMainData.todoInComplete = sharedPreference.getBoolean("todoInComplete", true)
        User.alarmAprove = sharedPreference.getBoolean("alarmAprove", true)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText) {
            currentFocus!!.clearFocus()
            return false
        }

        val fragment = fragments[2] as CalendarFragment

        fragment.closeAddBtn(ev)

        return super.dispatchTouchEvent(ev)
    }

    private fun initFragments() {
        fragments[0] = SnsFragment.newInstance()
        fragments[1] = ChecklistFragment.newInstance()
        fragments[2] = CalendarFragment.newInstance(this)
        fragments[3] = TimetableFragment.newInstance()
        fragments[4] = EtcFragment.newInstance()
    }

    private fun setDefaultFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_frame, fragments[2]!!)
            .commit()

        binding.bottomNav.selectedItemId = R.id.menu_calendar;
    }

    private fun handleNavigation(itemId: Int): Boolean {
        val index = when (itemId) {
            R.id.menu_sns -> 0
            R.id.menu_checklist -> 1
            R.id.menu_calendar -> 2
            R.id.menu_timetable -> 3
            R.id.menu_etc -> 4
            else -> -1
        }

        if (index >= 0) {
            var fragment = fragments[index]
            if (fragment == null) {
                fragment = createFragment(index)
                fragments[index] = fragment
            }
            replaceFragment(fragment)
            return true
        }
        return false
    }

    private fun createFragment(index: Int): Fragment {
        return when (index) {
            0 -> SnsFragment.newInstance()
            1 -> ChecklistFragment.newInstance()
            2 -> CalendarFragment.newInstance(this)
            3 -> TimetableFragment.newInstance()
            4 -> EtcFragment.newInstance()
            else -> throw IllegalStateException("Unexpected fragment index $index")
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        while(supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStackImmediate()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_frame, fragment)
            .commit()
    }
}
