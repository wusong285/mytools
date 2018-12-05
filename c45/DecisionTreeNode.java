package com.ky.alert.util.c45;

import java.util.Map;

public class DecisionTreeNode {

    DecisionTreeNode parentNode;  

    String parentArrtibute;  

    String nodeName;

    Map probability;

    String[] arrtibutesArray;  

    DecisionTreeNode[] childNodesArray;

    public DecisionTreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(DecisionTreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public String getParentArrtibute() {
        return parentArrtibute;
    }

    public void setParentArrtibute(String parentArrtibute) {
        this.parentArrtibute = parentArrtibute;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Map getProbability() {
        return probability;
    }

    public void setProbability(Map probability) {
        this.probability = probability;
    }

    public String[] getArrtibutesArray() {
        return arrtibutesArray;
    }

    public void setArrtibutesArray(String[] arrtibutesArray) {
        this.arrtibutesArray = arrtibutesArray;
    }

    public DecisionTreeNode[] getChildNodesArray() {
        return childNodesArray;
    }

    public void setChildNodesArray(DecisionTreeNode[] childNodesArray) {
        this.childNodesArray = childNodesArray;
    }
}
