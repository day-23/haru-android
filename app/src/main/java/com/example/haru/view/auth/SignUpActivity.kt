package com.example.haru.view.auth

import BaseActivity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.haru.R
import com.example.haru.data.model.UpdateHaruId
import com.example.haru.data.model.UpdateName
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.TodoRepository
import com.example.haru.databinding.ActivitySignUpBinding
import com.example.haru.view.MainActivity
import kotlinx.coroutines.launch

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val profileRepository = ProfileRepository()
    var checkId: Boolean? = null
    var checkName: Boolean? = null

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

        binding.etSignupId.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                val id = binding.etSignupId.text.toString().replace(" ", "")
                if (id == "") {
                    checkId = null
                    binding.etSignupId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                    binding.tvIdDescription.text = getString(R.string.signUpIdDescription)
                    binding.tvIdDescription.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.light_gray
                        )
                    )
                } else
                    testId(id)
            }
            return@setOnKeyListener false
        }

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
                val name = binding.etSignupNickname.text.toString().replace(" ", "")
                if (name == "") {
                    checkName = null
                    binding.etSignupNickname.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                    binding.tvNickNameDescription.text = ""
                } else
                    testNickName(name)
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
            if (checkId != true || checkName != true) {
                //모달 띄우기
                Toast.makeText(this, "ID와 닉네임을 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                //회원가입 성공
                lifecycleScope.launch {
                    profileRepository.editProfileInit(nickname, haruId) {
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
//
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }


    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText) {
            if (currentFocus == binding.etSignupId) {
                val id = binding.etSignupId.text.toString().replace(" ", "")
                if (id == "") {
                    binding.etSignupId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                    binding.tvIdDescription.text = getString(R.string.signUpIdDescription)
                    binding.tvIdDescription.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.light_gray
                        )
                    )
                    binding.etSignupId.clearFocus()
                    return false
                }
                testId(id)
            } else if (currentFocus == binding.etSignupNickname) {
                val name = binding.etSignupNickname.text.toString().replace(" ", "")
                if (name == "") {
                    binding.etSignupNickname.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                    binding.tvNickNameDescription.text = ""
                    binding.etSignupNickname.clearFocus()
                    return false
                }
                testNickName(name)
            }
            currentFocus!!.clearFocus()

            return false
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onResume() {
        super.onResume()
        this.adjustTopMargin(binding.headerTitle.id)
    }

    fun testId(id: String) {
        lifecycleScope.launch {
            profileRepository.testHaruId(UpdateHaruId(id)) {
                val icon: Drawable?
                val color: Int
                val text: String

                if (it?.success == true) {
                    checkId = true
                    text = "사용 가능한 아이디입니다."
                    icon = ContextCompat.getDrawable(
                        this@SignUpActivity,
                        R.drawable.check_icon
                    )
                    color = ContextCompat.getColor(this@SignUpActivity, R.color.highlight)
                } else {
                    checkId = false
                    text = "이미 존재하는 아이디입니다."
                    icon = ContextCompat.getDrawable(
                        this@SignUpActivity,
                        R.drawable.cancel_icon
                    )
                    color = ContextCompat.getColor(this@SignUpActivity, R.color.delete_red)
                }
                icon?.setTint(color)
                this@SignUpActivity.runOnUiThread {
                    binding.tvIdDescription.text = text
                    binding.tvIdDescription.setTextColor(color)
                    binding.etSignupId.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        icon,
                        null
                    )
                }
            }
        }
    }

    fun testNickName(name: String) {
        lifecycleScope.launch {
            profileRepository.testName(UpdateName(name)) {
                val icon: Drawable?
                val color: Int
                val text: String

                if (it?.success == true) {
                    checkName = true
                    text = "사용 가능한 닉네임입니다."
                    icon = ContextCompat.getDrawable(
                        this@SignUpActivity,
                        R.drawable.check_icon
                    )
                    color = ContextCompat.getColor(this@SignUpActivity, R.color.highlight)
                } else {
                    checkName = false
                    text = "사용 불가능한 닉네임입니다."
                    icon = ContextCompat.getDrawable(
                        this@SignUpActivity,
                        R.drawable.cancel_icon
                    )
                    color = ContextCompat.getColor(this@SignUpActivity, R.color.delete_red)
                }
                this@SignUpActivity.runOnUiThread {
                    binding.tvNickNameDescription.text = text
                    binding.tvNickNameDescription.setTextColor(color)
                    binding.etSignupNickname.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        icon,
                        null
                    )
                }
            }
        }
    }


}