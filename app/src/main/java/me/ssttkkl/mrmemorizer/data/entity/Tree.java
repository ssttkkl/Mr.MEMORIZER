package me.ssttkkl.mrmemorizer.data.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

import de.blox.graphview.Graph;
import de.blox.graphview.Node;

public class Tree {
    private final String content;
    private final Collection<Tree> children;

    public Tree(String content, Collection<Tree> children) {
        this.content = content;
        this.children = children;
    }

    static public Tree parseText(String text) {
        String[] strings = text.split("\n");
        String root = strings[0];

        final StringBuilder prevChildText = new StringBuilder();
        final Collection<Tree> children = new ArrayList<>();
        for (int i = 1; i < strings.length; i++) {
            if (strings[i].isEmpty())
                continue;
            strings[i] = strings[i].substring(1);
            if (!strings[i].startsWith(" ")) {
                if (prevChildText.length() != 0) {
                    children.add(Tree.parseText(prevChildText.toString()));
                }
                prevChildText.delete(0, prevChildText.length());
            } else {
                prevChildText.append('\n');
            }
            prevChildText.append(strings[i]);
        }

        if (prevChildText.length() != 0) {
            children.add(Tree.parseText(prevChildText.toString()));
        }

        return new Tree(root, children);
    }

    private void appendDisplayText(@NonNull StringBuilder sb, int prefixNum) {
        for (int i = 0; i < prefixNum; i++)
            sb.append(' ');
        sb.append(this.content);
        sb.append('\n');
        for (Tree c : children) {
            c.appendDisplayText(sb, prefixNum + 1);
        }
    }

    @NonNull
    public String toDisplayText() {
        StringBuilder sb = new StringBuilder();
        appendDisplayText(sb, 0);
        sb.deleteCharAt(sb.length() - 1); // 删除最后一行的换行
        return sb.toString();
    }

    public String getContent() {
        return content;
    }

    public Collection<Tree> getChildren() {
        return children;
    }

    public Graph convertToGraph() {
        Graph graph = new Graph();
        Node parentNode = new Node(getContent());
        graph.addNode(parentNode);
        for (Tree childTree : getChildren()) {
            Node childNode = new Node(childTree.getContent());
            graph.addNode(childNode);
            graph.addEdge(parentNode, childNode);
            addSubTreeToGraph(graph, childNode, childTree);
        }
        return graph;
    }


    private void addSubTreeToGraph(Graph graph, Node parentNode, Tree tree) {
        for (Tree childTree : tree.getChildren()) {
            Node childNode = new Node(childTree.getContent());
            graph.addNode(childNode);
            graph.addEdge(parentNode, childNode);
            addSubTreeToGraph(graph, childNode, childTree);
        }
    }
}
