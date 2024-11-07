package com;

import javax.swing.*;

public class Main extends JPanel {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(true);
        window.setTitle("Raycaster");

        RaycasterPanel panel = new RaycasterPanel();
        window.add(panel);

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
