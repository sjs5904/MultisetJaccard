import java.io.IOException;

public class MinHashTime {

    public static void main(String[] args) {
        try {
            MinHashTime.timer("./resources/space",600);
        } catch (IOException e) {
            System.out.println("Data file IO exception, ./resources/space ");
            e.printStackTrace();
        }
    }

    public static void timer(String folder, int numPermutations) throws IOException {
        // construction timer
        long start = System.currentTimeMillis();
        MinHashSimilarities mhs= new MinHashSimilarities(folder, numPermutations);

        long end = System.currentTimeMillis();
        double sec = (end - start) / 1000.0;
        System.out.println("Time taken to construct an instance of MinHashSimilarities: "+sec + " seconds");

        // exact timer
        start = System.currentTimeMillis();
        String[] allDocs = mhs.minHash.allDocs();
        String f1;
        String f2;
        double exJac;
        for (int i = 0; i <allDocs.length ; i++) {
            for (int j = i; j < allDocs.length; j++) {
                f1=allDocs[i];
                f2=allDocs[j];
                exJac=mhs.exactJaccard(f1, f2);
            }
        }
        end = System.currentTimeMillis();
        sec = (end - start) / 1000.0;

        System.out.println("Time taken to compute exact Jaccard similarity: "+sec + " seconds");

        // approx timer
        start = System.currentTimeMillis();

        int differCount=0;
        double appJac;
        for (int i = 0; i <allDocs.length ; i++) {
            for (int j = i; j < allDocs.length; j++) {
                f1=allDocs[i];
                f2=allDocs[j];
                appJac=mhs.approximateJaccard(f1, f2);
            }
        }
        end = System.currentTimeMillis();
        sec = (end - start) / 1000.0;
        System.out.println("Time taken to approximate Jaccard similarity: "+sec + " seconds");

    }
}
