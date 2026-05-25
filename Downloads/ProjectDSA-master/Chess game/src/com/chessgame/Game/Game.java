package com.chessgame.Game;

import com.chessgame.AI.ChessAI;
import com.chessgame.Board.Board;
import com.chessgame.Board.Move;
import com.chessgame.Pieces.Bishop;
import com.chessgame.Pieces.King;
import com.chessgame.Pieces.Knight;
import com.chessgame.Pieces.Pawn;
import com.chessgame.Pieces.Piece;
import com.chessgame.Pieces.PieceImages;
import com.chessgame.Pieces.Queen;
import com.chessgame.Pieces.Rook;
import com.chessgame.Settings.GameSettings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game {
    public static Board board = new Board();

    static King wk; // Vua trắng
    static King bk; // Vua đen
    static ArrayList<Piece> wPieces = new ArrayList<>(); // Quân trắng còn trên bàn
    static ArrayList<Piece> bPieces = new ArrayList<>(); // Quân đen còn trên bàn

    static boolean player = true; // true for White, false for Black
    public Piece active = null; // Quân cờ đang được chọn/kéo
    public static boolean drag = false; // Trạng thái kéo thả
    public static ArrayList<Piece> AllPieces = new ArrayList<>(); // Tất cả các quân cờ (để dễ lặp)

    public static boolean aiMode = false;

    static List<Move> allPlayersMove = new ArrayList<>(); // Các nước đi của người chơi hiện tại
    public static List<Move> allEnemysMove = new ArrayList<>(); // Các nước đi của đối thủ
    private static boolean gameOver = false;

    public Game() {
        new PieceImages();
        loadFenPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        start();
    }

    public void start() {
        fillPieces();
        generatePlayersTurnMoves(board);
        generateEnemysMoves(board);
        checkPlayersLegalMoves();
    }

    // Getter công khai cho biến 'player'
    public static boolean isWhitePlayerTurn() {
        return player;
    }

    public void draw(Graphics g, int x, int y, JPanel panel) {
        drawBoard(g);
        drawPiece(g, panel);
        drawPossibleMoves(g, panel);
        drag(active, x, y, g, panel);
        drawKingInCheck(player, g, panel);
    }

    public static void generatePlayersTurnMoves(Board currentBoard) {
        allPlayersMove.clear();
        List<Piece> piecesForCurrentPlayer = player ? wPieces : bPieces;
        for (Piece p : piecesForCurrentPlayer) {
            p.fillAllPseudoLegalMoves(currentBoard);
            allPlayersMove.addAll(p.getMoves());
        }
    }

    public static void generateEnemysMoves(Board currentBoard) {
        allEnemysMove.clear();
        List<Piece> piecesForEnemy = !player ? wPieces : bPieces;
        for (Piece p : piecesForEnemy) {
            p.fillAllPseudoLegalMoves(currentBoard);
            allEnemysMove.addAll(p.getMoves());
        }
    }

    public static void changeSide() {
        player = !player;
        generatePlayersTurnMoves(board);
        generateEnemysMoves(board);
        checkPlayersLegalMoves();
        checkMate();
    }

    public void aiPlay() {
        if (gameOver || player) return;
        Move bestMove = ChessAI.getBestMove(board);
        if (bestMove != null) {
            active = board.getPiece(bestMove.getFromX(), bestMove.getFromY());
            if (active != null) {
                move(bestMove.getToX(), bestMove.getToY());
            }
        }
        active = null;
    }

    public void randomPlay() {
        if (gameOver) {
            return;
        }
        if (!player) {
            Random r = new Random();
            if (bPieces.isEmpty()) return;

            List<Piece> playablePieces = new ArrayList<>();
            for(Piece p : bPieces) {
                if(!p.getMoves().isEmpty()){
                    playablePieces.add(p);
                }
            }
            if(playablePieces.isEmpty()) return;

            active = playablePieces.get(r.nextInt(playablePieces.size()));
            Move m = active.getMoves().get(r.nextInt(active.getMoves().size()));
            move(m.getToX(), m.getToY());
        }
    }

    public void selectPiece(int x, int y) {
        if (x < 0 || x >= Board.COLUMNS || y < 0 || y >= Board.ROWS) return;
        
        Piece selected = board.getPiece(x, y);
        if (active == null && selected != null && selected.isWhite() == player) {
            active = selected;
        } else if (active != null && active == selected) {
            // Bỏ chọn hoặc giữ nguyên
        } else if (active != null && selected != null && selected.isWhite() == player) {
            active = selected;
        }
    }

    public static void checkMate() {
        if (gameOver) return;

        King currentKing = player ? wk : bk;
        List<Piece> currentPlayerPieces = player ? wPieces : bPieces;

        boolean hasLegalMoves = false;
        for (Piece p : currentPlayerPieces) {
            if (!p.getMoves().isEmpty()) {
                hasLegalMoves = true;
                break;
            }
        }

        if (!hasLegalMoves) {
            gameOver = true;
            if (currentKing != null && currentKing.isInCheck()) { // Thêm kiểm tra null cho currentKing
                String winner = !player ? "White" : "Black";
                JOptionPane.showMessageDialog(null, "Checkmate! " + winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Stalemate! It's a draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void checkPlayersLegalMoves() {
        List<Piece> piecesToCheck = player ? wPieces : bPieces;
        // King kingToCheck = player ? wk : bk; // Biến này không được sử dụng trực tiếp trong logic hiện tại

        for (Piece p : new ArrayList<>(piecesToCheck)) {
            List<Move> legalMovesForPiece = new ArrayList<>();
            for (Move pseudoMove : new ArrayList<>(p.getMoves())) {
                Board clonedBoard = board.getNewBoard();
                Piece clonedPiece = clonedBoard.getPiece(p.getXcord(), p.getYcord());

                if (clonedPiece == null) continue;

                clonedBoard.updatePieces(clonedPiece.getXcord(), clonedPiece.getYcord(), pseudoMove.getToX(), pseudoMove.getToY(), clonedPiece);
                clonedPiece.setXcord(pseudoMove.getToX());
                clonedPiece.setYcord(pseudoMove.getToY());

                King kingOnClonedBoard = null;
                // Cần đảm bảo Board.piecesList được cập nhật đúng trong getNewBoard và updatePieces
                // Hoặc lặp qua mảng pieces[][] của clonedBoard
                for(int c = 0; c < Board.COLUMNS; c++) {
                    for(int r = 0; r < Board.ROWS; r++) {
                        Piece pieceOnClone = clonedBoard.getPiece(c, r);
                        if(pieceOnClone instanceof King && pieceOnClone.isWhite() == player){
                            kingOnClonedBoard = (King) pieceOnClone;
                            break;
                        }
                    }
                    if (kingOnClonedBoard != null) break;
                }
                
                if (kingOnClonedBoard == null && clonedPiece instanceof King) {
                    kingOnClonedBoard = (King) clonedPiece;
                } else if (kingOnClonedBoard == null) {
                    continue; 
                }

                boolean kingInCheckAfterMove = false;
                List<Piece> enemyPiecesOnClonedBoard = new ArrayList<>();
                 for(int i=0; i<Board.COLUMNS; i++){
                    for(int j=0; j<Board.ROWS; j++){
                        Piece pieceClone = clonedBoard.getPiece(i,j);
                        if(pieceClone != null && pieceClone.isWhite() != player){
                            enemyPiecesOnClonedBoard.add(pieceClone);
                        }
                    }
                }

                for (Piece enemyP : enemyPiecesOnClonedBoard) {
                    enemyP.fillAllPseudoLegalMoves(clonedBoard);
                    for (Move enemyMove : enemyP.getMoves()) {
                        if (enemyMove.getToX() == kingOnClonedBoard.getXcord() && enemyMove.getToY() == kingOnClonedBoard.getYcord()) {
                            kingInCheckAfterMove = true;
                            break;
                        }
                    }
                    if (kingInCheckAfterMove) break;
                }

                if (!kingInCheckAfterMove) {
                    legalMovesForPiece.add(pseudoMove);
                }
            }
            p.getMoves().clear();
            p.getMoves().addAll(legalMovesForPiece);
        }
        allPlayersMove.clear();
        for (Piece pieceCurrentPlayer : piecesToCheck) {
            allPlayersMove.addAll(pieceCurrentPlayer.getMoves());
        }
    }

    public void drag(Piece piece, int mouseX, int mouseY, Graphics g, JPanel panel) {
        if (piece != null && drag == true) {
            piece.draw2(g, player, mouseX, mouseY, panel);
        }
    }

    public void move(int toX, int toY) {
        if (gameOver) return;
        if (active == null) {
            drag = false;
            return;
        }

        if (active.makeMove(toX, toY, board)) {
            tryToPromote(active);
            changeSide(); 
            active = null;
        }
        drag = false;
    }

    public void drawKingInCheck(boolean isWhitePlayer, Graphics g, JPanel panel) {
        g.setColor(Color.RED);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));

        King kingToCheck = isWhitePlayer ? wk : bk;
        
        if (kingToCheck != null && kingToCheck.isInCheck()) {
            g2d.drawRect(kingToCheck.getXcord() * Piece.size, kingToCheck.getYcord() * Piece.size, Piece.size, Piece.size);
        }
    }

    public void drawBoard(Graphics g) {
        for (int i = 0; i < Board.COLUMNS; i++) {
            for (int j = 0; j < Board.ROWS; j++) {
                if ((i + j) % 2 == 1) {
                    g.setColor(GameSettings.darkSquareColor);
                } else {
                    g.setColor(GameSettings.lightSquareColor);
                }
                g.fillRect(i * Piece.size, j * Piece.size, Piece.size, Piece.size);
            }
        }
    }

    public void tryToPromote(Piece p) {
        if (p instanceof Pawn) {
            if (((Pawn) p).madeToTheEnd()) {
                int choice = showMessageForPromotion();
                promotePawn((Pawn)p, choice);
            }
        }
    }

    public int showMessageForPromotion() {
        Object[] options = { "Queen", "Rook", "Knight", "Bishop" };
        boolean oldDragState = drag;
        drag = false; 
        
        int choice = JOptionPane.showOptionDialog(null,
                "Choose Piece To Promote to:", "Pawn Promotion", 
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, 
                null,
                options, 
                options[0]);
        
        drag = oldDragState;
        return choice;
    }

    public static void promotePawn(Pawn pawnToPromote, int choice) {
        int x = pawnToPromote.getXcord();
        int y = pawnToPromote.getYcord();
        boolean isWhite = pawnToPromote.isWhite();
        
        AllPieces.remove(pawnToPromote);
        if (isWhite) wPieces.remove(pawnToPromote); else bPieces.remove(pawnToPromote);
        board.setPieceIntoBoard(x,y, null);

        Piece newPiece = null;
        switch (choice) {
            case 0:
                newPiece = new Queen(x, y, isWhite, board, isWhite ? 9 : -9);
                break;
            case 1:
                newPiece = new Rook(x, y, isWhite, board, isWhite ? 5 : -5);
                break;
            case 2:
                newPiece = new Knight(x, y, isWhite, board, isWhite ? 3 : -3);
                break;
            case 3:
                newPiece = new Bishop(x, y, isWhite, board, isWhite ? 3 : -3);
                break;
            default:
                newPiece = new Queen(x, y, isWhite, board, isWhite ? 9 : -9);
                break;
        }
        
        if (newPiece != null) {
            AllPieces.add(newPiece);
            // board.setPieceIntoBoard(x, y, newPiece); // Đã được gọi trong constructor của Piece
        }
        fillPieces();
        generatePlayersTurnMoves(board);
        generateEnemysMoves(board);
        checkPlayersLegalMoves();
    }

    public void drawPossibleMoves(Graphics g, JPanel panel) {
        if (active != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            active.showMoves(g2, panel);
        }
    }

    public void drawPiece(Graphics g, JPanel panel) {
        for (Piece p : AllPieces) {
            if (p != active || !drag) {
                 p.draw(g, false, panel);
            }
        }
    }

    public void loadFenPosition(String fenString) {
        AllPieces.clear();
        wPieces.clear();
        bPieces.clear();
        wk = null;
        bk = null;
        for (int r = 0; r < Board.ROWS; r++) {
            for (int c = 0; c < Board.COLUMNS; c++) {
                board.grid[c][r] = 0;
                board.pieces[c][r] = null;
            }
        }

        String[] parts = fenString.split(" ");
        String position = parts[0];
        int row = 0;
        int col = 0;

        for (char c : position.toCharArray()) {
            if (c == '/') {
                row++;
                col = 0;
            } else if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
            } else if (Character.isLetter(c)) {
                boolean isWhitePiece = Character.isUpperCase(c);
                addToBoard(col, row, c, isWhitePiece); 
                col++;
            }
        }

        if (parts.length > 1) {
            player = parts[1].equals("w");
        }
        
        fillPieces();

        for (Piece p : AllPieces) {
            if (p instanceof King) {
                if (p.isWhite()) wk = (King) p;
                else bk = (King) p;
            }
        }
         if (wk == null || bk == null) {
            System.err.println("Error: King(s) not found after FEN load. FEN: " + fenString);
        }
    }

    public static void fillPieces() {
        wPieces.clear();
        bPieces.clear();
        for (Piece p : AllPieces) {
            if (p.isWhite()) {
                wPieces.add(p);
            } else {
                bPieces.add(p);
            }
        }
    }

    public void addToBoard(int x, int y, char c, boolean isWhite) {
        Piece newPiece = null;
        switch (String.valueOf(c).toUpperCase()) {
            case "R":
                newPiece = new Rook(x, y, isWhite, board, isWhite ? 5 : -5);
                break;
            case "N":
                newPiece = new Knight(x, y, isWhite, board, isWhite ? 3 : -3);
                break;
            case "B":
                newPiece = new Bishop(x, y, isWhite, board, isWhite ? 3 : -3);
                break;
            case "Q":
                newPiece = new Queen(x, y, isWhite, board, isWhite ? 9 : -9);
                break;
            case "K":
                King king = new King(x, y, isWhite, board, isWhite ? 100 : -100);
                newPiece = king;
                break;
            case "P":
                newPiece = new Pawn(x, y, isWhite, board, isWhite ? 1 : -1);
                break;
        }
        if (newPiece != null) {
            AllPieces.add(newPiece);
        }
    }
}