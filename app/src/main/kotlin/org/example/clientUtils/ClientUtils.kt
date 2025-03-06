package org.example.clientUtils

fun printWelcome() {
    println("¡Bienvenido al generador de analizadores para gramáticas de operadores!")
    println("Para ver uso del generador, ingresar 'help'.")
}

fun printWrongNonTerminalErr() {
    System.err.println("El símbolo no terminal ingresado no es válido.")
    System.err.println("Para cosideraciones sobre los símbolos de la gramática ver 'help'")
}

fun printWrongTokenErr() {
    System.err.println("ERROR: Alguno de los símbolos ingresados no es válido.")
    System.err.println("Para consideraciones sobre los símbolos de la gramática ver 'help'")
}

fun printWrongProductionErr() {
    System.err.println("ERROR: La producción ingresada no es válida para una gramática de operadores.")
}

fun printArgCountErr() {
    System.err.println("ERROR: Número incorrecto de argumentos.")
    System.err.println("Para ver utilización de los comandos ver 'help'.")
}

fun printWrongOpErr() {
    System.err.println("ERROR: El operador de precedencia ingresado no es válido.")
    System.err.println("Para ver los operadores disponibles, ver 'help'")
}

fun printSymNotRegisteredErr() {
    System.err.println("Alguno de los símbolos ingresados no existe en la gramática.")
    System.err.println("Registre alguna regla que involucre a los símbolos para registrarlos en la gramática.")
}

fun printCyclicGraphErr() {
    System.err.println("ERROR: Se detectaron ciclos de precedencia entre los operadores.")
    System.err.println("El analizador no se puede construir.")
}

fun printUnknownErr() {
    System.err.println("ERROR: Ocurrió algún error desconocido.")
}

fun getCommand(): String {
    print("Ingrese un comando: ")
    return readln()
}

fun printHelp() {
    println("GENERADOR DE PARSERS PARA GRAMÁTICAS DE OPERADORES")
    println("Uso: <COMANDO> [<ARGUMENTOS>]")
    println("Comandos:")
    println("\t+ RULE <no-terminal> [<simbolo>]")
    println("\t\tAgrega a la gramática la producción <no-terminal> → [<símbolos>]. Si la lista de símbolos es vacía se " +
            "agrega la producción <no-terminal> → λ.")
    println("\t\t[<simbolos>] es una lista de símbolos de la gramática separados por espacios.")
    println("\t+ INIT <no-terminal>")
    println("\t\tDefine a <no-terminal> como el símbolo inicial de la gramática.")
    println("\t+ PREC <terminal> <op> <terminal>")
    println("\t\tDefine la relación de precedencia entre dos símbolos no terminales. Los operadores disponibles son:")
    println("\t\t- <: Establece que el primer terminal tiene menor precedencia que el segundo.")
    println("\t\t- >: Establece que el primer terminal tiene mayor precedencia que el segundo.")
    println("\t\t- =: Establece que el primer termianl tiene igual precedencia que  el segundo.")
    println("\t+ BUILD")
    println("\t\tConstruye el analizador sintático con las reglas y precedencias establecidas.")
    println("\t+ PARSE <string>")
    println("\t\tRealiza análisis sintáctico sobre <string>, mostrando los estados de la entrada y la pila y las " +
            "acciones realizadas")
    println("\t+ SALIR")
    println("\t\tAborta la ejecución del programa.")
    println("Consideraciones:")
    println("\t- Todos los símbolos de la gramática deben ser caracteres ASCII individuales. Los no terminales deben" +
            "ser letras mayúsculas y los no terminales letras minúsculas o símbolos (excepto $)")
    println("\t- En la definición de reglas todos los símbolos deben estar debidamente separados por espacios.")
}

