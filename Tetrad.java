import java.util.*;
import java.awt.Color;
import java.util.concurrent.Semaphore;
/**
 * Write a description of class Tetrad here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Tetrad
{
    private Color color;
    private MyBoundedGrid<Block> grid;
    private ArrayList<Block> blocks;
    private int rotInd;
    private Semaphore lock;
    public Tetrad(int id, MyBoundedGrid<Block> g, int type)
    {
        lock = new Semaphore(1, true);
        blocks = new ArrayList<Block> ();
        grid = g;
        int center = (grid.getNumCols()+1)/2-1;
        Location[] locs = new Location[4];

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
        rotInd = 1;
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
            if (!grid.empty(loc))
            {
                Tetris.alive[id] = false;
                Tetris.display[id].showBlocks();
                for (int i = 0; i < Tetris.PLAYERS; i++) {
                    if (Tetris.alive[i]) {
                        Tetris.display[i].showBlocks();
                    }
                }
            }
        }
        this.addToLocations(grid, locs); 
        this.checkDown();
    }
    
    public Location[] getLocations()
    {
        Location[] ans = new Location[4];
        for (int i = 0; i < 4; i++)
        { 
            ans[i] = blocks.get(i).getLocation();
        }
        return ans;
    }
    private void addToLocations(MyBoundedGrid<Block> grid, Location[] locs)
    {
        for (Location loc : locs)
        {
            Block b = new Block(color);
            blocks.add(b);
            b.putSelfInGrid(grid, loc);
        }
    }
    
     public void removeSelfFromGrid()
    {
        for (Block b: blocks)
        {
            b.removeSelfFromGrid();
        }
    }
    
    private Location[] removeBlocks()
    {
        Location[] ret = new Location[4];
        for (int i = 0; i < 4; i++)
        {
            ret[i] = blocks.get(0).getLocation();
            blocks.get(0).removeSelfFromGrid();
            blocks.remove(0);
        }
        return ret;
    }
    
    public int checkDown()
    {
        grid.clearMarked();
        int count = 0;
        while (this.translate(1, 0))
        {
            count++;
        }
        for (int i = 0; i < count; i++)
        {
            this.translate(-1, 0);
        }
        for (Block b: blocks)
        {
            Location loc = b.getLocation();
            grid.mark(new Location(loc.getRow() + count, loc.getCol()));
        }
        return count;
    }
    
    public boolean translate(int deltaRow, int deltaCol)
    {
        try
        {
            lock.acquire();
            Location[] prev = this.removeBlocks();
            boolean works = true;
            for (int i = 0; i < 4; i++)
            {
                prev[i] = new Location(prev[i].getRow() + deltaRow, prev[i].getCol() + deltaCol);
                if (!grid.isValid(prev[i]) || !grid.empty(prev[i])) works = false;
            }
            if (!works)
            {
                for (int i = 0; i < 4; i++)
                {
                    prev[i] = new Location(prev[i].getRow() - deltaRow, prev[i].getCol() - deltaCol);
                }
                this.addToLocations(grid, prev);
                return false;
            }
            this.addToLocations(grid, prev);
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
        finally
        {
            lock.release();
        }
        
    }
    
    public boolean rotate()
    {   
        try
        {
            lock.acquire();
            if (color == new Color(204, 197, 100)) return false;
            int crow = blocks.get(rotInd).getLocation().getRow();
            int ccol = blocks.get(rotInd).getLocation().getCol();
            Location[] prev = this.removeBlocks();
            boolean works = true;
            for (int i = 0; i < 4; i++)
            {
                prev[i] = new Location(crow - ccol + prev[i].getCol(), crow + ccol - prev[i].getRow());
                if (!grid.isValid(prev[i]) || !grid.empty(prev[i])) works = false;
            }
            if (!works)
            {
                for (int turns = 0; turns < 3; turns++)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        prev[i] = new Location(crow - ccol + prev[i].getCol(), crow + ccol - prev[i].getRow());
                    }
                }
                this.addToLocations(grid, prev);
                return false;
            }
            this.addToLocations(grid, prev);
            return true;
        }
        catch (InterruptedException e)
        {
            return false;
        }
        finally
        {
            lock.release();
        }
    }
    
}
