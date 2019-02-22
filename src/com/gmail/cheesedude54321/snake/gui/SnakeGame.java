/*
 * Date: 12/7/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.snake.gui;

import java.awt.Desktop;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.gmail.cheesedude54321.snake.game.Direction;
import com.gmail.cheesedude54321.snake.game.Snake;
import com.gmail.cheesedude54321.snake.game.SnakeMoveResult;
import com.gmail.cheesedude54321.utility.AboutDialog;
import com.gmail.cheesedude54321.utility.ActionUtils;
import com.gmail.cheesedude54321.utility.AudioUtils;
import com.gmail.cheesedude54321.utility.HighResolutionTimer;
import com.gmail.cheesedude54321.utility.ImageUtils;
import com.gmail.cheesedude54321.utility.KonamiCode;
import com.gmail.cheesedude54321.utility.SmoothLabel;

/*
 * Name: Sam Haskins
 * Date: 12/7/2018
 * Inputs: User input, arrow keys
 * Outputs: Shows the snake game and high scores
 * Description:
 *     The Snake Game's Graphical User Interface. Acts as a container for the board display
 *     and high scores, and also accepts user input.
 *     Advances the game when applicable in realtime.
 *     Uses the Snake class to store state and perform the simulation
 */
public final class SnakeGame extends JFrame implements KeyEventDispatcher {
    // The game state/logic
    private Snake game;
    // The game tick count
    private int tickCount;
    private Boolean continueGame;
    
    // The game clock, set based on difficulty
    private Timer gameClock;
    
    private JPanel root;
    
