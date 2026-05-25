package com.chessgame.Frame;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.chessgame.AI.ChessAI;
import com.chessgame.Board.Board;
import com.chessgame.Game.Game;
import com.chessgame.Pieces.Piece;
import com.chessgame.Settings.ColorSettingsDialog;

public class Frame extends JFrame {
    
    private static final long serialVersionUID = -4442947819954124379L;
    private Panel gamePanel;

    public Frame() {
        this.gamePanel = new Panel(); 
        this.setContentPane(this.gamePanel);
        this.setTitle("Chess DSA — Minimax AI (Player vs Player)");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true); 
        
        setupMenuBar();

        // Kích thước ban đầu sẽ được Panel tự quyết định thông qua getPreferredSize()
        // và pack() sẽ điều chỉnh Frame theo đó.
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.gamePanel.requestFocusInWindow();

        new TutorialDialog(this).setVisible(true);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ---- Menu: Settings ----
        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem changeColorsItem = new JMenuItem("Change Board Colors...");
        changeColorsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ColorSettingsDialog dialog = new ColorSettingsDialog(Frame.this, gamePanel);
                dialog.setVisible(true);
            }
        });
        settingsMenu.add(changeColorsItem);

        // ---- Menu: AI (DSA) ----
        JMenu aiMenu = new JMenu("AI (DSA)");

        // Toggle AI Mode
        JCheckBoxMenuItem aiModeItem = new JCheckBoxMenuItem("AI Mode — Minimax Alpha-Beta (Black plays AI)");
        aiModeItem.setSelected(false);
        aiModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game.aiMode = aiModeItem.isSelected();
                String mode = Game.aiMode ? "Player vs AI (Minimax)" : "Player vs Player";
                setTitle("Chess DSA — " + mode);
                // Nếu bật AI và đang đến lượt Black, trigger AI ngay
                if (Game.aiMode && !Game.isWhitePlayerTurn()) {
                    gamePanel.triggerAI();
                }
            }
        });
        aiMenu.add(aiModeItem);

        aiMenu.addSeparator();

        // Chọn độ sâu tìm kiếm (Search Depth)
        JMenu depthMenu = new JMenu("Search Depth (Minimax)");
        for (int d = 1; d <= 4; d++) {
            final int depth = d;
            String label = "Depth " + d + (d == 3 ? " (Default)" : d == 1 ? " (Fast)" : d == 4 ? " (Slow)" : "");
            JMenuItem depthItem = new JMenuItem(label);
            depthItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ChessAI.setSearchDepth(depth);
                    JOptionPane.showMessageDialog(Frame.this,
                        "Minimax search depth set to " + depth + "\n" +
                        "Độ phức tạp: O(b^" + depth + ") thuần, O(b^" + (depth/2.0) + ") với Alpha-Beta",
                        "DSA — Search Depth", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            depthMenu.add(depthItem);
        }
        aiMenu.add(depthMenu);

        // ---- Menu: Help ----
        JMenu helpMenu = new JMenu("Help");
        JMenuItem tutorialItem = new JMenuItem("Tutorial / Hướng dẫn...");
        tutorialItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TutorialDialog(Frame.this).setVisible(true);
            }
        });
        helpMenu.add(tutorialItem);

        menuBar.add(settingsMenu);
        menuBar.add(aiMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }
}