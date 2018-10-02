# DD2380 - Artificial Intelligence

Assignment 1 & 2 of the Artificial Intelligence course at KTH. Diving into Hidden Markov Models and applying alfa-beta pruning to zero-sum games.



<<<<<<< Updated upstream
## HMM0 Next Emission Distribution
=======
##A.1 HMMs

####HMM0 Next Emission Distribution
>>>>>>> Stashed changes

Task: https://kth.kattis.com/problems/kth.ai.hmm0

In this task you should show that you know how to predict how the system will evolve over time and estimate the probability for different emissions / events in the system i.e. what can be observed from the HMM. You will be given the state probability distribution (i.e. the probability that the system is in each of the N states), the transition matrix (i.e. the matrix that gives the probability to transition from one state to another) and the emission matrix (i.e. the matrix that gives the probability for the different emissions / events / observations given a certain state).

More specifically, given the current state probability distribution what is the probabity for the different emissions after the next transition (i.e. after the system has made a single transition)?

- Using given parameters λ = (A,B,π) to calculate probability for the different states after one transition

<<<<<<< Updated upstream
## HMM1 Probability of Emission Sequence
=======


####HMM1 Probability of Emission Sequence
>>>>>>> Stashed changes

Task: https://kth.kattis.com/problems/kth.ai.hmm1

In this task you should show that you know how to calculate the probability to observe a certain emission sequence given a HMM model. You will be given the HMM model and a sequence of observations (aka emissions, events, etc) and your task is to calculate the probability for this sequence.

- Using the forward algorithm or α-pass algorithm, which estimates the prob. to be in a certain state i at time t AND having observed the observation sequence up to time t (calculated using α_(t−1))

  

####HMM2 Estimate Sequence of States

Task: https://kth.kattis.com/problems/kth.ai.hmm2

In this task you should show that you know how to calculate the most likely sequence of (hidden) states that the system moves through given an emission sequence and an HMM model.

- Using Viterbi matrix with backpointers to find the most likely emission sequence



####HMM3 Estimate Model

Task: https://kth.kattis.com/problems/kth.ai.hmm3

In this task you should show that you know how to estimate the model parameters for an HMM. You will be given a starting guess of a HMM (transition matrix, emission matrix and initial state probability distribution) and a sequence of emissions and you should train the HMM to maximize the probability of observing the given sequence of emissions.

- Combining the forward- and backward-passing algorithms: α-pass & β-pass
<<<<<<< Updated upstream
- Creating di-gamma γ_t(i,j) and gamma γ_t(i) in order to re-estimate parameters λ = (A,B,π)  iteratively until convergence criteria is met
=======
- Creating di-gamma $γ_{t}(i,j)$ and gamma $γ_{t}(i)$ in order to re-estimate parameters λ = (A,B,π)  iteratively until convergence criteria is met



##A.2 Games

####TTT

Task: https://kth.kattis.com/problems/kth.ai.tictactoe2d

In this assignment, our goal is to find the best possible move for player X given a particular state of the game.

Two dimensional tic-tac-toe: 4x4.

Alfa-beta-pruning applied on the minimax algorithm's search tree. Branching factor of 16. All three directions of winning rows considered in heuristic algorithm. 4 rows, 4 columns and 2 diagonals, i.e. 10 winning rows.

####TTT3D

Task: https://kth.kattis.com/problems/kth.ai.tictactoe3d

In this assignment, our goal is to find the best possible move for player X given a particular state of the game.

Three dimensional tic-tac-toe: 4x4x4

Alfa-beta-pruning applied on the minimax algorithm's search tree. Branching factor of 64. A total of 76 possible winning rows.