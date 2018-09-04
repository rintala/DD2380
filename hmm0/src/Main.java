/**
 * Created by jonathanrintala on 2018-09-02.
 */

import org.omg.CORBA.INTERNAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


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

    public static void print2D(Float matrix[][]){
        int counter = 0;

        try{
            for (Float[] row : matrix) {
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

    public static ArrayList<Float[][]> createMatrices(ArrayList<String[]> inputVectors){
        ArrayList<Float[][]> theMatrices = new ArrayList<>();

        for(int i = 0;i<inputVectors.size();i++){
            String[] matrixContent = inputVectors.get(i);
            //System.out.println("Matrix: "+matrixContent.toString());

            int noOfRows = Integer.valueOf(matrixContent[0]);
            int noOfCols = Integer.valueOf(matrixContent[1]);

            //System.out.println("No of rows: "+noOfRows);
            //System.out.println("No of cols: "+noOfCols);

            Float[][] eachMatrix = new Float[noOfRows][noOfCols];
            //Ex.noOfRows 4 - will repeat creating theRow four times
            for(int iter = 0;iter<noOfRows;iter++){
                Float[] theRow = new Float[noOfCols];

                //vill skjuta fram k beroende på iteration
                //noOfCols +
                int start = iter*noOfCols+2;
                int end = iter*noOfCols+noOfCols+2;

                //System.out.println("-----------\nSTART: "+start);
                //System.out.println("END: "+end);

                int counter=0;

                for(int k=start;k<end;k++){
                    theRow[counter] = Float.valueOf(matrixContent[k]);
                    counter++;
                }

                //add each row to the matrix
                eachMatrix[iter] = theRow;
                //System.out.println(eachMatrix.length);
            }
            theMatrices.add(eachMatrix);

        }

        return theMatrices;
    }


    public static float[] computePiA(Float[][] pi, Float[][] A){
        //float colSum;

        float colSum;
        float[] outputVector = new float[A.length];

        for(int i=0;i<pi.length;i++){
            //System.out.println("PIL"+pi.length);
            //System.out.println("A: "+ A.length);
            for(int rowA =0;rowA<A.length;rowA++){
                colSum = 0;
                //System.out.println("pi[i].length: "+pi[i].length);
                for(int j=0;j<pi[i].length;j++){
                    //System.out.println("Comp: "+pi[i][j]);
                    //System.out.println("COL DATA FROM A: "+A[j][rowA]);
                    //for each element in pi go through col in A and sum up each in new 4x1
                    //System.out.println(A[i]);
                    colSum += pi[i][j] * A[j][rowA];
                }

                //System.out.println("NOW SHOULD WE SUM: "+colSum);
                outputVector[rowA] = colSum;

            }
        }

        return outputVector;
    }


    public static float[] computeOutput(Float[][] B, float[] piA){
        float colSum;
        float[] outputVector = new float[B[0].length];

        //System.out.println("PIL"+piA.length);
        //System.out.println("B lengththt: "+ B.length);
        for(int rowA =0;rowA<B[0].length;rowA++){
            colSum = 0;
            for(int j=0;j<piA.length;j++){
                //System.out.println("Comp: "+piA[j]);
                //System.out.println("COL DATA FROM B: "+B[j][rowA]);
                //for each element in piA go through col in B and sum up each in new 4x1
                colSum += piA[j] * B[j][rowA];
            }

            //System.out.println("NOW SHOULD WE SUM: "+colSum);
            outputVector[rowA] = colSum;
        }

        return outputVector;
    }

    public static void main(String[] args){
        Main theMain = new Main();
        try{
            ArrayList<String[]> inputV = theMain.readFile();

            //the Arraylist which will contain our matrices
            ArrayList<Float[][]> theMatrices = new ArrayList<>();
            theMatrices = createMatrices(inputV);
            //System.out.println("THEMATRIX OUTPUT: " + theMatrices.toString());

            /*
            for(int i=0;i<theMatrices.size();i++){
                System.out.println("\n-----------");
                print2D(theMatrices.get(i));
            }
            */
            //System.out.println("\n-----------");

            //You will be given three matrices (in this order);
                //1. transition matrix (A)
                //2. emission matrix (B)
                //3. initial state probability distribution (pi)

            Float[][] transitionMatrix = theMatrices.get(0);
            Float[][] emissionMatrix = theMatrices.get(1);
            Float[][] initialstateMatrix = theMatrices.get(2);

            float[] piA = computePiA(initialstateMatrix, transitionMatrix);

            /*
            System.out.println("\n-----------");

            for(int i=0;i<piA.length;i++){
                System.out.println(piA[i]);
            }
            System.out.println("\n-----------");
            */

            float[] output = computeOutput(emissionMatrix, piA);

            System.out.print("1 "+output.length);
            for(int i =0;i<output.length;i++){
                System.out.print(" "+output[i]);
            }


        }
        catch(IOException e){
            System.out.println("Error occurred when reading file");
        }
    }


}
