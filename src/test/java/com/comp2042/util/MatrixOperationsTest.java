package com.comp2042.util;

import com.comp2042.data.ClearRow;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests MatrixOperations critical game logic
// Tests collision detection, row clearing, and merge operations
class MatrixOperationsTest {

    // COLLISION DETECTION TESTS

    @Test
    void testIntersectWithEmptyBoard() {
        int[][] board = new int[10][10];
        int[][] brick = {{1, 1}, {1, 1}};
        
        // No collision on empty board
        boolean intersects = MatrixOperations.intersect(board, brick, 4, 4);
        assertFalse(intersects);
    }

    @Test
    void testIntersectWithOccupiedSpace() {
        int[][] board = new int[10][10];
        board[5][5] = 1; // Occupied cell
        int[][] brick = {{1, 1}, {1, 1}};
        
        // Collision with occupied space
        boolean intersects = MatrixOperations.intersect(board, brick, 4, 4);
        assertTrue(intersects);
    }

    @Test
    void testIntersectAtBoundary() {
        int[][] board = new int[10][10];
        int[][] brick = {{1, 1}, {1, 1}};
        
        // Left boundary collision
        assertTrue(MatrixOperations.intersect(board, brick, -1, 0));
        
        // Right boundary collision
        assertTrue(MatrixOperations.intersect(board, brick, 9, 0));
        
        // Bottom boundary collision
        assertTrue(MatrixOperations.intersect(board, brick, 0, 9));
    }

    // ROW CLEARING ALGORITHM TESTS

    @Test
    void testCheckRemovingNoFullRows() {
        int[][] board = new int[10][10];
        board[9][0] = 1;
        board[9][5] = 1; // Incomplete row
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
    }

    @Test
    void testCheckRemovingOneFullRow() {
        int[][] board = new int[10][10];
        
        // Fill bottom row
        for (int j = 0; j < 10; j++) {
            board[9][j] = 1;
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(1, result.getLinesRemoved());
        assertEquals(50, result.getScoreBonus()); // 50 * 1 * 1
    }

    @Test
    void testCheckRemovingTwoFullRows() {
        int[][] board = new int[10][10];
        
        // Fill rows 8 and 9
        for (int j = 0; j < 10; j++) {
            board[8][j] = 1;
            board[9][j] = 1;
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus()); // 50 * 2 * 2
    }

    @Test
    void testCheckRemovingThreeFullRows() {
        int[][] board = new int[10][10];
        
        // Fill rows 7, 8, 9
        for (int j = 0; j < 10; j++) {
            board[7][j] = 1;
            board[8][j] = 1;
            board[9][j] = 1;
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(3, result.getLinesRemoved());
        assertEquals(450, result.getScoreBonus()); // 50 * 3 * 3
    }

    @Test
    void testCheckRemovingFourFullRows() {
        int[][] board = new int[10][10];
        
        // Fill rows 6, 7, 8, 9 (TETRIS!)
        for (int j = 0; j < 10; j++) {
            board[6][j] = 1;
            board[7][j] = 1;
            board[8][j] = 1;
            board[9][j] = 1;
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(4, result.getLinesRemoved());
        assertEquals(800, result.getScoreBonus()); // 50 * 4 * 4 (TETRIS)
    }

    @Test
    void testCheckRemovingRowsDropDown() {
        int[][] board = new int[10][10];
        board[5][3] = 7; // Block above cleared row
        
        // Fill bottom row
        for (int j = 0; j < 10; j++) {
            board[9][j] = 1;
        }
        
        ClearRow result = MatrixOperations.checkRemoving(board);
        
        assertEquals(1, result.getLinesRemoved());
        
        // Block shifts down by 1
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(7, newMatrix[6][3]);
    }

    // MERGE OPERATION TESTS

    @Test
    void testMergeSimpleBrick() {
        int[][] board = new int[10][10];
        int[][] brick = {{1, 1}, {1, 1}};
        
        int[][] result = MatrixOperations.merge(board, brick, 4, 4);
        
        // Brick merged at correct position
        assertEquals(1, result[4][4]);
        assertEquals(1, result[5][5]);
    }

    @Test
    void testMergeDoesNotModifyOriginal() {
        int[][] board = new int[10][10];
        int[][] brick = {{1, 1}, {1, 1}};
        
        int[][] result = MatrixOperations.merge(board, brick, 4, 4);
        
        // Original unchanged
        assertEquals(0, board[4][4]);
        // Result has brick
        assertEquals(1, result[4][4]);
    }

    @Test
    void testMergeIgnoresZeroCells() {
        int[][] board = new int[10][10];
        board[5][5] = 9; // Existing block
        
        // Brick with zeros 
        int[][] brick = {
            {0, 1},
            {1, 0}
        };
        
        int[][] result = MatrixOperations.merge(board, brick, 4, 4);
        
        // Zero cells do not overwrite existing blocks
        assertEquals(9, result[5][5]); 
        assertEquals(1, result[4][5]); 
        assertEquals(1, result[5][4]); 
    }

    // COPY OPERATION TESTS

    @Test
    void testCopyMatrix() {
        int[][] original = {{1, 2, 3}, {4, 5, 6}};
        int[][] copy = MatrixOperations.copy(original);
        
        // Copy has same values
        assertEquals(5, copy[1][1]);
        
        // Modify copy does not affect original
        copy[1][1] = 99;
        assertEquals(5, original[1][1]); // Original unchanged
    }
}
