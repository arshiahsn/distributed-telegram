package pkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Node {
    //Server's IP+Port
    private String id;
    private final List<Node> children = new ArrayList<>();
    private Node parent;
    private Map<String, String> dataBase;

    public Node() {
        dataBase = new HashMap<String,String>();
    }
    public void setParent(Node parent){
    	this.parent = parent;
    }
    public Map<String,String> getDataBase(){
    	return dataBase;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public List<Node> getChildren() {
        return children;
    }
    
    public Node getParent() {
        return parent;
    }
    
    public void addChild(String id) {
        Node node = new Node();
        node.setParent(this);
        node.setId(id);
        getChildren().add(node);
    }
    
}



/*
 * End of tree 
 */
