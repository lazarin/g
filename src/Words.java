import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Words {

    static final String LOCAL_RESOURCE = "file:///projects/g/resources/scrabble-words.txt";
    static final String REMOTE_RESOURCE = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";

    List<String>[] wordBuckets;
    Set<String>[] solutionBuckets;


    public static void main(String[] args) {

        new Words().run();

    }

    private void run() {

        loadWords(REMOTE_RESOURCE);

        long startTime = System.currentTimeMillis();

        solve();

        long duration = System.currentTimeMillis() - startTime;
        System.out.printf("millis: %d\n", duration);

        printSolution();
    }

    /**
     * Loads all the words and splits them in groups by their length.
     * As we are interested only in words with length 9 all words longer than that are
     * stored in bucket 0 which is not used.
     *
     * @param resource location to load the resources from
     */
    private void loadWords(String resource) {

        wordBuckets = new ArrayList[10];
        Arrays.setAll(wordBuckets, (_) -> new ArrayList<String>());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(resource).openStream()))) {
            br
                    .lines()
                    .skip(2)
                    .forEach(word ->
                            wordBuckets[word.length() < 10 ? word.length() : 0].add(word)
                    );
        } catch (IOException e) {
            throw new RuntimeException("io error with resource '" + resource + "': " + e.getMessage());
        }
    }

    /**
     * Search for words with size 2, then with 3, 4, .., 9. If after scratching a character
     * for given word the new word is in the dictionary then add the current one to the solutions.
     * At the end the bucket with 9 character words contains all searched words.
     * The complexity of the algorithm is O(n^2) which for the concrete dictionary runs for 0.1 sec.
     */
    private void solve() {

        solutionBuckets = new HashSet[10];
        Arrays.setAll(solutionBuckets, (_) -> new HashSet<String>());

        // initialize with all valid single character words
        solutionBuckets[1].add("A");
        solutionBuckets[1].add("I");

        for (int i = 2; i < 10; i++) {
            for (int j = 0; j < wordBuckets[i].size(); j++) {

                String currentWord = wordBuckets[i].get(j);

                // construct a new word by scratching the character in position k and check if it is a valid word
                for (int k = 0; k < currentWord.length(); k++) {

                    String wordToCheck = currentWord.substring(0, k) + currentWord.substring(k + 1);

                    // if the new word is valid then add the current one to the solution bucket and
                    // continue with the next word; we are not interested in all the solutions, one is enough
                    if (solutionBuckets[i - 1].contains(wordToCheck)) {
                        solutionBuckets[i].add(currentWord);
                        break;
                    }
                }
            }
        }
    }

    private void printSolution() {

        for (String word : solutionBuckets[9]) {
            System.out.println(word);
        }
        System.out.println("count: " + solutionBuckets[9].size());
    }

}