package com.chessgame.Settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
// ActionListener không cần import riêng nếu đã có java.awt.* hoặc sử dụng lambda

public class ColorSettingsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JPanel panelToRepaint;
    private JButton lightColorButton, darkColorButton;
    private JPanel lightColorPreview, darkColorPreview;

    private Color currentLightColor;
    private Color currentDarkColor;

    public ColorSettingsDialog(JFrame owner, JPanel gamePanel) {
        super(owner, "Board Color Settings", true);
        this.panelToRepaint = gamePanel;

        this.currentLightColor = GameSettings.lightSquareColor;
        this.currentDarkColor = GameSettings.darkSquareColor;

        initComponents();
        
        setSize(350, 200);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel colorSelectionPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        colorSelectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        colorSelectionPanel.add(new JLabel("Light Squares:"));
        lightColorPreview = new JPanel();
        lightColorPreview.setBackground(currentLightColor);
        lightColorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorSelectionPanel.add(lightColorPreview);

        lightColorButton = new JButton("Choose...");
        lightColorButton.addActionListener(e -> chooseColor(true));
        colorSelectionPanel.add(lightColorButton);

        colorSelectionPanel.add(new JLabel("Dark Squares:"));
        darkColorPreview = new JPanel();
        darkColorPreview.setBackground(currentDarkColor);
        darkColorPreview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorSelectionPanel.add(darkColorPreview);
        
        darkColorButton = new JButton("Choose...");
        darkColorButton.addActionListener(e -> chooseColor(false));
        colorSelectionPanel.add(darkColorButton);
        
        add(colorSelectionPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> applyChanges());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chooseColor(boolean isLight) {
        Color initialColor = isLight ? currentLightColor : currentDarkColor;
        Color chosenColor = JColorChooser.showDialog(this, 
                                                     isLight ? "Choose Light Square Color" : "Choose Dark Square Color", 
                                                     initialColor);
        if (chosenColor != null) {
            if (isLight) {
                currentLightColor = chosenColor;
                lightColorPreview.setBackground(currentLightColor);
            } else {
                currentDarkColor = chosenColor;
                darkColorPreview.setBackground(currentDarkColor);
            }
        }
    }

    private void applyChanges() {
        GameSettings.lightSquareColor = currentLightColor;
        GameSettings.darkSquareColor = currentDarkColor;
        if (panelToRepaint != null) {
            panelToRepaint.repaint();
        }
        dispose();
    }
}