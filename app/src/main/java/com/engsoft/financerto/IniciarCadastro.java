package com.engsoft.financerto;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class IniciarCadastro extends AppCompatActivity {

    private EditText editNome, editSobreNome, editEmail, editConfEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getSupportActionBar().hide();

        editNome = findViewById(R.id.edit_nome);
        editSobreNome = findViewById(R.id.edit_sobrenome);
        editEmail = findViewById(R.id.edit_email_cadastro);
        editConfEmail = findViewById(R.id.edit_email_verificar_cadastro);


        Button btnContinuar = findViewById(R.id.btn_continuar);
        btnContinuar.setOnClickListener(view -> {

            String nome = editNome.getText().toString();
            String sobreNome = editSobreNome.getText().toString();
            String email = editEmail.getText().toString();
            String confEmail = editConfEmail.getText().toString();

            String nomeCompleto = editNome + " " + editSobreNome;

            if(TextUtils.isEmpty(nome)){
                editNome.setError("Campo vazio!");
                return;
            }

            if(TextUtils.isEmpty(sobreNome)){
                editSobreNome.setError("Campo vazio!");
                return;
            }

            if(TextUtils.isEmpty(email)) {
                editEmail.setError("Campo vazio!");
                return;
            }

            if(TextUtils.isEmpty(confEmail)){
                editConfEmail.setError("Campo vazio!");
                return;
            }

            if(!email.equals(confEmail)) {
                editEmail.setError("Os emails não coincidem!");
                editConfEmail.setError("Os emails não coincidem!");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editEmail.setError("Email inválido");
                return;
            }


            Intent intent = new Intent(this, FinalizarCadastro.class);
            intent.putExtra("nome", nome);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }
}