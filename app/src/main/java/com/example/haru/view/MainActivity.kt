package com.example.haru.view

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.view.calendar.CalendarFragment
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.databinding.ActivityMainBinding
import com.example.haru.view.calendar.calendarMainData
import com.example.haru.view.etc.EtcFragment
import com.example.haru.view.sns.SnsFragment
import com.example.haru.view.timetable.TimetableFragment

class MainActivity : AppCompatActivity(){
    private val fragments = arrayOfNulls<Fragment>(5)

    private lateinit var sharedPreference: SharedPreferences
    private lateinit var editor: Editor

    companion object{
        private lateinit var binding: ActivityMainBinding
        fun hideNavi(state: Boolean){
            if (state)
                binding.bottomNav.visibility = View.GONE
            else binding.bottomNav.visibility = View.VISIBLE
        }
    }

    override fun onPause(){
        editor.putBoolean("todoApply", calendarMainData.todoApply)
        editor.putBoolean("scheduleApply", calendarMainData.scheduleApply)
        editor.putBoolean("unclassifiedCategory", calendarMainData.unclassifiedCategory)
        editor.putBoolean("todoComplete", calendarMainData.todoComplete)
        editor.putBoolean("todoInComplete", calendarMainData.todoInComplete)
        editor.apply()

        super.onPause()
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
        calendarMainData.unclassifiedCategory = sharedPreference.getBoolean("unclassifiedCategory", true)
        calendarMainData.todoComplete = sharedPreference.getBoolean("todoComplete", true)
        calendarMainData.todoInComplete = sharedPreference.getBoolean("todoInComplete", true)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText){
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
            .replace(R.id.fragments_frame, fragments[0]!!)
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
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_frame, fragment)
            .commit()
    }
}
