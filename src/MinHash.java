
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Song
 */


public class MinHash {
    boolean debug = true;
    boolean big=false;
    File folder;
    int numPermutations;
    // Set of permutations
    Permutation perms[];

    Preprocessing pre;

    List<String> allUniqueTerms;

    // for fast lookup
    HashMap<String, Integer> fileIndex;
    // hello:0, world:1, ...
    HashMap<String, Integer> uniqueWordIndex;
    // 0:0 (hello), 1:4 (world)
    int[] multiWordStartIndex;

    // cache variables
    int[][] termDocumentMatrix;
    int[] multiSetUnion;

    /**
     * The constructor functions are semi-dependent on the call order.
     *
     * @param folder
     * @param numPermutations
     */
    public MinHash(String folder, int numPermutations) {
        this.folder = new File(folder);
        this.numPermutations = numPermutations;
        perms = new Permutation[numPermutations];
        pre = new Preprocessing(folder);
        constructAllUniqueWordIndex();
        constructFileIndex();
        termDocumentMatrix= termDocumentMatrix();
        constructMultiSetUnion();
        constructPermutations();
    }


    /**
     * @param fileName
     * @return
     */
    public int[] minHashSig(String fileName) {
        int[] termVector;
        if (!big){
            termVector = termDocumentMatrix[fileIndex.get(fileName)];
        }else{
            termVector = termDocumentFrequency(fileName);
        }

        int[] minHashVals = new int[numPermutations];
//        int[] duplicateCount = new int[numUniqueTerms()];
        Arrays.fill(minHashVals, Integer.MAX_VALUE);
//        Arrays.fill(duplicateCount, 0);

        int hashVal;
        if (debug) {
            assert (termVector.length == multiWordStartIndex.length);
        }

        int multiWordIndex;
        int permutedIndex;
        for (int i = 0; i < termVector.length; i++) {
            for (int j = 0; j < termVector[i]; j++) {
                multiWordIndex = multiWordStartIndex[i] + j;
                for (int k = 0; k < numPermutations; k++) {
                    permutedIndex = perms[k].to(multiWordIndex);
//					hashVal=(""+permutedIndex).hashCode();
                    hashVal = permutedIndex;
                    if (hashVal < minHashVals[k]) {
                        minHashVals[k] = hashVal;
                    }
                }
            }
        }

//
//        for (int j = 0; j < words.length; j++) {
//            for (int i = 0; i < numPermutations; i++) {
//                // get current word index
//                duplicateCount[]
//                // permute
//                perms[i].to(words[j])
//                hashVal = hashFunction.hash(words[j]);
//                if (hashVal < minHashVals[i])
//                    minHashVals[i] = hashVal;
//            }
//        }

        return minHashVals;
    }

