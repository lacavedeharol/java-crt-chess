package com.lacavedeharol.chess;

import javax.swing.SwingUtilities;

import com.lacavedeharol.chess.app.components.window.Window;

/**
 * Main class for the chess application.
 */
public class Main {

    /**
     * Private constructor to prevent instantiation.
     */
    private Main() {
    }

    /**
     * Main method.
     * 
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Window::new);
    }
}