package se.lth.cs.perf;

/**
 * Parsing the perf output to get training data.
 */

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

    public static String parse() {
        Path p = Paths.get("output-perf.txt");
        ArrayList<String> lines = new ArrayList<>();
        try {
            // Getting all the lines in the output-perf.txt file
            FileReader fr = new FileReader(p.toFile());
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            String message = "File not found. The working directory is:";
            String currDir = "'" + System.getProperty("user.dir") + "'";
            System.out.println(message + currDir);
        }

        ArrayList<String> data = filterLines(lines);

        return "";
    }

    /**
     * Skips through the beginning of the log up to "Perf stats:"
     * @param lines
     * @return
     */
    public static ArrayList<String> filterLines(ArrayList<String> lines) {
        String perfHeader = "Perf stats:";
        boolean perfHeadFound = false;
        // Read full file after the header "Perf stats:"
        ArrayList<String> data = new ArrayList<>();
        for (String line : lines) {
            if (line == "Perf stats:") {
                perfHeadFound = true;
            }
            if (perfHeadFound) {
                data.add(line);
            }
        }
        return data;
    }

    /**
     * Takes a list of values and puts them into separated bins (one per run)
     * @param lines
     * @return
     */
    public static ArrayList<ArrayList<String>> subDivide(ArrayList<String> lines) {
        // First line should match this
        Pattern beginning = Pattern.compile("task-clock");
        // End line of chunk should match this
        Pattern end = Pattern.compile("seconds time elapsed");

        Pattern whitespace = Pattern.compile("\\s+");
        ArrayList<String> lines2 = lines.stream().filter( whitespace.asPredicate() )
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        boolean capture = false;
        ArrayList<String> current = null;
        for(String line : lines2) {
            if(beginning.matcher(line).find()) {
                current = new ArrayList<>();
                result.add(current);
                capture = true;
            }
            if(capture) {
                current.add(line);
            }
            if(end.matcher(line).find()) {
                current = null;
                capture = false;
            }
        }
        return result;
    }

    public static HashMap<String, String> tokenizeLine(String line) {
        Pattern whitespaceRegex = Pattern.compile("\\s+");
        Pattern number = Pattern.compile("[0-9]+(\\.[0-9]+)?");
        Pattern word = Pattern.compile("[a-zA-Z0-9]+");
        Pattern propertyLabel = Pattern.compile( word + "(" + "-" + word + ")*");

        Stream<String> chunks = Arrays.stream(whitespaceRegex.split(line)).filter((s) -> !s.isEmpty());
        ArrayList<String> tokens = chunks.collect(Collectors.toCollection(ArrayList::new));
        if (tokens.size() == 0) {
            return new HashMap<>();
        }
        if (number.matcher(tokens.get(0)).matches()) {
            if (propertyLabel.matcher(tokens.get(1)).matches()) {
                HashMap<String, String> result = new HashMap<>();
                result.put("label", tokens.get(1));
                result.put("value", tokens.get(0));
                return result;
            }
        }
        return new HashMap<>();
    }
}