    /**
     * @return
     */
    public int[][] minHashMatrix() {
        File[] allFiles = folder.listFiles();
        int[][] minHashMatrix = new int[allFiles.length][numPermutations];

        int[] doc;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].isFile()) {
                doc = minHashSig(allFiles[i].getName());

                for (int j = 0; j < numPermutations; j++) {
                    minHashMatrix[i][j] = doc[j];
                }
            }
        }
        return minHashMatrix;
    }

    public int[] termDocumentFrequency(String fileName) {
        if (termDocumentMatrix!=null){
            return termDocumentMatrix[getIndex(fileName)];
        }
        int[] currentTDF = new int[numUniqueTerms()];
        Arrays.fill(currentTDF, 0);
        String[] words = pre.process(fileName);

        String currentWord;
        for (int i = 0; i < words.length; i++) {
            currentWord = words[i];
            currentTDF[uniqueWordIndex.get(currentWord)] += 1;
        }

        return currentTDF;
    }

    /**
     * @return
     */
    public int[][] termDocumentMatrix() {
        if (termDocumentMatrix != null || big) {
            return termDocumentMatrix;
        }
        File[] allFiles = folder.listFiles();
        int[][] termDocumentMatrix;
        try{
            termDocumentMatrix = new int[allFiles.length][numUniqueTerms()];
        }catch (OutOfMemoryError e){
            System.out.println("I cannot generate termDocumentMatrix, JVM out of memory. " +
                    "I will try to compute MinHash still by accessing files directly.");
            big=true;
            this.termDocumentMatrix=null;
            return null;
        }

        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i].isFile()) {
                int[] dtf = termDocumentFrequency(allFiles[i].getName());
                termDocumentMatrix[i] = dtf;
            }
        }
        this.termDocumentMatrix = termDocumentMatrix;
        return termDocumentMatrix;
    }

    /***
     * has the side effect of constructing multiWordStartIndex
     * @return
     */

    public int[] constructMultiSetUnion() {
        multiWordStartIndex = new int[numUniqueTerms()];
        int nut=numUniqueTerms();
        if (multiSetUnion != null) {
            return multiSetUnion;
        }
        if (!big){
            int[][] tdm = termDocumentMatrix();
            int[] union = new int[tdm[0].length];
            Arrays.fill(union, 0);
            int multiSetUniqueWord = 0;
            if (debug) {
                assert (nut == tdm[0].length);
            }

            for (int termi = 0; termi < tdm[0].length; termi++) {
                multiWordStartIndex[termi] = multiSetUniqueWord;
                for (int docj = 0; docj < tdm.length; docj++) {
//                union[termi] = Math.max(tdm[docj][termi], union[termi]);
                    union[termi] = tdm[docj][termi]> union[termi]? tdm[docj][termi]:union[termi];
                }
                multiSetUniqueWord += union[termi];
            }
            if (debug) {
                for (int termj = 0; termj < tdm[0].length; termj++) {
                    // all terms must have at least one apperance in one of the documents
                    assert (union[termj] > 0);
                }
            }
            multiSetUnion = union;
            return union;
        }else{
            // if term document matrix is too big
            int[] union = new int[nut];
            Arrays.fill(union, 0);
            // One pass through all files to collect the maximum frequency of each word
            for (String fileName: allDocs()
                 ) {
                int[] thisFrequency = termDocumentFrequency(fileName);
                for (int i = 0; i < thisFrequency.length; i++) {
                    if (union[i]<thisFrequency[i]){
                        union[i]=thisFrequency[i];
                    }
                }
            }

            // Construct multiWordStartIndex
            int multiSetUniqueWord = 0;
            for (int i = 0; i < nut; i++) {
                multiWordStartIndex[i] = multiSetUniqueWord;
                multiSetUniqueWord+=union[i];
            }

            if (debug) {
                for (int termj = 0; termj < nut; termj++) {
                    // all terms must have at least one apperance in one of the documents
                    if (union[termj] <= 0){
                        throw new ValueException("Term does not appear?");
                    }
                }
            }
            multiSetUnion = union;
            return union;
        }
    }


    private void constructFileIndex() {
        fileIndex = fileList();
    }

    private HashMap<String, Integer> fileList() {
        File[] contents = folder.listFiles();
        HashMap<String, Integer> fileIndex = new HashMap<>();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isFile()) {
                fileIndex.put(contents[i].getName(), i);
            }
        }
        return fileIndex;
    }

    /**
     * @return
     */
    public String[] allDocs() {
        return folder.list();
    }

    /***
     * Num of multiset terms, not unique
     * hello.0 hello.1 hello.2 count as three words here, but one in numUniqueTerms
     * @return
     */
    public int numTerms() {
        int sum = 0;
        for (int termj = 0; termj < multiSetUnion.length; termj++) {
            sum += multiSetUnion[termj];
        }
        return sum;
    }

    public int numMultiTerms() {
        return numTerms();
    }

    public int numUniqueTerms() {
        return allUniqueTerms.size();
    }

    /**
     * All terms is an expensive operation. It is stored in memory and computed only once.
     * Also it has side effect of initializing two vars, see below
     *
     * @return
     */
    public void constructAllUniqueWordIndex() {
        List<String> allUniqueTerms = new ArrayList<>();
        File[] contents = folder.listFiles();
        HashMap<String, Integer> uniqueWordIndex = new HashMap<>();
        try {
            int clen = contents.length;
        } catch (NullPointerException e) {
            System.out.println(folder + " has no files");
            e.printStackTrace();
        }
        int uniqueWords = 0;
        String currentWord;
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isFile()) {
                String[] words = pre.process(contents[i].getName());
                for (int j = 0; j < words.length; j++) {
                    currentWord = words[j];
                    // faster than array contains
                    if (!uniqueWordIndex.containsKey(currentWord)) {
                        allUniqueTerms.add(currentWord);
                        uniqueWordIndex.put(currentWord, uniqueWords);
                        uniqueWords++;
                    }
                }
            }
        }
        this.uniqueWordIndex = uniqueWordIndex;
        this.allUniqueTerms = allUniqueTerms;
    }

    /**
     * @return
     */
    public int numPermutations() {
        return numPermutations;
    }


    public void constructPermutations() {
        int numMultiTerms = numTerms();
        for (int i = 0; i < numPermutations; i++) {
            perms[i] = new Permutation(numMultiTerms);
        }
    }

    /**
     * @param filename
     */
    public int getIndex(String filename) {
        return fileIndex.get(filename);
    }

    /**
     *
     * @author Song
     *
     */


}
