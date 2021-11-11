package com.company.service.algorithms;

import com.company.domain.Tuple;

import java.util.*;

import static java.util.Collections.max;

public class GraphBFS<ID> {
    private Map<ID, Set<ID>> connections;
    private Map<ID, Boolean> viz;
    private ID root = null;

    public GraphBFS() {
        this.connections = new HashMap<>();
        this.viz = new HashMap<>();
    }

    public void addEdgeOneWay(ID st, ID dr){
        if(root == null)
            root = st;
        if(!connections.containsKey(st))
            connections.put(st, new HashSet<>());
        if(st != dr)
            connections.get(st).add(dr);

        if(!viz.containsKey(st))
            viz.put(st, false);
        if(!viz.containsKey(dr))
            viz.put(dr, false);
    }

    public void addEdge(ID st, ID dr){
        addEdgeOneWay(st, dr);
        addEdgeOneWay(dr, st);
    }

    private List<ID> getNodes(){
        return this.connections.keySet().stream().toList();
    }

    private List<ID> getConnections(ID nod){
        return this.connections.get(nod).stream().toList();
    }

    private Map<ID, Set<ID>> connectedComponentsBFS(ID start){
        __resetViz();
        Map<ID, Set<ID>> rez = new HashMap<>();
        // List<ID> keys = this.connections.keySet().stream().toList(); for tests
        for(ID key: this.getNodes()) {
            if(!viz.get(key)) {
                rez.put(key, new HashSet<>()); // initialize community map with a certain key as root
                //BFS start
                Queue<ID> q = new LinkedList<>();
                q.add(key);
                viz.remove(key);
                viz.put(key, true);
                while (q.size() != 0) {
                    ID nod = q.peek();
                    q.remove();
                    // Connections list
                    //List<ID> tempL = this.connections.get(key).stream().toList();
                    for (ID i : this.getConnections(nod)) // loop throughout the list
                        if (!viz.get(i)) {
                            viz.remove(i);
                            viz.put(i, true);
                            q.add(i);
                            rez.get(key).add(i); // Append to the community
                        }
                }
            }
        }
        return rez;
    }

    private Tuple<ID, Integer> furthestNodeBFS(ID start){
        ID endNode = start;
        Queue<ID> q = new LinkedList<>();
        q.add(start);
        Map<ID, Integer> VIZ = new HashMap<>();
        VIZ.put(start, 0);
        while (q.size() != 0) {
            ID nod = q.peek();
            q.remove();
            Integer distance = VIZ.get(nod);
            Set<ID> minNodes = new HashSet<>();
            // List of connections
            //List<ID> tempL = this.connections.get(key).stream().toList();
            for (ID i : this.getConnections(nod)) // loop throughout the list
                if (!VIZ.containsKey(i)) {
                    VIZ.put(i, distance + 1);
                    q.add(i);
                    minNodes.add(i);
                }
            if(minNodes.size() > 0)
                endNode = minNodes.stream().toList().get(0);
        }


        return new Tuple<ID, Integer>(endNode, VIZ.get(endNode));
    }

    public Map<ID, Set<ID>> getConnectedComponents(){
        return connectedComponentsBFS(this.root);
    }

    public int getDiameter(){
        int rez = 1;
        List<ID> roots = connectedComponentsBFS(this.root).keySet().stream().toList();
        for(ID startNode: roots) {
            int distance = 0;
            Tuple<ID, Integer> endPair = furthestNodeBFS(startNode);
            distance += endPair.getRight();
            endPair = furthestNodeBFS(endPair.getLeft());
            if(endPair.getLeft() != startNode)
                distance += endPair.getRight();
            if(distance > rez)
                rez = distance;
        }
        return rez;
    }

    private void __resetViz(){
        this.connections.keySet().forEach(x->{
           viz.remove(x);
           viz.put(x, false);
        });
    }

    public List<ID> getVertices(){
        return this.connections.keySet().stream().toList();
    }
}
