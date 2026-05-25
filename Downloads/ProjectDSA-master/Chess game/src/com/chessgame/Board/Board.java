package com.chessgame.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.chessgame.Game.Game;
import com.chessgame.Pieces.Pawn;
import com.chessgame.Pieces.Piece;
import com.chessgame.Pieces.Rook;
import com.chessgame.Pieces.King;


public class Board implements Cloneable{
    public static final int ROWS = 8;
    public static final int COLUMNS = 8;
    
    public int[][] grid;
    public Piece[][] pieces;
    public Move lastMove; // Nước đi cuối cùng được thực hiện trên bàn cờ này
    // public Piece died; // Không cần thiết nếu deadPieces lưu trữ tốt
    
    public Stack<Move> lastMoves = new Stack<>();
    public Stack<Piece> deadPieces = new  Stack<>(); // Quân cờ bị ăn trong nước đi tương ứng
    public List<Piece> piecesList = new ArrayList<Piece>(); // Dùng để hỗ trợ getNewBoard
    
    public Board() {
        grid = new int[COLUMNS][ROWS];
        pieces = new Piece[COLUMNS][ROWS];
    }
    
    public void setPieceIntoBoard(int x,int y,Piece piece) {
        if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS) return;
        
        // Nếu có quân cũ ở vị trí này, xóa nó khỏi piecesList
        Piece oldPiece = pieces[x][y];
        if (oldPiece != null) {
            piecesList.remove(oldPiece);
        }

