package com.fiafeng.common.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {
    String text;

    List<Node> childNodeList;

    HashMap<String, Integer> childMap;

    String url;

    public Node(String text){
        this.text = text;
        childMap = new HashMap<>();
        childNodeList = new ArrayList<>();
    }


    public Node getNode(String text){
        if (!childMap.containsKey(text)){
            return null;
        }
        return childNodeList.get(childMap.get(text));
    }
}
