package cs321.create;

import cs321.btree.BTree;
import cs321.btree.BTreeException;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;



/**
 * The driver class for building a BTree representation of an SSH Log file.
 *
 * @author 
 */
public class SSHCreateBTree {
    /**
     * Main driver of program.
     * @param args
     */
    public static void main(String[] args) throws Exception 
	{
        if (args.length == 0){
            System.out.println("java -jar build/libs/SSHCreateBTree.jar --cache=<0/1> --degree=<btree-degree> \\\r\n" + //
                                "          --sshFile=<ssh-File> --type=<tree-type> [--cache-size=<n>] \\\r\n" + //
                                "          --database=<yes/no> [--debug=<0|1>]");
            return;
        }
		// System.out.println("Hello world from cs321.create.SSHCreateBTree.main");
        SSHCreateBTreeArguments myArgs = parseArguments(args);
        // other code 
        BTree btree = new BTree(myArgs.getDegree(), myArgs.getSSHFileName());
        btree.dumpToDatabase("jdbc:sqlite:SSHLogDB.db", "SSHLog");
        // btree.dumpToFile();
        btree.close();
        
	}


    /**
     * Process command line arguments.
     * @param args  The command line arguments passed to the main method.
     */
    public static SSHCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException
    {
        int degree;
        try{
            degree = Integer.parseInt(getOpt("--degree=", args));
        }catch(Exception e){
            // System.err.println(e.getMessage());
            degree = 0;
        }

        boolean cache;
        if (getOpt("--cache=", args).equals("1")){
            cache = true;
        }else {
            cache = false;
        }

        int cacheSize;
        try{
            cacheSize = Integer.parseInt(getOpt("--cache-size=", args));
        }catch(Exception e){
            // System.err.println(e.getMessage());
            cacheSize = 0;
        }

        int debugLevel;
        try{
            debugLevel = Integer.parseInt(getOpt("--debug=", args));
        }catch(Exception e){
            // System.err.println(e.getMessage());
            debugLevel = 0;
        }
        
        SSHCreateBTreeArguments bta = new SSHCreateBTreeArguments(
                cache, 
                degree, 
                getOpt("--sshFile=", args), 
                getOpt("--type=", args), 
                cacheSize, 
                debugLevel);

        return bta;
    }


	/** 
	 * Print usage message and exit.
	 * @param errorMessage the error message for proper usage
	 */
	private static void printUsageAndExit(String errorMessage)
    {
        System.err.println(errorMessage);
        System.exit(1);
	}


    /**
     * Returns a string value for a command line option (usually beginning with --)
     * @param optName the option name (include -- and =)
     * @param args command line arguments
     * @return first option string minus nameString
     */
    private static String getOpt(String optName, String[] args){
        for(String s : args){
            if (s.startsWith(optName)){
                return s.substring(optName.length());
            }
        }
        return null;
    }

}
