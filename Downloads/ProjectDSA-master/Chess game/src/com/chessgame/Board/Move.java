package com.chessgame.Board;

import com.chessgame.Pieces.Piece;
// Import các lớp quân cờ cụ thể để kiểm tra instanceof
import com.chessgame.Pieces.Pawn;
import com.chessgame.Pieces.King;
import com.chessgame.Pieces.Rook;

public class Move implements Comparable<Move>{
    int fromX, fromY, toX, toY;
    Piece piece;
    // Thông tin để undo trạng thái đặc biệt của quân cờ
    boolean pieceWasFirstMove; // Cho Pawn
    boolean pieceHadMoved;     // Cho King hoặc Rook

    public Move(int fromX, int fromY, int toX, int toY, Piece piece) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.piece = piece;

        // Lưu trạng thái của quân cờ TRƯỚC KHI di chuyển để có thể undo
        if (piece instanceof Pawn) { // Sử dụng import trực tiếp
            this.pieceWasFirstMove = ((Pawn) piece).isFirstMove();
        }
        // Quan trọng: Cần kiểm tra cho cả King và Rook riêng biệt
        if (piece instanceof King) { // Sử dụng import trực tiếp
            this.pieceHadMoved = ((King) piece).hasMoved();
        } else if (piece instanceof Rook) { // Sử dụng import trực tiếp
            this.pieceHadMoved = ((Rook) piece).HasMoved();
        }
        // Các quân cờ khác không có trạng thái `hasMoved` hoặc `isFirstMove` đặc biệt
        // cần lưu cho undo trong ngữ cảnh này, nên các biến boolean tương ứng sẽ giữ giá trị mặc định (false).
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }
    
    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    // Getter cho trạng thái pieceWasFirstMove
    public boolean getPieceWasFirstMove() {
        return pieceWasFirstMove;
    }

    // Getter cho trạng thái pieceHadMoved
    public boolean getPieceHadMoved() {
        return pieceHadMoved;
    }

    @Override
    public int compareTo(Move o) {
        // Logic so sánh cơ bản, có thể cần điều chỉnh tùy theo nhu cầu sắp xếp
        if(this.toX == o.toX && this.toY == o.toY && this.fromX == o.fromX && this.fromY == o.fromY) {
            // So sánh thêm quân cờ nếu cần độ chính xác cao hơn
            if (this.piece == o.piece) return 0; // Cùng một đối tượng quân cờ
            if (this.piece != null && this.piece.equals(o.piece)) return 0; // Quân cờ tương đương
        }
        // Trả về giá trị khác 0 nếu không giống hệt
        // Ví dụ đơn giản:
        if (this.fromX != o.fromX) return Integer.compare(this.fromX, o.fromX);
        if (this.fromY != o.fromY) return Integer.compare(this.fromY, o.fromY);
        if (this.toX != o.toX) return Integer.compare(this.toX, o.toX);
        if (this.toY != o.toY) return Integer.compare(this.toY, o.toY);
        return -1; // Hoặc 1, tùy thuộc vào logic sắp xếp mong muốn nếu chỉ một số trường giống nhau
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move otherM = (Move) o;
        // So sánh tất cả các trường quan trọng để xác định sự bằng nhau
        return this.fromX == otherM.fromX && 
               this.fromY == otherM.fromY &&
               this.toX == otherM.toX && 
               this.toY == otherM.toY &&
               // So sánh đối tượng piece, cẩn thận với null
               ((this.piece == null && otherM.piece == null) || (this.piece != null && this.piece.equals(otherM.piece)));
    }
    
    @Override
    public int hashCode() {
        int result = 17; // Giá trị ban đầu là số nguyên tố
        result = 31 * result + fromX;
        result = 31 * result + fromY;
        result = 31 * result + toX;
        result = 31 * result + toY;
        result = 31 * result + (piece != null ? piece.hashCode() : 0);
        // Có thể thêm pieceWasFirstMove và pieceHadMoved vào hashCode nếu chúng
        // là một phần của định nghĩa "bằng nhau" của đối tượng Move.
        // Hiện tại, equals không so sánh chúng, nên không cần thiết trong hashCode.
        return result;
    }
}