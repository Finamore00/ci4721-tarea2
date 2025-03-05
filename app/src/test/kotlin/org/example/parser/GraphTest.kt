package org.example.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class GraphTest {
    @Test fun longestPathTest() {
        val g: Graph = Graph()

        //Add symbols to the graph
        g.addNode('n')
        g.addNode('+')
        g.addNode('*')
        g.addNode('$')

        //Add all the respective edges (Es el grafo de la clase)
        g.addConnection(g.getGNode('n')!!, g.getFNode('*')!!)
        g.addConnection(g.getGNode('n')!!, g.getFNode('+')!!)
        g.addConnection(g.getGNode('n')!!, g.getFNode('$')!!)

        g.addConnection(g.getFNode('n')!!, g.getGNode('*')!!)
        g.addConnection(g.getFNode('n')!!, g.getGNode('+')!!)
        g.addConnection(g.getFNode('n')!!, g.getGNode('$')!!)

        g.addConnection(g.getFNode('*')!!, g.getGNode('*')!!)
        g.addConnection(g.getFNode('*')!!, g.getGNode('$')!!)
        g.addConnection(g.getFNode('*')!!, g.getGNode('+')!!)

        g.addConnection(g.getGNode('*')!!, g.getFNode('+')!!)
        g.addConnection(g.getGNode('*')!!, g.getFNode('$')!!)

        g.addConnection(g.getFNode('+')!!, g.getGNode('+')!!)
        g.addConnection(g.getFNode('+')!!, g.getGNode('$')!!)

        g.addConnection(g.getGNode('+')!!, g.getFNode('$')!!)

        assertEquals(4u, g.longestPathLen(g.getFNode('n')!!))
        assertEquals(2u, g.longestPathLen(g.getFNode('+')!!))
        assertEquals(4u, g.longestPathLen(g.getFNode('*')!!))
        assertEquals(0u, g.longestPathLen(g.getFNode('$')!!))

        assertEquals(5u, g.longestPathLen(g.getGNode('n')!!))
        assertEquals(1u, g.longestPathLen(g.getGNode('+')!!))
        assertEquals(3u, g.longestPathLen(g.getGNode('*')!!))
        assertEquals(0u, g.longestPathLen(g.getGNode('$')!!))
    }

    @Test fun cycleDetectionTest() {
        val g: Graph = Graph()

        //Add symbols to the graph
        g.addNode('n')
        g.addNode('+')
        g.addNode('*')
        g.addNode('$')

        //Add all the respective edges
        g.addConnection(g.getGNode('n')!!, g.getFNode('*')!!)
        g.addConnection(g.getGNode('n')!!, g.getFNode('+')!!)
        g.addConnection(g.getGNode('n')!!, g.getFNode('$')!!)

        g.addConnection(g.getFNode('n')!!, g.getGNode('*')!!)
        g.addConnection(g.getFNode('n')!!, g.getGNode('+')!!)
        g.addConnection(g.getFNode('n')!!, g.getGNode('$')!!)

        g.addConnection(g.getFNode('*')!!, g.getGNode('*')!!)
        g.addConnection(g.getFNode('*')!!, g.getGNode('$')!!)
        g.addConnection(g.getFNode('*')!!, g.getGNode('+')!!)

        g.addConnection(g.getGNode('*')!!, g.getFNode('+')!!)
        g.addConnection(g.getGNode('*')!!, g.getFNode('$')!!)

        g.addConnection(g.getFNode('+')!!, g.getGNode('+')!!)
        g.addConnection(g.getFNode('+')!!, g.getGNode('$')!!)

        g.addConnection(g.getGNode('+')!!, g.getFNode('$')!!)

        //This connection generates a cycle
        g.addConnection(g.getFNode('$')!!, g.getFNode('n')!!)

        assertFailsWith(IllegalStateException::class) { g.longestPathLen(g.getFNode('+')!!) }
    }
}