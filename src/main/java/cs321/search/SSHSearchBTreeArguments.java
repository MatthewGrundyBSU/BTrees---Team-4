package cs321.search;


import cs321.common.ParseArgumentException;


public class SSHSearchBTreeArguments {


   private final boolean useCache;
   private final int degree;
   private final String bTreeFile;
   private final String queryFile;
   private final int topFrequency;
   private final int cacheSize;
   private final int debugLevel;


   private SSHSearchBTreeArguments(boolean useCache, int degree, String bTreeFile,
                                   String queryFile, int topFrequency, int cacheSize, int debugLevel) {
       this.useCache = useCache;
       this.degree = degree;
       this.bTreeFile = bTreeFile;
       this.queryFile = queryFile;
       this.topFrequency = topFrequency;
       this.cacheSize = cacheSize;
       this.debugLevel = debugLevel;
   }


   public static SSHSearchBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
       boolean useCache = false;
       int degree = 0;
       String bTreeFile = null;
       String queryFile = null;
       int topFrequency = 0;
       int cacheSize = 100;
       int debugLevel = 0;


       for (String arg : args) {
           if (arg.startsWith("--cache=")) {
               useCache = arg.substring("--cache=".length()).equals("1");
           } else if (arg.startsWith("--degree=")) {
               degree = Integer.parseInt(arg.substring("--degree=".length()));
           } else if (arg.startsWith("--btree-file=")) {
               bTreeFile = arg.substring("--btree-file=".length());
           } else if (arg.startsWith("--query-file=")) {
               queryFile = arg.substring("--query-file=".length());
           } else if (arg.startsWith("--top-frequency=")) {
               topFrequency = Integer.parseInt(arg.substring("--top-frequency=".length()));
           } else if (arg.startsWith("--cache-size=")) {
               cacheSize = Integer.parseInt(arg.substring("--cache-size=".length()));
           } else if (arg.startsWith("--debug=")) {
               debugLevel = Integer.parseInt(arg.substring("--debug=".length()));
           } else {
               throw new ParseArgumentException("Unknown argument: " + arg);
           }
       }


       if (degree <= 0 || bTreeFile == null || queryFile == null) {
           throw new ParseArgumentException("Missing required arguments.");
       }


       return new SSHSearchBTreeArguments(useCache, degree, bTreeFile, queryFile, topFrequency, cacheSize, debugLevel);
   }


   public boolean isCacheEnabled() {
       return useCache;
   }


   public int getDegree() {
       return degree;
   }


   public String getBTreeFile() {
       return bTreeFile;
   }


   public String getQueryFile() {
       return queryFile;
   }


   public int getTopFrequency() {
       return topFrequency;
   }


   public int getCacheSize() {
       return cacheSize;
   }


   public int getDebugLevel() {
       return debugLevel;
   }


   @Override
   public String toString() {
       return "SSHSearchBTreeArguments{" +
               "useCache=" + useCache +
               ", degree=" + degree +
               ", bTreeFile='" + bTreeFile + '\'' +
               ", queryFile='" + queryFile + '\'' +
               ", topFrequency=" + topFrequency +
               ", cacheSize=" + cacheSize +
               ", debugLevel=" + debugLevel +
               '}';
   }
}