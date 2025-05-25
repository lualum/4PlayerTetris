import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.awt.Color;
import java.awt.event.KeyEvent;
/**
 * Write a concise summary of class Tetris here.
 * 
 * @author  (your name) 
 *   With assistance from 
 *
 * @version (a version number or a date) 
 */
public class Tetris implements ArrowListener
{
    public static final int PLAYERS = 3;
    private static final int[][] keyDict = {
        {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_Q, KeyEvent.VK_E}, 
        //{KeyEvent.VK_T, KeyEvent.VK_F, KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_R, KeyEvent.VK_Y}, 
        {KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L, KeyEvent.VK_U, KeyEvent.VK_O}, 
        {KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_SLASH, KeyEvent.VK_SHIFT}
    };

    private int GRAVITY = 200;
    private int G[];
    private static final int HEIGHT = 20;
    private static final int WIDTH = 10;
    private MyBoundedGrid<Block>[] grid;
    
    public static BlockDisplay[] display;
    
    private Tetrad[] curr;
    private double[] odds;
    private int prev[];
    private boolean move;
    private boolean[] skip;
    public static int[] hold;
    private int[] type;
    private boolean[] swapped;
    private int[] q;
    
    private Semaphore startingSemaphore = new Semaphore(0);

    public static boolean[] alive;
    @SuppressWarnings("unchecked")
    public Tetris()
    {
        q = new int[PLAYERS];
        hold = new int[PLAYERS];
        type = new int[PLAYERS];
        swapped = new boolean[PLAYERS];
        skip = new boolean[PLAYERS];
        odds = new double[PLAYERS];
        G = new int[PLAYERS];
        prev = new int[PLAYERS];
        curr = new Tetrad[PLAYERS];
        grid = (MyBoundedGrid<Block>[]) new MyBoundedGrid[PLAYERS];
        display = new BlockDisplay[PLAYERS];
        alive = new boolean[PLAYERS];

        for (int i = 0; i < PLAYERS; i++) {
            alive[i] = true;
            q[i] = 0;
            hold[i] = -1;
            type[i] = (int) (Math.random() * 7);
            swapped[i] = false;
            skip[i] = false;
            odds[i] = 0;
            G[i] = GRAVITY;
            grid[i] = new MyBoundedGrid<Block>(HEIGHT, WIDTH, WIDTH + 5);
            curr[i] = new Tetrad(i, grid[i], type[i]);
            curr[i].checkDown();
            display[i] = new BlockDisplay(i, grid[i]);
            display[i].setArrowListener(this);
            display[i].setTitle("Player " + (i + 1));
            display[i].showBlocks();
        }
        move = false;

        try {
            startingSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        move = true;
        while (true)
        {
            for (int i = 0; i < PLAYERS; i++) {
                move(i);
            }
        }
    }
    private void move(int i)
    {
        if (!alive[i]) return;
        /*if (hold[i] != -1)
            grid[i].putInHold(hold[i]);*/
        curr[i].checkDown();
        display[i].showBlocks();
        try
        {
            if (!skip[i]) Thread.sleep(G[i]);
        }
        catch (InterruptedException e) {}
        if (!curr[i].translate(1, 0))
        {
            try
            {
                if (!skip[i]) Thread.sleep(200);
                skip[i] = false;
            }
            catch (InterruptedException e) {}
            if (curr[i].translate(1, 0))
            {
                return;
            }
            swapped[i] = false;
            this.clearCompletedRows(i, grid[i]);
            type[i] = (int)(Math.random() * 7);
            curr[i] = new Tetrad(i, grid[i], type[i]);
            this.addRows(i, grid[i], q[i]);
            q[i] = 0;
        }
    }
    private void addRows(int player, MyBoundedGrid<Block> g, int count)
    {
        int add = count;
        double rgen = Math.random();
        int ind = (int)(Math.random() * grid[player].getNumCols());
        if (rgen < odds[player])
        {
            ind = prev[player];
            odds[player] = 0.1;
        }
        else
        {
            odds[player] = 0.7;
        }
        for (int i = 0; i < add; i++)
        {
            this.addRow(grid[player], ind);
        }
    }
    private void addRow(MyBoundedGrid<Block> g, int ind)
    {
        for (int i = 0; i < g.getNumRows(); i++)
        {
            for (int j = 0; j < g.getNumCols(); j++) {
                Location loc = new Location(i, j);
                Location dest = new Location(i - 1, j);
                if (!g.empty(loc) && g.isValid(dest)) {
                    g.get(loc).moveTo(dest);
                }
            }
        }
        for (int i = 0; i < g.getNumCols(); i++) {
            if (i != ind) {
                (new Block(new Color(103, 94, 105))).putSelfInGrid(g, new Location(g.getNumRows()-1, i));
            }
        }
    }
    private boolean isCompletedRow(MyBoundedGrid<Block> g, int row)
    {
        for (int i = 0; i < g.getNumCols(); i++)
        {
            if (g.empty(new Location(row, i))) return false;
        }
        return true;
    }
    
    private void clearRow(MyBoundedGrid<Block> g, int row)
    {
        for (int i = 0; i < g.getNumCols(); i++)
        {
            Location loc = new Location(row, i);
            g.get(loc).setGrid(g);
            g.get(loc).setLocation(loc);
            g.get(loc).removeSelfFromGrid();
        }
        for (int i = row - 1; i >= 0; i--)
        {
            for (int j = 0; j < g.getNumCols(); j++) {
                Location loc = new Location(i, j);
                if (!g.empty(loc)) {
                    g.get(loc).moveTo(new Location(i + 1, j));
                }
            }
        }
    }
    
    private int nonRand(int player) {
        while (true) {
            int rand = (int) (Math.random() * PLAYERS);
            if (rand != player) return rand;
        }
    }

    private int clearCompletedRows(int player, MyBoundedGrid<Block> g)
    {
        int count = 0;
        for (int i = 0; i < g.getNumRows(); i++) {
            if (this.isCompletedRow(g, i)) {
                this.clearRow(g, i);
                count++;
            }
        }
        if (count > 1) {
            // broken prob
            if (g == grid[player]) {
                int add = 0;
                if (count == 1)
                    add = 0;
                if (count == 2)
                    add = 1;
                if (count == 3)
                    add = 2;
                if (count == 4)
                    add = 4;
                q[nonRand(player)] += add;

            }
        }
        return count;
    }
    
    public void keyPressed(int code) {
        for (int i = 0; i < PLAYERS; i++) {
            if (!alive[i]) continue;
            if (keyDict[i][0] == code)
                upPressed(i);
            if (keyDict[i][1] == code)
                leftPressed(i);
            if (keyDict[i][2] == code)
                downPressed(i);
            if (keyDict[i][3] == code)
                rightPressed(i);
            if (keyDict[i][4] == code)
                spacePressed(i);
            if (keyDict[i][5] == code)
                ePressed(i);
            if (code == KeyEvent.VK_SPACE)
                startingSemaphore.release(); 
        }
    }
    public void upPressed(int player)
    {
        if (move)
        {
            curr[player].rotate();
            curr[player].checkDown();
            display[player].showBlocks();
        }
    }
    
    public void downPressed(int player)
    {
        if (move)
        {
            curr[player].translate(1, 0);
            curr[player].checkDown();
            display[player].showBlocks();
        }
        
    }
    
    public void leftPressed(int player)
    {
        if (move)
        {
            curr[player].translate(0, -1);
            curr[player].checkDown();
            display[player].showBlocks();
        }
    }
    
    public void rightPressed(int player)
    {
        if (move)
        {
            curr[player].translate(0, 1);
            curr[player].checkDown();
            display[player].showBlocks();
        }
    }
    
    public void spacePressed(int player)
    {
        if (move)
        {
            skip[player] = true;
            curr[player].translate(curr[player].checkDown(), 0);
            swapped[player] = false;
            this.clearCompletedRows(player, grid[player]);
            type[player] = (int)(Math.random() * 7);
            this.addRows(player, grid[player], q[player]);
            q[player] = 0;
            curr[player] = new Tetrad(player, grid[player], type[player]);
            curr[player].checkDown();
            display[player].showBlocks();
        }
    }
    
    public void ePressed(int player)
    {
        if (!swapped[player])
        {
            curr[player].removeSelfFromGrid();
            int save2 = hold[player];
            if (save2 == -1) save2 = (int)(Math.random() * 7);
            hold[player] = type[player];
            type[player] = save2;
            curr[player] = new Tetrad(player, grid[player], type[player]);
            swapped[player] = true;
            //grid[player].putInHold(hold[player]);
            curr[player].checkDown();
            display[player].showBlocks();
        }
    }
}
