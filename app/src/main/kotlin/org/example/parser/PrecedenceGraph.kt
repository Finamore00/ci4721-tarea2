package org.example.parser

import java.util.*
import kotlin.NoSuchElementException

internal enum class NodeType {
    FNode,
    GNode
}

/*
* Class for a single graph node. Contains a character token as a main
* value and an indicator on whether it's an F or a G node.
*
* Data class is used in order to get structural equality by default, which will
* be useful for the adjacency list in the Graph class
* */
@ConsistentCopyVisibility
data class GraphNode internal constructor(
    val tok: Char,
    private val type: NodeType
)

/*
* Simple graph implementation using an adjacency list.
* */
internal class Graph {
    private var adjList: MutableMap<GraphNode, MutableList<GraphNode>> = mutableMapOf()
    private var topOrderCache: List<GraphNode>? = null
    private var maxDistancesCache: MutableMap<GraphNode, UInt> = mutableMapOf()

    /*
    * Creates the correspondent F and G nodes for the inputted token and
    * adds new adjacency lists for them in the graph. If the inputted token
    * already had their graph nodes created nothing is done.
    * */
    fun addNode(tok: Char) {
        val fNode = GraphNode(tok, NodeType.FNode)
        val gNode = GraphNode(tok, NodeType.GNode)

        //Create the node adjacency lists if character wasn't already in the graph
        adjList[fNode] = adjList[fNode] ?: mutableListOf()
        adjList[gNode] = adjList[gNode] ?: mutableListOf()
    }

    /*
    * Given a token character returns its correspondent F-type GraphNode.
    * If the token isn't present in the graph null is returned.
    * */
    fun getFNode(tok: Char): GraphNode? {
        val tokFNode = GraphNode(tok, NodeType.FNode)
        return if (adjList.contains(tokFNode)) tokFNode else null
    }

    /*
    * Given a token character returns its correspondent G-type GraphNode.
    * If the token isn't present in the graph null is returned.
    * */
    fun getGNode(tok: Char): GraphNode? {
        val tokGNode = GraphNode(tok, NodeType.GNode)
        return if (adjList.contains(tokGNode)) tokGNode else null
    }

    /*
    * Adds a connection to the graph. The inputted nodes are assumed to exist
    * within the graph and have their correspondent adjacency lists.
    * */
    fun addConnection(from: GraphNode, to: GraphNode) {
        adjList[from]!!.add(to)
    }

    /*
    * Calculates the topological order of the operator graph. Needed for finding
    * the longest path within the graph. The function throws if the graph happens
    * to be cyclic.
    * */
    private fun topologicalOrder(): List<GraphNode> {
        if (topOrderCache != null) {
            return topOrderCache!!
        }

        val visited: MutableSet<GraphNode> = mutableSetOf()
        val visiting: MutableSet<GraphNode> = mutableSetOf()
        val order: MutableList<GraphNode> = mutableListOf()

        fun dfsVisit(node: GraphNode) {
            if (visited.contains(node)) return
            if (visiting.contains(node)) throw IllegalStateException("Graph has a cycle.")

            visiting.add(node)

            for (adj in adjList[node]!!) {
                dfsVisit(adj)
            }

            visiting.remove(node)
            visited.add(node)
            order.add(0, node)
        }

        for (node in adjList.keys) {
            dfsVisit(node)
        }

        topOrderCache = order
        return order
    }

    /*
    * Given an input node 'start', returns the length of the longest path within
    * the graph originating at the 'start' node.
    * */
    fun longestPathLen(start: GraphNode): UInt {
        if (maxDistancesCache[start] != null) return maxDistancesCache[start]!!

        //Find the longest distance to all vertices from 'start' node
        val distances: MutableMap<GraphNode, Int> = mutableMapOf()
        adjList.keys.forEach { node ->
            distances[node] = Int.MIN_VALUE
        }
        distances[start] = 0
        val topOrder = topologicalOrder()
        for (node in topOrder) {
            adjList[node]?.forEach { adj ->
                if (distances[adj]!! < distances[node]!! + 1) {
                    distances[adj] = distances[node]!! + 1
                }
            }
        }

        maxDistancesCache[start] = distances.values.max().toUInt()
        return maxDistancesCache[start]!!
    }
}