    // The UI elements of Snake
    private SnakeBoardDisplay boardDisplay;
    private HighScores scores;
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: None
     * Outputs: Makes a new SnakeGame, setting up all of UI elements and event handlers
     * Description:
     *     Creates a new SnakeGame, making all the UI elements and event handlers
     *     This includes a handler for keypresses
     */
    public SnakeGame() {
        // The basic window properties
        setTitle("Snake");
        setSize(900, 675);
        
        // Load an icon (see THIRD-PARTY.txt)
        setIconImage(ImageUtils.load("icon.png"));
        
        // When shown, ask for the difficulty
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                HighResolutionTimer.enable();
                SnakeGame.this.selectDifficulty();
            }
        });
        
        // When the window closes, we have to disable high-resolution timers
        // considering the user's battery life
        addWindowListener(new WindowAdapter() {
           public void windowClosing(WindowEvent e) {
               HighResolutionTimer.disable();
           }
        });
        
        // Add the key event handler to set the snake direction
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        
        // Add the easter egg
        manager.addKeyEventDispatcher(new KonamiCode("https://www.youtube.com/watch?v=oHNKTlz1lps"));
        
        // Create the menu
        JMenuBar menu = new JMenuBar();
        
        // Snake
        JMenu snake = new JMenu("Snake");
        snake.add(ActionUtils.menu("Start new game", this::startNewGame));
        snake.add(ActionUtils.menu("Select difficulty", this::selectDifficulty));
        snake.add(ActionUtils.menu("Exit", this::exit));
        menu.add(snake);
        
        // Help
        JMenu help = new JMenu("Help");
        help.add(ActionUtils.menu("User Guide", this::help));
        help.add(ActionUtils.menu("About", this::about));
        menu.add(help);
        
        setJMenuBar(menu);
        
        // Make the actual game object
        game = new Snake();
        tickCount = 0;
        continueGame = true;
        
        // Create the game clock
        // Started in dispatchKeyEvent
        gameClock = new Timer(1, (e) -> gameTick());
        
        // Create the display, using a custom layout (see doLayout)
        root = new JPanel();
        root.setLayout(null);
        getContentPane().add(root);
        
        // Create the snake board display
        boardDisplay = new SnakeBoardDisplay();
        boardDisplay.game = game;
        root.add(boardDisplay);
        
        // Make the high scores display
        // (the constructor loads previous scores from a file)
        scores = new HighScores();
        root.add(scores);
        
        // Tell Swing to use doCustomLayout as the layout manager; this is relevant on resize
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                SnakeGame.this.doCustomLayout();
            }
        });
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: None
     * Outputs: Advances the animations or the game by one tick
     * Description:
     *     Receives events from the gameClock
     *     Updates animations in the SnakeBoardDisplay every tick
     *     Advances the game every 20 ticks
     */
    private void gameTick() {
        tickCount++;
        
        // Advance the game or merely update animations?
        if (tickCount == 20) {
            // Advance the game
            SnakeMoveResult result = game.advanceGame();
            
            // If we ate an apple, play the sound
            if (result == SnakeMoveResult.ATE_APPLE)
                AudioUtils.play("apple.wav");
            
            continueGame = (result != SnakeMoveResult.DIED);
            if (!continueGame) {
                // Try to add a high score
                if (scores.pushScore(game.tail.size())) {
                    AudioUtils.play("flourish.wav");
                    JOptionPane.showMessageDialog(this,
                            new SmoothLabel(ImageUtils.loadIcon("high_score.png")),
                            "High Score!!!", JOptionPane.PLAIN_MESSAGE);
                } else {
                    AudioUtils.play("death.wav");
                }
                gameClock.stop();
            } else {
                tickCount = 0;
            }
        }
        
        // Update the animations
        boardDisplay.tickCount = tickCount;
        boardDisplay.repaint();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: The desired difficulty, from the user
     * Outputs: When the user selects the difficulty, setDelay will be invoked
     * Description:
     *     Uses a SnakeDifficulty dialog to select the game's difficulty (speed)
     *     When the user makes the selection, control will flow to setDelay
     */
    private void selectDifficulty() {
        // Pause the game if one is in progress - don't want the user
        // to lose while setting the difficulty!
        gameClock.stop();
        
        // Make the dialog and hook up the event handler
        SnakeDifficulty dialog = new SnakeDifficulty(this);
        dialog.onComplete = gameClock::setDelay;
        
        // Display the dialog
        dialog.setVisible(true);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: Calculates the layout for the board display and scores
     * Outputs: Repositions the board display and scores
     * Description:
     *     Calculates a unit size, min(height, width/3)
     *     Makes an (almost) 2-unit-size by 2-unit-size board display
     *     and a unit-size by 2-unit-size scores list
     */
    private void doCustomLayout() {
        // Get the width and height
        int width = root.getWidth();
        int height = root.getHeight();
        
        // Get the unit size
        int sz = Math.min(height / 3, width / 5);
        
        // Figure out where the UI elements start, to be centered horizontally
        int ix = (width - (sz * 4)) / 2;
        
        // Get the board width/height (margin of 20px on each side)
        int boardSz = (sz * 3) - 40;
        
        // Place the board display
        boardDisplay.setBounds(ix, 20, boardSz, boardSz);
        
        // Place the score
        scores.setBounds(ix + boardSz, 20, (sz * 2) - 40, boardSz);
        // After resizing, force a re-layout
        scores.validate();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: A KeyEvent to process
     * Outputs: Sets the direction of the Snake based on the key pressed
     * Description:
     *     For every arrow key press event, sets the Snake's direction
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        // Only take action for key presses
        if (e.getID() != KeyEvent.KEY_PRESSED)
            return false;
        
        if (continueGame) {
            gameClock.start();
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // If the user lost and they press space, start a new game
            // - just a little keyboard shortcut
            startNewGame();
        }
        
        // Set the direction
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            game.setDirection(Direction.LEFT);
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            game.setDirection(Direction.RIGHT);
        else if (e.getKeyCode() == KeyEvent.VK_UP)
            game.setDirection(Direction.UP);
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
            game.setDirection(Direction.DOWN);
        
        return false;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: None
     * Outputs: Starts a new game; the game begins on key press
     * Description:
     *     Resets all state and starts a new game of Snake
     *     The game will begin on the next key press
     */
    private void startNewGame() {
        game = new Snake();
        tickCount = 0;
        continueGame = true;
        
        // Update the board display
        boardDisplay.game = game;
        boardDisplay.tickCount = 0;
        boardDisplay.repaint();
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/17/2018
     * Inputs: None
     * Outputs: Displays the user guide PDF; if this fails, allows the user to download Adobe Reader
     * Description:
     *     Uses Desktop->open to open "Snake-Guide.pdf" from the same directory
     *     If this fails, asks the user if they'd like to download Adobe Reader, and, if so,
     *     opens Adobe's web site
     */
    private void help() {
        Desktop desktop = Desktop.getDesktop();
        
        try {
            // Make sure the user guide exists
            File guide = new File("Snake-Guide.pdf");
            if (!guide.isFile()) {
                JOptionPane.showMessageDialog(this, "User Guide not found;" +
                                              " check your Snake install.",
                                              "File not found",
                                              JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Open the user guide
            desktop.open(guide);
        } catch (Exception e) {
            // The user must not have an application to open PDFs - offer to download adobe reader
            int choice = JOptionPane.showConfirmDialog(this, "Snake's" + 
                    " User Guide is delivered in the industry-standard" +
                    " Portable Document Format (PDF) by Adobe.\n\nWould" +
                    " you like to download Adobe Reader for PDF documents?",
                    "Download PDF reader?", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    /*
                     * Note the interesting, non-standard, choice of the Adobe Reader download URL
                     * This is because Adobe offers 3 download URLs:
                     *     1) The standard, consumer Adobe Reader URL.
                     *        This offers to install McAfee antivirus.
                     *        Yeah, not even once :/
                     *     2) The "enterprise," Adobe Reader URL.
                     *        No "anti"-malware bundled, but a lot of confusing, scary language.
                     *        Not great for a user facing application.
                     *     3) The direct download URL (generated from 2) )
                     *        Does a direct download, but it's the best of
                     *        three bad choices.
                     * 
                     * I chose 3) for the reasons outlined above.
                     */
                    desktop.browse(new URI("https://get.adobe.com/reader/com" +
                            "pletion/?installer=Reader_DC_2019.008.20081_Eng" +
                            "lish_for_Windows&stype=7765&direct=true&standalone=1"));
                } catch (Exception e2) {
                    // A platform without a browser. Interesting. Tell the user
                    JOptionPane.showMessageDialog(this, "No browser found.",
                                                  "An error occurred",
                                                  JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: None
     * Outputs: Shows an about dialog
     * Description:
     *     Invoked by Help -> About. Shows an AboutDialog for Snake
     */
    private void about() {
        // Opening the about dialog pauses the game
        gameClock.stop();
        
        // Create the dialog
        AboutDialog dialog = new AboutDialog(this, ImageUtils.loadIcon("icon.png"),
                "Snake", "Snake is a fun Java Swing game", "Sam Haskins",
                "1.2", "The Snake icon is licensed from Icons8. For more" +
                " details, please see THIRD-PARTY.txt, included in your" +
                " distribution of Snake.");
        // Show the dialog
        dialog.setVisible(true);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/9/2018
     * Inputs: None
     * Outputs: Immediatly exits Snake
     * Description:
     *     Posts a Window Closing message to the queue, causing Snake to exit
     */
    private void exit() {
        // Post an exit message to the queue
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}