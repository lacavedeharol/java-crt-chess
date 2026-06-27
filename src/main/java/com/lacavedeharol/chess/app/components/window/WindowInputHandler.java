package com.lacavedeharol.chess.app.components.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Handles window input.
 */
class WindowInputHandler {

    /**
     * Constructor.
     * 
     * @param frame the frame
     */
    WindowInputHandler(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

}
