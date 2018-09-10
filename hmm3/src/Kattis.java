import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Matrices {

    private double[][] betaMatrix; //array2
    private double[] alpha1;
    private double[][] alphaMatrix; //array1
    private double[][] gammaMatrix;
    private double[][][] diGammaMatrix; //array1

    public void setBeta(double[][] beta){
        this.betaMatrix = beta;
    }

    public void setAlpha1(double[] a1){
        this.alpha1= a1;
    }

    public void setAlpha(double[][] alpha){
        this.alphaMatrix = alpha;
    }

    public void setGamma(double[][] g){
        this.gammaMatrix = g;
    }

    public void setDiGamma(double[][][] dg){
        this.diGammaMatrix = dg;
    }

    public double[][] getBeta(){
        return this.betaMatrix;
    }

    public double[] getAlpha1(){
        return this.alpha1;
    }

    public double[][] getAlpha(){
        return this.alphaMatrix;
    }
    public double[][] getGamma(){
        return this.gammaMatrix;
    }
    public double[][][] getDiGamma(){
        return this.diGammaMatrix;
    }

}


public class Kattis {

    private static int N;
    private static int M;
    private static int T;

    //READ AND PRINT---------------------------------------------------------------------------------------------------
    public ArrayList<String[]> readFile() throws IOException {
        BufferedReader br;
        String sCurrentLine;
        br = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String[]> theInput = new ArrayList<>();

        //You will be given three matrices (in this order);
        //1. transition matrix
        //2. emission matrix
        //3. initial state probability distribution

        while ((sCurrentLine = br.readLine()) != null) {
            theInput.add(sCurrentLine.split(" "));

        }

        return theInput;
    }

    public static void print2D(double matrix[][]){
        int counter = 0;

        try{
            for (double[] row : matrix) {
                if(counter==0){}
                else{
                    System.out.print("\n");
                }
                for(int i =0;i<row.length;i++){
                    System.out.print(row[i]+" ");
                }
                counter++;
            }
        }

        catch(Exception e){
            System.out.println("ERROR w map print");
        }
    }


    public static ArrayList<double[][]> createMatrices(ArrayList<String[]> inputVectors){
        ArrayList<double[][]> theMatrices = new ArrayList<>();

        int countMatrix = 0;
        for(int i = 0;i<inputVectors.size();i++){
            String[] matrixContent = inputVectors.get(i);

            int noOfRows = Integer.valueOf(matrixContent[0]);
            int noOfCols = Integer.valueOf(matrixContent[1]);

            //If last row of input i.e. emission sequence - handle differently
            if(countMatrix==3){
                noOfRows = 1;
            }

            double[][] eachMatrix = new double[noOfRows][noOfCols];
            //Ex.noOfRows 4 - will repeat creating theRow four times

            for(int iter = 0;iter<noOfRows;iter++){
                int start = iter*noOfCols+2;
                int end = iter*noOfCols+noOfCols+2;

                //If last row of input i.e. emission sequence - handle differently
                if(countMatrix==3){
                    //System.out.println("LAST MATRIX - activated");
                    noOfCols = Integer.valueOf(matrixContent[0]);
                    //System.out.println("LAST MATRIX - actiaveted: "+ noOfRows + " " + noOfCols);

                    start = iter*noOfCols+1;
                    end = iter*noOfCols+noOfCols+1;
                }

                double[] theRow = new double[noOfCols];

                int counter=0;

                for(int k=start;k<end;k++){
                    theRow[counter] = Double.parseDouble(matrixContent[k]);
                    counter++;
                }

                //add each row to the matrix
                //System.out.println("NEW ROW");
                eachMatrix[iter] = theRow;
                //System.out.println(eachMatrix.length);
            }
            countMatrix++;
            theMatrices.add(eachMatrix);

        }

        return theMatrices;
    }

    //ALPHA---------------------------------------------------------------------------------------------------------

