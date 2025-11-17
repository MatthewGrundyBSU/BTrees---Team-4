package cs321.btree;

import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

public class BTree implements BTreeInterface
{
    int degree;
    ArrayList<bTreeNode> bTreeRoot;

    /**
     *  Constuctor
     */
    public BTree(int degree) {
        if (degree == 0) {
            throw new IllegalArgumentException("degree must be greater than 0");
        }
        this.degree = degree;
        bTreeRoot = new ArrayList<bTreeNode>();
    }

    /**
     * @return Returns the number of keys in the BTree.
     */
    @Override
    public long getSize() {
        return bTreeRoot.length;
    }

    /**
     * @return The degree of the BTree.
     */
    @Override
    public int getDegree() {
        return this.degree;
    }

    /**
     * @return Returns the number of nodes in the BTree.
     */
    public long getNumberOfNodes() {
        if (bTreeRoot == null) { return 0; }
        return recursiveGetNumberOfNodes(bTreeRoot);
    }

    // Recursive method that takes a list of nodes
    private long recursiveGetNumberOfNodes(ArrayList<bTreeNode> nodes) {
        long counter = 0;

        for (bTreeNode node : nodes) {
            counter += 1; // count this node
            counter += recursiveGetNumberOfNodes(node.children()); // count all children recursively
        }

        return counter;
    }

    /**
     * @return The height of the BTree
     */
    @Override
    public int getHeight() {
//        // Set up?
//        if (bTreeRoot == null || bTreeRoot.isEmpty()) { return 0; }
//
//        int maxHeight = 0;
//
//        for ( : bTreeRoot) {
//
//        }
        return 0;
    }


    /**
     * Insert a given SSH key into the B-Tree. If the key already exists in the B-Tree,
     * the frequency count is incremented. Otherwise, a new node is inserted
     * following the B-Tree insertion algorithm.
     *
     * @param obj A TreeObject representing an SSH key.
     */
    @Override
    public void insert(TreeObject obj) throws IOException {
        if (bTreeRoot.size() == (2 * degree)-1 ) {
            bTreeSplitRoot();

        }
        bTreeInsertNonfull(obj);
    }

    //
    private void bTreeSplitRoot() throws IOException {

    }
    /**
     * Print out all objects in the given BTree in an inorder traversal to a file.
     *
     * @param out PrintWriter object representing output.
     */
    @Override
    public void dumpToFile(PrintWriter out) throws IOException {

    }

    /**
     * Dump out all objects in the given BTree in an inorder traversal to a table in the database.
     * <p>
     * If the database does not exist, then it is created and the table is added.
     * <p>
     * If the provided database already exists, then the table is added. If the table already exists,
     * then the table is replaced.
     *
     * @param dbName    String referring to the name of the database.
     * @param tableName String referring to the table of the database.
     */
    @Override
    public void dumpToDatabase(String dbName, String tableName) throws IOException {

    }

    /**
     * Searches for a key in the given BTree.
     *
     * @param key The key value to search for.
     */
    @Override
    public TreeObject search(String key) throws IOException {
        return null;
    }

    /**
     * Deletes a key from the BTree. Not Implemented.
     *
     * @param key the key to be deleted
     */
    @Override
    public void delete(String key) {

    }
}
