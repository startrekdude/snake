/*
 * Date: 12/7/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.snake.gui;

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.gmail.cheesedude54321.utility.ActionUtils;

/*
 * Name: Sam Haskins
 * Date: 12/7/2018
 * Inputs: The user's difficulty choice
 * Outputs: Returns the game tick interval in milliseconds via onComplete
 * Description:
 *     A dialog that lets the user select the difficulty (speed) of their snake game
 *     Possible values are easy, normal, hard, and impossible
 */
public final class SnakeDifficulty extends JDialog {
    // Give the game speed in milliseconds to the consumer
    public Consumer<Integer> onComplete;
    
    private ButtonGroup selection;
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: A Frame to modal for
     * Outputs: Creates a SnakeDifficulty dialog that modals for the frame
     * Description:
     *     Creates a SnakeDifficulty dialog for the specified frame
     *     The user can select easy, normal, hard, or impossible and the game tick
     *     interval (in milliseconds) will be returned
     */
    public SnakeDifficulty(Frame parent) {
        // Make the dialog modal
        super(parent, Dialog.ModalityType.APPLICATION_MODAL);
        
        // Basic properties
        setTitle("Select Snake Difficulty");
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        // The user closes this dialog by clicking select, not using the x button
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Make the vertical layout
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        getContentPane().add(root);
        
        // Add the dialog's title
        JLabel title = new JLabel("Select Difficulty");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        root.add(title);
        
        // Create the choices
        selection = new ButtonGroup();
        
        // Easy
        JRadioButton easy = new JRadioButton("Easy");
        easy.setActionCommand("easy");
        easy.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        selection.add(easy);
        root.add(easy);
        
        // Normal
        JRadioButton normal = new JRadioButton("Normal");
        normal.setActionCommand("normal");
        normal.setSelected(true);
        normal.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        selection.add(normal);
        root.add(normal);
        
        // Hard
        JRadioButton hard = new JRadioButton("Hard");
        hard.setActionCommand("hard");
        hard.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        selection.add(hard);
        root.add(hard);
        
        // Impossible
        JRadioButton impossible = new JRadioButton("Impossible");
        impossible.setActionCommand("impossible");
        impossible.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        selection.add(impossible);
        root.add(impossible);
        
        // Add the Select button
        JButton select = ActionUtils.button("Select", this::complete);
        select.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        root.add(select);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: The user's choice of difficulty
     * Outputs: Invokes onComplete with the appropriate game tick interval
     * Description:
     *     Gives the game tick interval for the selected difficulty level
     *     to onComplete, then closes the dialog
     */
    private void complete() {
         // Only bother if the consumer is interested
        if (onComplete != null) {
            String difficulty = selection.getSelection().getActionCommand();
            
            // Return the correct game tick interval in milliseconds
            // !!! If you change these, change them in the pseudocode too !!!
            if (difficulty.equals("easy")) {
                onComplete.accept(15);
            } else if (difficulty.equals("normal")) {
                onComplete.accept(8);
            } else if (difficulty.equals("hard")) {
                onComplete.accept(5);
            } else {
                onComplete.accept(2);
            }
        }
        
        // Close the dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}