
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author bruceoutdoors
 */
public class Solver {

    private class Node {

        public Node previous = null;
        public final Board board;
        public final int moves;
        public final int priority;

        public Node(Board board, int moves) {
            this.board = board;
            this.moves = moves;

            priority = moves + board.manhattan();
        }
    }

    private class StepSolver {

        private MinPQ<Node> stepPQ;
        public Stack<Board> stepSolution;

        public StepSolver(Board initial) {
            stepSolution = new Stack<>();
            stepPQ = new MinPQ<>(new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return Integer.compare(o1.priority, o2.priority);
                }
            });

            Node root = new Node(initial, 0);
            stepPQ.insert(root);
        }

        public Boolean step() {
            Node min = stepPQ.delMin();

            // solution found:
            if (min.board.isGoal()) {
                stepSolution.push(min.board);

                Node parent = min.previous;
                while (parent != null) {
                    stepSolution.push(parent.board);
                    parent = parent.previous;
                }

                return true;
            }

            Iterable<Board> neighbors = min.board.neighbors();
            for (Board neighbor : neighbors) {
                // don't enqueue a neighbor if its board is the 
                // same as the board of the previous search node
                if (min.previous != null && neighbor.equals(min.previous.board)) {
                    continue;
                }

                Node nd = new Node(neighbor, min.moves + 1);
                nd.previous = min;

                stepPQ.insert(nd);
            }

            return false;
        }

    }

    private Stack<Board> solution;
    private Boolean canSolve = true;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new java.lang.NullPointerException();
        }

        StepSolver original = new StepSolver(initial);
        StepSolver twin = new StepSolver(initial.twin());
        
        while (true) {
            Boolean isOriginalSolved = original.step();
            
            if (isOriginalSolved) {
                solution = original.stepSolution;
                break;
            }
            
            Boolean isTwinSolved = twin.step();
            
            if (isTwinSolved) {
                canSolve = false;
                break;
            }
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return canSolve;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!canSolve) return -1;
        
        return solution.size() - 1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solution;
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }
}
