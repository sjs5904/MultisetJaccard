// https://introcs.cs.princeton.edu/java/14array/Permutation.java.html
// https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle

public class Permutation {
    private int[] mapping;

    public Permutation(int size) {
        int[] mapping = new int[size];

        // insert integers 0..size-1
        for (int i = 0; i < size; i++)
            mapping[i] = i;

        // shuffle
        for (int i = 0; i < size; i++) {
            int r = (int) (Math.random() * (i+1));     // int between 0 and i
            int swap = mapping[r];
            mapping[r] = mapping[i];
            mapping[i] = swap;
        }
        this.mapping=mapping;
    }

    public int to(int from){
        return mapping[from];
    }
}

