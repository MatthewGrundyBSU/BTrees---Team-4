package cs321.btree;

import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

public class BTree implements BTreeInterface
{
    int degree;
    TreeNode bTreeRoot;

    /**
     *  Constuctor
     */
    public BTree(String fileName) {
        // Has a standard degree of 2
        this(2);
    }

    public BTree(int degree, String filename) {
        this(degree);
    }

    public BTree(int degree) {
        if (degree == 0) {
            throw new IllegalArgumentException("degree must be greater than 0");
        }
        this.degree = degree;
        bTreeRoot = new TreeNode(degree, true);
    }

    /**
     * @return Returns the number of keys in the BTree.
     */
    @Override
    public long getSize() {
        //return bTreeRoot.length();
        return 0;
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
        return recursiveGetNumberOfNodes(bTreeRoot);
    }

    // Recursive method that takes a list of nodes
    private long recursiveGetNumberOfNodes(TreeNode node) {
        if (node == null) { return 0; }
        long counter = 1;

        if (!node.isLeaf()) {
            for (int i = 0; i < node.getNumChildren(); i++) {
                counter += recursiveGetNumberOfNodes(node.getChild(i));
            }
        }
        return counter;
    }

    /**
     * @return The height of the BTree
     */
    @Override
    public int getHeight() {
        /**
         * Not needed for checkpoint 1
         */
        return 0;
    }

    //Temp so it runs
    public String[] getSortedKeyArray() {
        return "A B C D E F G H I J K".split(", ");
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

        TreeNode r = bTreeRoot;

        if (bTreeRoot.getNumObjects() == (2 * degree)-1 ) {
            TreeNode s = bTreeSplitRoot();
            bTreeInsertNonfull(s, obj);

        } else {
            bTreeInsertNonfull(r, obj);
        }
    }

    //
    private TreeNode bTreeSplitRoot() {
        TreeNode oldRoot = bTreeRoot;
        TreeNode newRoot = new TreeNode(degree, false);

        // Add old root as child 0
        newRoot.addChildNode(oldRoot);

        // Split child 0 (old root)
        bTreeSplitChild(newRoot, 0);

        bTreeRoot = newRoot;
        return newRoot;
    }

    private void bTreeInsertNonfull(TreeNode x, TreeObject k) {
        int i = x.getNumObjects() - 1;

        if (x.isLeaf()) {
            // Insert k into the correct position in nodeObjects
            while (i >= 0 && k.compareTo(x.getObject(i)) < 0) {
                i--;
            }
            x.insertObject(k, i + 1);
        } else {
            // Find the child to descend into
            while (i >= 0 && k.compareTo(x.getObject(i)) < 0) {
                i--;
            }
            i++; // child index

            // Ensure the child exists
            while (i >= x.getNumChildren()) {
                x.addChildNode(new TreeNode(x.degree, true));
            }

            TreeNode child = x.getChild(i);

            // If child is full, split it
            if (child.getNumObjects() == 2 * x.degree - 1) {
                bTreeSplitChild(x, i);

                // After split, determine which child to go into
                if (k.compareTo(x.getObject(i)) > 0) {
                    i++;
                }
                child = x.getChild(i);
            }

            bTreeInsertNonfull(child, k);
        }
    }

    private void bTreeSplitChild(TreeNode parent, int i) {
        TreeNode y = parent.getChild(i);
        int t = degree;

        TreeNode z = new TreeNode(t, y.isLeaf());

        // Move last t-1 keys from y to z
        for (int j = 0; j < t - 1; j++) {
            z.insertObject(y.getObject(j + t));
        }

        // Move last t children if not a leaf
        if (!y.isLeaf()) {
            for (int j = 0; j < t; j++) {
                z.addChildNode(y.getChild(j + t));
            }
            // Remove moved children from y
            for (int j = y.getNumChildren() - 1; j >= t; j--) {
                y.childNodes.remove(j);
            }
        }

        // Remove moved keys from y
        for (int j = y.getNumObjects() - 1; j >= t - 1; j--) {
            y.nodeObjects.remove(j);
        }

        // Insert z as a new child of parent
        parent.addChildNode(z, i + 1);

        // Move median key from y to parent
        TreeObject median = y.getObject(t - 1);
        parent.insertObject(median, i);
    }




    /**
     * Print out all objects in the given BTree in an inorder traversal to a file.
     *
     * @param out PrintWriter object representing output.
     */
    @Override
    public void dumpToFile(PrintWriter out) throws IOException {
        return;
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
