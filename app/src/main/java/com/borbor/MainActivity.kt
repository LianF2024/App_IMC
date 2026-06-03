package com.borbor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IMCApp()
        }
    }
}

// Commit 3: Configuración de rutas
@Composable
fun IMCApp() {
    val navController = rememberNavController() // Aquí se crea un NavController, que es el objeto encargado de manejar la navegación entre pantallas.
                                                // rememberNavController() asegura que el controlador se mantenga en memoria mientras la app esté activa, evitando que se reinicie cada vez que se recompone la interfaz.
    NavHost(navController = navController, startDestination = "input") { //NavHost es el contenedor que define todas las rutas (pantallas) disponibles en la aplicación.
        // 📌 Pantalla 1: Ingreso de datos                                                        // startDestination = "input" indica que la primera pantalla que se mostrará al abrir la app será la llamada "input".
        composable("input") { InputScreen(navController) } //Aquí se define la Pantalla 1, asociada a la ruta "input".

        // 📌 RETO 2: Ruta con múltiples parámetros                                                          //Se pasa el navController como parámetro para que esta pantalla pueda iniciar la navegación hacia otras pantallas (por ejemplo, la de resultados).
        composable(
            route = "resultado/{nombre}/{imc}",
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType },
                navArgument("imc") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
            val imc = backStackEntry.arguments?.getFloat("imc") ?: 0f

            // 📌 Pantalla 2: Resultado IMC
            ResultScreen(navController, nombre, imc)
        }
    }
}
//Commit 1: "Pantalla 1 y UI"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calculadora de IMC") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text("Altura (m)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (error) {

                // 📌 RETO 1: Validación de entradas
                Text(
                    text = "Por favor, ingresa valores válidos",
                    color = Color.Red
                )
            }

            Button(
                onClick = {
                    val pesoVal = peso.toFloatOrNull()
                    val alturaVal = altura.toFloatOrNull()
                    if (pesoVal != null && alturaVal != null && pesoVal > 0 && alturaVal > 0) {
                        val imc = pesoVal / (alturaVal * alturaVal)
                        navController.navigate("resultado/$nombre/$imc")
                        error = false
                        // 📌 Botón Volver: limpiar campos al regresar
                        nombre = ""
                        peso = ""
                        altura = ""
                    } else {
                        error = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular")
            }
        }
    }
}

// Commit 4: Pantalla 2 terminada
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavController, nombre: String, imc: Float) {
    val categoria: String
    val color: Color
    
    // Commit 5: Retos visuales completados
    // RETO 1: Validación de entradas (ya implementado en InputScreen)
    // RETO 2: Ruta con múltiples parámetros (ya implementado en NavHost)
    // RETO 3: Clasificación dinámica con colores
    when {
        imc < 18.5 -> {
            categoria = "Bajo peso"
            color = Color.Red
        }
        imc in 18.5..24.9 -> {
            categoria = "Peso normal"
            color = Color.Green
        }
        imc in 25.0..29.9 -> {
            categoria = "Sobrepeso"
            color = Color(0xFFFFA500) // Naranja
        }
        else -> {
            categoria = "Obesidad"
            color = Color.Red
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resultado IMC") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Hola $nombre, tu resultado es:")
            Spacer(modifier = Modifier.height(8.dp))
            Text(String.format("IMC: %.1f", imc))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = categoria, color = color)
            Spacer(modifier = Modifier.height(16.dp))

            // 📌 Botón Volver con popBackStack
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}
