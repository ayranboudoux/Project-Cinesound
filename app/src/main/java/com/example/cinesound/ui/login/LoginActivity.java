package com.example.cinesound.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinesound.repository.FirebaseRepository;
import com.example.cinesound.MainActivity;
import com.example.cinesound.R;

public class LoginActivity extends AppCompatActivity {



    private EditText campoEmail, campoSenha;

    private Button btnEntrar;

    private TextView linkCadastro, linkEsqueci;

    private FirebaseRepository firebase;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebase = new FirebaseRepository();

        if (firebase.getUsuarioAtual() != null) {
            irParaMain();
            return;
        }


        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.et_email);
        campoSenha = findViewById(R.id.et_password);
        btnEntrar = findViewById(R.id.btn_login);
        linkCadastro = findViewById(R.id.tv_create_account);
        linkEsqueci = findViewById(R.id.tv_forgot_password);


        btnEntrar.setOnClickListener(v -> fazerLogin());
        linkCadastro.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
        linkEsqueci.setOnClickListener(v -> startActivity(new Intent(this, RecuperarSenhaActivity.class)));

    }



    private void fazerLogin() {

        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString().trim();


        if (email.isEmpty() || senha.isEmpty()) {
            mostrarErro("Preencha todos os campos");
            return;
        }



        firebase.login(email, senha, ok  -> irParaMain(), err -> mostrarErro("E-mail ou senha incorretos"));

    }



    private void irParaMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }



    private void mostrarErro(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}