/*
 * Date: 12/7/2018
 * Dev: Sam Haskins
 * Version: 1.0
 */
package com.gmail.cheesedude54321.snake.game;

import java.util.LinkedList;

import com.gmail.cheesedude54321.utility.RandomUtils;

/*
 * Name: Sam Haskins
 * Date: 12/7/2018
 * Inputs: Moves and game advances in a Snake simulation
 * Outputs: The results of snake moves
 * Description:
 *     An object representing a Snake game
 *     It keeps track of the snake's direction and tail, and the overall board state
 *     It provides methods to:
 *         - Set the direction, rejecting 180-degree turns
 *         - Advance the game
 *         - Get the next snake move
 *     Uses a LinkedList to represent the Snake
 */
public final class Snake {
    // The board; cells are empty, tail, or apple
    public SnakeCell[][] board;
    
    // Snake uses a LinkedList implementing the Deque interface to store the snake
    // This is a very appropriate data type; the documentation describes it as
    // having a head and a tail, just like a snake
    // Grows from tail towards the head (getLast is the head)
    public LinkedList<SnakeMove> tail;
    
    // The current direction the snake is moving in
    private Direction direction;
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: None
     * Outputs: Creates a new Snake game with an 18x18 board, a size 2 snake, and an apple
     * Description:
     *     Initializes a Snake object with an 18x18 board, a small size 2 snake, and an
     *     apple at position (9, 9)
     */
    public Snake() {
        board = new SnakeCell[18][18];
        
        // Most of the board should be empty
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                board[i][j] = SnakeCell.EMPTY;
            }
        }
        
        // Add the apple and the snake
        board[9][9] = SnakeCell.APPLE;
        board[9][1] = SnakeCell.TAIL;
        board[9][2] = SnakeCell.TAIL;
        
        // Add the Snake's head and tail
        tail = new LinkedList<SnakeMove>();
        tail.add(new SnakeMove(9, 1, Direction.RIGHT));
        tail.add(new SnakeMove(9, 2, Direction.RIGHT));
        
        // Set the direction; snake goes right by default
        direction = Direction.RIGHT;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: The new direction for the Snake
     * Outputs: Sets the Snake's direction, unless it is opposite the snake's head's direction.
     * Description:
     *     Sets the Snake's direction to the provided value,
     *     unless it is opposite the Snake's current direction.
     *     Ex: setDirection(Direction.RIGHT) will do nothing if tail.getLast().direction == Direction.LEFT
     */
    public Boolean setDirection(Direction newDirection) {
        // Get the last direction the snake moved in
        Direction lastDirection = tail.getLast().direction;
        
        // Try to set the direction, if possible
        if (newDirection == Direction.LEFT && lastDirection != Direction.RIGHT) {
            direction = newDirection;
            return true;
        } else if (newDirection == Direction.RIGHT &&
                   lastDirection != Direction.LEFT) {
            direction = newDirection;
            return true;
        } else if (newDirection == Direction.UP && lastDirection != Direction.DOWN) {
            direction = newDirection;
            return true;
        } else if (newDirection == Direction.DOWN && lastDirection != Direction.UP) {
            direction = newDirection;
            return true;
        }
        
        // We couldn't set the direction; return false to indicate this
        return false;
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: None, uses Snake's direction
     * Outputs: Returns whether the game can continue; updates board and tail
     * Description:
     *     Evaluates Snake's rules against the current state and returns whether
     *     the game can continue. The game cannot continue if:
     *         - The move would collide with the Snake's tail
     *         - The move would exit the board
     *         - After moving, all cells all filled with the tail
     */
    public SnakeMoveResult advanceGame() {
        // Get the next move
        SnakeMove move = getNextMove();
        
        // If the move is off the board, the game ends
        if (move.row > 17 || move.row < 0 || move.column > 17 || move.column < 0)
            return SnakeMoveResult.DIED;
        
        // If the move collides with the tail, the game ends
        // UNLESS it's the last tail, in which case we can continue
        // they get a pass, because the last tail will move out of the way
        SnakeCell cell = board[move.row][move.column];
        SnakeMove lastTail = tail.getFirst();
        
        if (cell == SnakeCell.TAIL &&
            (move.row != lastTail.row || move.column != lastTail.column))
            return SnakeMoveResult.DIED;
        
        // Make the move
        tail.add(move);
        board[move.row][move.column] = SnakeCell.TAIL;
        
        // Does the tail fill the entire board? (18x18)
        if (tail.size() == 324)
            return SnakeMoveResult.DIED; // Good job, player.
        
        // If the snake ate an apple, make a new one
        if (cell == SnakeCell.APPLE) {
            placeApple();
            return SnakeMoveResult.ATE_APPLE;
        } else {
            // Simulate growth when eating an apple by not removing a cell
            tail.remove(lastTail);
            board[lastTail.row][lastTail.column] = SnakeCell.EMPTY;
            return SnakeMoveResult.NO_RESULT;
        }
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: None, uses the Snake's tail and direction fields
     * Outputs: Returns the next move that would be performed by the snake
     * Description:
     *     Determines the next SnakeMove based on the position of the head
     *     and the direction of motion; returns this SnakeMove
     */
    public SnakeMove getNextMove() {
        // First, find the current position of the head
        SnakeMove head = tail.getLast();
        
        // Determine new row and column figures, using the current direction
        // Start from the current position
        int row = head.row;
        int column = head.column;
        
        // Move as specified from the direction
        switch (direction) {
        case RIGHT:
            column++;
            break;
        case LEFT:
            column--;
            break;
        case UP:
            row--;
            break;
        case DOWN:
            row++;
            break;
        }
        
        // Create and return the SnakeMove using the direction
        return new SnakeMove(row, column, direction);
    }
    
    /*
     * Name: Sam Haskins
     * Date: 12/7/2018
     * Inputs: None
     * Outputs: Randomly places a SnakeCell->Apple on an empty part of the board
     * Description:
     *     Randomly selects an empty spot of the board, and places an Apple at that spot
     *     Does not return if the snake fills the entire board
     */
    private void placeApple() {
        int row, column;
        
        // Pick an *empty* snake cell, randomly
        // This will go into an infinite loop if the snake fills the entire board,
        // caller should check if tail->size is 18*18
        do {
            row = RandomUtils.range(0, 17);
            column = RandomUtils.range(0, 17);
        } while (board[row][column] != SnakeCell.EMPTY);
        
        // Place the apple
        board[row][column] = SnakeCell.APPLE;
    }
}