    public static double[] computeAlpha(double[][] initialStateDistribution, double[][] seqEmissions, double[][] B, double[][] A, double[] c, Matrices theMatrices){
        //first add alpha1 to outputMatrix
        //declare and initialize outputmatrix
        //double[] alpha1 = theMatrices.getAlpha1();

        int currentState = (int)seqEmissions[0][0];

        double[] outputAlpha1 = new double[B.length];

        c[0] = 0;

        for(int i = 0; i< N; i++){
            //System.out.println("stateProb "+initialStateDistribution[0][i]);
            outputAlpha1[i] = initialStateDistribution[0][i] * B[i][currentState];
            c[0] += outputAlpha1[i];
        }

        //scale the alpha1 vector
        c[0] = 1 / c[0];
        for(int i = 0; i< N; i++){
            //System.out.println("stateProb "+initialStateDistribution[0][i]);
            outputAlpha1[i] *= c[0];
        }

        //theMatrices.setAlpha1(outputAlpha1);
        double[] alpha1 = outputAlpha1;
        double[][] alphaMatrix = new double[N][T];

        for(int i = 0; i<N;i++){
            alphaMatrix[i][0] = alpha1[i];
        }

        //then start with the filling of the rest
        for(int t=1;t<T;t++) {
            c[t] = 0;
            //System.out.println("SEQ TIMESTEP: "+t+": "+seqEmissions[0][t]);
            //System.out.println(seqEmissionsMatrix.length + "VS."+A.length);
            for (int i = 0; i < N; i++) {

                //double loopSum = 0;
                alphaMatrix[i][t] = 0;
                //OK
                for (int j = 0; j < N; j++) {
                    alphaMatrix[i][t] += alphaMatrix[j][t-1] * A[j][i];
                }
                //System.out.println("LoOK AT COLUMN: "+Math.round(seqEmissions[0][t]));
                int obsT = (int)Math.round(seqEmissions[0][t]);
                alphaMatrix[i][t] = alphaMatrix[i][t] * B[i][obsT];

                c[t] += alphaMatrix[i][t];
            }

            //scale the alphaMatrix
            c[t] = 1 / c[t];

            for(int i =0;i < N;i++){
                alphaMatrix[i][t] *=c[t];
            }
        }

        theMatrices.setAlpha(alphaMatrix);

        return c;
    }

    //BETA----------------------------------------------------------------------------------------------------------
    /*public static double[] computeBetaT(int colT){
        //creating delta1 array

        double[] outputBeta = new double[colT];

        for(int i = 0; i<colT; i++){
            //System.out.println("stateProb "+initialStateDistribution[0][i]);
            outputBeta[i] = 1;
        }

        return outputBeta;
    }
    */


    public static void computeBetaMatrix(double[][] seqEmissions, double[][] B, double[][] A, double[] c, Matrices theMatrices){
        //first add alpha1 to outputMatrix
        //declare and initialize outputmatrix
        double[][] betaMatrix = new double[N][T];

        //int tMinus1 = betaMatrix[0].length-1;
        //int N = betaMatrix.length;
        //System.out.println("BETAMATRIX LEN dvs. N = 4 = ? = "+N);

        for(int i = 0; i < N;i++){
            betaMatrix[i][T-1] = c[T-1];
        }

        //then start with the filling of the rest
        //int tMinus2 = betaMatrix[0].length-2;

        for(int t=T-2;t>=0;t--) {

            for (int i = 0; i < N; i++) {

                //double loopSum = 0;

                //betaMatrix[i][t] = 0;
                betaMatrix[i][t] = 0;

                for (int j = 0; j < N; j++) {
                    int obsTplus1 = (int)seqEmissions[0][t+1];
                    betaMatrix[i][t] += A[i][j] * B[j][obsTplus1] * betaMatrix[j][t+1];
                }

                betaMatrix[i][t] = c[t]*betaMatrix[i][t];

            }

        }

        theMatrices.setBeta(betaMatrix);
    }


    //DI-GAMMA-------------------------------------------------------------------------------------------------------
    public static void computeDiGamma(double[][] betaMatrix, double[][] seqEmissions, double[][] B, double[][] A, double [][] alphaMatrix, Matrices theMatrices){

        //int N = A.length;
        //int T = alphaMatrix[0].length;

        double[][][] diGamma = new double[N][N][T-1];
        double[][] gamma = new double[diGamma.length][T];

        double denom = 0;

        for(int t=0;t<T-1;t++) {
            //where t = current time step
            denom = 0;
            for(int i=0;i < N;i++){
                for (int j = 0; j < N; j++) {

                    int obsTplus1 = (int)Math.round(seqEmissions[0][t+1]);

                    denom += alphaMatrix[i][t] * A[i][j] * B[j][obsTplus1] * betaMatrix[j][t+1];
                }
            }

            for(int i=0;i < N;i++){
                gamma[i][t] = 0;

                for(int j=0;j < N;j++){
                    //note that A[i][j] which means prev state (OBS PREVIOUS = j since i is FUTURE in this case!!) is kept constant
                    int obsTplus1 = (int)Math.round(seqEmissions[0][t+1]);
                    diGamma[i][j][t] = (alphaMatrix[i][t] * A[i][j] * B[j][obsTplus1] * betaMatrix[j][t+1])/denom;
                    gamma[i][t] += diGamma[i][j][t];
                }
            }
        }
        //special case for gamma_(T-1)
        denom = 0;

        for(int i=0;i < N;i++){
            denom+=alphaMatrix[i][T-1];
        }

        for(int i=0;i < N;i++){
            gamma[i][T-1] = alphaMatrix[i][T-1] / denom;
        }

        theMatrices.setGamma(gamma);
        theMatrices.setDiGamma(diGamma);

    }


