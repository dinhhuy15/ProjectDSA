package com.chessgame.Settings;

import java.awt.Color;

public class GameSettings {
    // Màu mặc định ban đầu
    public static Color lightSquareColor = new Color(238, 238, 210);
    public static Color darkSquareColor = new Color(118, 150, 86);

    // Ngăn không cho tạo instance của lớp này (chỉ dùng static)
    private GameSettings() {} 
}