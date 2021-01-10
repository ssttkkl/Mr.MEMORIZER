package me.ssttkkl.mrmemorizer.data.entity;

import java.util.ArrayList;
import java.util.List;

import de.blox.graphview.Graph;
import de.blox.graphview.Node;

public class Tree {
    private String content;
    private List<Tree> children = new ArrayList<>();

    public Tree(String data){
        String[] strings = data.split("\n");
        this.content = strings[0];
        String child = "";
        for(int i=1;i<strings.length;i++){
            strings[i] = strings[i].substring(1);
            if (!strings[i].contains("\t")){
                if(!child.isEmpty()){
                    children.add(new Tree(child));
                }
                child = strings[i];
            }else{
                child += "\n"+strings[i];
            }
        }
        if(!child.isEmpty()){
            children.add(new Tree(child));
        }
    }

    public String getContent() {
        return content;
    }

    public List<Tree> getChildren() {
        return children;
    }

    public Graph convertToGraph(){
        Graph graph = new Graph();
        Node parentNode = new Node(getContent());
        graph.addNode(parentNode);
        for(Tree childTree:getChildren()){
            Node childNode = new Node(childTree.getContent());
            graph.addNode(childNode);
            graph.addEdge(parentNode,childNode);
            addSubTreeToGraph(graph,childNode,childTree);
        }
        return graph;
    }


    private void addSubTreeToGraph(Graph graph, Node parentNode, Tree tree){
        for(Tree childTree:tree.getChildren()){
            Node childNode = new Node(childTree.getContent());
            graph.addNode(childNode);
            graph.addEdge(parentNode,childNode);
            addSubTreeToGraph(graph,childNode,childTree);
        }
    }
}
