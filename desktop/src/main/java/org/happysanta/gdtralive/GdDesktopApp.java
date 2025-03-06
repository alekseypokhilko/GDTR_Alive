package org.happysanta.gdtralive;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GdDesktopApp extends JFrame {
    public static GdDesktopApp instance;
    public static JLayeredPane lpane = new JLayeredPane();
    public static DesktopGdView gameView;
    public static JPanel menu;

    public GdDesktopApp() {
        instance = this;
        setTitle("GDTR Alive!");
        setSize(DesktopGdView.width, DesktopGdView.height);
        setPreferredSize(new Dimension(DesktopGdView.width, DesktopGdView.height));
        setLayout(new BorderLayout());

        menu = new JPanel(new GridLayout(0, 1));
        menu.setBackground(new Color(0,0,0,0));
        menu.setOpaque(false);
        menu.setVisible(true);
        menu.setBounds(50, 50, 300, 700);

        gameView = new DesktopGdView();
        gameView.setBounds(0, 0, DesktopGdView.width, DesktopGdView.height);
        gameView.setPreferredSize(new Dimension(DesktopGdView.width, DesktopGdView.height));

        add(lpane, BorderLayout.CENTER);
        lpane.setPreferredSize(new Dimension(DesktopGdView.width, DesktopGdView.height));
        lpane.add(gameView, 0, 0);
        lpane.add(menu, 1, 0);

        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        GdDesktopApp gd = new GdDesktopApp();
    }
}
