package com.fiafeng.common.pojo.Vo;

import com.fiafeng.common.pojo.Vo.Node;

import java.util.HashMap;


/**
 * url 搜索树
 */
public class SearchTree {

    public static final Node rootNode = new Node("*");

    public static final HashMap<String ,String>  hashMap = new HashMap<>();

    public void insert(String text) {

        if (text.startsWith("/")) {
            text = text.substring(1);
        }

        String[] splits = text.split("/");
        if (splits.length < 1) {
            return;
        }
        String s = splits[0];
        Node node = getInsertNode(rootNode, s);

        for (int i = 1; i < splits.length; i++) {
            String string = splits[i];
            // 如果是路径参数，替换成*
            if (string.startsWith("{") && string.endsWith("}")) {
                string = "*";
            }
            node = getInsertNode(node, string);
        }
        node.url = "/" + text;
    }

    private Node getInsertNode(Node node, String string) {
        Node nextNode;
        if (node.childMap.containsKey(string)) {
            nextNode = node.childNodeList.get(node.childMap.get(string));
        } else {
            nextNode = new Node(string);
            node.childMap.put(string, node.childNodeList.size());
            node.childNodeList.add(nextNode);
        }
        return nextNode;
    }

    public String  valueExistTree(String text) {
        if (text.startsWith("/")) {
            text = text.substring(1);
        }
        String[] splits = text.split("/");
        if (splits.length == 0) {
            return null;
        }
        String s = splits[0];
        Node node;
        if (!rootNode.childMap.containsKey(s)) {
            return null;
        }
        node = rootNode.childNodeList.get(rootNode.childMap.get(s));
        for (int i = 1; i < splits.length; i++) {
            String string = splits[i];
            if (!node.childMap.containsKey(string)) {
                if (node.getNode("*") != null) {
                    string = "*";
                }else {
                    return null;
                }
            }
            node = node.getNode(string);
        }
        return node.url;
    }


    public boolean  removeNode(String text) {


        if (text.startsWith("/")) {
            text = text.substring(1);
        }
        String[] splits = text.split("/");
        if (splits.length == 0) {
            return false;
        }
        String s = splits[0];
        Node node;
        if (!rootNode.childMap.containsKey(s)) {
            return false;
        }
        node = rootNode.childNodeList.get(rootNode.childMap.get(s));
        for (int i = 1; i < splits.length; i++) {
            String string = splits[i];
            if (!node.childMap.containsKey(string)) {
                if (node.getNode("*") != null) {
                    string = "*";
                }else {
                    return false;
                }
            }
            node = node.getNode(string);
        }
        node = null;
        return true;
    }

}
