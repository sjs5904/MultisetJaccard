import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

public class BandSizeCalculator {
    // bisect search, nothing new
    // no derivative needed. I don't even know if Newton's approximation applies.
    static int approximateR(int k, double s){
        int upperR=16;
        int lowerR=0;
        double guessK=approximateK(upperR, s);

        while (guessK<k){
            lowerR=upperR;
            upperR*=2;
            guessK=approximateK(upperR, s);
        }

        // now the returned r is in [lowerR, upperR]
        int midR=lowerR+(upperR-lowerR)/2;
        while (midR!=lowerR) {
            guessK=approximateK(midR, s);
            if (guessK > k){
                upperR=midR;
            }else{
                lowerR=midR;
            }
            midR=lowerR+(upperR-lowerR)/2;
        }

        return midR==0? 1: midR;
    }

    static int bestFactorR(int k, double s){
        int apr=approximateR(k, s);
        int lower=0;
        int higher=k+1;
        for (int i = apr; i > 0; i--) {
            if ((k%i)==0){
                lower=i;
                break;
            }
        }
        for (int i = apr; i < k+1; i++) {
            if ((k%i)==0){
                higher=i;
                break;
            }
        }
        int ret= (apr-lower)>(higher-apr)? higher: lower;
        if (ret==0){
            throw new ValueException("Unable to generate LSH band size. No factor exists for k="+k);
        }
        return ret;
    }

    static int upwardsBestFactorR(int k, double s){
        int apr=approximateR(k, s);
        int lower=0;
        int higher=k+1;
        for (int i = apr; i > 0; i--) {
            if ((k%i)==0){
                lower=i;
                break;
            }
        }
        for (int i = apr; i < k+1; i++) {
            if ((k%i)==0){
                higher=i;
                break;
            }
        }
        int ret= (apr-lower)>(higher-apr)? higher: lower;
        if (ret==0){
            throw new ValueException("Unable to generate LSH band size. No factor exists for k="+k);
        }
        return ret;
    }

    // helper, do not call this function
    private static double approximateK(int r, double s){
        return r/Math.pow(s, r);
    }
}
