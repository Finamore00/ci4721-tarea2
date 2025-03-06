package org.example.parser

import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.*

class InvalidTokenException(message: String): Exception(message)
class InvalidProductionException(message: String): Exception(message)
class InvalidComparisonException(message: String): Exception(message)

enum class PrecedenceTypes {
    LowerThan,
    EqualThan,
    HigherThan
}

private fun isTerminal(a: Char): Boolean {
    return a in ('a'..'z') + ('!'..'#') + ('%'..'/')
}

private fun isNonTerminal(a: Char): Boolean {
    return a in 'A'..'Z'
}

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
                        throw InvalidProductionException(
                            "Not an operator grammar production."
                        )
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

    fun setInitial(c: Char) {
        if (!isNonTerminal(c)) throw InvalidTokenException("Non terminal must be capital.")

        nonTerminals.add(c)
        initial = c
    }

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

    fun parse(input: String): List<GrammarRule> {
        val st: Stack<Char> = Stack<Char>()
        val inputTokenized: MutableList<Char> = input.split("\\s+".toRegex()).map {
            if (it.length != 1) throw IllegalArgumentException("Invalid token found: $it.")
            it[0]
        }.toMutableList()
        inputTokenized.add('$')

        st.push('$')
        var currInputPos: Int = 0
        var e: Char = inputTokenized[currInputPos]
        currInputPos += 1
        val result: MutableList<GrammarRule> = mutableListOf()

        do {
            val p: Char = st.peek()
            if (p == '$' && e == '$') break

            when (precedenceTable[Pair(p, e)]) {
                PrecedenceTypes.LowerThan, PrecedenceTypes.EqualThan -> {
                    st.push(e)
                    e = inputTokenized[currInputPos]
                    currInputPos += 1
                }
                PrecedenceTypes.HigherThan -> {
                    var x: String = ""
                    do {
                        x += "${st.pop()}"
                    } while (precedenceTable[Pair(st.peek(), x[x.length - 1])]!! != PrecedenceTypes.LowerThan)
                    result.add(prodMap[x]!!)
                }
                null -> {
                    throw InvalidComparisonException("ERROR: $p no es comparable con $e")
                }
            }
        } while (true)

        return result
    }

}