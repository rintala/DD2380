
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

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
        //System.out.println("CURRENT STATE "+currentState);
        double[] outputAlpha1 = new double[emissionMatrix.length];

        //Look for correct col (state t+1) in emissionMatrix since we might not always start in state 0 i.e. not always col=0
        for(int i = 0; i<initialStateDistribution[0].length; i++){
            outputAlpha1[i] = initialStateDistribution[0][i] * emissionMatrix[i][currentState];
        }

        return outputAlpha1;
    }


    public static double[][] computeOutput(double[] alpha1, double[][] seqEmissions, double[][] B, double[][] A){
        //first add alpha1 to outputMatrix
        //declare and initialize outputmatrix
        double[][] seqEmissionsMatrix = new double[alpha1.length][seqEmissions[0].length];

        for(int j = 0; j<seqEmissionsMatrix.length;j++){
            seqEmissionsMatrix[j][0] = alpha1[j];
        }

        //then start with the filling of the rest
        for(int t=1;t<seqEmissionsMatrix[0].length;t++) {
            //System.out.println("SEQ TIMESTEP: "+t+": "+seqEmissions[0][t]);
            //System.out.println(seqEmissionsMatrix.length + "VS."+A.length);
            for (int j = 0; j < seqEmissionsMatrix.length; j++) {
                //since we need to know which timestep where in
                //we use the outer for loop to get correct element for t resp. t-1
                double loopSum = 0;
                for (int sumI = 0; sumI < seqEmissionsMatrix.length; sumI++) {
                    loopSum += seqEmissionsMatrix[sumI][t-1] * A[sumI][j];
                }
                seqEmissionsMatrix[j][t] = B[j][(int)Math.round(seqEmissions[0][t])] * loopSum;
            }
        }

        return seqEmissionsMatrix;
    }


    public static double sumCol(double[][] seqEmissonMatrix){
        double theOutputSum = 0;

        for(int i=0;i<seqEmissonMatrix.length;i++){
            theOutputSum+=seqEmissonMatrix[i][seqEmissonMatrix[i].length-1];
        }

        return theOutputSum;
    }

    public static void main(String[] args){
        Kattis theMain = new Kattis();
        try{
            ArrayList<String[]> inputV = theMain.readFile();

            //the Arraylist which will contain our matrices
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

            //compute alpha1 vector w. help of pi & A
            double[] alpha1 = computeAlpha1(initialstateMatrix, emissionMatrix, sequenceEmissions);


            double[][] sequenceEmissionsMatrix = computeOutput(alpha1, sequenceEmissions, emissionMatrix, transitionMatrix);


            double output = sumCol(sequenceEmissionsMatrix);
            System.out.print(output);

        }
        catch(IOException e){
            System.out.println("Error occurred when reading file");
        }
    }


}
