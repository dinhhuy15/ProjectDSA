package com.chessgame.Frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Tutorial dialog — hướng dẫn chơi và giải thích DSA trong game.
 */
public class TutorialDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public TutorialDialog(Frame parent) {
        super(parent, "Hướng dẫn — Chess DSA", true);
        setSize(580, 520);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabs.addTab("🎮  Cách chơi",   buildPlayTab());
        tabs.addTab("🤖  AI Mode",      buildAITab());
        tabs.addTab("📚  DSA",          buildDSATab());

        add(tabs, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Bắt đầu chơi!");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(46, 125, 50));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setOpaque(true);
        closeBtn.setBorderPainted(false);
        closeBtn.addActionListener((ActionEvent e) -> dispose());

        JPanel bottom = new JPanel();
        bottom.setBorder(new EmptyBorder(8, 0, 10, 0));
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JScrollPane buildPlayTab() {
        String text =
            "<html><body style='font-family:Segoe UI;font-size:13px;padding:10px;width:490px'>" +
            "<h2 style='color:#1565C0'>🎮 Cách chơi cờ vua</h2>" +

            "<h3>Di chuyển quân</h3>" +
            "<ul>" +
            "<li><b>Kéo & thả</b>: Giữ chuột trái kéo quân đến ô muốn đi, thả ra.</li>" +
            "<li><b>Click</b>: Click chọn quân → các ô hợp lệ hiện dấu chấm → click ô muốn đến.</li>" +
            "</ul>" +

            "<h3>Phím tắt</h3>" +
            "<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse'>" +
            "<tr style='background:#E3F2FD'><th>Phím</th><th>Chức năng</th></tr>" +
            "<tr><td><b>← (mũi tên trái)</b></td><td>Undo — đi lại nước vừa đi</td></tr>" +
            "<tr><td><b>Ctrl + N</b></td><td>New Game — ván cờ mới</td></tr>" +
            "</table>" +

            "<h3>Giá trị quân cờ</h3>" +
            "<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse'>" +
            "<tr style='background:#E3F2FD'><th>Quân</th><th>Giá trị</th></tr>" +
            "<tr><td>♟ Tốt (Pawn)</td><td>1</td></tr>" +
            "<tr><td>♞ Mã (Knight)</td><td>3</td></tr>" +
            "<tr><td>♝ Tượng (Bishop)</td><td>3</td></tr>" +
            "<tr><td>♜ Xe (Rook)</td><td>5</td></tr>" +
            "<tr><td>♛ Hậu (Queen)</td><td>9</td></tr>" +
            "<tr><td>♚ Vua (King)</td><td>100</td></tr>" +
            "</table>" +

            "<h3>Luật đặc biệt</h3>" +
            "<ul>" +
            "<li><b>Nhập thành (Castling)</b>: Vua chưa đi + Xe chưa đi + không bị chiếu.</li>" +
            "<li><b>Bắt tốt qua đường (En Passant)</b>: Bắt tốt vừa đi 2 ô.</li>" +
            "<li><b>Phong cấp (Promotion)</b>: Tốt đến cuối bàn → chọn quân mới.</li>" +
            "</ul>" +
            "</body></html>";

        JLabel label = new JLabel(text);
        JScrollPane scroll = new JScrollPane(label);
        scroll.setBorder(new EmptyBorder(5, 5, 5, 5));
        return scroll;
    }

    private JScrollPane buildAITab() {
        String text =
            "<html><body style='font-family:Segoe UI;font-size:13px;padding:10px;width:490px'>" +
            "<h2 style='color:#1565C0'>🤖 Chế độ AI</h2>" +

            "<h3>Bật AI</h3>" +
            "<ol>" +
            "<li>Vào menu <b>AI (DSA)</b> trên thanh menu.</li>" +
            "<li>Tích vào <b>AI Mode — Minimax Alpha-Beta</b>.</li>" +
            "<li>Bạn chơi quân <b>Trắng</b>, AI chơi quân <b>Đen</b>.</li>" +
            "</ol>" +

            "<h3>Chọn độ khó</h3>" +
            "<p>Vào <b>AI (DSA) → Search Depth</b>:</p>" +
            "<table border='1' cellpadding='5' cellspacing='0' style='border-collapse:collapse'>" +
            "<tr style='background:#E3F2FD'><th>Depth</th><th>Tốc độ</th><th>Độ mạnh</th></tr>" +
            "<tr><td>1</td><td>Tức thì</td><td>Yếu</td></tr>" +
            "<tr><td>2</td><td>Rất nhanh</td><td>Trung bình</td></tr>" +
            "<tr><td><b>3 (mặc định)</b></td><td>~0.5 giây</td><td>Khá mạnh</td></tr>" +
            "<tr><td>4</td><td>~3–5 giây</td><td>Mạnh</td></tr>" +
            "</table>" +

            "<h3>Thống kê DSA (xem trong console)</h3>" +
            "<p>Sau mỗi nước AI đi, console in:</p>" +
            "<pre style='background:#F5F5F5;padding:8px;border-radius:4px'>" +
            "[DSA Minimax Alpha-Beta]\n" +
            "  depth=3 | nodes=1247 | prunings=389 | time=312ms\n" +
            "</pre>" +
            "<ul>" +
            "<li><b>nodes</b>: số trạng thái bàn cờ đã đánh giá</li>" +
            "<li><b>prunings</b>: số nhánh đã cắt (Alpha-Beta)</li>" +
            "<li><b>time</b>: thời gian tính toán</li>" +
            "</ul>" +
            "</body></html>";

        JLabel label = new JLabel(text);
        JScrollPane scroll = new JScrollPane(label);
        scroll.setBorder(new EmptyBorder(5, 5, 5, 5));
        return scroll;
    }

    private JScrollPane buildDSATab() {
        String text =
            "<html><body style='font-family:Segoe UI;font-size:13px;padding:10px;width:490px'>" +
            "<h2 style='color:#1565C0'>📚 Cấu trúc dữ liệu & Giải thuật</h2>" +

            "<h3>1. Thuật toán Minimax</h3>" +
            "<p>AI duyệt cây trò chơi theo chiều sâu (DFS):</p>" +
            "<ul>" +
            "<li><b>Maximizer</b> (Trắng): chọn nước có điểm cao nhất.</li>" +
            "<li><b>Minimizer</b> (Đen/AI): chọn nước có điểm thấp nhất.</li>" +
            "<li>Hàm đánh giá = tổng giá trị các quân còn trên bàn.</li>" +
            "<li>Độ phức tạp: <b>O(b<sup>d</sup>)</b> (b ≈ 30 nước, d = độ sâu).</li>" +
            "</ul>" +

            "<h3>2. Alpha-Beta Pruning</h3>" +
            "<p>Tối ưu Minimax bằng cách cắt nhánh thừa:</p>" +
            "<ul>" +
            "<li><b>alpha</b>: điểm tốt nhất Trắng đảm bảo được.</li>" +
            "<li><b>beta</b>: điểm tốt nhất Đen đảm bảo được.</li>" +
            "<li>Nếu <b>beta ≤ alpha</b> → cắt nhánh, không duyệt tiếp.</li>" +
            "<li>Trường hợp tốt nhất: <b>O(b<sup>d/2</sup>)</b> — hiệu quả gấp đôi.</li>" +
            "</ul>" +

            "<h3>3. Move Ordering (Sắp xếp nước đi)</h3>" +
            "<ul>" +
            "<li>Ưu tiên các nước <b>ăn quân</b> trước (giá trị cao hơn).</li>" +
            "<li>Dùng <b>List.sort() + Comparator</b>.</li>" +
            "<li>Giúp Alpha-Beta cắt được nhiều nhánh hơn.</li>" +
            "</ul>" +

            "<h3>4. Stack (Ngăn xếp)</h3>" +
            "<ul>" +
            "<li>Lưu lịch sử nước đi (<code>Stack&lt;Move&gt;</code>).</li>" +
            "<li>Lưu quân bị ăn (<code>Stack&lt;Piece&gt;</code>).</li>" +
            "<li>Hỗ trợ tính năng <b>Undo</b> (phím ←).</li>" +
            "</ul>" +

            "<h3>5. Mảng 2 chiều (2D Array)</h3>" +
            "<ul>" +
            "<li>Bàn cờ = <code>int[8][8]</code> lưu giá trị quân.</li>" +
            "<li>AI deep-copy mảng để giả lập mỗi nút trong cây.</li>" +
            "</ul>" +

            "<h3>6. ArrayList & Comparable</h3>" +
            "<ul>" +
            "<li><code>ArrayList&lt;Piece&gt;</code>: danh sách quân cờ động.</li>" +
            "<li><code>Comparable&lt;Move&gt;</code>: so sánh nước đi.</li>" +
            "</ul>" +
            "</body></html>";

        JLabel label = new JLabel(text);
        JScrollPane scroll = new JScrollPane(label);
        scroll.setBorder(new EmptyBorder(5, 5, 5, 5));
        return scroll;
    }
}
