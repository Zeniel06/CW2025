package com.comp2042.util;

import com.comp2042.data.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing matrix operations for the Tetris game.
 * Includes collision detection, matrix manipulation, and row clearing functionality.
 */
public class MatrixOperations {



    private MatrixOperations(){

    }

    /**
     * Checks if a brick would intersect with existing blocks or boundaries at the given position.
     * 
     * @param matrix the game board matrix
     * @param brick the brick shape matrix to check
     * @param x the x-coordinate to check at
     * @param y the y-coordinate to check at
     * @return true if there would be a collision, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        return targetX < 0 || targetY >= matrix.length || targetX >= matrix[targetY].length;
    }

    /**
     * Creates a deep copy of a 2D integer matrix.
     * 
     * @param original the original matrix to copy
     * @return a deep copy of the matrix
     */
    public static int[][] copy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int rowLength = original[i].length;
            copy[i] = new int[rowLength];
            System.arraycopy(original[i], 0, copy[i], 0, rowLength);
        }
        return copy;
    }

    /**
     * Merges a brick into the game board matrix at the specified position.
     * 
     * @param filledFields the current game board matrix
     * @param brick the brick shape matrix to merge
     * @param x the x-coordinate to merge at
     * @param y the y-coordinate to merge at
     * @return a new matrix with the brick merged in
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Checks for completed rows and removes them from the matrix.
     * Calculates the score bonus based on the number of rows cleared.
     * 
     * @param matrix the game board matrix to check
     * @return a ClearRow object containing the number of lines removed, updated matrix, and score bonus
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of 2D integer matrices.
     * 
     * @param list the list of matrices to copy
     * @return a deep copy of the list with all matrices copied
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}

