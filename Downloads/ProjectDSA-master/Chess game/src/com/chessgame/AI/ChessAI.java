package com.chessgame.AI;

import com.chessgame.Board.Board;
import com.chessgame.Board.Move;
import com.chessgame.Pieces.Piece;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Chess AI using Minimax with Alpha-Beta Pruning.
 *
 * DSA concepts used:
 *  1. Minimax      — recursive game-tree search (DFS)
 *  2. Alpha-Beta   — pruning to skip branches that cannot affect the result
 *  3. Move Ordering — sort captures first to maximise pruning efficiency
 *  4. 2D Array     — deep-copied board state per tree node
 *  5. ArrayList    — move lists and piece lists
 */
public class ChessAI {

    public static int SEARCH_DEPTH = 3;

    private static int nodesEvaluated = 0;
    private static int pruningCount   = 0;

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    /**
     * Returns the best move for Black (minimizing player).
     * Prints DSA statistics to console after each call.
     */
    public static Move getBestMove(Board board) {
        nodesEvaluated = 0;
        pruningCount   = 0;
        long startTime = System.currentTimeMillis();

        List<Move> moves = generateAllMoves(board, false);
        if (moves.isEmpty()) return null;

        orderMoves(moves, board);

        Move bestMove  = null;
        int  bestScore = Integer.MAX_VALUE;
        int  alpha     = Integer.MIN_VALUE;
        int  beta      = Integer.MAX_VALUE;

        for (Move move : moves) {
            Board simBoard = simulateMoveOnNewBoard(board, move);
            int score = minimax(simBoard, SEARCH_DEPTH - 1, alpha, beta, true);
            if (score < bestScore) {
                bestScore = score;
                bestMove  = move;
            }
            beta = Math.min(beta, score);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.printf(
            "[DSA Minimax Alpha-Beta] depth=%d | nodes=%d | prunings=%d | time=%dms | score=%d%n",
            SEARCH_DEPTH, nodesEvaluated, pruningCount, elapsed, bestScore
        );

        return bestMove;
    }

    // ----------------------------------------------------------------
    // Minimax with Alpha-Beta Pruning
    // ----------------------------------------------------------------

    /**
     * Minimax search.
     *
     * Complexity (pure Minimax): O(b^d)
     * Complexity (with Alpha-Beta, best case): O(b^(d/2))
     *
     * @param isMaximizing  true = White's turn (maximize), false = Black's turn (minimize)
     */
    private static int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        nodesEvaluated++;

        if (depth == 0) {
            return evaluateBoard(board);
        }

        List<Move> moves = generateAllMoves(board, isMaximizing);
        if (moves.isEmpty()) {
            return evaluateBoard(board);
        }

        orderMoves(moves, board);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : moves) {
                Board simBoard = simulateMoveOnNewBoard(board, move);
                int eval = minimax(simBoard, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha   = Math.max(alpha, eval);
                if (beta <= alpha) {
                    pruningCount++;
                    break; // alpha cutoff
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : moves) {
                Board simBoard = simulateMoveOnNewBoard(board, move);
                int eval = minimax(simBoard, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta    = Math.min(beta, eval);
                if (beta <= alpha) {
                    pruningCount++;
                    break; // beta cutoff
                }
            }
            return minEval;
        }
    }

    // ----------------------------------------------------------------
    // Board evaluation
    // ----------------------------------------------------------------

    /**
     * Static evaluation: sum of all piece values on the board.
     * Positive = White advantage, negative = Black advantage.
     *
     * Piece values: Pawn=±1, Knight/Bishop=±3, Rook=±5, Queen=±9, King=±100
     */
    private static int evaluateBoard(Board board) {
        int score = 0;
        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                score += board.grid[i][j];
            }
        }
        return score;
    }

    // ----------------------------------------------------------------
    // Move generation
    // ----------------------------------------------------------------

    /**
     * Generates all pseudo-legal moves for one side.
     * "Pseudo-legal" means moves are valid for the piece type but do not
     * filter out moves that leave the king in check (sufficient for internal nodes).
     */
    private static List<Move> generateAllMoves(Board board, boolean isWhite) {
        List<Move> allMoves = new ArrayList<>();
        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && p.isWhite() == isWhite) {
                    p.fillAllPseudoLegalMoves(board);
                    allMoves.addAll(p.getMoves());
                }
            }
        }
        return allMoves;
    }

    // ----------------------------------------------------------------
    // Move ordering
    // ----------------------------------------------------------------

    /**
     * Sorts moves so captures (higher absolute value targets) come first.
     * This improves Alpha-Beta efficiency: better moves are explored earlier,
     * allowing more pruning.
     *
     * Complexity: O(m log m) where m = number of moves.
     */
    private static void orderMoves(List<Move> moves, Board board) {
        moves.sort(
            Comparator.comparingInt(
                (Move m) -> Math.abs(board.getXY(m.getToX(), m.getToY()))
            ).reversed()
        );
    }

    // ----------------------------------------------------------------
    // Board simulation
    // ----------------------------------------------------------------

    /**
     * Returns a new board with the given move applied.
     * Does NOT touch Game global state (Game.AllPieces etc.),
     * so each tree node works on an independent copy.
     */
    private static Board simulateMoveOnNewBoard(Board board, Move move) {
        Board newBoard = new Board();

        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                newBoard.grid[i][j] = board.grid[i][j];
                Piece p = board.pieces[i][j];
                if (p != null) {
                    Piece cloned = p.getClone();
                    newBoard.pieces[i][j] = cloned;
                    newBoard.piecesList.add(cloned);
                }
            }
        }

        int fromX = move.getFromX();
        int fromY = move.getFromY();
        int toX   = move.getToX();
        int toY   = move.getToY();

        Piece movingPiece = newBoard.pieces[fromX][fromY];
        if (movingPiece == null) return newBoard;

        Piece capturedPiece = newBoard.pieces[toX][toY];
        if (capturedPiece != null) {
            newBoard.piecesList.remove(capturedPiece);
        }

        newBoard.grid[toX][toY]       = newBoard.grid[fromX][fromY];
        newBoard.pieces[toX][toY]     = movingPiece;
        newBoard.grid[fromX][fromY]   = 0;
        newBoard.pieces[fromX][fromY] = null;
        movingPiece.setXcord(toX);
        movingPiece.setYcord(toY);

        return newBoard;
    }

    // ----------------------------------------------------------------
    // Getters
    // ----------------------------------------------------------------

    public static int getNodesEvaluated() { return nodesEvaluated; }
    public static int getPruningCount()   { return pruningCount;   }
    public static void setSearchDepth(int depth) { SEARCH_DEPTH = Math.max(1, Math.min(depth, 5)); }
}
