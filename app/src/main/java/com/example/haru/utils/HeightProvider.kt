package com.example.haru.utils

import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow


class HeightProvider(private val mActivity: Activity) : PopupWindow(mActivity),
    OnGlobalLayoutListener {
    private val rootView = View(mActivity)
    private var listener: HeightListener? = null
    private var heightMax = 0

    init {
        contentView = rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setBackgroundDrawable(ColorDrawable(0))
        width = 0
        height = LinearLayout.LayoutParams.MATCH_PARENT
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
    }

    fun init(): HeightProvider {
        if (!isShowing) {
            val view: View = mActivity.window.decorView
            view.post(Runnable { showAtLocation(view, Gravity.NO_GRAVITY, 0, 0) })
        }
        return this
    }

    fun setHeightListener(listener: HeightListener?): HeightProvider {
        this.listener = listener
        return this
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        Log.e("20191627", "rect bottom : ${rect.bottom}")
        Log.e("20191627", "rect top : ${rect.top}")
        if (rect.bottom > heightMax) {
            heightMax = rect.bottom
        }
        Log.e("20191627", "heightMax : ${heightMax}")
        val keyboardHeight: Int = heightMax - rect.bottom
        if (listener != null) {
            listener!!.onHeightChanged(keyboardHeight, heightMax)
        }
    }

    interface HeightListener {
        fun onHeightChanged(height: Int, maxheight : Int)
    }
}