/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example

import org.example.parser.Parser
import org.example.clientUtils.*
import org.example.parser.InvalidProductionException
import org.example.parser.InvalidTokenException

fun main() {
    /*
    * Simple REPL-like loop that prints user input and
    * exits when receiving "quit"
    * */
    var input: List<String>
    var quitFlag: Boolean = false
    val p: Parser = Parser()

    printWelcome()

    while (!quitFlag) {
        input = getCommand().split("\\s+".toRegex())

        when (input[0].lowercase()) {
            "rule" -> {
                if (input.size < 2) {
                    printArgCountErr()
                    continue
                }
                if (input[1].length != 1) {
                    printWrongNonTerminalErr()
                    continue
                }
                try {
                    val nonTerm = input[1][0]
                    val prod = input.takeLast(input.size - 2).joinToString(" ")
                    p.addRule(nonTerm, prod)
                    println("Agregada la regla '$nonTerm → $prod'")
                } catch (e: Exception) {
                    when (e) {
                        is InvalidTokenException -> {
                            printWrongTokenErr()
                        }
                        is InvalidProductionException -> {
                            printWrongProductionErr()
                        }
                        else -> printUnknownErr()
                    }
                }
            }
            "init" -> println("Se solicitó definir el símbolo inicial de la gramática")
            "prec" -> println("Se solicitó definir la relación de precedencia entre dos no-terminales")
            "build" -> println("Se solicitó construir el analizador sintáctico")
            "parse" -> println("Se solicitó parsear una frase del lenguaje")
            "help" -> printHelp()
            "exit", "quit" -> quitFlag = true
            else -> println("ERROR: Comando desconocido")
        }
    }
}
