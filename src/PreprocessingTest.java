import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class PreprocessingTest {
    @Test
    public void process() throws FileNotFoundException {
        String filename="hello";
        Preprocessing prep = new Preprocessing("./resources/");
        String[] words=prep.process(filename);
        for (String word:words
             ) {
            assertTrue(!word.equals("the"));
            assertTrue(word.length()>2);
        }
    }
}