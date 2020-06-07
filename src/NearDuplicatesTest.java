import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NearDuplicatesTest {



    @Test
    void nearDuplicateDetectorConfusionMatrix() {
        // you must use the F17PA2 dataset.
        // I have assumed that the conditional positive has 8 for each text.
        // Or else you must do exact jaccard for all, which is certainly doable.
        boolean printAll=true;
        String folderPath="./resources/F17PA2";
        File folder=new File(folderPath);
        NearDuplicates nd = new NearDuplicates(folderPath, 1024, 0.9);
        System.out.println("Near Duplicate Constructed");
        File[] allFiles=folder.listFiles();
        ArrayList<File> originalFiles = new ArrayList<>();
        for (File file:allFiles
             ) {
            if (!file.getName().contains("copy")){
                originalFiles.add(file);
            }
        }
        double truePositive;
        double falsePositive;
        double cumulativePrecision=0;
        double cumulativeRecall=0;
        double trueSim=0;
        int positiveCnt=0;
        ArrayList<String> positives;
        for (File originFile: originalFiles){
            if(printAll){
                System.out.println("Original file: "+originFile.getName());
            }
            truePositive=0;
            falsePositive=0;
            positives=nd.nearDuplicateDetector(originFile.getName());
            if(printAll){
                System.out.println("Positive File:");
            }
            for (String fname:positives
                 ) {
                if(printAll){
                    System.out.print(fname+", ");
                }
                positiveCnt++;
                if (fname.contains(originFile.getName())){
                    truePositive++;
                }else{
                    falsePositive++;
                }
                trueSim+=nd.minSim.exactJaccard(originFile.getName(), fname);
            }
            System.out.println();
            if (truePositive+falsePositive!=0){
                cumulativePrecision+= truePositive/(truePositive+falsePositive);
                cumulativeRecall+= truePositive/8;
            }
        }
        System.out.println("Average precision:"+cumulativePrecision/originalFiles.size());
        System.out.println("Average recall:"+cumulativeRecall/originalFiles.size());
        System.out.println("Average true Jaccard:"+trueSim/positiveCnt);

    }

    @Test
    void nearDuplicateDetector() {
        // set your param here
        double s = 0.9;
        String wantedFileName="baseball6.txt";


        String folderPath="./resources/F17PA2";
        File folder=new File(folderPath);
        NearDuplicates nd = new NearDuplicates(folderPath, 1024, s);
        System.out.println("Near Duplicate Constructed");
        File[] allFiles=folder.listFiles();
        ArrayList<File> originalFiles = new ArrayList<>();
        for (File file:allFiles
        ) {
            if (!file.getName().contains("copy")){
                originalFiles.add(file);
            }
        }
        double truePositive;
        double falsePositive;
        double cumulativePrecision=0;
        double cumulativeRecall=0;
        double trueSim=0;
        int positiveCnt=0;
        ArrayList<String> positives;
        truePositive=0;
        falsePositive=0;
        positives=nd.nearDuplicateDetector(wantedFileName);
        for (String fname:positives
        ) {
            System.out.println(fname);
            positiveCnt++;
            if (fname.contains(wantedFileName)){
                truePositive++;
            }else{
                falsePositive++;
            }
            trueSim+=nd.minSim.exactJaccard(wantedFileName, fname);
        }
        if (truePositive+falsePositive!=0){
            cumulativePrecision+= truePositive/(truePositive+falsePositive);
            cumulativeRecall+= truePositive/8;
        }
        System.out.println("Average precision:"+cumulativePrecision/originalFiles.size());
        System.out.println("Average recall:"+cumulativeRecall/originalFiles.size());
        System.out.println("Average true Jaccard:"+trueSim/positiveCnt);
    }
}