package cs321.btree;

import java.util.ArrayList;

public class TreeNode {
    
    
    int degree;
    ArrayList<TreeObject> nodeObjects;
    ArrayList<TreeNode> childNodes;
    public long address;

    /**
     * creates a BTree Node with a specified degree.
     * @param degree the BTree's degree
     * @param isleaf true if the variable is a leaf (will have no children)
     */
    public TreeNode (int degree) {
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
     * get the number of objects in the Node.
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

    public ArrayList<TreeNode> getChildren(){
        return childNodes;
    }

    public boolean isLeaf(){
        return childNodes.isEmpty();
    }

    public boolean isEmpty(){
        return childNodes.isEmpty() && nodeObjects.isEmpty();
    }

    public long getSize(){
        long count = getNumObjects();
        for (TreeNode node : childNodes) {
            count += node.getSize();
        }
        return count;
    }

    public int maxChildren(){
        return 2*degree;
    }

    public boolean isFull(){
        return nodeObjects.size() == maxSize();
    }

    public int maxSize(){
        return 2*degree -1;
    }

    //smoke test
    public static void main(String[] args) {
        TreeNode testNode = new TreeNode(2);
    }





}