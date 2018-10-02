import java.util.*;


public class Player {
    /**
     * Performs a move
     *
     * @param gameState the current state of the board
     * @param deadline  time before which we must have returned
     * @return the next state the board is in after our move
     */

    private static int PLAYER;
    private static int OTHER_PLAYER;
    private static final int DEPTH = 4;

    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        /**
         * Here you should write your algorithms to get the best next move, i.e.
         * the best next state. This skeleton returns a random move instead.
         */

        //this will always be start value
        double maxProb = -999999999;
        int maxMove = 0;
        double[] finalVals = new double[nextStates.size()];

        PLAYER = gameState.getNextPlayer();
        OTHER_PLAYER = PLAYER == 1 ? 2 : 1;

        for (int i = 0; i < nextStates.size(); i++) {

            //Choose algorithm here - alphabeta-pruning OR minimax
            double theVal = alphabetaPruning(nextStates.get(i), 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, OTHER_PLAYER);

            //System.err.println("THE FINAL VAL: " + theVal);
            finalVals[i] = theVal;
            if (theVal > maxProb) {
                maxProb = theVal;
                maxMove = i;
            }

        }

        System.err.println("THE MAX Prob-...................................." + maxProb);
        System.err.println("THE MAX MOVE-...................................." + maxMove);
        System.err.println("fiVals: "+Arrays.toString(finalVals));

        return nextStates.elementAt(maxMove);
    }

    public int[][] createGameMatrix(GameState gState) {
        int[][] theGameMatrix = new int[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                theGameMatrix[i][j] = gState.at(i, j);
            }
        }

        return theGameMatrix;
    }

    public double getHeuristic(GameState gState) {

        //Check if depth was reached or actual leaf node
        if (gState.isEOG()) {
            if (gState.isXWin()) {
                return Double.MAX_VALUE;
            }
            else if (gState.isOWin()) {
                return -Double.MAX_VALUE;
            }
            else {
                return 0;
            }
        }

        //will be used by both max and min player i.e. both player X and player O
        int playerIs = Constants.CELL_X;
        int opponentIs = playerIs == 1 ? 2 : 1;

        int[][] theGameMatrix = createGameMatrix(gState);

        //print2D(theGameMatrix);


        //Will use the following sub-heuristics to add onto each player's score
            //1.rows
            //2.columns
            //3.diagonals

        int playerScore = 0;
        int opponentScore = 0;

        //1. Rows----------------------------------------------------------
        for (int j = 0; j < theGameMatrix[0].length; j++) {
            for (int i = 0; i < theGameMatrix.length; i++) {
                if (theGameMatrix[i][j] == playerIs) {
                    playerScore++;

                } else if (theGameMatrix[i][j] == opponentIs) {
                    opponentScore++;
                }
            }
        }

        //2. Cols----------------------------------------------------------
        for (int i = 0; i < theGameMatrix.length; i++) {
            for (int j = 0; j < theGameMatrix[0].length; j++) {
                //System.err.println("\n\n "+i+" "+j);
                if (theGameMatrix[i][j] == playerIs) {
                    playerScore++;

                } else if (theGameMatrix[i][j] == opponentIs) {
                    opponentScore++;
                }
            }

        }

        //3. Diagonals------------------------------------------------------

        //3.1 Upper left to lower right
        for (int i = 0; i < theGameMatrix.length; i++) {
            //System.err.println("\n\n "+i+" "+i);
            if (theGameMatrix[i][i] == playerIs) {
                playerScore++;

            } else if (theGameMatrix[i][i] == opponentIs) {
                opponentScore++;
            }
        }

        //3.2 Lower left to upper right-------------------------
        int j = 0;
        for (int i = theGameMatrix.length - 1; i >= 0; i--) {
            if (theGameMatrix[i][j] == playerIs) {
                playerScore++;

            } else if (theGameMatrix[i][j] == opponentIs) {
                opponentScore++;
            }
            j++;
        }

        //-----------------------------------------------------------------------------
        double playerHeuristic = Math.pow(playerScore, 2) - Math.pow(opponentScore, 2);
        return playerHeuristic;
    }

    private double alphabetaPruning(GameState gameState, int depth, double alpha, double beta, int player){
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);

        double v = 0;

        if (nextStates.size() == 0 || depth == DEPTH){
            return getHeuristic(gameState);
        }

        else{
            if (player == PLAYER){
                v = Double.NEGATIVE_INFINITY;
                for (int child = 0; child < nextStates.size(); child++) {
                    v = Math.max(v, alphabetaPruning(nextStates.get(child), depth+1, alpha, beta, OTHER_PLAYER));
                    alpha = Math.max(alpha, v);
                    if (beta <= alpha){
                        break;
                    }
                }
            }

            else if(player == OTHER_PLAYER){
                v = Double.POSITIVE_INFINITY;
                for (int child = 0; child < nextStates.size(); child++) {
                    v = Math.min(v, alphabetaPruning(nextStates.get(child), depth+1, alpha, beta, PLAYER));
                    beta = Math.min(beta, v);
                    if (beta <= alpha){
                        break;
                    }

                }
            }
        }

        return v;

    }

    public static void print2D(int matrix[][]){
        int counter = 0;

        try{
            for (int[] row : matrix) {
                if(counter==0){}
                else{
                    System.err.print("\n");
                }
                for(int i =0;i<row.length;i++){
                    System.err.print(row[i]+" ");
                }
                counter++;
            }
        }

        catch(Exception e){
            System.err.println("ERROR w map print");
        }
    }
}


