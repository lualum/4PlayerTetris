import java.awt.Color;
/**
* class BLock encapsulates a Block abstraction which can be placed into a Gridworld style grid
* You are expected to comment this class according to the style guide.
* 
* @author Aarav Mann
* @version 7 March 2025
*/
public class Block
{
    private MyBoundedGrid<Block> grid;
    private Location location;
    private Color color;
    /**
    * constructs a blue block, because blue is the greatest color ever!
    */
    public Block()
    {
        color = Color.BLUE;
        grid = null;
        location = null;
    }
    
    public Block(Color c)
    {
        color = c;
        grid = null;
        location = null;
    }
    
    /**
    * Returns the color of the block
    * 
    * @return the color of the block
    */
    public Color getColor()
    {
        return color;
    }
    /**
    * Changes the color of the block
    * 
    * @param newColor the new color of the block
    */
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
    /**
    * Returns the grid the block is in
    * 
    * @return the grid the block is in
    */
    public MyBoundedGrid<Block> getGrid()
    {
        return grid;
    }
    
    /**
    * Returns the location of the block
    * 
    * @return the location of the block
    */
    public Location getLocation()
    {
        return location;
    }
    
    public void setGrid(MyBoundedGrid<Block> gr)
    {
        grid = gr;
    }
    
    public void setLocation(Location loc)
    {
        location = loc;
    }
    /**
    * Removes the block from the grid it is in
    * 
    * @postcondition the block is no longer in any grid
    */
    public void removeSelfFromGrid()
    {
        grid.remove(location);
        grid = null;
        location = null;
    }
    
    /**
    * Inserts the block into a grid
    * 
    * @param gr the grid we are inserting into
    * @param loc the location we are inserting into
    * 
    * @postcondition the block is in gr with location loc
    */
    public void putSelfInGrid(MyBoundedGrid<Block> gr, Location loc)
    {
        grid = gr;
        if (gr.get(loc) != null) gr.get(loc).removeSelfFromGrid();
        location = loc;
        gr.put(location, this);
    }

    /**
    * Moves the block within a grid
    * 
    * @param newLocation the new location of the block
    * 
    * @postcondition the block has location newLocation
    */
    public void moveTo(Location newLocation)
    {
        if (grid != null) {
            grid.remove(location);
            this.putSelfInGrid(grid, newLocation);
        }
    }

    /**
    * Returns the string form of the block
    * 
    * @return returns the string form of the block
    */
    public String toString()
    {
        return "Block[location=" + location + ",color=" + color + "]";
    }
}