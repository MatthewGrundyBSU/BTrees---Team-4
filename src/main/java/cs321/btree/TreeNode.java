package cs321.btree;

import java.util.ArrayList;

public class TreeNode {

    int degree;
    ArrayList<TreeObject> nodeObjects;
    ArrayList<TreeNode> childNodes;
    ArrayList<Long> childAddresses;
    long address = -1;

    public TreeNode(int degree) {
        this.degree = degree;
        nodeObjects = new ArrayList<TreeObject>(2 * degree - 1);
        childNodes = new ArrayList<TreeNode>(2 * degree);
        childAddresses = new ArrayList<Long>(2 * degree);
    }

    public void insertObject(TreeObject object) {
        nodeObjects.add(object);
    }

    public void insertObject(TreeObject object, int index) {
        nodeObjects.add(index, object);
    }

    public void addChildNode(TreeNode childNode) {
        childNodes.add(childNode);
        childAddresses.add(childNode.address);
    }

    public void addChildNode(TreeNode childNode, int index) {
        childNodes.add(index, childNode);
        childAddresses.add(index, childNode.address);
    }

    public TreeNode getChild(int index) {
        if (index >= childNodes.size()) return null;
        return childNodes.get(index);
    }

    public TreeObject getObject(TreeObject obj) {
        for (TreeObject node : nodeObjects) {
            if (node.compareTo(obj) == 0) {
                return node;
            }
        }
        return null;
    }

    public TreeObject getObject(int index) {
        if (index >= nodeObjects.size()) return null;
        return nodeObjects.get(index);
    }

    public int getNumObjects() {
        return nodeObjects.size();
    }

    public int getNumChildren() {
        return Math.max(childNodes.size(), childAddresses.size());
    }

    public ArrayList<TreeNode> getChildren() {
        return childNodes;
    }

    public boolean isLeaf() {
        return childAddresses.isEmpty() && childNodes.isEmpty();
    }

    public boolean isEmpty() {
        return childNodes.isEmpty() && nodeObjects.isEmpty();
    }

    /**
     * DEPRECATED: getSize() should not be called on TreeNode directly.
     * Use BTree.getSize() instead which properly handles lazy loading.
     * This method only counts objects in THIS node, not children.
     */
    public long getSize() {
        long count = 0;
        for (TreeObject obj : nodeObjects) {
            count += obj.getCount();
        }
        return count;
    }

    public int maxChildren() {
        return 2 * degree;
    }

    public boolean isFull() {
        return nodeObjects.size() == maxSize();
    }

    public int maxSize() {
        return 2 * degree - 1;
    }

    public static void main(String[] args) {
        TreeNode testNode = new TreeNode(2);
        System.out.println("TreeNode created successfully with degree 2");
    }
}