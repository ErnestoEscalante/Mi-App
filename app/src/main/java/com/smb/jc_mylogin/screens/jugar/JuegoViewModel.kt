package com.smb.jc_mylogin.screens.jugar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class JuegoViewModel : ViewModel() {

    private val _indexPreguntaActual = MutableLiveData(0)
    val indexPreguntaActual: LiveData<Int> get() = _indexPreguntaActual

    private val _puntos = MutableLiveData(0)
    val puntos: LiveData<Int> get() = _puntos

    private val _pregunta = MutableLiveData<List<Pregunta>>(emptyList())
    val pregunta: LiveData<List<Pregunta>> get() = _pregunta

    private val _errorCarga = MutableLiveData<Event<String>>()
    val errorCarga: LiveData<Event<String>> get() = _errorCarga

    private val _eventoJuegoFinalizado = MutableLiveData<Event<Boolean>>()
    val eventoJuegoFinalizado: LiveData<Event<Boolean>> get() = _eventoJuegoFinalizado

    private var dificultadActual: Dificultad? = null

    // Función para iniciar el juego con la dificultad seleccionada
    fun startGame(dificultad: Dificultad) {
        dificultadActual = dificultad
        _indexPreguntaActual.value = 0
        _puntos.value = 0
        cargarPreguntas(dificultad)
    }

    // Función para cargar preguntas desde Firestore
    private fun cargarPreguntas(dificultad: Dificultad) {
        val db = FirebaseFirestore.getInstance()
        val preguntasRef = db.collection("preguntas").document(dificultad.name.lowercase())

        preguntasRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val preguntas = document.get("preguntas") as? List<Map<String, Any>> ?: emptyList()
                    val preguntasTransformadas = preguntas.map { preguntaMap ->
                        val texto = preguntaMap["texto"] as? String ?: ""
                        val opciones = (preguntaMap["opciones"] as? List<String>) ?: emptyList()
                        val respuestaCorrecta = preguntaMap["respuesta"] as? String ?: ""
                        Pregunta(texto, opciones, respuestaCorrecta)
                    }

                    _pregunta.value = preguntasTransformadas.shuffled().take(5)
                } else {
                    _errorCarga.value = Event("No se encontraron preguntas para la dificultad: ${dificultad.name.lowercase()}")
                }
            }
            .addOnFailureListener { exception ->
                _errorCarga.value = Event("Error al cargar preguntas: ${exception.message}")
            }
    }


    // Función para gestionar la respuesta del jugador
    fun submitAnswer(respuesta: String) {
        val currentQuestion = _pregunta.value?.get(_indexPreguntaActual.value ?: 0)
        if (currentQuestion != null) {
            val puntosGanados: Int
            val puntosPerdidos: Int

            when (dificultadActual) {
                Dificultad.FACILES -> {
                    puntosGanados = 1
                    puntosPerdidos = 5
                }
                Dificultad.MEDIAS -> {
                    puntosGanados = 3
                    puntosPerdidos = 3
                }
                Dificultad.DIFICILES -> {
                    puntosGanados = 5
                    puntosPerdidos = 2
                }
                else -> return
            }

            // Actualizar puntos según la respuesta
            if (currentQuestion.respuesta == respuesta) {
                _puntos.value = (_puntos.value ?: 0) + puntosGanados
            } else {
                _puntos.value = (_puntos.value ?: 0) - puntosPerdidos
            }

            siguientePregunta()
        }
    }

    // Función para pasar a la siguiente pregunta
    fun siguientePregunta() {
        val index = (_indexPreguntaActual.value ?: 0) + 1
        if (index < (_pregunta.value?.size ?: 0)) {
            _indexPreguntaActual.value = index
        } else {
            _eventoJuegoFinalizado.value = Event(true) // Notifica que el juego terminó
        }
    }
}

class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}