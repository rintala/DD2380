
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


public class Main {

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


    public static double[] computeDelta(double[][] initialStateDistribution, double[][] emissionMatrix, double[][] seqEmissions){
        //creating delta1 array
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

        //then start with the filling of the rest of the viterbiMatrix using delta1, A & B
        //starting at t=1, i.e. after delta1 column
        for(int t=1;t<viterbiMatrix[0].length;t++) {
            System.out.println("SEQ TIMESTEP: "+t+": "+seqEmissions[0][t]);
            double mostProbPrevState = 0;

            //for each time step, go through each row of the viterbi matrix
            for (int j = 0; j < viterbiMatrix.length; j++) {
                double maxProb = 0;
                double theProb = 0;

                //for each pos in the Viterbi - calculate the viterbi by:  v[t-1]*A*B
                for (int sumI = 0; sumI < viterbiMatrix.length; sumI++) {
                    theProb = viterbiMatrix[sumI][t - 1] * A[sumI][j] * B[j][(int)Math.round(seqEmissions[0][t])];
                     System.out.println("PREVIOUS PROBABILITY: "+theProb);

                     //update maxProbability if the probability is larger than current max
                     if(theProb > maxProb){
                         maxProb = theProb;
                         mostProbPrevState = sumI;
                     }
                     else{}

                }

                //add the most probable previous state to the current matrix coord (make a record of movement)
                backptrMatrix[j][t] = mostProbPrevState;

                System.out.println("--toBackptr: "+ mostProbPrevState);
                System.out.println(" THE MOST PROB PREV STATE: "+mostProbPrevState);

                //add the maxProbability to the viterbi
                viterbiMatrix[j][t] = maxProb;
            }

        }

        theMatrices.set(viterbiMatrix, backptrMatrix);
    }

    // rounds double to specified decimal (unlike Math.round)
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static ArrayList<Integer> computeMostLikelySequence(double[][] viterbi, double[][] backptr){
        ArrayList<Integer> mostLikelySeq = new ArrayList<>();
        int nextState = 0;
        for(int col = viterbi[0].length-1;col>=0;col--){
            System.out.println("THE COLUMMN IS: "+col);
            for(int i=0;i<viterbi.length;i++){
                System.out.println(i+","+viterbi[i][col]);
            }

            //record the maximal probable state, depending on previous states
            double maxProbOfCol = 0;
            int maxProbOfColROW = 0;
            int maxProbOfColCOL = 0;
            System.out.println("VITERBI ROWS: "+viterbi.length+ "COLS: "+viterbi[0].length);


            for(int row=0;row<viterbi.length;row++){

                if(viterbi[row][col]>maxProbOfCol){
                  maxProbOfCol = viterbi[row][col];
                  maxProbOfColROW = row;
                  maxProbOfColCOL = col;

                  System.out.println("updates maxprob of the column: "+col);
                  System.out.println("max state is now: "+maxProbOfColROW);
               }
               else{
                   System.out.println("NO UPDATE");
               }

            }

            //last column in viterbi (first move) - look at backpointer and record it
            if(col == viterbi[0].length-1){
                System.out.println("the first coord: "+maxProbOfColCOL+ ","+ maxProbOfColROW);
                mostLikelySeq.add(maxProbOfColROW);
                //mostLikelySeq.add((int)backptr[maxProbOfColROW][maxProbOfColCOL]);
                nextState = (int)backptr[maxProbOfColROW][maxProbOfColCOL];
                System.out.println("NEXT STATE !!!! !! ! : "+nextState);
            }

            else {
                if(col==0){
                    //first column (last move)
                    mostLikelySeq.add(nextState);
                }

                else{
                    //follow backpointer and record move
                    System.out.println("NEXT STATE IS ----------------------------- "+nextState);
                    System.out.println("LOOKING AT BACKPTR OF: "+maxProbOfColCOL+ ","+maxProbOfColROW + "| bkacptr is: "+backptr[maxProbOfColROW][maxProbOfColCOL]);
                    mostLikelySeq.add(nextState);
                    nextState = (int)backptr[nextState][col];
                }
            }


        }

        return mostLikelySeq;
    }

    public static void main(String[] args){
        Matrices matricesInstance = new Matrices();

        Main theMain = new Main();
        try{
            ArrayList<String[]> inputV = theMain.readFile();

            //the ArrayList which will contain our matrices
            ArrayList<double[][]> theMatrices = new ArrayList<>();
            theMatrices = createMatrices(inputV);

            for(int i=0;i<theMatrices.size();i++){
                System.out.println("\n-----------");
                print2D(theMatrices.get(i));
            }
            System.out.println("\n-----------");

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

            computeOutput(delta1, sequenceEmissions, emissionMatrix, transitionMatrix, matricesInstance);

            double[][] viterbiMatrix = matricesInstance.getViterbi();
            double[][] backptrMatrix = matricesInstance.getBackptr();

            ArrayList<Integer> outputArray= computeMostLikelySequence(viterbiMatrix, backptrMatrix);
            String outputString = "";
            for(int i = outputArray.size()-1;i>=0;i--){
                outputString += " "+outputArray.get(i);
            }

            //RETURN OUTPUT ON SYSTEM OUT
            System.out.println("\n----OUTPUT-----");
            System.out.println(outputString.trim());
            System.out.println("----------------");

            System.out.println("\n-------- VITERBI --------");
            print2D(viterbiMatrix);
            System.out.println("\n-------- BACKPTR --------");
            print2D(backptrMatrix);

            /*
            System.out.println("----------");
            System.out.println("transition matrix: "+transitionMatrix.length+ ", "+transitionMatrix[0].length);
            System.out.println("emission matrix: "+emissionMatrix.length+ ", "+emissionMatrix[0].length);
            System.out.println("intitial state matrix: "+initialstateMatrix.length+ ", "+initialstateMatrix[0].length);
            System.out.println("sequenceemissions: "+sequenceEmissions.length+ ", "+sequenceEmissions[0].length);
            */

        }
        catch(IOException e){
            System.out.println("Error occurred when reading file");
        }
    }


}
