import android.os.Build
import android.os.Bundle
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.haru.R
import kotlin.math.log

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
