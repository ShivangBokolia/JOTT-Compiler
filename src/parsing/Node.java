package parsing;

import scanning.Token;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private T data;
    private Node parent;

    private List<Node> children;

    public Node(T data, Node parent) {
        this.data = data;
        this.parent = parent;
        children = new ArrayList<>();
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getChild(int index){
        return children.get(index);
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
        // String to Token function
    }

    public void inorderPrint(){
        for (Node child: children){
            child.inorderPrint();
        }
        System.out.println(getData());
    }

}
