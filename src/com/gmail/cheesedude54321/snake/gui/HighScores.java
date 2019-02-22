/*
 * Date: 12/9/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.snake.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.gmail.cheesedude54321.utility.Sorts;
import com.gmail.cheesedude54321.utility.ThrowableUtils;

/*
 * Name: Sam Haskins
 * Date: 12/9/2018
 * Inputs: High scores, from the game and a file
 * Outputs: Displays the high scores list and saves it
 * Description:
 *     The high scores component
 *     Responsible for receiving, displaying, saving, and loading high scores
 *     Uses a JEditorPane to display the scores list, but wraps it in a JScrollPane
 *     to allow scrolling
 */
public final class HighScores extends JPanel {
    // The format String for five high scores. Used during display
    private static final String FORMAT_STRING =
            "<h2>! High Scores !</h2>" +
            "<ol><li style=\"color: #DAA520\">&nbsp;%d</li>" +
            "<li>&nbsp;%d</li><li>&nbsp;%d</li>" + 
            "<li>&nbsp;%d</li><li>&nbsp;%d</li></ol>";
    
    private JEditorPane display;
    
    // The scores list
    private List<Integer> scores;
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: Indirectly reads from the .snakescores file
     * Outputs: Creates a new HighScores, displaying the current high scores
     * Description:
     *     Creates a new JEditorPane and adds it to this
     *     Loads the existing scores using the load methods
     *     Pushes 5 zeros to fix a corrupt database and then saves
     *     Displays the scores
     */
    public HighScores() {
        // Fill all available space
        setLayout(new BorderLayout());
        
        // Load the high scores font
        // if this fails, fallback to sans-serif
        Font font;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("BreeSerif-Regular.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(48f);
        } catch (Throwable e) {
            font = new Font("Sans Serif", Font.PLAIN, 48);
        }
        
        // First, make the HTML displayer
        display = new JEditorPane() {
            protected void paintComponent (Graphics g) {
                // Force antialiasing on because it makes everything look better
                // Why Java doesn't do this by default is beyond me
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                super.paintComponent(g);
            }
        };
        display.setEditable(false);
        display.setContentType("text/html");
        display.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        display.setFont(font);
        
        add(new JScrollPane(display));
        
        scores = new ArrayList<Integer>();
        
        // First, load any existing scores
        load();
        
        /*
         * Here, we push five scores of zero.
         * This has the following effects:
         *     - Sorts the score list
         *     - Possibly truncates the score list to 5
         *     - Ensures that at least 5 items are in the scores list
         *     - (last invocation) Saves the (now valid) scores list
         */
        pushScore(0, false);
        pushScore(0, false);
        pushScore(0, false);
        pushScore(0, false);
        pushScore(0);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: Adds a snake score to the HighScores, always saving
     * Outputs: Whether the new score is a top-5 high score
     * Description:
     *     Uses the other pushScore overload to add the score and save
     */
    public Boolean pushScore(int score) {
        return pushScore(score, true);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: The score to potentially add and whether to save or not
     * Outputs: Maybe adds the score and maybe saves. Returns whether the list was updated
     * Description:
     *     Adds the score to the list, sorts the list, picks the first five, and saves
     *     Returns whether an item was added to the list
     */
    private Boolean pushScore(int score, Boolean save) {
        // Save the old scores so we can see if they changed
        List<Integer> oldScores = new ArrayList<Integer>(scores);
        
        // Add the new score
        scores.add(score);
        
        // Sort the scores
        Comparator<Integer> comparator = (i1, i2) -> i2 - i1;
        Sorts.quick(scores, comparator, Sorts.FORWARDS);
        
        // Pick the first five
        if (scores.size() > 5)
            scores = new ArrayList<Integer>(scores.subList(0, 5));
        
        // Possibly save
        if (save)
            save();
        
        // Update the GUI, but only if five scores have been established
        // This will not be the case until the constructor completes
        if (scores.size() == 5) {
            display.setText(String.format(FORMAT_STRING, scores.get(0),
                                          scores.get(1), scores.get(2),
                                          scores.get(3), scores.get(4)));
        }
        
        // Return whether the list changed
        // Snake uses this to play a different sound effect
        return !oldScores.equals(scores);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: Uses the data in scores
     * Outputs: Writes the scores to .snakescores
     * Description:
     *     Saves the user's high scores into .snakescores
     *     On failure, displays a message
     */
    private void save() {
        // The file will be ".snakescores" in the user's home directory
        // (should be in AppData, but that's not cross-platform :( )
        String path = Paths.get(System.getProperty("user.home"),
                                ".snakescores").toString();
        
        // If the file exists, delete it
        new File(path).delete();
        
        // Try to write the file. If this fails, tell the user
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            scores.forEach(writer::println);
        } catch (Throwable e) {
            String message = "An error was encountered saving the high scores." +
                    " The high scores were not saved.\n\nThe error was: " +
                    ThrowableUtils.getStackTrace(e);
            JOptionPane.showMessageDialog(this, message, "An error occurred",
                                          JOptionPane.ERROR_MESSAGE);
        }
        
        // For windows systems, set the file as hidden
        // The name starting with a "." will do this for linux and macos
        // this is expected to fail on non-windows systems, do so silently
        try {
            Files.setAttribute(Paths.get(path), "dos:hidden", Boolean.TRUE,
                               LinkOption.NOFOLLOW_LINKS);
        } catch (Throwable e) {
            // Yeah, this'll fail on macOS and Linux
            // That's fine. No worries.
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: Reads the .snakescores file into scores
     * Outputs: Modifies scores
     * Description:
     *     Reads the entire snakescores file into scores
     *     On failure, does nothing (the constructor will load zeros)
     */
    private void load() {
        // The file will be ".snakescores" in the user's home directory
        // (should be in AppData, but that's not cross-platform :( )
        String path = Paths.get(System.getProperty("user.home"),
                                ".snakescores").toString();
        
        // Try to load the file. If this fails for any reason, we don't care...
        // ...because the constructor will push zeros
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Try to parse the line. If it fails, move on
                try {
                    scores.add(Integer.parseInt(line));
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            // Oh well, we can't load scores. The constructor pushes five zeros to reset
        }
    }
}