        if(piece != null) {
            grid[x][y] = piece.getValueInTheboard();
            pieces[x][y] = piece;
            if (!piecesList.contains(piece)) { // Chỉ thêm nếu chưa có
                piecesList.add(piece);          
            }
        }else {
            grid[x][y] = 0;
            pieces[x][y] = null;
            // oldPiece đã được remove ở trên nếu có
        }
    }   

    public void updatePieces(int fromX,int fromY,int toX,int toY,Piece piece) {
        if (fromX < 0 || fromX >= COLUMNS || fromY < 0 || fromY >= ROWS ||
            toX < 0 || toX >= COLUMNS || toY < 0 || toY >= ROWS) return;

        // Tạo đối tượng Move trước khi thay đổi trạng thái của quân cờ
        Move currentMove = new Move(fromX, fromY, toX, toY, piece);
        this.lastMove = currentMove; // Cập nhật lastMove của Board
        lastMoves.add(currentMove); // Thêm vào stack để undo
        
        Piece capturedPiece = pieces[toX][toY]; // Quân cờ tại ô đích (nếu có)
        if(capturedPiece != null) {
            deadPieces.add(capturedPiece); // Lưu quân bị ăn
            piecesList.remove(capturedPiece); // Xóa khỏi danh sách trên bàn cờ này
            Game.AllPieces.remove(capturedPiece); // Xóa khỏi danh sách toàn cục của Game
            Game.fillPieces(); // Cập nhật danh sách quân trắng/đen của Game
        }else {
            deadPieces.add(null); // Không có quân nào bị ăn
        }
        
        // Dọn ô cũ
        grid[fromX][fromY] = 0;
        pieces[fromX][fromY] = null;
        // piecesList.remove(piece); // Không remove ở đây, vì piece vẫn còn trên bàn cờ (ở vị trí mới)
                                  // setPieceIntoBoard sẽ xử lý việc piece đã có trong list hay chưa.

        // Đặt quân cờ vào ô mới
        // grid[toX][toY] =  piece.getValueInTheboard(); // setPieceIntoBoard sẽ làm
        // pieces[toX][toY] = piece;                    // setPieceIntoBoard sẽ làm
        setPieceIntoBoard(toX, toY, piece); // Sử dụng setPieceIntoBoard để đảm bảo piecesList được cập nhật đúng
    }
    
    public void undoMove() {
        if(!lastMoves.isEmpty()) {
            Move move = lastMoves.pop(); // Lấy nước đi cuối cùng
            Piece pieceThatMoved = move.getPiece();
            Piece restoredDeadPiece = deadPieces.pop(); // Lấy quân cờ đã bị ăn (hoặc null)

            // 1. Khôi phục quân cờ đã di chuyển về vị trí cũ
            // Dọn ô hiện tại của nó (toX, toY của move)
            grid[move.toX][move.toY] = 0;
            pieces[move.toX][move.toY] = null;
            // piecesList.remove(pieceThatMoved); // Không remove, vì nó sẽ được đặt lại ở fromX, fromY

            // Đặt lại vào ô cũ
            // grid[move.fromX][move.fromY] = pieceThatMoved.getValueInTheboard(); // setPieceIntoBoard làm
            // pieces[move.fromX][move.fromY] = pieceThatMoved;                   // setPieceIntoBoard làm
            setPieceIntoBoard(move.fromX, move.fromY, pieceThatMoved);
            
            pieceThatMoved.setXcord(move.fromX);
            pieceThatMoved.setYcord(move.fromY);
            
            // Khôi phục trạng thái đặc biệt
            if(pieceThatMoved instanceof Pawn) {
                ((Pawn) pieceThatMoved).setFirstMove(move.getPieceWasFirstMove());
            }
            if(pieceThatMoved instanceof Rook) {
                ((Rook) pieceThatMoved).setHasMoved(move.getPieceHadMoved());
            }
            if(pieceThatMoved instanceof King) {
                ((King) pieceThatMoved).setHasMoved(move.getPieceHadMoved());
            }
            
            // 2. Khôi phục quân cờ đã bị ăn (nếu có) về ô toX, toY của move
            if(restoredDeadPiece != null) {
                Game.AllPieces.add(restoredDeadPiece); // Thêm lại vào danh sách toàn cục
                // setPieceIntoBoard sẽ thêm vào piecesList của board này
                // grid[move.toX][move.toY] = restoredDeadPiece.getValueInTheboard(); // setPieceIntoBoard làm
                // pieces[move.toX][move.toY] = restoredDeadPiece;                   // setPieceIntoBoard làm
                setPieceIntoBoard(move.toX, move.toY, restoredDeadPiece);
                // Không cần setXcord, setYcord cho restoredDeadPiece vì nó được đặt vào toX, toY
                Game.fillPieces(); // Cập nhật danh sách quân trắng/đen của Game
            }
            // Nếu không có quân bị ăn, ô toX, toY đã được dọn ở bước 1.

            Game.changeSide(); // Đổi lượt sau khi undo
        }
    }
    
    public Piece getPiece(int x,int y) {
        if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS) return null;
        return pieces[x][y];
    }

    public int[][] getGrid() { return grid; }
    public void setGrid(int[][] grid) { this.grid = grid; }
    public int getXY(int x,int y) {
         if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS) return 0;
        return grid[x][y];
    }
    public void setXY(int x,int y,int v) {
        if (x < 0 || x >= COLUMNS || y < 0 || y >= ROWS) return;
         grid[x][y] = v ;
    }
    
    public Board getNewBoard() {
        Board b = new Board();
        // Sao chép grid
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++) {
                b.grid[i][j] = this.grid[i][j];
            }
        }
        // Sao chép quân cờ và piecesList
        for (Piece p : this.piecesList) { // Lặp qua piecesList của board gốc
            if (p != null) {
                Piece clonedPiece = p.getClone(); // Tạo bản sao của quân cờ
                // Đặt bản sao vào bàn cờ mới. Constructor của Piece clone KHÔNG nên tự đặt vào board.
                // Hoặc, nếu constructor của Piece clone tự đặt vào board, thì board truyền vào phải là 'b'
                // Tốt nhất là Piece.getClone() chỉ clone Piece, không tương tác với Board.
                b.pieces[clonedPiece.getXcord()][clonedPiece.getYcord()] = clonedPiece;
                b.piecesList.add(clonedPiece);
            }
        }
        // Sao chép lastMoves và deadPieces nếu cần cho logic phức tạp hơn (ví dụ: AI lookahead)
        // Hiện tại, getNewBoard chủ yếu dùng để thử nước đi, không cần sao chép stack.
        return b;
    }
    
    public void printBoard() {
        for(int j=0; j<ROWS; j++) {
            for(int i=0; i<COLUMNS; i++) {
                System.out.print(grid[i][j] +  "\t");
            }
            System.out.println();
        }
        System.out.println("--------------------");
    }
}