    //LAMBDA ESTIMATION - parameters -----------------------------------------------------------------------------
    public static void lambdaOut(double[][] A, double[][] B){
        //print2D(A);
        //System.out.println("\n");
        //print2D(B);
        //System.out.println("\n ---------");

        String output = "";
        output += N+" "+N;

        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                output+=" "+round(A[i][j],6);
            }
        }
        output+="\n";
        output += N+" "+M;

        for(int i=0;i<N;i++){
            for(int j=0;j<M;j++){
                output+=" "+round(B[i][j],6);
            }
        }
        System.out.println(output);
        System.exit(0);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void main(String[] args){
        Matrices matricesInstance = new Matrices();

        Kattis theMain = new Kattis();

        try{
            ArrayList<String[]> inputV = theMain.readFile();

            //the ArrayList which will contain our matrices
            ArrayList<double[][]> theMatrices = new ArrayList<>();
            theMatrices = createMatrices(inputV);

            double[][] iTransitionMatrix = theMatrices.get(0);
            double[][] iEmissionMatrix = theMatrices.get(1);
            double[][] iInitialstateMatrix = theMatrices.get(2);

            //observed sequence of states
            double[][] sequenceEmissions = theMatrices.get(3);

            //initialize scaling vector
            double[] c = new double[sequenceEmissions[0].length];

            int iters = 0;
            int maxIters = 100;
            double oldLogProb =  -999999999;

            double [][] alphaMatrix;

            //double[] betaT = computeBetaT(iTransitionMatrix.length);
            double[][] betaMatrix;
            double[][][] diGammaMatrix;
            double[][] gammaMatrix;

            N = iInitialstateMatrix[0].length;
            M = iEmissionMatrix[0].length;
            T = sequenceEmissions[0].length;

            while(iters<maxIters){

                //ALPHA---------------------------------------------------------------------------------------------------
                c = computeAlpha(iInitialstateMatrix, sequenceEmissions, iEmissionMatrix, iTransitionMatrix, c, matricesInstance);

                //alphaMatrix = matricesInstance.getAlpha();

                //BETA----------------------------------------------------------------------------------------------------
                computeBetaMatrix(sequenceEmissions, iEmissionMatrix, iTransitionMatrix, c, matricesInstance);

                betaMatrix = matricesInstance.getBeta();
                alphaMatrix = matricesInstance.getAlpha();

                //diGamma------------------------------------------------------------------------------------------------
                computeDiGamma(betaMatrix, sequenceEmissions, iEmissionMatrix, iTransitionMatrix, alphaMatrix, matricesInstance);
                gammaMatrix = matricesInstance.getGamma();
                diGammaMatrix = matricesInstance.getDiGamma();


                //----------------------------------------------------------------------------------------------------
                // RE-ESTIMATIONS
                //----------------------------------------------------------------------------------------------------

                //int N = diGammaMatrix.length;
                //int M = iEmissionMatrix[0].length;
                //int T = gammaMatrix[0].length;

                //re-estimate pi (iInitialstateMatrix)---------------------------------
                for(int i=0; i<gammaMatrix.length;i++){
                    iInitialstateMatrix[0][i] = gammaMatrix[i][0];
                }

                //re-estimate A------------------------------------------------------------
                //transition matrix
                for(int i=0;i < N;i++){

                    for(int j=0;j < N;j++){
                        double numer = 0;
                        double denom = 0;

                        for(int t=0;t<T-1;t++){
                            //System.out.println("TLoop"+t + ". "+i+ "."+j);
                            numer += diGammaMatrix[i][j][t];
                            denom += gammaMatrix[i][t];
                        }

                        iTransitionMatrix[i][j] = numer/denom;
                    }
                }

                //re-estimate B------------------------------------------------------------
                //emission matrix
                for(int i=0;i < N;i++){

                    for(int j=0;j < M;j++){
                        double numer = 0;
                        double denom = 0;

                        for(int t=0;t < T;t++){

                            int obsT = (int)sequenceEmissions[0][t];
                            if(obsT == j){
                                numer += gammaMatrix[i][t];
                            }

                            denom += gammaMatrix[i][t];
                        }

                        iEmissionMatrix[i][j] = numer/denom;
                    }
                }

                //compute log()------------------------------------------------------------
                double logProb = 0;

                for(int i=0;i<T;i++){
                    logProb+= Math.log(c[i]);
                }

                logProb =-logProb;

                iters++;

                //if(iters<maxIters){
                if(iters<maxIters && logProb > oldLogProb){
                        oldLogProb = logProb;
                }
                else{
                    lambdaOut(iTransitionMatrix, iEmissionMatrix);
                }

            }

        }
        catch(IOException e){
            System.out.println("Error occurred when reading file");
        }
    }


}
