package org.happysanta.gdtralive;

import javax.swing.JFrame;

public class GdDesktopApp extends JFrame {

    public GdDesktopApp() {
        setTitle("Gravity Defied: Alive!");
        setSize(DesktopGdView.width, DesktopGdView.height);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        GdDesktopApp gd = new GdDesktopApp();
        DesktopGdView view = new DesktopGdView();
        gd.add(view);
        gd.pack();
        view.requestFocus();
        gd.setVisible(true);
    }
}
