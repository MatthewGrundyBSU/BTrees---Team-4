package cs321.search;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Driver program for searching a B-Tree index using a set of query keys.
 * Reads queries from a file, looks them up in a B-Tree, and prints frequencies.
 */
public class SSHSearchBTree {
    public static void main(String[] args) throws BTreeException {
        SSHSearchBTreeArguments arguments;

        try {
            arguments = SSHSearchBTreeArguments.parseArguments(args);
        } catch (ParseArgumentException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            printUsageAndExit();
            return;
        }
        
        // Initialize BTree and Query Reader with try-with-resources to prevent leaks
        try (BTree bTree = new BTree(arguments.getDegree(), arguments.getBTreeFile());
             BufferedReader queryReader = new BufferedReader(new FileReader(arguments.getQueryFile()))) {

            //Read all query keys into memory
            List<String> queryKeys = new ArrayList<>();
            String line;
            while ((line = queryReader.readLine()) != null) {
                queryKeys.add(line.trim());
            }

            //Use a PriorityQueue ordered by frequency then alphabetically
            PriorityQueue<Entry<String, Integer>> pq = new PriorityQueue<>(
                Comparator.<Entry<String, Integer>>comparingInt(Entry::getValue).reversed().thenComparing(Entry::getKey)
            );

            //Search the BTree for each query key
            for (String key : queryKeys) {
                TreeObject treeObj = bTree.search(key);
                if (treeObj != null) {
                    pq.offer(new AbstractMap.SimpleEntry<>(key, (int) treeObj.getCount()));
                }
            }

			//Prints results
			if (arguments.getTopFrequency() > 0) {
               System.out.println("Top " + arguments.getTopFrequency() + " results:");
               for (int i = 0; i < arguments.getTopFrequency() && !pq.isEmpty(); i++) {
                   Entry<String, Integer> entry = pq.poll();
                   System.out.println(entry.getKey() + ": " + entry.getValue());
               }
           } else {
               System.out.println("Results for all keys:");
               while (!pq.isEmpty()) {
                   Entry<String, Integer> entry = pq.poll();
                   System.out.println(entry.getKey() + ": " + entry.getValue());
               }
		   }

        } catch (IOException e) {
            // Handle IO exceptions (e.g., file reading errors)
            System.err.println("Error during file operations: " + e.getMessage());
        }
        // Resources are automatically closed here due to try-with-resources
    }
	
    private static void printUsageAndExit() {
        System.out.println(
            "Usage: java -jar SSHSearchBTree.jar --cache=<0/1> --degree=<btree-degree> \\\n" +
            "\t--btree-file=<btree-filename> --query-file=<query-file> [--top-frequency=<10/25/50>] \\\n" +
            "\t[--cache-size=<n>] [--debug=<0|1>]"
        );
        System.exit(1);
    }
}

