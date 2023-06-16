package com.example.haru.view.auth

import BaseActivity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.haru.R
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.databinding.ActivityLoginBinding
import com.example.haru.databinding.ActivitySignUpBinding
import com.example.haru.view.MainActivity
import com.example.haru.view.calendar.CalendarFragment
import kotlinx.coroutines.launch

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    val imm: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.adjustTopMargin(binding.headerTitle.id)

        binding.etSignupId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()

                val typeFace = if (str == "") Typeface.NORMAL else Typeface.BOLD

                binding.etSignupId.setTypeface(null, typeFace)
            }
        })

        binding.etSignupNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()

                val typeFace = if (str == "") Typeface.NORMAL else Typeface.BOLD

                binding.etSignupNickname.setTypeface(null, typeFace)
            }

        })

        binding.etSignupNickname.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                binding.etSignupNickname.clearFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        //

        //기본정보 입력 완료시에 회원가입 성공
        binding.btnSignup.setOnClickListener {

            val haruId = binding.etSignupId.text.toString()
            val nickname = binding.etSignupNickname.text.toString()

            //haruId나 nickname이 ""이면 모달 뜨게하기
            if (haruId == "" || nickname == "") {
                //모달 띄우기
                Toast.makeText(this, "ID와 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                //회원가입 성공
                lifecycleScope.launch {
                    ProfileRepository().editProfileInit(nickname, haruId) {
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText) {
            currentFocus!!.clearFocus()
            return false
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        this.adjustTopMargin(binding.headerTitle.id)
    }


}