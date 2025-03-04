/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example

import kotlin.collections.MutableSet

fun main() {
    /*
    * Simple REPL-like loop that prints user input and
    * exits when receiving "quit"
    * */
    var input: List<String>
    var quitFlag: Boolean = false

    while (!quitFlag) {
        print("Gimme something boy: ")
        input = readln().split(" ")
        if (input[0].isBlank()) continue

        when (input[0].lowercase()) {
            "rule" -> println("Se solicitó definir una nueva regla")
            "init" -> println("Se solicitó definir el símbolo inicial de la gramática")
            "prec" -> println("Se solicitó definir la relación de precedencia entre dos no-terminales")
            "build" -> println("Se solicitó construir el analizador sintáctico")
            "parse" -> println("Se solicitó parsear una frase del lenguaje")
            "help" -> println("Se solicitó imprimir el manual del generador")
            "exit", "quit" -> quitFlag = true
            else -> println("ERROR: Comando desconocido")
        }
    }
}
