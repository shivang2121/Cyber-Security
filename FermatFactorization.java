import java.math.BigInteger;
import java.util.*;

public class FermatFactorization {

    public static int Bval;
    public static List<Integer> primes1=new ArrayList<>() ;
    public static boolean isSquare(BigInteger num) {
        if (num.compareTo(BigInteger.ZERO) < 0) {
            return false;
        }
        BigInteger sqrtNum = sqrt(num);
        return sqrtNum.multiply(sqrtNum).equals(num);
    }


    public static BigInteger gen_random(int num_digits) {
        Random random = new Random();
        BigInteger lower_bound = BigInteger.TEN.pow(num_digits - 1);
        BigInteger upper_bound = BigInteger.TEN.pow(num_digits).subtract(BigInteger.ONE);
        BigInteger range_length = upper_bound.subtract(lower_bound).add(BigInteger.ONE);
        BigInteger result = new BigInteger(range_length.bitLength(), random);
        if (result.compareTo(lower_bound) < 0)
            result = result.add(lower_bound);
        if (result.compareTo(upper_bound) >= 0)
            result = result.mod(range_length).add(lower_bound);
        return result;
    }

    public static BigInteger genN(int numDigits) {
        BigInteger p = gen_random(numDigits).nextProbablePrime();
        BigInteger q = gen_random(numDigits).nextProbablePrime();

        BigInteger N = p.multiply(q);
        System.out.println("N: " + N);
        System.out.println("p: " + p);
        System.out.println("q: " + q);

        return N;
    }
    public static List<Bsmooth> bsmooth=new ArrayList<>();
    public static List<List<Integer>> counts = new ArrayList<>();

    static List<BigInteger> entriesToBeSquared = new ArrayList<>();
    static List<BigInteger> squareMods = new ArrayList<>();

    public static BitSet fermat(BigInteger N) {
        primesTillB();

        boolean basicFlag = false;
        int i = 1;
        while (!basicFlag) {
            i++;
            BigInteger c = sqrt(N).add(BigInteger.valueOf(i));
            BigInteger c1 = c;
            c = c.pow(2).mod(N);

            BitSet vc=smoothOrNot(c);
            if(vc!=null){
                bsmooth.add(new Bsmooth(c1,c,vc));

                if(bsmooth.size()>=Bval)
                {
                    break;
                }
            }
        }
        return null;
    }

    private static void primesTillB() {
        BigInteger firstPrime=BigInteger.valueOf(2);
        for(int i=0;i<Bval;i++)
        { primes1.add(firstPrime.intValue());
            firstPrime= firstPrime.nextProbablePrime();


        }
    }

    static class Bsmooth {
        BigInteger x, y;
        BitSet vc;

        public Bsmooth(BigInteger x, BigInteger y, BitSet vc) {
            this.x = x;
            this.y = y;
            this.vc=vc;
        }


    }


        public static BitSet smoothOrNot(BigInteger a) {
            List<Integer> counts2=new ArrayList<Integer>();

            BitSet vector = new BitSet(primes1.size());

            for (int i = 0; i < primes1.size(); i++) {
                BigInteger currPrime = BigInteger.valueOf(primes1.get(i));
                int ct = 0;
                BigInteger pBig = currPrime;
                if (!a.mod(pBig).equals(BigInteger.ZERO)) {
                    counts2.add(ct); // Add whatever default value you want here

                }
                    if (a.mod(pBig).equals(BigInteger.ZERO)) {
                    while (a.mod(pBig).equals(BigInteger.ZERO)) {
                        a = a.divide(pBig);
                        ct++;

                    }
                    //System.out.println(ct);
                    counts2.add(ct); // Add whatever default value you want here
                    //System.out.println(ct);


                    if (ct % 2 == 1) {

                        vector.set(i);


                    }
                    if (a.equals(BigInteger.ONE)) {

                        System.out.println("Creating the 4X4 Matrix");
                        //System.out.println(counts2);
                        if (counts2.size() < primes1.size()) {
                            int remainingZeros = primes1.size() - counts2.size();
                            for (int j = 0; j < remainingZeros; j++) {
                                counts2.add(0);
                            }
                        }

                        counts.add(counts2);

                        return vector;

                    }
                }

            }
            return null;
        }


