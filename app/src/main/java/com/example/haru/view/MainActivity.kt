package com.example.haru.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.view.calendar.CalendarFragment
import com.example.haru.view.checklist.ChecklistFragment
import com.example.haru.databinding.ActivityMainBinding
import com.example.haru.view.etc.EtcFragment
import com.example.haru.view.sns.SnsFragment
import com.example.haru.view.timetable.TimetableFragment
import com.example.haru.view.timetable.TodotableFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragments = arrayOfNulls<Fragment>(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragments()
        setDefaultFragment()

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            handleNavigation(menuItem.itemId)
        }

    }

    private fun initFragments() {
        fragments[0] = SnsFragment.newInstance()
        fragments[1] = ChecklistFragment.newInstance()
        fragments[2] = CalendarFragment.newInstance()
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
            2 -> CalendarFragment.newInstance()
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
