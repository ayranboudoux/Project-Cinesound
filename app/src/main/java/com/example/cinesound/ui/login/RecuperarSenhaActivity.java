package com.example.cinesound.ui.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.R;

public class RecuperarSenhaActivity extends AppCompatActivity {
    private EditText campoEmail;
    private Button btnEnviar;
    private TextView linkVoltarLogin;
    private FirebaseRepository firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);


        firebase = new FirebaseRepository();

        campoEmail = findViewById(R.id.et_email);
        btnEnviar = findViewById(R.id.btn_send_recovery);
        linkVoltarLogin = findViewById(R.id.tv_back_to_login);

        btnEnviar.setOnClickListener(v -> enviarLink());

    }

    private void enviarLink() {
        String email = campoEmail.getText().toString().trim();

        if (email.isEmpty()) {
            toast("Digite seu e-mail");
            return;
        }

        firebase.recuperarSenha(email, ok  -> {
            toast("Link enviado! Verifique sua caixa de entrada");
            finish();},
                err -> toast("E-mail não encontrado"));

    }



    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}