    public static BigInteger sqrt(BigInteger x) {
        BigInteger y = x;
        while (y.compareTo(x.divide(y)) > 0) {
            y = x.divide(y).add(y).divide(BigInteger.valueOf(2));
        }
        return y;
    }
    public static void printBsmooth()
    {
        for (Bsmooth bs : bsmooth) {
            System.out.print("x: " + bs.x + ", y: " + bs.y + ", vc: [");
            for (int j = 0; j < primes1.size(); j++) {
                if (bs.vc.get(j)) {
                    System.out.print("1");
                } else {
                    System.out.print("0");
                }
                if (j != primes1.size() - 1) {
                    System.out.print(", ");
                }

            }
            //System.out.println();
            System.out.println("]");

        }
        System.out.println(primes1);
    }

    public static void gaussian()
    {
        for (Bsmooth bs : bsmooth) {
            System.out.print("x: " + bs.x + ", y: " + bs.y + ", vc: [");
            for (int j = 0; j < primes1.size(); j++) {
                if (bs.vc.get(j)) {
                    System.out.print("1");
                } else {
                    System.out.print("0");
                }
                if (j != primes1.size() - 1) {
                    System.out.print(", ");
                }

            }
            //System.out.println();
            System.out.println("]");

        }
        System.out.println(primes1);
    }
    public static int[][] printBsmooth2() {

        int n = primes1.size();
        int[][] matrix = new int[bsmooth.size()][n];
        for (int i = 0; i < bsmooth.size(); i++) {
            Bsmooth bs = bsmooth.get(i);
            BitSet vc = bs.vc;
            entriesToBeSquared.add(bs.x); // Add whatever default value you want here
            squareMods.add(bs.y);
            for (int j = 0; j < n; j++) {
                matrix[i][j] = vc.get(j) ? 1 : 0;
            }
        }

//        for (int i = 0; i < matrix.length; i++) {
//            System.out.println(Arrays.toString(matrix[i]));
//        }

    return matrix;
    }


    public static void main(String[] args) {
        int numDigits =3;

        //BigInteger N = genN(numDigits);
        BigInteger N = new BigInteger("129709");


        Bval=9;

        BitSet p = fermat(N);


        //printBsmooth();

        int mat[][]=printBsmooth2();
        for (int i = 0; i < mat.length; i++) {
            System.out.println(Arrays.toString(mat[i]));
        }        //printBsmooth(bsmooth);
        System.out.println(entriesToBeSquared);
        System.out.println(squareMods);
        System.out.println(primes1);
        System.out.println(counts);
        int idenrows[]=solver(mat);
        //System.out.println(Arrays.toString(idenCols));
        BigInteger pp=finalSolver(idenrows,N);
        System.out.println("N: "+N);
        System.out.println("P: "+pp);
        BigInteger qq=N.divide(pp);
        System.out.println("Q: "+qq);
        //System.out.println(p);
//        BigInteger q = N.divide(p);
//        System.out.println("N: " + N);
//        System.out.println("p: " + p);
//        System.out.println("q: " + q);
    }

