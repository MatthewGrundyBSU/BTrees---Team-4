package cs321.btree;

import java.util.ArrayList;

public class TreeNode {
    
    
    int degree;
    bool isleaf;
    ArrayList<TreeObject> nodeObjects;
    ArrayList<TreeObject> childNodes;

    public TreeNode (int degree, bool isleaf) {
        this.degree = degree;
        nodeObjects = new ArrayList<TreeObject>(2*degree-1);
        childNodes = new TreeNode<TreeNode>(2*degree);
    }

    public void insertObject(TreeObject object){
        
    }

    public void insertObject(){

    }

    public void addChildNode(TreeNode childNode){

    }

    public TreeNode getChild(int index){

    }

    public TreeObject getObject(String key){

    }

}