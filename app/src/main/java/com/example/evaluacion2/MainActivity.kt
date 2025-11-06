package com.example.evaluacion2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// IMPORTACIONES CRÍTICAS DE FIREBASE Y GMS
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
// FIN IMPORTACIONES CRÍTICAS

class MainActivity : AppCompatActivity() {

    // Variables para los campos de entrada y botón
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button

    // Inicialización de Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Asignar vistas (ajusta los IDs si son diferentes en tu XML)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            val email = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // 1. Validar campos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese email y contraseña.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Autenticación con Firebase
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task: Task<AuthResult> ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Autenticación exitosa.")
                        Toast.makeText(baseContext, "Login exitoso.", Toast.LENGTH_SHORT).show()

                        // Navegar a la siguiente actividad (ControlMenuActivity)
                        val intent = Intent(this@MainActivity, ControlMenuActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // Si falla el login, mostrar un mensaje al usuario
                        Log.w("Firebase", "Fallo de autenticación.", task.exception)
                        Toast.makeText(baseContext, "Fallo de autenticación: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}