package com.chessgame.Frame;

import com.chessgame.AI.ChessAI;
import com.chessgame.Board.Board;
import com.chessgame.Board.Move;
import com.chessgame.Game.Game;
import com.chessgame.Pieces.Piece;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Panel extends JPanel {

    private static final long serialVersionUID = 1L;
    Game game; // Tham chiếu đến đối tượng Game
    public static int xx, yy;

    Panel(){
        this.setFocusable(true);
        this.addMouseListener(new Listener());
        this.addMouseMotionListener(new Listener());
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT) { // Undo
                    if (game != null && Game.board != null) {
                         Game.board.undoMove();
                         repaint();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_N && (e.isControlDown() || e.isMetaDown())) { // Ctrl+N hoặc Cmd+N cho New Game
                     if (game != null) {
                        game.loadFenPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                        game.start();
                        repaint();
                    }
                }
            }
        });
        game = new Game(); // Khởi tạo đối tượng Game

        this.setPreferredSize(new Dimension(Piece.size * Board.COLUMNS, Piece.size * Board.ROWS));
    }
    
    private void updateCellSize() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (Board.COLUMNS > 0 && Board.ROWS > 0 && panelWidth > 0 && panelHeight > 0) {
            int newCellSizeBasedOnWidth = panelWidth / Board.COLUMNS;
            int newCellSizeBasedOnHeight = panelHeight / Board.ROWS;
            Piece.size = Math.min(newCellSizeBasedOnWidth, newCellSizeBasedOnHeight);
            if (Piece.size <= 0) Piece.size = 1;
        } else if (Piece.size <=0 ) {
            Piece.size = 1;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateCellSize();
        if (game != null) { // Đảm bảo game đã được khởi tạo
            game.draw(g, xx, yy, this);
        }
    }
    
    class Listener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (Piece.size <= 0 || game == null) return;
            if(SwingUtilities.isLeftMouseButton(e)) {
                // int x = e.getX() / Piece.size;
                // int y = e.getY() / Piece.size;
                // Không cần logic phức tạp ở đây nữa nếu mousePressed và mouseReleased đã xử lý
                repaint();
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e) {
            if (Piece.size <= 0 || game == null) return;
            int temp_ti = e.getX() / Piece.size;
            int temp_tj = e.getY() / Piece.size;

            if (temp_ti >= 0 && temp_ti < Board.COLUMNS && temp_tj >= 0 && temp_tj < Board.ROWS) {
                if (Game.board.getPiece(temp_ti, temp_tj) != null && Game.board.getPiece(temp_ti, temp_tj).isWhite() == Game.isWhitePlayerTurn()) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (Piece.size <= 0 || game == null) return;
            if (SwingUtilities.isLeftMouseButton(e)) {
                int x_coord = e.getX() / Piece.size;
                int y_coord = e.getY() / Piece.size;
                
                if (x_coord >= 0 && x_coord < Board.COLUMNS && y_coord >= 0 && y_coord < Board.ROWS) {
                    game.selectPiece(x_coord, y_coord);
                    if (game.active != null) {
                        Game.drag = true;
                        xx = e.getX();
                        yy = e.getY();
                    } else {
                        Game.drag = false;
                    }
                } else {
                     Game.drag = false;
                }
            }
            repaint();
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (Piece.size <= 0 || game == null) return;
            if (SwingUtilities.isLeftMouseButton(e) && Game.drag && game.active != null) {
                xx = e.getX();
                yy = e.getY();              
                repaint();
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (Piece.size <= 0 || game == null) return;
            if (SwingUtilities.isLeftMouseButton(e)) {
                int toX = e.getX() / Piece.size;
                int toY = e.getY() / Piece.size;

                if (Game.drag && game.active != null) {
                    if (toX >= 0 && toX < Board.COLUMNS && toY >= 0 && toY < Board.ROWS) {
                        game.move(toX, toY);
                    }
                } else if (!Game.drag && game.active != null) {
                    if (toX >= 0 && toX < Board.COLUMNS && toY >= 0 && toY < Board.ROWS) {
                        if (game.active.getXcord() != toX || game.active.getYcord() != toY) {
                            game.move(toX, toY);
                        }
                    }
                }
                Game.drag = false;
                repaint();

                if (Game.aiMode && !Game.isWhitePlayerTurn()) {
                    triggerAI();
                }
            }
        }
    }

    void triggerAI() {
        new Thread(() -> {
            Move bestMove = ChessAI.getBestMove(Game.board);
            SwingUtilities.invokeLater(() -> {
                if (bestMove != null && !Game.isWhitePlayerTurn()) {
                    game.active = Game.board.getPiece(bestMove.getFromX(), bestMove.getFromY());
                    if (game.active != null) {
                        game.move(bestMove.getToX(), bestMove.getToY());
                    }
                }
                repaint();
            });
        }, "AI-Thread").start();
    }
}