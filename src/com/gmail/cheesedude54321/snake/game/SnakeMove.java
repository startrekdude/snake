/*
 * Date: 12/7/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.snake.game;

/*
 * Name: Sam Haskins
 * Date: 12/7/2018
 * Inputs: Represents a snake's move with a row, column, and direction
 * Outputs: Stores snake move data
 * Description:
 *     A simple structure representing the snake's move
 *     The snake's tail is a queue of SnakeMove structures
 */
public final class SnakeMove {
    // The position the move was made
    public int row;
    public int column;
    // The direction of the move
    public Direction direction;
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: The row, column, and direction of the SnakeMove
     * Outputs: Creates a new SnakeMove with the provided row, column, and direction
     * Description:
     *     Creates a new SnakeMove, filling in all the fields with
     *     information provided by the consumer
     */
    public SnakeMove(int row, int column, Direction direction) {
        // Set the SnakeMove's fields
        this.row = row;
        this.column = column;
        this.direction = direction;
    }
}