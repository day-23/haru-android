package com.example.haru.view.auth

import BaseActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.databinding.ActivityLoginBinding
import com.example.haru.databinding.ActivitySignUpBinding
import com.example.haru.view.MainActivity
import kotlinx.coroutines.launch

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.adjustTopMargin(binding.headerTitle.id)

        //기본정보 입력 완료시에 회원가입 성공
        binding.btnSignup.setOnClickListener {

            val haruId = binding.etSignupId.text.toString()
            val nickname = binding.etSignupNickname.text.toString()

            //haruId나 nickname이 ""이면 모달 뜨게하기
            if(haruId == "" || nickname == ""){
                //모달 띄우기
                Toast.makeText(this, "ID와 닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else{
                //회원가입 성공
                lifecycleScope.launch {
                    ProfileRepository().editProfileInit(nickname, haruId){
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

    override fun onResume() {
        super.onResume()
        this.adjustTopMargin(binding.headerTitle.id)
    }


}