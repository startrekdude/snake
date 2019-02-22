/*
 * Date: 12/7/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.utility;

import java.awt.Desktop;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Name: Sam Haskins
 * Date: 12/7/2018
 * Inputs: Key press events
 * Outputs: Opens a URL when the user enters the Konami Code
 *     Provides a KeyEventDispatcher that opens a URL in the user's web browser
 *     when they type the Konami Code; that is, up, up, down, down, left, right,
 *     left, right, b, a
 *     If opening the links fails, fails silently (no message)
 */
public final class KonamiCode implements KeyEventDispatcher {
    // The complete Konami Code
    private static final List<Integer> codeComplete = Arrays.asList(new Integer[] {
            KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN,
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT,
            KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A
    });
    
    private String url;
    private List<Integer> code;
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: A URL to open when the konami code is typed
     * Outputs: Creates a KonamiCode that opens the provided URL
     * Description:
     *     Creates a new KonamiCode with url equal to url
     */
    public KonamiCode(String url) {
        // Check for null
        if (url == null)
            throw new NullPointerException("url != null");
        
        this.url = url;
        code = new ArrayList<Integer>();
    }
    
    /*
     * Method dispatchKeyEvent
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: The key event to operate on
     * Outputs: Opens url if the key is the last key of the Konami Code, otherwise does nothing
     * Description:
     *     Adds the new key press to code, clears it if not expected keypress, opens
     *     URL when code is complete
     */
    public boolean dispatchKeyEvent(KeyEvent e) {
        // Only operate on key presses
        if (e.getID() == KeyEvent.KEY_PRESSED || e.getKeyCode() == 0)
            return false;
        
        // Add the key press to the list
        code.add(e.getKeyCode());
        
        // Make sure code matches so far; if not, clear it
        for (int i = 0; i < code.size(); i++) {
            if (!code.get(i).equals(codeComplete.get(i))) {
                code.clear();
                break;
            }
        }
        
        // The Code is complete
        if (code.size() == 10) {
            code.clear();
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                // Do nothing; this isn't an important feature
            }
        }
        
        return false;
    }
}