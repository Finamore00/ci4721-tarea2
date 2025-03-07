package org.example

import org.example.clientUtils.*
import org.example.parser.*

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
                        is InvalidTokenException -> printWrongTokenErr()
                        is InvalidProductionException -> printWrongProductionErr()
                        else -> printUnknownErr()
                    }
                    continue
                }
            }
            "init" -> {
                if (input.size != 2) {
                    printArgCountErr()
                    continue
                }
                if (input[1].length != 1) {
                    printWrongNonTerminalErr()
                    continue
                }
                val nonTerm = input[1][0]
                try {
                    p.setInitial(nonTerm)
                    println("'$nonTerm' es ahora el símbolo inicial de la gramática.")
                } catch (e: Exception) {
                    when (e) {
                        is InvalidTokenException -> printWrongTokenErr()
                        else -> printUnknownErr()
                    }
                }
            }
            "prec" -> {
                if (input.size != 4) {
                    printArgCountErr()
                    continue
                }

                if (input.takeLast(input.size - 1).any {tok -> tok.length != 1}) {
                    printWrongTokenErr()
                    continue
                }

                try {
                    var relation: String
                    when (input[2][0]) {
                        '<' -> {
                            p.setPrecedence(input[1][0], PrecedenceTypes.LowerThan, input[3][0])
                            relation = "menor"
                        }
                        '>' -> {
                            p.setPrecedence(input[1][0], PrecedenceTypes.HigherThan, input[3][0])
                            relation = "mayor"
                        }
                        '=' -> {
                            p.setPrecedence(input[1][0], PrecedenceTypes.EqualThan, input[3][0])
                            relation = "igual"
                        }
                        else -> {
                            printWrongOpErr()
                            continue
                        }
                    }
                    println("'${input[1][0]}' tiene $relation precedencia que '${input[3][0]}'")

                } catch (e: Exception) {
                    when (e) {
                        is InvalidTokenException -> printWrongTokenErr()
                        is NoSuchElementException -> printSymNotRegisteredErr()
                    }
                }
            }
            "build" -> {
                try {
                    p.build()
                } catch (e: Exception) {
                    when (e) {
                        is IllegalStateException -> printCyclicGraphErr()
                    }
                }
            }
            "parse" -> {
                val w = input.takeLast(input.size - 1).joinToString(" ")

                try {
                    println(p.parse(w))
                } catch (e: Exception) {
                    when (e) {
                        is InvalidComparisonException -> System.err.println(e.message)
                        is NoSuchElementException -> System.err.println(e.message)
                        else -> printUnknownErr()
                    }
                }

            }
            "help" -> printHelp()
            "salir" -> quitFlag = true
            else -> println("ERROR: Comando desconocido")
        }
    }
}
