import java.util.concurrent.ThreadLocalRandom;

class HashFunctionRan extends HashFunction {
    int prime;
    int a;
    int b;

    /***
     *
     * @param lowerBoundSize the lower bound of the hash size.
     *                       say lowerBoundSize=10, the next prime is 11, so the hash will have an image of 11
     */
    public HashFunctionRan(int lowerBoundSize) {
        this.prime = getPrime(lowerBoundSize);
        this.a=ThreadLocalRandom.current().nextInt(0, prime);
        this.b=ThreadLocalRandom.current().nextInt(0, prime);
    }

    public int hash(String s) {
        return hash(s.hashCode());
    }

    private int hash(int x) {
        return mod((a * x) + b, prime);
    }

    /**
     * get prime number bigger than n
     *
     * @param n
     * @return boolean
     */
    private int getPrime(int n) {
        boolean found = false;

        while (!found) {
            if (isPrime(n)) {
                found = true;
            } else {
                if (n == 1 || n % 2 == 0) {
                    n = n + 1;
                } else {
                    n = n + 2;
                }
            }
        }
        return n;
    }

    /**
     * return true if inputNum is prime
     *
     * @param inputNum
     * @return boolean
     */
    private boolean isPrime(int inputNum) {
        if (inputNum <= 3 || inputNum % 2 == 0)
            return inputNum == 2 || inputNum == 3;
        int divisor = 3;
        while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0))
            divisor += 2;
        return inputNum % divisor != 0;
    }
}