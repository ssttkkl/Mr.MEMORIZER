package me.ssttkkl.mrmemorizer.data.entity

import dev.bandb.graphview.graph.Graph
import dev.bandb.graphview.graph.Node


fun Tree.convertToGraph(): Graph {
    val graph = Graph()
    val parentNode = Node(content)
    graph.addNode(parentNode)
    for (childTree in children) {
        val childNode = Node(childTree.content)
        graph.addNode(childNode)
        graph.addEdge(parentNode, childNode)
        addSubTreeToGraph(graph, childNode, childTree)
    }
    return graph
}

private fun addSubTreeToGraph(
    graph: Graph,
    parentNode: Node,
    tree: Tree
) {
    for (childTree in tree.children) {
        val childNode = Node(childTree.content)
        graph.addNode(childNode)
        graph.addEdge(parentNode, childNode)
        addSubTreeToGraph(graph, childNode, childTree)
    }
}