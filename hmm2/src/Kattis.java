
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.math.BigDecimal;



class Matrices {

    private double[][] viterbi; //array2
    private double[][] backptr; //array1

    public void set(double[][] v, double[][] b){
        this.viterbi = v;
        this.backptr = b;
    }
    public double[][] getViterbi(){
        return this.viterbi;
    }
    public double[][] getBackptr(){
        return this.backptr;
    }
    //Setters + getters. Etc.
}


public class Kattis {

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
                //System.out.println("SET NO OF ROWS TO: 1");
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


    public static double[] computeDelta(double[][] initialStateDistribution, double[][] emissionMatrix, double[][] seqEmissions){

        int currentState = (int)seqEmissions[0][0];

        double[] outputDelta = new double[emissionMatrix.length];

        for(int i = 0; i<initialStateDistribution[0].length; i++){
            //System.out.println("stateProb "+initialStateDistribution[0][i]);
            outputDelta[i] = initialStateDistribution[0][i] * emissionMatrix[i][currentState];
        }

        return outputDelta;
    }

    public static void computeOutput(double[] delta1, double[][] seqEmissions, double[][] B, double[][] A, Matrices theMatrices){
        //first add alpha1 to outputMatrix
        //declare and initialize outputmatrix
        double[][] viterbiMatrix = new double[delta1.length][seqEmissions[0].length];
        double[][] backptrMatrix = new double[delta1.length][seqEmissions[0].length];

        //add delta1 to our viterbiMatrix

        for(int j = 0; j<viterbiMatrix.length;j++){
            viterbiMatrix[j][0] = delta1[j];
        }

        //then start with the filling of the rest
        for(int t=1;t<viterbiMatrix[0].length;t++) {

            double mostProbPrevState = 0;
            for (int j = 0; j < viterbiMatrix.length; j++) {
                double maxProb = 0;
                double theProb = 0;
                for (int sumI = 0; sumI < viterbiMatrix.length; sumI++) {

                    theProb = viterbiMatrix[sumI][t - 1] * A[sumI][j] * B[j][(int)Math.round(seqEmissions[0][t])];

                    if(theProb > maxProb){
                        maxProb = theProb;
                        mostProbPrevState = sumI;
                    }
                    else{
                    }

                }

                backptrMatrix[j][t] = mostProbPrevState;
                viterbiMatrix[j][t] = maxProb;
            }

        }

        theMatrices.set(viterbiMatrix, backptrMatrix);

    }

    public static ArrayList<Integer> computeMostLikelySequence(double[][] viterbi, double[][] backptr){
        ArrayList<Integer> mostLikelySeq = new ArrayList<>();
        int nextState = 0;

        double maxProbOfCol = 0;
        int maxProbOfColROW = 0;
        int maxProbOfColCOL = 0;

        for(int row=0;row<viterbi.length;row++){
            if(viterbi[row][viterbi[0].length-1]>maxProbOfCol){
                maxProbOfCol = viterbi[row][viterbi[0].length-1];
                maxProbOfColROW = row;
                maxProbOfColCOL = viterbi[0].length-1;
            }
            else{
                //System.out.println("NO UPDATE");
            }

        }

        for(int col = viterbi[0].length-1;col>=0;col--){

            if(col == viterbi[0].length-1){
                mostLikelySeq.add(maxProbOfColROW);
                nextState = (int)backptr[maxProbOfColROW][maxProbOfColCOL];

            }

            else {
                if(col==0){
                    mostLikelySeq.add(nextState);
                }

                else{
                    mostLikelySeq.add(nextState);
                    nextState = (int)backptr[nextState][col];
                }
            }


        }

        return mostLikelySeq;
    }

    public static void main(String[] args){
        Matrices matricesInstance = new Matrices();

        Kattis theMain = new Kattis();
        try{
            ArrayList<String[]> inputV = theMain.readFile();

            ArrayList<double[][]> theMatrices = new ArrayList<>();
            theMatrices = createMatrices(inputV);

            //You will be given three matrices (in this order) + sequence of emissions;
                //1. transition matrix (A)
                //2. emission matrix (B)
                //3. initial state probability distribution (pi)
                //4. Then a sequence of emissions

            double[][] transitionMatrix = theMatrices.get(0);
            double[][] emissionMatrix = theMatrices.get(1);
            double[][] initialstateMatrix = theMatrices.get(2);
            double[][] sequenceEmissions = theMatrices.get(3);

            //compute DELTA(prev alpha1) vector w. help of pi & A
            double[] delta1 = computeDelta(initialstateMatrix, emissionMatrix, sequenceEmissions);

            //System.out.println("DELTA: "+delta1);

            computeOutput(delta1, sequenceEmissions, emissionMatrix, transitionMatrix, matricesInstance);

            double[][] viterbiMatrix = matricesInstance.getViterbi();
            double[][] backptrMatrix = matricesInstance.getBackptr();


            ArrayList<Integer> outputArray= computeMostLikelySequence(viterbiMatrix, backptrMatrix);
            String outputString = "";
            for(int i = outputArray.size()-1;i>=0;i--){
                outputString += " "+outputArray.get(i);
            }

            System.out.println(outputString.trim());

        }
        catch(IOException e){
            System.out.println("Error occurred when reading file");
        }
    }


}
