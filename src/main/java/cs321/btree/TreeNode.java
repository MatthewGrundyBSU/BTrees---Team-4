package cs321.btree;

import java.util.ArrayList;

public class TreeNode {
    
    
    int degree;
    boolean isleaf;
    ArrayList<TreeObject> nodeObjects;
    ArrayList<TreeNode> childNodes;

    /**
     * creates a BTree Node with a specified degree.
     * @param degree the BTree's degree
     * @param isleaf true if the variable is a leaf (will have no children)
     */
    public TreeNode (int degree, boolean isleaf) {
        this.degree = degree;
        nodeObjects = new ArrayList<TreeObject>(2*degree-1);
        childNodes = new ArrayList<TreeNode>(2*degree);
    }

    public void insertObject(TreeObject object){
        nodeObjects.add(object);
    }

    public void insertObject(TreeObject object, int index){
        nodeObjects.add(index, object);
    }

    public void addChildNode(TreeNode childNode){
        childNodes.add(childNode);
    }

    public void addChildNode(TreeNode childNode, int index){
        childNodes.add(index, childNode);
    }

    public TreeNode getChild(int index){
        return childNodes.get(index);
    }

    public TreeObject getObject(TreeObject obj){
        for (TreeObject node : nodeObjects) {
            if (node.compareTo(obj) == 0){
                return node;
            }
        }
        return null;
    }

    public TreeObject getObject(int index){
        return nodeObjects.get(index);
    }

    /**
     * get the number 
     * @return
     */
    public int getNumObjects(){
        return nodeObjects.size();
    }

    /**
     * Get the number of children
     * @return number of child nodes.
     */
    public int getNumChildren(){
        return childNodes.size();
    }

    public boolean isLeaf(){
        return isleaf;
    }

    public void setIsLeaf(boolean isLeaf){
        this.isleaf = isLeaf;
    }

    //smoke test
    public static void main(String[] args) {
        TreeNode testNode = new TreeNode(2, false);
        System.out.println(testNode.nodeObjects.size());
    }



}