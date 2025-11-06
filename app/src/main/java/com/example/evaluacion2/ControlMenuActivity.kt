package com.example.evaluacion2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class ControlMenuActivity : AppCompatActivity() {

    // --- CONFIGURACIÓN DE CONEXIÓN ---
    private val SERVER_PORT = 5000  // Puerto del servidor
    private lateinit var etServerIP: EditText  // Campo para ingresar IP

    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private var isConnected = false
    // -----------------------------------

    // UI
    private lateinit var tvStatus: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button
    private lateinit var btnToggle1: Button
    private lateinit var btnToggle2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_menu)

        // Vistas
        tvStatus = findViewById(R.id.tv_status)
        btnConnect = findViewById(R.id.btn_connect)
        btnDisconnect = findViewById(R.id.btn_disconnect)
        btnToggle1 = findViewById(R.id.toggle_btn)
        btnToggle2 = findViewById(R.id.toggle2_btn)
        etServerIP = findViewById(R.id.et_server_ip) // agrega este EditText en tu XML

        btnConnect.setOnClickListener {
            val ip = etServerIP.text.toString().trim()
            if (ip.isEmpty()) {
                showToast("Ingrese la IP del servidor")
            } else {
                connectToServer(ip)
            }
        }

        btnDisconnect.setOnClickListener { disconnect() }

        btnToggle1.setOnClickListener { sendCommand("1") }
        btnToggle2.setOnClickListener { sendCommand("0") }

        updateUiState(false)
    }

    // --- CONEXIÓN TCP ---
    private fun connectToServer(ip: String) {
        if (isConnected) {
            showToast("Ya está conectado")
            return
        }

        tvStatus.text = "Estado: Conectando..."
        setConnectionButtonsEnabled(false)

        thread {
            try {
                socket = Socket(ip, SERVER_PORT)
                writer = PrintWriter(socket!!.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                isConnected = true

                runOnUiThread {
                    updateUiState(true)
                    showToast("Conectado a $ip:$SERVER_PORT")
                }

                // Escuchar respuestas del servidor
                var line: String? = null
                while (socket != null) {
                    line = reader?.readLine() ?: break
                    Log.d("WiFi", "Respuesta del servidor: $line")
                }

            } catch (e: Exception) {
                Log.e("WiFi", "Error al conectar: ${e.message}")
                runOnUiThread {
                    tvStatus.text = "Estado: Error de conexión"
                    showToast("No se pudo conectar. Verifique IP o red Wi-Fi.")
                    updateUiState(false)
                }
                disconnect()
            }
        }
    }

    private fun disconnect() {
        thread {
            try {
                writer?.close()
                reader?.close()
                socket?.close()
            } catch (e: Exception) {
                Log.e("WiFi", "Error al cerrar socket: ${e.message}")
            } finally {
                writer = null
                reader = null
                socket = null
                isConnected = false
                runOnUiThread {
                    updateUiState(false)
                    showToast("Desconectado.")
                }
            }
        }
    }

    // --- ENVÍO DE DATOS ---
    private fun sendCommand(cmd: String) {
        if (!isConnected || writer == null) {
            showToast("No hay conexión activa.")
            return
        }

        thread {
            try {
                writer!!.println(cmd)
                runOnUiThread { showToast("Comando '$cmd' enviado.") }
            } catch (e: Exception) {
                Log.e("WiFi", "Error al enviar: ${e.message}")
                runOnUiThread { showToast("Error al enviar comando.") }
                disconnect()
            }
        }
    }

    // --- UI ---
    private fun updateUiState(connected: Boolean) {
        isConnected = connected
        tvStatus.text = if (connected) "Estado: CONECTADO" else "Estado: DESCONECTADO"
        tvStatus.setTextColor(
            ContextCompat.getColor(
                this,
                if (connected) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )

        btnConnect.isEnabled = !connected
        btnDisconnect.isEnabled = connected
        btnToggle1.isEnabled = connected
        btnToggle2.isEnabled = connected
        setConnectionButtonsEnabled(true)
    }

    private fun setConnectionButtonsEnabled(enabled: Boolean) {
        btnConnect.isEnabled = enabled && !isConnected
        btnDisconnect.isEnabled = enabled && isConnected
    }

    private fun showToast(msg: String) {
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }
}
