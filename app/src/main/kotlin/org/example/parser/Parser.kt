package org.example.parser

import kotlin.collections.*

class InvalidTokenException(message: String): Exception(message)
class InvalidProductionException(message: String): Exception(message)

/*
* Parser for Operator grammars.
* */
class Parser {
    private var nonTerminals: MutableSet<Char> = mutableSetOf()
    private var terminals: MutableSet<Char> = mutableSetOf()
    private var initial: Char? = null
    private var rules: MutableMap<Char, MutableList<String>> = mutableMapOf()
    private var f: MutableMap<Char, UInt> = mutableMapOf()
    private var g: MutableMap<Char, UInt> = mutableMapOf()
    private var built: Boolean = false
    private var opGraph: Graph = Graph()

    fun addRule(nonTerm: Char, prod: String) {

        //Check that the production is a valid operator grammar production
        if (nonTerm !in 'A'..'Z') throw InvalidTokenException("No-Terminal debe ser una única letra mayúscula.")
        nonTerminals.add(nonTerm)
        var foundNonTerm: Boolean = false
        prod.split(" ").forEach { sym ->
            if (sym.length != 1) {
                throw InvalidTokenException("Los símbolos del lenguaje deben ser caracteres individuales.")
            }

            when (val c = sym[0]) {
                in 'A'..'Z' -> {
                    if (foundNonTerm) {
                        throw InvalidProductionException(
                            "La producción ingresada no pertenece a una gramática de operadores."
                        )
                    }
                    foundNonTerm = true
                    nonTerminals.add(c)
                }
                in 'a'..'z', in ('!'..'#') + ('%'..'/')-> {
                    foundNonTerm = false
                    terminals.add(c)
                }
                else -> throw InvalidTokenException("Símbolo inválido '$c'")
            }
        }

        rules.getOrPut(nonTerm) { mutableListOf() }.add(prod)
        return
    }

    fun setInitial(c: Char) {
        initial = c
    }

    fun setPrecedence(higher: Char, lower: Char) {}
    fun parse(input: String): Boolean = true

}