package com.engsoft.financerto;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FinalizarCadastro extends AppCompatActivity {

    private EditText editSenha, editConfirmarSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finalizar_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        editSenha = findViewById(R.id.edit_senha);
        editConfirmarSenha = findViewById(R.id.edit_confirmar_senha);

        Button btnCadastrar = findViewById(R.id.btn_cadastrar);
        btnCadastrar.setOnClickListener(view -> {

            String senha = editSenha.getText().toString();
            String confirmarSenha = editConfirmarSenha.getText().toString();

            if(TextUtils.isEmpty(senha)){
                editSenha.setError("Campo vazio!");
                return;
            }

            if(TextUtils.isEmpty(confirmarSenha)){
                editConfirmarSenha.setError("Campo vazio!");
            }

            if(!senha.equals(confirmarSenha)){
                editSenha.setError("As senha não coincidem!");
                editConfirmarSenha.setError("As senhas não coincidem!");
            }

            if(!senhaValida(senha)){
                editSenha.setError("A senha deve ter pelo menos 8 caracteres, incluindo uma letra maiúscula, um número e um caractere especial!");
                return;
            }

            Intent intent = new Intent(this, TelaPrincipal.class);
            String nome = getIntent().getStringExtra("nome");
            String email = getIntent().getStringExtra("email");
            startActivity(intent);
        });
    }
    private boolean senhaValida(String senha){
        return senha.length() >= 8 &&
                senha.matches(".*[A-Z].*") &&
                senha.matches(".*\\d.*") &&
                senha.matches(".*[@#$%^&*+=!].*");
    }
    private void limparCampos () {
        editSenha.setText("");
        editConfirmarSenha.setText("");
    }
}