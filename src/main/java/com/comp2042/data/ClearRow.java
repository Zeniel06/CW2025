package com.comp2042.data;

import com.comp2042.util.MatrixOperations;

/**
 * Data class representing the result of clearing completed rows from the game board.
 * Contains information about the number of lines removed, the updated board matrix, and the score bonus earned.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Constructs a new ClearRow object containing the results of row clearing operation.
     * 
     * @param linesRemoved the number of lines that were cleared
     * @param newMatrix the updated game board matrix after clearing rows
     * @param scoreBonus the bonus score earned from clearing rows
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Gets the number of lines that were cleared.
     * 
     * @return the number of lines removed
     */
    public int getLinesRemoved() {
        return linesRemoved;
    }

    /**
     * Gets a copy of the updated game board matrix after clearing rows.
     * 
     * @return a copy of the new board matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Gets the score bonus earned from clearing rows.
     * 
     * @return the score bonus points
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}

