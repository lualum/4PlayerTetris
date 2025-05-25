import java.util.*;
import java.awt.Color;
/**
 * The MyBoundedGrid is a 2-Dimensional grid containing several objects
 *
 * @author Aarav Mann
 * @version 7 March 2025
 * 
 * @param <E> the type of element inside the grid
 */
public class MyBoundedGrid<E>
{
    // instance variables - replace the example below with your own
    private int rows;
    private int cols;
    private int colsReal;
    private Object[][] grid;
    private boolean[][] marked;
    private Color color;
    /**
     * Constructor for objects of class MyBoundedGrid
     */
    public MyBoundedGrid(int rows, int cols, int cr)
    {
        this.rows = rows;
        this.cols = cols;
        this.colsReal = cr;
        grid = new Object[rows][cr];
        marked = new boolean[rows][cr];
    }
    /**
     * Returns the number of rows in the grid
     * 
     * @return the number of rows in the grid
     */
    public int getNumRows()
    {
        return rows;
    }
    
    public int getColsReal()
    {
        return colsReal;
    }
    public void clearMarked()
    {
       for (int i = 0; i < rows; i++)
       {
           for (int j = 0; j < cols; j++)
           {
               marked[i][j] = false;
           }
       }
    }
    
    public boolean isMarked(Location loc)
    {
        return marked[loc.getRow()][loc.getCol()];
    }
    
    public void mark(Location loc)
    {
        marked[loc.getRow()][loc.getCol()] = true;
    }
    
    /**
     * Returns the number of columns in the grid
     * 
     * @return the number of columns in the grid
     */
    public int getNumCols()
    {
        return cols;
    }
    /**
     * Returns a boolean representing if the location is a valid
     * location in the grid
     * 
     * @param x the location we are checking
     * 
     * @return a boolean representing if the location is a valid
     * location in the grid
     */
    public boolean isValid(Location x)
    {
        int r = x.getRow();
        int c = x.getCol();
        if (r < 0 || r >= rows) return false;
        if (c < 0 || c >= cols) return false;
        return true;
    }
    /**
     * Puts an object into a specific location of the grid
     * 
     * @param loc the location we are inserting into
     * @param obj the object we are putting in the grid
     * 
     * @return the object that was previously in loc
     */
    public Block put(Location loc, Block obj)
    {
        int r = loc.getRow();
        int c = loc.getCol();
        Block save = (Block) grid[r][c];
        grid[r][c] = obj;
        return save;
    }
    /**
     * Removes an object from a specific location of the grid
     * 
     * @param loc the location we are removing from
     * 
     * @return the object we are removing from the grid
     */
    public Block remove(Location loc)
    {
        int r = loc.getRow();
        int c = loc.getCol();
        Block save = (Block) grid[r][c];
        grid[r][c] = null;
        return save;
    }
    /**
     * Gets an object from a specific location of the grid
     * 
     * @param loc the location we are looking at
     * 
     * @return the object in the specific location
     */
    public Block get(Location loc)
    {
        int r = loc.getRow();
        int c = loc.getCol(); 
        return (Block) grid[r][c];
    }
    /**
     * Returns an arraylist containing all locations not equal
     * to null
     * 
     * @return an arraylist containing all locations not equal
     * to null
     * 
     */
    public ArrayList<Location> getOccupiedLocations()
    {
        ArrayList<Location> ans = new ArrayList<Location> ();
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (grid[i][j] != null)
                {
                    ans.add(new Location(i, j));
                }
            }
        }
        return ans;
    }
    
    @SuppressWarnings("unchecked")
    public void putInHold(int type)
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = cols; j < colsReal; j++) grid[i][j] = null;
        }
        Location[] locs = new Location[4];
        int center = (int)((cols + colsReal+1)/2-1);
        locs[0] = new Location(0, center);
        locs[1] = new Location(1, center);


        final Color[] colorList = {
            new Color(117, 203, 174), // I
            new Color(181, 89, 200),  // T
            new Color(204, 197, 100), // O
            new Color(195, 132, 90),  // L
            new Color(99, 83, 199),   // J
            new Color(146, 204, 99),  // S
            new Color(191, 90, 92)    // Z
        };
        color = colorList[type];
        if (type == 0)
        {
            locs[0] = new Location(1, center - 1);
            locs[1] = new Location(1, center);
            locs[2] = new Location(1, center + 1);
            locs[3] = new Location(1, center + 2);
        }
        if (type == 1)
        {
            locs[0] = new Location(0, center);
            locs[1] = new Location(1, center);
            locs[2] = new Location(1, center - 1);
            locs[3] = new Location(1, center + 1);
        }
        if (type == 2)
        {
            locs[0] = new Location(0, center);
            locs[1] = new Location(1, center);
            locs[2] = new Location(0, center + 1);
            locs[3] = new Location(1, center + 1);
        }
        if (type == 3)
        {
            locs[0] = new Location(1, center - 1);
            locs[1] = new Location(1, center);
            locs[2] = new Location(1, center + 1);
            locs[3] = new Location(0, center + 1);
        }
        if (type == 4)
        {
            locs[0] = new Location(1, center - 1);
            locs[1] = new Location(1, center);
            locs[2] = new Location(1, center + 1);
            locs[3] = new Location(0, center - 1);
        }
        if (type == 5)
        {
            locs[0] = new Location(1, center - 1);
            locs[1] = new Location(1, center);
            locs[2] = new Location(0, center);
            locs[3] = new Location(0, center + 1);
        }
        if (type == 6)
        {
            locs[0] = new Location(0, center - 1);
            locs[1] = new Location(1, center);
            locs[2] = new Location(0, center);
            locs[3] = new Location(1, center + 1);
        }
        for (Location loc: locs)
        {
            int row = loc.getRow();
            int col = loc.getCol();
            (new Block(color)).putSelfInGrid((MyBoundedGrid<Block>) this, new Location(row, col));
        }
    }
    
    public boolean empty(Location loc)
    {
        return (grid[loc.getRow()][loc.getCol()] == null);
    }
}
