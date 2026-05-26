package com.seunome.cinesound.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.seunome.cinesound.R
import com.seunome.cinesound.ui.MainActivity

// =============================================
// CINESOUND — RegisterActivity
// Cria conta com Firebase Auth + salva perfil no Firestore
// =============================================
class RegisterActivity : AppCompatActivity() {

    // Firebase
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // Views
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var checkboxTerms: MaterialCheckBox
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var tvGoToLogin: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var cardError: MaterialCardView
    private lateinit var tvErrorMessage: TextView
    private lateinit var strengthContainer: View
    private lateinit var tvStrengthLabel: TextView
    private lateinit var bar1: View
    private lateinit var bar2: View
    private lateinit var bar3: View
    private lateinit var bar4: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        bindViews()
        setupListeners()
    }

    // =============================================
    // Vincula as views pelo ID
    // =============================================
    private fun bindViews() {
        tilName            = findViewById(R.id.til_name)
        tilEmail           = findViewById(R.id.til_email)
        tilPassword        = findViewById(R.id.til_password)
        tilConfirmPassword = findViewById(R.id.til_confirm_password)
        etName             = findViewById(R.id.et_name)
        etEmail            = findViewById(R.id.et_email)
        etPassword         = findViewById(R.id.et_password)
        etConfirmPassword  = findViewById(R.id.et_confirm_password)
        checkboxTerms      = findViewById(R.id.checkbox_terms)
        btnRegister        = findViewById(R.id.btn_register)
        btnBack            = findViewById(R.id.btn_back)
        tvGoToLogin        = findViewById(R.id.tv_go_to_login)
        progressBar        = findViewById(R.id.progress_bar)
        cardError          = findViewById(R.id.card_error)
        tvErrorMessage     = findViewById(R.id.tv_error_message)
        strengthContainer  = findViewById(R.id.password_strength_container)
        tvStrengthLabel    = findViewById(R.id.tv_strength_label)
        bar1               = findViewById(R.id.bar1)
        bar2               = findViewById(R.id.bar2)
        bar3               = findViewById(R.id.bar3)
        bar4               = findViewById(R.id.bar4)
    }

    // =============================================
    // Configura todos os listeners
    // =============================================
    private fun setupListeners() {

        // Voltar para Login
        btnBack.setOnClickListener { finish() }
        tvGoToLogin.setOnClickListener { finish() }

        // Validação em tempo real ao sair dos campos (onFocusChange)
        etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateName()
        }
        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateEmail()
        }
        etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateConfirmPassword()
        }

        // Indicador de força da senha
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s?.toString() ?: ""
                if (password.isEmpty()) {
                    strengthContainer.visibility = View.GONE
                } else {
                    strengthContainer.visibility = View.VISIBLE
                    updatePasswordStrength(password)
                }
                updateButtonState()
            }
        })

        // Habilitar botão somente com todos os campos válidos + checkbox marcado
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { updateButtonState() }
        }
        etName.addTextChangedListener(watcher)
        etEmail.addTextChangedListener(watcher)
        etConfirmPassword.addTextChangedListener(watcher)
        checkboxTerms.setOnCheckedChangeListener { _, _ -> updateButtonState() }

        // Botão cadastrar
        btnRegister.setOnClickListener { attemptRegister() }
    }

    // =============================================
    // Indicador de força de senha
    // =============================================
    private fun updatePasswordStrength(password: String) {
        val strength = calculateStrength(password)
        val colorWeak   = ContextCompat.getColor(this, R.color.red_negative)
        val colorMedium = ContextCompat.getColor(this, R.color.yellow_star)
        val colorStrong = ContextCompat.getColor(this, R.color.green_positive)
        val colorEmpty  = ContextCompat.getColor(this, R.color.border_medium)

        when (strength) {
            1 -> { // Fraca
                bar1.backgroundTintList = android.content.res.ColorStateList.valueOf(colorWeak)
                bar2.backgroundTintList = android.content.res.ColorStateList.valueOf(colorEmpty)
                bar3.backgroundTintList = android.content.res.ColorStateList.valueOf(colorEmpty)
                bar4.backgroundTintList = android.content.res.ColorStateList.valueOf(colorEmpty)
                tvStrengthLabel.text = "Fraca"
                tvStrengthLabel.setTextColor(colorWeak)
            }
            2 -> { // Razoável
                bar1.backgroundTintList = android.content.res.ColorStateList.valueOf(colorMedium)
                bar2.backgroundTintList = android.content.res.ColorStateList.valueOf(colorMedium)
                bar3.backgroundTintList = android.content.res.ColorStateList.valueOf(colorEmpty)
                bar4.backgroundTintList = android.content.res.ColorStateList.valueOf(colorEmpty)
                tvStrengthLabel.text = "Razoável"
                tvStrengthLabel.setTextColor(colorMedium)
            }
            3 -> { // Boa
                bar1.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                bar2.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                bar3.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                bar4.backgroundTintList = android.content.res.ColorStateList.valueOf(colorEmpty)
                tvStrengthLabel.text = "Boa"
                tvStrengthLabel.setTextColor(colorStrong)
            }
            4 -> { // Forte
                bar1.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                bar2.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                bar3.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                bar4.backgroundTintList = android.content.res.ColorStateList.valueOf(colorStrong)
                tvStrengthLabel.text = "Forte"
                tvStrengthLabel.setTextColor(colorStrong)
            }
        }
    }

    private fun calculateStrength(password: String): Int {
        var score = 0
        if (password.length >= 6)  score++
        if (password.length >= 10) score++
        if (password.any { it.isDigit() } && password.any { it.isLetter() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++
        return score.coerceAtLeast(1)
    }

    // =============================================
    // Validações individuais de campo
    // =============================================
    private fun validateName(): Boolean {
        val name = etName.text.toString().trim()
        return if (name.length < 2) {
            tilName.error = "Informe seu nome completo"
            false
        } else {
            tilName.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = etEmail.text.toString().trim()
        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Informe um e-mail válido"
            false
        } else {
            tilEmail.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = etPassword.text.toString()
        return if (password.length < 6) {
            tilPassword.error = "A senha deve ter pelo menos 6 caracteres"
            false
        } else {
            tilPassword.error = null
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val password = etPassword.text.toString()
        val confirm  = etConfirmPassword.text.toString()
        return if (password != confirm) {
            tilConfirmPassword.error = "As senhas não coincidem"
            false
        } else {
            tilConfirmPassword.error = null
            true
        }
    }

    private fun formIsValid() =
        validateName() && validateEmail() && validatePassword() && validateConfirmPassword()

    private fun updateButtonState() {
        val allFilled = etName.text?.isNotBlank() == true &&
                etEmail.text?.isNotBlank() == true &&
                etPassword.text?.isNotBlank() == true &&
                etConfirmPassword.text?.isNotBlank() == true
        btnRegister.isEnabled = allFilled && checkboxTerms.isChecked
    }

    // =============================================
    // Tentativa de cadastro no Firebase
    // =============================================
    private fun attemptRegister() {
        if (!formIsValid()) return

        val name     = etName.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        setLoading(true)
        hideError()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user ?: return@addOnSuccessListener

                // Atualiza o displayName no Firebase Auth
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdate)

                // Salva perfil no Firestore: users/{uid}
                val userProfile = hashMapOf(
                    "uid"       to user.uid,
                    "name"      to name,
                    "email"     to email,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                db.collection("users").document(user.uid)
                    .set(userProfile)
                    .addOnCompleteListener {
                        setLoading(false)
                        goToMain()
                    }
            }
            .addOnFailureListener { exception ->
                setLoading(false)
                showError(mapFirebaseError(exception.message))
            }
    }

    // =============================================
    // Mapeia erros do Firebase para mensagens amigáveis
    // =============================================
    private fun mapFirebaseError(message: String?): String = when {
        message?.contains("email-already-in-use") == true ->
            "Este e-mail já está cadastrado. Tente fazer login."
        message?.contains("invalid-email") == true ->
            "Endereço de e-mail inválido."
        message?.contains("weak-password") == true ->
            "Senha muito fraca. Use pelo menos 6 caracteres."
        message?.contains("network") == true ->
            "Sem conexão. Verifique sua internet e tente novamente."
        else -> "Erro ao criar conta. Tente novamente."
    }

    // =============================================
    // Helpers de UI
    // =============================================
    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnRegister.visibility = if (loading) View.INVISIBLE else View.VISIBLE
        btnRegister.isEnabled  = !loading
    }

    private fun showError(message: String) {
        tvErrorMessage.text = message
        cardError.visibility = View.VISIBLE
    }

    private fun hideError() {
        cardError.visibility = View.GONE
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
