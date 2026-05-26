package com.example.cinesound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {
    private EditText campoNome, campoEmail, campoSenha, campoConfirmar;
    private Button btnCriar;
    private FirebaseRepository firebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        firebase = new FirebaseRepository();

        campoNome = findViewById(R.id.et_name);
        campoEmail = findViewById(R.id.et_email);
        campoSenha = findViewById(R.id.et_password);
        campoConfirmar = findViewById(R.id.et_confirm_password);
        btnCriar = findViewById(R.id.btn_register);

        btnCriar.setOnClickListener(v -> fazerCadastro());

    }



    private void fazerCadastro() {
        String nome = campoNome.getText().toString().trim();

        String email = campoEmail.getText().toString().trim();

        String senha = campoSenha.getText().toString().trim();

        String confirmar = campoConfirmar.getText().toString().trim();


        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            toast("Preencha todos os campos");
            return;
        }
        if (!senha.equals(confirmar)) {
            toast("As senhas não coincidem");
            return;
        }
        if (senha.length() < 6) {
            toast("A senha deve ter pelo menos 6 caracteres");
            return;
        }



        firebase.cadastrar(email, senha, resultado -> {
                    String uid = resultado.getUser().getUid();

                    firebase.salvarPerfil(uid, nome, email, ok -> {

                                startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                                finish();},

                            err -> toast("Erro ao salvar perfil"));
                },
                err -> toast("Erro ao criar conta: " + err.getMessage())
        );

    }



    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}