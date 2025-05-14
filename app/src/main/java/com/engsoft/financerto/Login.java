package com.engsoft.financerto;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();

        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        btnEntrar = findViewById(R.id.btn_entrar);
        TextView textCadastro = findViewById(R.id.text_cadastro);

        textCadastro.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, IniciarCadastro.class);
            startActivity(intent);
        });

        btnEntrar.setOnClickListener(view -> {
            btnEntrar.setEnabled(false);

            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if(TextUtils.isEmpty(email)){
                editEmail.setError(("Campo vazio!"));
                btnEntrar.setEnabled(true);
                return;
            }

            if(TextUtils.isEmpty(senha)){
                editSenha.setError(("Campo vazio!"));
                btnEntrar.setEnabled(true);
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editEmail.setError("Email inválido");
                btnEntrar.setEnabled(true);
                return;
            }

            ConexaoFrontEnd.loginUsuario(email, senha, new ConexaoFrontEnd.LoginCallback() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                        // Redirecionar para a tela principal após login bem-sucedido
                        Intent intent = new Intent(Login.this, TelaPrincipal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        btnEntrar.setEnabled(true);
                    });

                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        if (error.toLowerCase().contains("email")) {
                            editEmail.setError("Email incorreto!");
                        } else if (error.toLowerCase().contains("senha")) {
                            editSenha.setError("Senha incorreta!");
                        } else {
                            Toast.makeText(Login.this, error, Toast.LENGTH_LONG).show();
                        }
                        btnEntrar.setEnabled(true);
                    });

                }
            });

        });

    }
}