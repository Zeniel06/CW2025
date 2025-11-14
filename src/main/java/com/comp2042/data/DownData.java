package com.comp2042.data;

/**
 * Data class encapsulating the results of a downward brick movement.
 * Contains information about cleared rows and updated view data.
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    /**
     * Constructs a new DownData object with cleared row information and view data.
     * 
     * @param clearRow the cleared row information, or null if no rows were cleared or game is over
     * @param viewData the updated view data after the downward movement
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the cleared row information.
     * 
     * @return the cleared row data, or null if no rows were cleared or game is over
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view data.
     * 
     * @return the view data after the downward movement
     */
    public ViewData getViewData() {
        return viewData;
    }
}

