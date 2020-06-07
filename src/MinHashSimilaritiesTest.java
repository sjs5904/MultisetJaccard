import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHashSimilaritiesTest {

    String folder ="./resources/nearduplicatetest";
    int numPermutations= 400;
    MinHashSimilarities mhs= new MinHashSimilarities(folder, numPermutations);

    @Test
    void jaccardErrorBound() {

        String folderPath="./resources/nearduplicatetest";
        File folder=new File(folderPath);
        System.out.println("Near Duplicate Constructed");
        File[] allFiles=folder.listFiles();
        ArrayList<File> originalFiles = new ArrayList<>();
        for (File file:allFiles
        ) {
            if (!file.getName().contains("copy")){
                originalFiles.add(file);
            }
        }

        int numTests=100;

        Random rand=new Random();
        double[] diffs=new double[numTests];
        for (int i = 0; i < numTests; i++) {
            String originalPath= originalFiles.get(rand.nextInt(originalFiles.size())).getName();
            String copyPath= originalPath + ".copy" + (1 + rand.nextInt(7));
            double exactJ=mhs.exactJaccard(originalPath, copyPath);
            double approJ=mhs.approximateJaccard(originalPath, copyPath);
            double diff=exactJ-approJ;
            diffs[i]=diff;
        }
        double meanerror=+mean(diffs);
        double varerror=var(diffs);
        System.out.println("Jaccard mean error: " +meanerror);
        System.out.println("Jaccard var error: "+ varerror);
        assertTrue(-0.1<meanerror && meanerror<0.1);
        assertTrue(varerror<0.1);
    }

    @Test
    void smallTest(){
        double exactJ=mhs.exactJaccard("baseball0.txt","baseball0.txt.copy1");
        System.out.println(exactJ);
        double approJ=mhs.approximateJaccard("baseball0.txt","baseball0.txt.copy1");
        System.out.println(approJ);
    }

    private double mean(double[] vals){
        double sum =0;
        for (double val:vals
        ) {
            sum+=val;
        }
        return sum/vals.length;
    }

    private double var(double[] vals){
        double mean=mean(vals);
        double l2=0;
        for (double val: vals
        ) {
            l2+=(val-mean)*(val-mean);
        }
        return l2/vals.length;
    }
}