package com.example.middleprojectinter.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.middleprojectinter.R
import com.example.middleprojectinter.data.pref.UserPreference
import com.example.middleprojectinter.databinding.ActivityLoginBinding
import com.example.middleprojectinter.view.ViewModelFactory
import com.example.middleprojectinter.view.main.MainActivity
import com.example.middleprojectinter.view.main.dataStore
import com.example.middleprojectinter.view.signup.RegisterActivity
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LoginActivity::class.java)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityLoginBinding


    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupViewModel()
        setupAction()
        animatedPlay()
    }

    private fun setupView() {
        binding.edtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.btnLogin.isEnabled = s.toString().length >= 8
            }
        })
    }


    private fun setupViewModel() {
        loginViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                UserPreference.setToken(user.tokenAuth)
                MainActivity.start(this)
                finish()
            }
        }
        loginViewModel.login.observe(this) { isSuccess ->
            if (isSuccess) {
                MainActivity.start(this)
                finish()
            }
        }

        loginViewModel.snackbarText.observe(this) { text ->
            when {
                text.contains("Invalid password") -> {
                    binding.btnLogin.error =
                        getString(R.string.invalid_password)
                    binding.btnLogin.requestFocus()
                }
                text.contains("must be a valid email") -> {
                    binding.edtEmail.error =
                        getString(R.string.email_not_valid)
                    binding.edtEmail.requestFocus()
                }
                text.contains("success") -> {
                }
                text.contains("User not found") -> Snackbar.make(binding.root, getString(R.string.user_not_found), Snackbar.LENGTH_SHORT).show()
                else -> Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(value: Boolean) {
        with(binding) {
            btnLogin.isInvisible = value
            btnRegister.isEnabled = !value
            loading.isVisible = value
        }
    }

    private fun animatedPlay() {

        val fadeInDuration = 500L
        val labelAnimator = ObjectAnimator.ofFloat(binding.text, View.TRANSLATION_X, -45f, 45f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val emailAnimator = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(fadeInDuration)
        val passwordAnimator = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(fadeInDuration)
        val loginAnimator = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(fadeInDuration)
        val registerAnimator = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(fadeInDuration)

        val togetherAnimator = AnimatorSet().apply {
            playTogether(labelAnimator)
        }

        AnimatorSet().apply {
            playSequentially(labelAnimator, loginAnimator, emailAnimator, passwordAnimator,registerAnimator, togetherAnimator)
            start()
        }
    }

    private fun setupAction() {
        with(binding) {
            btnRegister.setOnClickListener {
                RegisterActivity.start(this@LoginActivity)
            }

            btnLogin.setOnClickListener {
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                when {
                    email.isEmpty() -> {
                        binding.edtEmail.error = "Enter E-mail"
                    }
                    password.isEmpty() -> {
                        binding.edtPassword.error = "Enter Password"
                    }
                    else -> {
                        with(binding) {
                            edtPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            edtPassword.clearFocus()
                            edtEmail.clearFocus()
                        }
                        loginViewModel.login(email, password)
                    }
                }
            }
        }
    }
}