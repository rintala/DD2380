/**
 * Created by jonathanrintala on 2018-09-04.
 */
/**
 * Created by jonathanrintala on 2018-09-02.
 */

import org.omg.CORBA.INTERNAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.math.BigDecimal;

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


    public static double[] computeAlpha1(double[][] initialStateDistribution, double[][] emissionMatrix, double[][] seqEmissions){

        int currentState = (int)seqEmissions[0][0];
        System.out.println("CURRENT STATE "+currentState);
        double[] outputAlpha1 = new double[emissionMatrix.length];


        //System.out.println("LEN INTIIALVECCTOR: "+initialStateDistribution[0].length);
        for(int i = 0; i<initialStateDistribution[0].length; i++){
            //System.out.println("stateProb "+initialStateDistribution[0][i]);
            outputAlpha1[i] = initialStateDistribution[0][i] * emissionMatrix[i][currentState];
        }

        return outputAlpha1;
    }


    public static double[][] computeOutput(double[] alpha1, double[][] seqEmissions, double[][] B, double[][] A){
        //first add alpha1 to outputMatrix
        //declare and initialize outputmatrix
        double[][] seqEmissionsMatrix = new double[alpha1.length][seqEmissions[0].length];

        /*
        for(int i =0; i<seqEmissions[0].length;i++){
            System.out.println("SEQ EMM:"+ seqEmissions[0][i]);
        }
        */

        for(int j = 0; j<seqEmissionsMatrix.length;j++){
            seqEmissionsMatrix[j][0] = alpha1[j];
        }

        //then start with the filling of the rest
        for(int t=1;t<seqEmissionsMatrix[0].length;t++) {
            System.out.println("SEQ TIMESTEP: "+t+": "+seqEmissions[0][t]);
            //System.out.println(seqEmissionsMatrix.length + "VS."+A.length);
            for (int j = 0; j < seqEmissionsMatrix.length; j++) {
                //OBS ÄNDRA HÄR 1 & 0 TILL "t" resp "t-1" (dvs. måste va stateful => recursive LR yttre for-loop kanske)
                //OBS2! Måste ha tag i värdet på t dvs. vad det e för state - talet
                    //hamta fran vector med states - dvs. seqEmissions

                double loopSum = 0;
                for (int sumI = 0; sumI < seqEmissionsMatrix.length; sumI++) {
                    loopSum += seqEmissionsMatrix[sumI][t-1] * A[sumI][j];
                }
                //System.out.println("LoOK AT COLUMN: "+Math.round(seqEmissions[0][t]));
                seqEmissionsMatrix[j][t] = B[j][(int)Math.round(seqEmissions[0][t])] * loopSum;
            }
        }

        return seqEmissionsMatrix;
    }


    public static float round2(Float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return ( (float) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
    }

    public static float round3(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static double sumCol(double[][] seqEmissonMatrix){
        double theOutputSum = 0;

        for(int i=0;i<seqEmissonMatrix.length;i++){
            theOutputSum+=seqEmissonMatrix[i][seqEmissonMatrix[i].length-1];
        }

        return theOutputSum;
    }

    // rounds double to specified decimal (unlike Math.round)
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static void main(String[] args){
        Main theMain = new Main();
        try{
            ArrayList<String[]> inputV = theMain.readFile();

            //the Arraylist which will contain our matrices
            ArrayList<double[][]> theMatrices = new ArrayList<>();
            theMatrices = createMatrices(inputV);

            /*
            for(int i=0;i<theMatrices.size();i++){


                System.out.println("\n-----------");
                print2D(theMatrices.get(i));
            }

            System.out.println("\n-----------");
             */

            //You will be given three matrices (in this order) + sequence of emissions;
                //1. transition matrix (A)
                //2. emission matrix (B)
                //3. initial state probability distribution (pi)
                //4. Then a sequence of emissions

            double[][] transitionMatrix = theMatrices.get(0);
            double[][] emissionMatrix = theMatrices.get(1);
            double[][] initialstateMatrix = theMatrices.get(2);
            double[][] sequenceEmissions = theMatrices.get(3);

            //System.out.println("seq emission length "+sequenceEmissions.length);
            //System.out.println("seq miession i length "+sequenceEmissions[0].length);

            //System.out.println(":P " +sequenceEmissions[0]);

            //compute alpha1 vector w. help of pi & A
            double[] alpha1 = computeAlpha1(initialstateMatrix, emissionMatrix, sequenceEmissions);

            /*
            for(int i = 0; i<alpha1.length;i++){

                System.out.println(alpha1[i]);
            }
            */

            double[][] sequenceEmissionsMatrix = computeOutput(alpha1, sequenceEmissions, emissionMatrix, transitionMatrix);

            //Float[][] sequenceEmissionsMatrix = new Float[alpha1.length][sequenceEmissions[0].length];

            //System.out.println("ROWS IN output: "+sequenceEmissionsMatrix.length);
            //System.out.println("COLS IN output: "+sequenceEmissionsMatrix[0].length);

            print2D(sequenceEmissionsMatrix);

            //RETURN OUTPUT ON SYSTEM OUT

            //System.out.println(sequenceEmissionsMatrix[sequenceEmissionsMatrix.length-1][sequenceEmissionsMatrix[0].length-1]);
            System.out.println("----------");
            System.out.println("transition matrix: "+transitionMatrix.length+ ", "+transitionMatrix[0].length);
            System.out.println("emission matrix: "+emissionMatrix.length+ ", "+emissionMatrix[0].length);
            System.out.println("intitial state matrix: "+initialstateMatrix.length+ ", "+initialstateMatrix[0].length);
            System.out.println("sequenceemissions: "+sequenceEmissions.length+ ", "+sequenceEmissions[0].length);

            for(int row=0;row<sequenceEmissionsMatrix.length;row++){
                //System.out.println("L "+alpha[0].length);
                //System.out.println("L "+alpha.length);
                System.out.println("L "+sequenceEmissionsMatrix[row][sequenceEmissionsMatrix[0].length-1]);
            }

            double output = sumCol(sequenceEmissionsMatrix);
            System.out.print(round(output,6));
            //System.out.print(round3(output,6));
            //System.out.print(1.0);
            //System.out.println();


        }
        catch(IOException e){
            System.out.println("Error occurred when reading file");
        }
    }


}
