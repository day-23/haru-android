import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haru.R
import kotlin.math.log

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //가로모드 방지
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)

            window.decorView.post {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }


    // Add this function to BaseActivity
    fun adjustTopMargin(viewId: Int) {
        Log.d("Padding", "$viewId")
        findViewById<View>(viewId)?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val statusBarHeight: Int
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                } else {
                    statusBarHeight = insets.systemWindowInsetTop
                }
                val paddingLeft = view.paddingLeft
                val paddingRight = view.paddingRight
                val paddingBottom = view.paddingBottom

                // Set the padding, preserving the current left, right and bottom padding values
                view.setPadding(paddingLeft, (statusBarHeight * 1.4).toInt(), paddingRight, paddingBottom)
                Log.d("Padding", "$statusBarHeight")
                insets
            }
        }
    }

    fun adjustTopMargin(viewId: Int, ratio : Float) {
        findViewById<View>(viewId)?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { view, insets ->
                val statusBarHeight: Int
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                } else {
                    statusBarHeight = insets.systemWindowInsetTop
                }
                val paddingLeft = view.paddingLeft
                val paddingRight = view.paddingRight
                val paddingBottom = view.paddingBottom

                // Set the padding, preserving the current left, right and bottom padding values
                view.setPadding(paddingLeft, (statusBarHeight * ratio).toInt(), paddingRight, paddingBottom)
                Log.d("Padding", "$statusBarHeight")
                insets
            }
        }
    }

}
