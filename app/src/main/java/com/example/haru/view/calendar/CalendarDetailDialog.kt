package com.example.haru.view.calendar

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.haru.R
import com.example.haru.data.model.Category
import com.example.haru.databinding.CalendarDetailDialogBinding
import com.example.haru.databinding.CategoryChooseDialogBinding
import com.example.haru.databinding.FragmentAddPostBinding
import com.example.haru.view.adapter.AdapterCalendarDetailDialog
import com.example.haru.view.adapter.AdapterMonth
import com.example.haru.view.adapter.CategoriesAdapterInPost
import com.example.haru.view.checklist.CalendarAddFragment
import java.util.Date
import kotlin.math.abs

class CalendarDetailDialog (val context : Context,
                            val lifecycleOwner: LifecycleOwner,
                            val startDate: Date,
                            val adaptermonth:AdapterMonth,
                            val activity: FragmentActivity
                            ){
    private val dlg = Dialog(context)

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    fun show(height:Int) {
        dlg.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.calendar_detail_dialog)

        dlg.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            dpToPx(context, 480f).toInt()
        )

        dlg.setCanceledOnTouchOutside(true)
        dlg.setCancelable(true)

        val datailViewPager = dlg.findViewById<ViewPager2>(R.id.detail_viewpager2)

        datailViewPager.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        datailViewPager.apply {
            clipToPadding= false
            clipChildren= false
            offscreenPageLimit = 1
            adapter = AdapterCalendarDetailDialog(
                lifecycleOwner,
                datailViewPager,
                startDate,
                adaptermonth,
                activity,
                dlg
            )
        }

        var transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(50))

        transform.addTransformer { page, position ->
            val darkenLayout = page.findViewById<View>(R.id.darken_layout)
            darkenLayout.alpha = abs(position)
        }

        transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
            var v = 1-Math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        })

        datailViewPager.setPageTransformer(transform)

        datailViewPager.setPadding(130,0,130,0)

        datailViewPager.setCurrentItem(Int.MAX_VALUE / 2, false)

        dlg.show()
    }
}