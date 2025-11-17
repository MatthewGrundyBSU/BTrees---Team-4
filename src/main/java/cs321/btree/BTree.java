package cs321.btree;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree implements BTreeInterface
{
    int degree;
    TreeNode bTreeRoot;
    private FileChannel file;
    private ByteBuffer buffer;

    /**
     *  Constuctor
     */
    public BTree(int degree) {
        if (degree <= 1) {
            throw new IllegalArgumentException("degree must be greater than 1");
        }
        this.degree = degree;
        bTreeRoot = new TreeNode(degree);
    }

    public BTree(int degree, String fileName) throws BTreeException{
        this(degree);
        File file = new File(fileName);
        try{
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                this.insert(new TreeObject(scan.nextLine()));
            }
        }catch (FileNotFoundException e){
            throw new BTreeException("invalid file: "+ e.getMessage());
        }catch (IOException e) {
            throw new BTreeException("IOException: " + e.getMessage());
        }
        

    }

    public BTree(String fileName)throws BTreeException{
        this(2048, fileName);
        
    }

    public String[] getSortedKeyArray(){
        return null;
    }


    /**
     * @return Returns the number of keys in the BTree.
     */
    @Override
    public long getSize() {
        return bTreeRoot.getSize();
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
    private long recursiveGetNumberOfNodes(TreeNode node) {
        long counter = 0;
        ArrayList<TreeNode> children = node.getChildren();
        for (TreeNode child : children) {
            counter += 1; // count this node
            counter += recursiveGetNumberOfNodes(child); // count all children recursively
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
        if (bTreeRoot.getSize() == (2 * degree)-1 ) {
            bTreeSplitRoot();

        }
        bTreeInsertNonfull(obj);
    }

    //
    private void bTreeInsertNonfull(TreeObject obj) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bTreeInsertNonfull'");
    }

    private void bTreeSplitRoot() throws IOException {
        TreeNode oldRoot = this.bTreeRoot;

        TreeNode newRoot = new TreeNode(degree);
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

    public Node diskRead(long diskAddress) throws IOException{
        if(diskAddress == 0) return null;
        file.position(diskAddress);
        buffer.clear();
        file.read(buffer);
        buffer.flip();
        int numObjects = buffer.getInt();

        byte leafFlag = buffer.get();
        boolean isLeaf = (leafFlag == 1);

        TreeNode node = new TreeNode(degree, isLeaf);
        node.address = diskAddress;

        for (int i = 0; i < numObjects; i++) {
             byte[] keyBytes = new byte[64];
            buffer.get(keyBytes);

            long count = buffer.getLong();

            if (i < numObjects) {
                int end = 0;
                while (end < 64 && keyBytes[end] != 0) end++;
                String key = new String(keyBytes, 0, end, java.nio.charset.StandardCharsets.UTF_8);

                TreeObject obj = new TreeObject(key, count); // adjust constructor
                node.insertObject(obj);
            }
        }
        return node;
    }

    public void diskWrite(TreeNode node) throws IOException {
        file.position(node.address);
        buffer.clear();

        int n = node.getNumObjects();
        buffer.putInt(n);

        for (int i = 0; i < n; i++) {
            TreeObject obj = node.getObject(i);
            buffer.putInt(obj.getKey().length());
            buffer.put(obj.getKey().getBytes());
    }
        if(node.isLeaf()) {
            buffer.put((byte) 1);
        } else {
            buffer.put((byte) 0);
            for (int i = 0; i < node.getNumChildren(); i++) {
                TreeNode child = node.getChild(i);
                buffer.putLong(child.address);
            }
        }
    
        buffer.flip();
        file.write(buffer);
    }
}
