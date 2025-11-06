package com.example.evaluacion2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// -- NUEVAS IMPORTACIONES EXPLÍCITAS Y CORREGIDAS --
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
// --------------------------------------------------

class ControlMenuActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnToggle1: Button // ACTIVAR SISTEMA
    private lateinit var btnToggle2: Button // DESACTIVAR SISTEMA

    // Instancia de Firestore (Ahora usa el KTX/Firebase explícito)
    private val db: FirebaseFirestore = Firebase.firestore

    // Referencia al documento que la placa leerá
    private val controlDocRef = db.collection("control_dispositivos").document("sistema_retroceso")

    // Listener para monitorear el estado actual del sistema
    private var statusListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_menu)

        // Inicialización de las vistas
        tvStatus = findViewById(R.id.tv_status)
        btnToggle1 = findViewById(R.id.toggle_btn)
        btnToggle2 = findViewById(R.id.toggle2_btn)

        // En el modo WiFi/Firestore, siempre estamos "conectados" a la nube.
        setCommandButtonsEnabled(true)
        tvStatus.text = "Estado: Conectado a Firebase"

        // Configurar Listeners de botones
        btnToggle1.setOnClickListener {
            sendCommandToFirestore(1, "ACTIVAR") // Comando para ACTIVAR SISTEMA (estado 1)
        }

        btnToggle2.setOnClickListener {
            sendCommandToFirestore(0, "DESACTIVAR") // Comando para DESACTIVAR SISTEMA (estado 0)
        }

        // Iniciar la escucha del estado del sistema
        startStatusListener()
    }

    // Función para escribir el comando (estado 1 o 0) en Firestore
    private fun sendCommandToFirestore(command: Int, action: String) {
        val data = hashMapOf(
            "estado" to command,
            "timestamp" to System.currentTimeMillis()
        )

        controlDocRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "$action Sistema (Comando: $command) enviado a Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al escribir comando", e)
                Toast.makeText(this, "ERROR: Fallo al enviar el comando a Firebase.", Toast.LENGTH_LONG).show()
            }
    }

    // Función para leer el estado de la base de datos en tiempo real (para feedback visual)
    private fun startStatusListener() {
        statusListener = controlDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("Firestore", "Escucha de estado fallida.", e)
                tvStatus.text = "Error de escucha con Firebase"
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val estadoActual = snapshot.getLong("estado")?.toInt()

                if (estadoActual == 1) {
                    tvStatus.text = "Estado: Sistema ACTIVO (Remoto)"
                    btnToggle1.isEnabled = false
                    btnToggle2.isEnabled = true
                } else if (estadoActual == 0) {
                    tvStatus.text = "Estado: Sistema DESACTIVADO (Remoto)"
                    btnToggle1.isEnabled = true
                    btnToggle2.isEnabled = false
                } else {
                    tvStatus.text = "Estado: En espera de comando"
                    btnToggle1.isEnabled = true
                    btnToggle2.isEnabled = true
                }
            } else {
                // Si el documento no existe (primera vez), lo creamos con el estado 0
                val initialData = hashMapOf("estado" to 0)
                controlDocRef.set(initialData)
                tvStatus.text = "Estado: Documento creado, DESACTIVADO"
            }
        }
    }

    private fun setCommandButtonsEnabled(isEnabled: Boolean) {
        btnToggle1.isEnabled = isEnabled
        btnToggle2.isEnabled = isEnabled
    }

    override fun onDestroy() {
        super.onDestroy()
        statusListener?.remove()
    }
}