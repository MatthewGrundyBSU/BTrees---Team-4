package cs321.common;

public class ParseArgumentUtils
{
    /**
     * Verifies if lowRangeInclusive <= argument <= highRangeInclusive
     */
    public static void verifyRanges(int argument, int lowRangeInclusive, int highRangeInclusive) throws ParseArgumentException
    {

    }

    public static int convertStringToInt(String argument) throws ParseArgumentException
    {
        return 0;
    }

    public static String parseDatabaseArgument(String arg) {
        return arg.substring("--database=".length());
    }

    public static String parseTypeArgument(String arg) {
        return arg.substring("--type=".length()).replaceAll("-","_");
    }

    public static int parseTopFrequencyArgument(String arg) {
        return Integer.parseInt(arg.substring("--top-frequency=".length()));
    }
}
