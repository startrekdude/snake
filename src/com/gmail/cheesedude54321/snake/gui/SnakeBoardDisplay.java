/*
 * Date: 12/8/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.snake.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

import com.gmail.cheesedude54321.snake.game.Direction;
import com.gmail.cheesedude54321.snake.game.Snake;
import com.gmail.cheesedude54321.snake.game.SnakeCell;
import com.gmail.cheesedude54321.snake.game.SnakeMove;

/*
 * Name: Sam Haskins
 * Date: 12/8/2018
 * Inputs: Snake state and game tick count
 * Outputs: Draws the snake board
 * Description:
 *     Displays a Snake board, using a checkerboard green-lightgreen pattern and
 *     red for the apple
 *     Draws the snake tail separately from the board, with support for
 *     smooth animations via tickCount
 */
public final class SnakeBoardDisplay extends JComponent {
    // Color constants
    private final static Color APPLE = new Color(164, 0, 0);
    private final static Color LIGHT_GREEN = new Color(144, 238, 144);
    private final static Color GREEN = new Color(119, 220, 119);
    private final static Color SNAKE = new Color(28, 123, 252);
    
    // The Snake game to display the board of
    public Snake game;
    // The game tick count
    public int tickCount;
    
    /*
     * Name: Sam Haskins
     * Date: 12/8/2018
     * Inputs: A Graphics to draw to, the grid cell size
     * Outputs: Draws the grid background and the apple to the graphics
     * Description:
     *     Draws the grid background in a checkerboard green-lightgreen pattern
     *     Draws the apple in red
     *     Does not draw the snake's tail at all, these squares are treated as background
     */
    private void drawGrid(Graphics g, int sz) {
        // Iterate over every cell of the board
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                // Pick a color - apple is red, otherwise checkerboard
                if (game.board[i][j] == SnakeCell.APPLE)
                    g.setColor(APPLE);
                else if (i % 2 == j % 2)
                    g.setColor(LIGHT_GREEN);
                else
                    g.setColor(GREEN);
                
                // Fill in the cell
                g.fillRect(sz * j, sz * i, sz, sz);
            }
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/8/2018
     * Inputs: A Graphics to draw to and the cell size
     * Outputs: Draws the snake's tail, in blue, to the graphics
     * Description:
     *     Draws the snake's tail using snakeTailShape in blue
     *     Does smooth animations via animating the head and tail of the snake
     *     using tickCount
     */
    private void drawSnakeTail(Graphics g, int sz) {   
        // Set the color
        g.setColor(SNAKE);
        
        // Go over every move in the tail
        for (int i = 0; i < game.tail.size(); i++) {
            SnakeMove move = game.tail.get(i);
            Shape shape;
            
            // Do smooth animations for the first item (the last tail) and the head
            if (i == 0) {
                // For the last tail, we have to reverse where it's coming from
                Direction direction = move.direction;
                Direction nextDirection = game.tail.get(1).direction;
                
                // Reverse the direction
                switch (nextDirection) {
                case RIGHT:
                    nextDirection = Direction.LEFT;
                    break;
                case LEFT:
                    nextDirection = Direction.RIGHT;
                    break;
                case UP:
                    nextDirection = Direction.DOWN;
                    break;
                case DOWN:
                    nextDirection = Direction.UP;
                    break;
                }
                
                // When animating this, always draw it straight
                direction = nextDirection;
                
                shape = snakeTailShape(sz, direction, nextDirection, 20 - tickCount);
            } else if (i == game.tail.size() - 1) {
                // The head. Animate using the next move that would be made
                shape = snakeTailShape(sz, move.direction,
                                       game.getNextMove().direction, tickCount);
            } else {
                // Not one of the snake's extremities; draw the entire tail-piece
                shape = snakeTailShape(sz, move.direction,
                                       game.tail.get(i + 1).direction, 20);
            }
            
            // Okay, we have the snake tail shape. Move it to the desired location
            AffineTransform transform = AffineTransform.getTranslateInstance(sz * move.column,
                                                                             sz * move.row);
            shape = transform.createTransformedShape(shape);
            
            ((Graphics2D) g).fill(shape);
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/8/2018
     * Inputs: The cell size, the direction the tail is coming from, the direction the tail is going to, the percent
     * Outputs: Returns a shape representing a snake tail segment
     * Description:
     *     Creates a snake tail segment, either straight or a corner
     *     Only goes as far as specified by percent - this is used for animations
     */
    private static Shape snakeTailShape(int sz, Direction from, Direction to,
                                        int percent) {
        // The tail shape
        GeneralPath path = new GeneralPath();
        
        // Calculate the margin/padding value
        int ps = sz / 6;
        int psz = sz - ps;
        
        // The starting point of the percent-controlled rectangle
        // Zero, except for corners
        int sp = 0;
        
        if (from != to) {
            sp = sz / 6;
            
            // A corner, draw the corner bits
            if (from == Direction.RIGHT) {
                path.moveTo(0, ps);
                path.lineTo(ps, ps);
                path.lineTo(ps, psz);
                path.lineTo(0, psz);
            } else if (from == Direction.LEFT) {
                path.moveTo(sz, ps);
                path.lineTo(psz, ps);
                path.lineTo(psz, psz);
                path.lineTo(sz, psz);
            } else if (from == Direction.UP) {
                path.moveTo(ps, sz);
                path.lineTo(ps, psz);
                path.lineTo(psz, psz);
                path.lineTo(psz, sz);
            } else if (from == Direction.DOWN) {
                path.moveTo(ps, 0);
                path.lineTo(ps, ps);
                path.lineTo(psz, ps);
                path.lineTo(psz, 0);
            }
        }
        
        // The percent factor, i.e. how far it should go
        double pf = Math.max((percent / 20.0) * sz, sp);
        
        // The tail should take up two-third of the cell, leaving
        // a third of padding evenly distributed on both sides
        if (to == Direction.RIGHT) {
            // Right
            path.moveTo(sp, ps);
            path.lineTo(pf, ps);
            path.lineTo(pf, psz);
            path.lineTo(sp, psz);
        } else if (to == Direction.LEFT) {
            // Left
            path.moveTo(sz - sp, ps);
            path.lineTo(sz - pf, ps);
            path.lineTo(sz - pf, psz);
            path.lineTo(sz - sp, psz);
        } else if (to == Direction.UP) {
            // Up
            path.moveTo(ps, sz - sp);
            path.lineTo(ps, sz - pf);
            path.lineTo(psz, sz - pf);
            path.lineTo(psz, sz - sp);
        } else if (to == Direction.DOWN) {
            // Down
            path.moveTo(ps, sp);
            path.lineTo(ps, pf);
            path.lineTo(psz, pf);
            path.lineTo(psz, sp);
        }
        
        return path;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/8/2018
     * Inputs: A Graphics to draw to
     * Outputs: Paints the current game state onto the graphics
     * Description:
     *     Paints an 18-by-18 grid representing the current
     *     game state. Uses drawGrid and drawSnakeTail
     */
    @Override
    public void paintComponent(Graphics g) {
        // Calculate the grid cell size
        int sz = getWidth() / 18;
        
        // Draw the grid (background and apple)
        drawGrid(g, sz);
        
        // Draw the snake's tail
        drawSnakeTail(g, sz);
    }
}