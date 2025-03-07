package org.example.parser

import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.*

class InvalidTokenException(message: String): Exception(message)
class InvalidProductionException(message: String): Exception(message)
class InvalidComparisonException(message: String): Exception(message)

/*
* Enum class for precedence operators within operator grammars.
* */
enum class PrecedenceTypes {
    LowerThan,
    EqualThan,
    HigherThan
}

/*
* Indicates if a given character is a valid terminal symbol. Terminal symbols are either a lower case
* ASCII character or any ASCII symbol that isn't '$'
* */
private fun isTerminal(a: Char): Boolean {
    return a in ('a'..'z') + ('!'..'#') + ('%'..'/')
}

/*
* Indicates if a given character is a valid non-terminal symbol. Non-terminal symbols are single, upper case
* ASCII characters.
* */
private fun isNonTerminal(a: Char): Boolean {
    return a in 'A'..'Z'
}

/*
* Simple data class for a Grammar Rule. Stores the non-terminal symbol on its left side and the
* string it produces on the right side.
* */
data class GrammarRule(val nonTerm: Char, val prod: String) {
    override fun toString(): String {
        return "$nonTerm → $prod"
    }
}


/*
* Parser for Operator grammars.
* */
class Parser {
    private var nonTerminals: MutableSet<Char> = mutableSetOf()
    private var terminals: MutableSet<Char> = mutableSetOf()
    private var initial: Char? = null
    private val prodMap: MutableMap<String, GrammarRule> = mutableMapOf()
    private var f: MutableMap<Char, UInt> = mutableMapOf()
    private var g: MutableMap<Char, UInt> = mutableMapOf()
    private var built: Boolean = false
    private var opGraph: Graph = Graph()

    private val precedenceTable: MutableMap<Pair<Char, Char>, PrecedenceTypes> = mutableMapOf()

    init {
        nonTerminals.add('$')
        opGraph.addNode('$')
        f['$'] = 0u
        g['$'] = 0u
    }

    /*
    * Adds a new rule to the grammar. The prod string is verified to consist only of single-character symbols
    * within the function. Function also registers any unregistered symbols it finds within the inputted
    * production.
    * */
    fun addRule(nonTerm: Char, prod: String) {
        //Check that the production is a valid operator grammar production
        if (!isNonTerminal(nonTerm)) throw InvalidTokenException("Non-terminal must be capital letter.")
        nonTerminals.add(nonTerm)

        var foundNonTerm: Boolean = false
        prod.split("\\s+".toRegex()).forEach { sym ->
            if (sym.length != 1) {
                throw InvalidTokenException("All language tokens must be single ASCII characters.")
            }

            when (val c = sym[0]) {
                in 'A'..'Z' -> {
                    if (foundNonTerm) {
                        throw InvalidProductionException("Not an operator grammar production.")
                    }
                    foundNonTerm = true
                    nonTerminals.add(c)
                    opGraph.addNode(c)
                }
                in 'a'..'z', in ('!'..'#') + ('%'..'/')-> {
                    foundNonTerm = false
                    terminals.add(c)
                    opGraph.addNode(c)
                }
                else -> throw InvalidTokenException("Invalid symbol '$c'")
            }
        }

        val prodOnlyTerminals = prod.filter {isTerminal(it)}
        prodMap[prodOnlyTerminals] = GrammarRule(nonTerm, prod)
        return
    }

    /*
    * Sets the initial symbol for the grammar.
    * */
    fun setInitial(c: Char) {
        if (!isNonTerminal(c)) throw InvalidTokenException("Non terminal must be capital.")

        nonTerminals.add(c)
        initial = c
    }

    /*
    * Establishes the precedence within the 'a' and 'b' symbols within the parser.
    * The available precedence operators are in the PrecedenceTypes enum.
    * */
    fun setPrecedence(a: Char, p: PrecedenceTypes, b: Char) {
        if ((!isTerminal(a) && a != '$') || (!isTerminal(b) && b != '$')) throw InvalidTokenException("Tokens must be non-terminal")

        val bGNode = opGraph.getGNode(b) ?: throw NoSuchElementException("Token $b not registered")
        val aFNode = opGraph.getFNode(a) ?: throw NoSuchElementException("Token $a not registered")
        when (p) {
            PrecedenceTypes.LowerThan -> {
                opGraph.addConnection(bGNode, aFNode)
            }
            PrecedenceTypes.HigherThan -> {
                opGraph.addConnection(aFNode, bGNode)
            }
            else -> {}
        }
        precedenceTable[Pair(a, b)] = p
    }

