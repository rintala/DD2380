import java.util.*;
import java.util.logging.XMLFormatter;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */

    private static HashMap<Integer, Double> theHashTable;
    private static final int DIM = 4;
    private static int PLAYER;
    private static int OTHER_PLAYER;
    private static final int DEPTH = 3;

    //Global best next move, will be assigned according to our heuristic function
    private GameState theBest = new GameState();

    //Create map of all our winning states, since we have full information
    //[76][4]
    private static final int[][] WINNING_STATES =
            {
                    //from front----------------------------------------------------------------
                    //straight rows Z-plane
                        // ---
                        // ---
                        // ---
                        // ---
                    {0, 1, 2, 3},
                    {4, 5, 6, 7},
                    {8, 9, 10,11},
                    {12, 13, 14, 15},

                    {16, 17, 18, 19},
                    {20, 21, 22, 23},
                    {24, 25, 26, 27},
                    {28, 29, 30, 31},

                    {32, 33, 34, 35},
                    {36, 37, 38, 39},
                    {40, 41, 42, 43},
                    {44, 45, 46, 47},

                    {48, 49, 50, 51},
                    {52, 53, 54, 55},
                    {56, 57, 58, 59},
                    {60, 61, 62, 63},

                    //straight cols Y-plane
                        // | | | |
                    //X=1
                    {0, 4, 8, 12},
                    {1, 5, 9, 13},
                    {2, 6, 10, 14},
                    {3, 7, 11, 15},

                    //X=2
                    {16, 20, 24, 28},
                    {17, 21, 25, 29},
                    {18, 22, 26, 30},
                    {19, 23, 27, 31},

                    //X=3
                    {32, 36, 40, 44},
                    {33, 37, 41, 45},
                    {34, 38, 42, 46},
                    {35, 39, 43, 47},

                    //X=4
                    {48, 52, 56, 60},
                    {49, 53, 57, 61},
                    {50, 54, 58, 62},
                    {51, 55, 59, 63},

                    //diagonals X-plane
                    //  /
                    {3, 6, 9, 12},
                    {19, 22, 25, 28},
                    {35, 38, 41, 44},
                    {51, 54, 57, 60},

                    //  \
                    {0, 5, 10, 15},
                    {16, 21, 26, 31},
                    {32, 37, 42, 47},
                    {48, 53, 58, 63},
                    //------------------------------------------------------------------------

                    //from top----------------------------------------------------------------
                    //diagonals Z-plane
                    //  /
                    {0, 17, 34, 51},
                    {4, 21, 38, 55},
                    {8, 25, 42, 59},
                    {12, 29, 46, 63},

                    // \
                    {3, 18, 33, 48},
                    {7, 22, 37, 52},
                    {11, 26, 41, 56},
                    {15, 30, 45, 60},

                    //X-plane
                    // | | | |
                    {0, 16, 32, 48},
                    {1, 17, 33, 49},
                    {2, 18, 34, 50},
                    {3, 19, 35, 51},

                    {4, 20, 36, 52},
                    {5, 21, 37, 53},
                    {6, 22, 38, 54},
                    {7, 23, 39, 55},

                    {8, 24, 40, 56},
                    {9, 25, 41, 57},
                    {10, 26, 42, 58},
                    {11, 27, 43, 59},

                    {12, 28, 44, 60},
                    {13, 29, 45, 61},
                    {14, 30, 46, 62},
                    {15, 31, 47, 63},
                    //------------------------------------------------------------------------

                    //from left---------------------------------------------------------------
                    //  /
                    {0, 20, 40, 60},
                    {1, 21, 41, 61},
                    {2, 22, 42, 62},
                    {3, 23, 43, 63},

                    // \
                    {12, 24, 36, 48},
                    {13, 25, 37, 49},
                    {14, 26, 38, 50},
                    {15, 27, 39, 51},
                    //------------------------------------------------------------------------

                    //main diagonals
                    {51, 38, 25, 12},
                    {48, 37, 26, 15},
                    {3, 22, 41, 60},
                    {0, 21, 42, 63},

            };

    //Matrix for assigning points in our heuristic function
        //first row: col given by number of no. opponent pieces in that line
        //first col: row given by number of no. player pieces in that line
        //other elements are set to 0: which means if block, set score to 0
    private static int[][] HEURISTIC_POINTS = {
            {0, -10, -100, -1000, -100000},
            {10, 0, 0, 0, 0},
            {100, 0, 0, 0, 0},
            {1000, 0, 0, 0, 0},
            {100000, 0, 0, 0, 0}
    };

    public Player(){
        //hashtable for store & lookup of previously visited states
        theHashTable = new HashMap<Integer, Double>();
    }

    public GameState play(final GameState gameState, final Deadline deadline) {

        //final long startTime = System.currentTimeMillis();

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

        //Assuming player will always be X (given in Kattis)
        PLAYER = gameState.getNextPlayer();
        OTHER_PLAYER = PLAYER == 1 ? 2 : 1;

        double theVal = alphabetaPruning(gameState, 0, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY, PLAYER);

        //FOR TIME MEASURING
        /*
        double endTime = System.currentTimeMillis();
        double diff = endTime-startTime;
        System.err.println((diff));
        */


        return theBest;
    }

    private double alphabetaPruning(GameState gameState, int depth, double alpha, double beta, int player){
        double v;
        double maxVal;
        GameState maxMove = new GameState();

        if (gameState.isEOG() || depth == DEPTH){
            /*
            int hashValue = gameState.toMessage().hashCode();

            if((theHashTable.get(hashValue) != null)){
                //System.err.println("HAS VALUE -------------------------------------------");
                v=theHashTable.get(hashValue);
                return v;
            }

            theHashTable.put(hashValue, maxVal);
            */
            if (gameState.isXWin()){
                return Double.POSITIVE_INFINITY;
            }
            else if (gameState.isOWin()){
                return Double.NEGATIVE_INFINITY;
            }

            return getHeuristic(gameState);
        }

        else{
            Vector<GameState> nextStates = new Vector<>();
            gameState.findPossibleMoves(nextStates);
            if (player == PLAYER){
                v = Double.NEGATIVE_INFINITY;
                maxVal = Double.NEGATIVE_INFINITY;

                for (int child = 0; child < nextStates.size(); child++) {
                    v = Math.max(v, alphabetaPruning(nextStates.get(child), depth+1, alpha, beta, OTHER_PLAYER));
                    alpha = Math.max(alpha, v);

                    if(maxVal<v){
                        maxVal = v;
                        maxMove = nextStates.get(child);
                    }

                    if (beta <= alpha){
                        //System.err.println("PRUNING!!!");
                        break;
                    }
                }

                theBest = maxMove;
            }

            else{
                v = Double.POSITIVE_INFINITY;
                maxVal = Double.POSITIVE_INFINITY;

                for(int child = 0; child < nextStates.size(); child++) {
                    v = Math.min(v, alphabetaPruning(nextStates.get(child), depth+1, alpha, beta, PLAYER));
                    beta = Math.min(beta, v);

                    if(maxVal>v){
                        maxVal = v;
                        maxMove = nextStates.get(child);
                    }

                    if(beta <= alpha){
                        //System.err.println("PRUNING!!!!");
                        break;
                    }
                }

                theBest = maxMove;
            }
        }

        return maxVal;

    }

    //Our heuristic function which estimates the utility, gamma for each state S
    public double getHeuristic(GameState gState) {
        int playerPieces;
        int opponentPieces;
        int finalScore = 0;

        //76 possible ways of winning
        for(int way=0;way<76;way++){
            playerPieces=0;
            opponentPieces=0;

            for(int j=0;j<DIM;j++){
                if(gState.at(WINNING_STATES[way][j]) == Constants.CELL_X){
                    playerPieces++;
                }
                else if(gState.at(WINNING_STATES[way][j]) == Constants.CELL_O){
                    opponentPieces++;
                }
            }

            //USE MATRIX FOR FAST LOOKUP OF SCORE
            finalScore+=HEURISTIC_POINTS[playerPieces][opponentPieces];
        }

        //System.err.println("FINAL SCORE"+finalScore);
        return finalScore;
    }
}