    public static BigInteger finalSolver(int[] idenrows, BigInteger N) {
        List<Integer> power = new ArrayList<>(Collections.nCopies(Bval, 0));
        List<Integer> squares2be=new ArrayList<Integer>();

        for (int i = 0; i < idenrows.length; i++) {
            if (idenrows[i] != 0) {
                //System.out.println(idenrows[i]);
                List<Integer> v1=((counts.get(i)));
                System.out.println(counts.get(i));

                //System.out.println((counts.get(i)));
                //System.out.println(primes1.get(i));
                System.out.println(entriesToBeSquared.get(i));

                for (int j = 0; j < v1.size(); j++) {
                    power.set(j, power.get(j) + v1.get(j));

                    //power= Collections.singletonList((power.get(j) + v1.get(j)));
                }


                //power.addAll(v1);
            }


        }
        System.out.println("Final Step");

        System.out.println(power);
        System.out.println( entriesToBeSquared);
        System.out.println( primes1);
        List<Integer> result = new ArrayList<>();

        power.replaceAll(integer -> {
            return integer / 2; // dividing each element by 2
        });
        System.out.println(power);

        for (int i = 0; i < primes1.size(); i++) {
            int p = primes1.get(i);
            int exp = power.get(i);
            result.add((int) Math.pow(p, exp));
        }

        System.out.println(result);
        //int x_final=1;
        BigInteger y_final=BigInteger.ONE;
        BigInteger x_final = BigInteger.ONE;
        //BigInteger y_final = new BigInteger("1");
        for (int number : result) {
            x_final = x_final.multiply(BigInteger.valueOf(number));
        }
        BigInteger x_final2 = x_final;

        for (int i = 0; i < idenrows.length; i++) {
            if (idenrows[i] == 1) {
                y_final = y_final.multiply(entriesToBeSquared.get(i));
            }
        }


        x_final = x_final.mod(N);
        //x_final = x_final.pow(2);

        y_final=y_final.mod(N);
        //y_final = y_final.pow(2);



        System.out.println(x_final);
        System.out.println(y_final);
        BigInteger difference = x_final.add(y_final).abs();
        System.out.println(difference);
        BigInteger gcd2 = difference.gcd(N);
        return gcd2;


    }


    public static int[] solver(int [][] matrix ) {



        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] identityMatrix = generateIdentityMatrix(rows);

//        for (int row = 0; row < rows; row++) {
//            for (int i = row + 1; i < rows; i++) {
//                if (matrix[i][row] == 1) {
//                    for (int j = 0; j < cols; j++) {
//                        matrix[i][j] ^= matrix[row][j];
//                        identityMatrix[i][j] ^= identityMatrix[row][j];
//                    }
//                }
//            }
//        }
//        for (int row = 0; row < rows; row++) {
//            for (int i = row + 1; i < rows; i++) {
//                if (matrix[i][row] == 1) {
//                    for (int j = 0; j < cols; j++) {
//                        matrix[i][j] = (matrix[i][j] + matrix[row][j]) % 2;
//                        identityMatrix[i][j] = (identityMatrix[i][j] + identityMatrix[row][j]) % 2;
//                    }
//                }
//            }
//        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 1) {
                    for (int k = 0; k < rows; k++) {
                        if (k != i && matrix[k][j] == 1) {
                            for (int l = 0; l < cols; l++) {
                                matrix[k][l] = (matrix[k][l] + matrix[i][l]) % 2;
                                identityMatrix[k][l] = (identityMatrix[k][l] + identityMatrix[i][l]) % 2;
                            }
                        }
                    }
                    break;
                }
            }
        }







        System.out.println("Reduced Matrix:");
        for (int i = 0; i < rows; i++) {
            System.out.println(Arrays.toString(matrix[i]));

        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.println(Arrays.toString(identityMatrix[i]));

        }

        for (int i = 0; i < rows; i++) {
            boolean allZeros = true;
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    allZeros = false;
                    break;
                }
            }
            if (allZeros) {
                System.out.println("Row in identity matrix for all zeros in the matrix: " + Arrays.toString(identityMatrix[i]));
                return identityMatrix[i];
            }
        }

        return new int[0];
    }
    public static int[][] generateIdentityMatrix(int n) {
        int[][] identityMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            identityMatrix[i][i] = 1;
        }
        return identityMatrix;
    }

}
