import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PermutationTest {

    @Test
    void to() {
        Permutation perm;
        Random rand = new Random();
        int size;
        Set<Integer> imageSet;
        int toNum;
        for (int i = 1; i < 100; i++) {
            size=rand.nextInt(i);
            perm=new Permutation(size);
            imageSet=new HashSet<>();
            for (int j = 0; j < size; j++) {
                toNum=perm.to(j);
                assertTrue(toNum<size);
                assertTrue(toNum>-1);
                imageSet.add(toNum);
            }
            assertTrue(imageSet.size()==size);
        }
    }
}