    /*
    * Calculates the values of the f and g functions for each token in the grammar,
    * subsequently locks the parser so no more registrations or precedence modifications
    * are possible
    * */
    fun build() {
        if (!built) {
            for (nt in terminals) {
                f[nt] = opGraph.longestPathLen(opGraph.getFNode(nt)!!)
                g[nt] = opGraph.longestPathLen(opGraph.getGNode(nt)!!)
            }
        }
        built = true

        println("Analizador sintáctico construido")
        println("Valores para f:")
        for (k in f.keys) {
            println("\t$k: ${f[k]}")
        }
        println("Valores para g:")
        for (k in g.keys) {
            println("\t$k: ${g[k]}")
        }
    }

    /*
    * Parses the input string under the stablished grammar rules and returns the list
    * of productions used during the parsing process. Also prints the
    * */
    fun parse(input: String): List<GrammarRule> {
        if (!built) {
            throw IllegalStateException()
        }

        //Variables for parser operation
        val st: Stack<Char> = Stack<Char>()
        val inputTokenized: MutableList<Char> = input.split("\\s+".toRegex()).map {
            if (it.length != 1) throw IllegalArgumentException("El token '$it' no es válido.")
            it[0]
        }.toMutableList()
        inputTokenized.add('$')

        //Variables for printing
        val printSt: Stack<Char> = Stack<Char>()
        var quitFlag: Boolean = false

        st.push('$')
        var currInputPos: Int = 0
        var e: Char = inputTokenized[currInputPos++]
        val result: MutableList<GrammarRule> = mutableListOf()

        do {
            var action: String = ""
            val stStr = printSt.joinToString(" ")
            val inputStr = inputTokenized.slice(currInputPos-1 until inputTokenized.size).joinToString(" ")

            if (quitFlag) {
                println("Parseo fallido.")
                break
            }

            val p: Char = st.peek()
            if (p == '$' && e == '$') {
                action = "Aceptar"
                println(String.format("%-25s%-25s%-10s", stStr, inputStr, action))
                break
            }

            if (!terminals.union(setOf('$')).contains(p)) {
                throw NoSuchElementException("El símbolo $p no pertenece a la gramática.")
            }

            if (!terminals.union(setOf('$')).contains(e)) {
                throw NoSuchElementException("El símbolo $e no pertenece a la gramática.")
            }

            when (precedenceTable[Pair(p, e)]) {
                PrecedenceTypes.LowerThan, PrecedenceTypes.EqualThan -> {
                    st.push(e)
                    printSt.push(e)
                    e = inputTokenized[currInputPos++]
                    action = "Leer"
                }
                PrecedenceTypes.HigherThan -> {
                    var x: String = ""
                    do {
                        x += "${st.pop()}"
                    } while (precedenceTable[Pair(st.peek(), x[x.length - 1])]!! != PrecedenceTypes.LowerThan)
                    if (printSt.joinToString(" ").endsWith(prodMap[x]!!.prod)) {
                        prodMap[x]!!.prod.filter { !it.isWhitespace() }.forEach { _ ->
                            printSt.pop()
                        }
                        printSt.push(prodMap[x]!!.nonTerm)
                        result.add(prodMap[x]!!)
                        action = "Reducir ${prodMap[x]}"
                    } else {
                        action = "Rechazar."
                        quitFlag = true
                    }

                }
                null -> {
                    throw InvalidComparisonException("ERROR: $p no es comparable con $e")
                }
            }
            println(String.format("%-25s%-25s%-10s", stStr, inputStr, action))
        } while (true)

        return result
    }

    /*
    * Helper function for adding the precedence operators to the input string
    * */
    private fun prettifyInput(input: String): String {
        return ""
    }

}