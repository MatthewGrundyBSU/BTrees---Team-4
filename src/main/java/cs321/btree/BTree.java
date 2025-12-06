package cs321.btree;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTree implements BTreeInterface, AutoCloseable {

    int degree;
    TreeNode bTreeRoot;
    private FileChannel file;
    private long nextDiskAddress = 0;
    private static final int NODE_SIZE = 4096;
    private ByteBuffer buffer;

    public BTree(int degree) {
        if (degree <= 1) throw new IllegalArgumentException("degree must be > 1");
        this.degree = degree;
        bTreeRoot = new TreeNode(degree);
        buffer = ByteBuffer.allocate(NODE_SIZE);
        bTreeRoot.address = 0;
        nextDiskAddress = NODE_SIZE;
    }

    public BTree(int degree, String fileName) throws BTreeException {
        if (degree <= 1) throw new IllegalArgumentException("degree must be > 1");
        this.degree = degree;
        buffer = ByteBuffer.allocate(NODE_SIZE);

        try {
            File f = new File(fileName);
            this.file = new RandomAccessFile(f, "rw").getChannel();

            boolean fileHasData = f.exists() && f.length() >= NODE_SIZE;

            if (fileHasData) {
                this.bTreeRoot = diskRead(0);
                nextDiskAddress = file.size();
            } else {
                file.truncate(0);
                this.bTreeRoot = new TreeNode(degree);
                this.bTreeRoot.address = 0;
                nextDiskAddress = NODE_SIZE;
                diskWrite(this.bTreeRoot);
            }

        } catch (FileNotFoundException e) {
            throw new BTreeException("Invalid file: " + e.getMessage());
        } catch (IOException e) {
            throw new BTreeException("IOException: " + e.getMessage());
        }
    }

    public BTree(String fileName) throws BTreeException {
        this(2048, fileName);
    }

    public String[] getSortedKeyArray() {
        List<String> keys = new ArrayList<>();
        inOrderTraversal(bTreeRoot, keys);
        return keys.toArray(new String[0]);
    }

    private void inOrderTraversal(TreeNode node, List<String> keys) {
        if (node == null) return;
        int n = node.getNumObjects();

        for (int i = 0; i < n; i++) {
            if (!node.isLeaf() && i < node.getNumChildren()) {
                TreeNode child = loadChildIfNeeded(node, i);
                inOrderTraversal(child, keys);
            }
            TreeObject obj = node.getObject(i);
            keys.add(obj.getKey());
        }

        if (!node.isLeaf() && n < node.getNumChildren()) {
            TreeNode child = loadChildIfNeeded(node, n);
            inOrderTraversal(child, keys);
        }
    }

    @Override
    public long getSize() {
        return getSizeRecursive(bTreeRoot);
    }

    private long getSizeRecursive(TreeNode node) {
        if (node == null) return 0;

        long count = 0;
        for (TreeObject obj : node.nodeObjects) {
            count += obj.getCount();
        }

        for (int i = 0; i < node.getNumChildren(); i++) {
            TreeNode child = loadChildIfNeeded(node, i);
            count += getSizeRecursive(child);
        }

        return count;
    }

    @Override
    public int getDegree() {
        return this.degree;
    }

    public long getNumberOfNodes() {
        if (bTreeRoot == null) return 0;
        return recursiveGetNumberOfNodes(bTreeRoot);
    }

    private long recursiveGetNumberOfNodes(TreeNode node) {
        if (node == null) return 0;
        long count = 1;
        for (int i = 0; i < node.getNumChildren(); i++) {
            TreeNode child = loadChildIfNeeded(node, i);
            count += recursiveGetNumberOfNodes(child);
        }
        return count;
    }

    @Override
    public int getHeight() {
        if (bTreeRoot == null || bTreeRoot.getNumObjects() == 0) return 0;
        return recursiveGetHeight(bTreeRoot);
    }

    private int recursiveGetHeight(TreeNode node) {
        if (node.isLeaf()) return 0;
        int maxChildHeight = 0;
        for (int i = 0; i < node.getNumChildren(); i++) {
            TreeNode child = loadChildIfNeeded(node, i);
            int childHeight = recursiveGetHeight(child);
            if (childHeight > maxChildHeight) maxChildHeight = childHeight;
        }
        return 1 + maxChildHeight;
    }

    @Override
    public void insert(TreeObject k) throws IOException {
        TreeNode r = this.bTreeRoot;

        TreeObject existingRoot = r.getObject(k);
        if (existingRoot != null) {
            existingRoot.incCount();
            diskWrite(r);
            return;
        }

        if (r.isFull()) {
            TreeNode s = bTreeSplitRoot();
            bTreeInsertNonfull(s, k);
        } else {
            bTreeInsertNonfull(r, k);
        }
    }

    private TreeNode bTreeSplitRoot() throws IOException {
        TreeNode oldRoot = this.bTreeRoot;
        TreeNode newRoot = new TreeNode(degree);

        newRoot.address = nextDiskAddress;
        nextDiskAddress += NODE_SIZE;

        newRoot.addChildNode(oldRoot, 0);
        this.bTreeRoot = newRoot;
        bTreeSplitChild(newRoot, 0);

        diskWrite(newRoot);

        return newRoot;
    }

    private void bTreeSplitChild(TreeNode x, int i) throws IOException {
        int t = x.degree;
        TreeNode y = x.getChild(i);
        TreeNode z = new TreeNode(t);

        z.address = nextDiskAddress;
        nextDiskAddress += NODE_SIZE;

        TreeObject median = y.nodeObjects.get(t - 1);

        for (int j = t; j < 2 * t - 1 && j < y.nodeObjects.size(); j++) {
            z.nodeObjects.add(y.nodeObjects.get(j));
        }

        while (y.nodeObjects.size() > t - 1) {
            y.nodeObjects.remove(y.nodeObjects.size() - 1);
        }

        if (!y.isLeaf()) {
            for (int j = t; j < 2 * t && j < y.childNodes.size(); j++) {
                z.childNodes.add(y.childNodes.get(j));
                if (j < y.childAddresses.size()) {
                    z.childAddresses.add(y.childAddresses.get(j));
                }
            }
            while (y.childNodes.size() > t) {
                y.childNodes.remove(y.childNodes.size() - 1);
            }
            while (y.childAddresses.size() > t) {
                y.childAddresses.remove(y.childAddresses.size() - 1);
            }
        }

        x.nodeObjects.add(i, median);
        x.childNodes.add(i + 1, z);
        x.childAddresses.add(i + 1, z.address);

        diskWrite(y);
        diskWrite(z);
        diskWrite(x);
    }

    private void bTreeInsertNonfull(TreeNode node, TreeObject insertedObject) throws IOException {
        int i = node.getNumObjects() - 1;

        TreeObject existingHere = node.getObject(insertedObject);
        if (existingHere != null) {
            existingHere.incCount();
            diskWrite(node);
            return;
        }

        if (node.isLeaf()) {
            while (i >= 0 && insertedObject.compareTo(node.getObject(i)) < 0) i--;
            node.insertObject(insertedObject, i + 1);
            diskWrite(node);
            return;
        }

        while (i >= 0 && insertedObject.compareTo(node.getObject(i)) < 0) i--;
        i++;

        TreeNode child = loadChildIfNeeded(node, i);

        if (child.isFull()) {
            bTreeSplitChild(node, i);
            if (insertedObject.compareTo(node.getObject(i)) > 0) i++;
            child = loadChildIfNeeded(node, i);
        }

        bTreeInsertNonfull(child, insertedObject);
    }

    @Override
    public void dumpToFile(PrintWriter out) throws IOException {}

    @Override
    public void dumpToDatabase(String dbName, String tableName) throws IOException {}

    @Override
    public void close() throws IOException {
        if (file != null && file.isOpen()) {
            file.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    @Override
    public TreeObject search(String key) throws IOException {
        return searchRecursive(bTreeRoot, key);
    }

    private TreeObject searchRecursive(TreeNode node, String key) throws IOException {
        if (node == null) return null;
        int i = 0;
        while (i < node.getNumObjects() && key.compareTo(node.getObject(i).getKey()) > 0) i++;

        if (i < node.getNumObjects() && key.equals(node.getObject(i).getKey())) {
            return node.getObject(i);
        }

        if (node.isLeaf()) return null;

        TreeNode child = loadChildIfNeeded(node, i);
        return searchRecursive(child, key);
    }

    @Override
    public void delete(String key) {}

    private TreeNode loadChildIfNeeded(TreeNode parent, int childIndex) {
        if (childIndex >= parent.childAddresses.size()) {
            return null;
        }

        if (childIndex < parent.childNodes.size() && parent.childNodes.get(childIndex) != null) {
            return parent.childNodes.get(childIndex);
        }

        try {
            long childAddr = parent.childAddresses.get(childIndex);
            TreeNode child = diskRead(childAddr);

            while (parent.childNodes.size() <= childIndex) {
                parent.childNodes.add(null);
            }
            parent.childNodes.set(childIndex, child);

            return child;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TreeNode diskRead(long diskAddress) throws IOException {
        buffer.clear();
        for (int i = 0; i < NODE_SIZE; i++) {
            buffer.put((byte) 0);
        }
        buffer.clear();

        file.position(diskAddress);
        int bytesRead = file.read(buffer);

        if (bytesRead < 5) {
            throw new IOException("Not enough data to read node header");
        }

        buffer.flip();

        int numObjects = buffer.getInt();

        if (numObjects < 0 || numObjects > 2 * degree - 1) {
            throw new IOException("Invalid numObjects read from disk: " + numObjects);
        }

        byte leafFlag = buffer.get();
        boolean isLeaf = (leafFlag == 1);

        TreeNode node = new TreeNode(degree);
        node.address = diskAddress;

        for (int i = 0; i < numObjects; i++) {
            byte[] keyBytes = new byte[64];
            buffer.get(keyBytes);
            long count = buffer.getLong();

            int end = 0;
            while (end < 64 && keyBytes[end] != 0) end++;
            String key = new String(keyBytes, 0, end, java.nio.charset.StandardCharsets.UTF_8);

            TreeObject obj = new TreeObject(key, count);
            node.insertObject(obj);
        }

        if (!isLeaf) {
            int numChildren = numObjects + 1;
            for (int i = 0; i < numChildren; i++) {
                long childAddr = buffer.getLong();
                node.childAddresses.add(childAddr);
            }
        }

        return node;
    }

    public void diskWrite(TreeNode node) throws IOException {
        if (node.address == -1) {
            node.address = nextDiskAddress;
            nextDiskAddress += NODE_SIZE;
        }

        buffer.clear();

        for (int i = 0; i < NODE_SIZE; i++) {
            buffer.put((byte) 0);
        }
        buffer.clear();

        int n = node.getNumObjects();
        buffer.putInt(n);
        buffer.put(node.isLeaf() ? (byte) 1 : (byte) 0);

        for (int i = 0; i < n; i++) {
            TreeObject obj = node.getObject(i);
            byte[] keyBytes = new byte[64];
            byte[] actual = obj.getKey().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            System.arraycopy(actual, 0, keyBytes, 0, Math.min(actual.length, 64));
            buffer.put(keyBytes);
            buffer.putLong(obj.getCount());
        }

        if (!node.isLeaf()) {
            int expectedChildren = n + 1;
            for (int i = 0; i < expectedChildren; i++) {
                long childAddr = 0;

                if (i < node.childAddresses.size()) {
                    childAddr = node.childAddresses.get(i);
                } else if (i < node.childNodes.size() && node.childNodes.get(i) != null) {
                    TreeNode child = node.childNodes.get(i);
                    if (child.address == -1) {
                        child.address = nextDiskAddress;
                        nextDiskAddress += NODE_SIZE;
                    }
                    childAddr = child.address;

                    while (node.childAddresses.size() <= i) {
                        node.childAddresses.add(0L);
                    }
                    node.childAddresses.set(i, childAddr);
                }

                buffer.putLong(childAddr);
            }
        }

        buffer.flip();
        file.position(node.address);
        file.write(buffer);
    }
}