import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
/**
 * @author Anu Datar
 * 
 * Changed block size and added a split panel display for next block and Score
 * 
 * @author Ryan Adolf
 * @version 1.0
 * 
 * Fixed the lag issue with block rendering 
 * Removed the JPanel
 */
// Used to display the contents of a game board
public class BlockDisplay extends JComponent implements KeyListener
{
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color BORDER = new Color(38, 38, 38);
    private static final Color MARKED = new Color(64, 64, 64);
    
    private static final int OUTLINE = 2;
    private static final int BLOCKSIZE = 20;

    private int player;
    private MyBoundedGrid<Block> board;
    private JFrame frame;
    private ArrowListener listener;

    private boolean win = false;
    private static final double centers[] = { 1.5, 2, 2.5, 2, 2, 2, 2 };

    // Constructs a new display for displaying the given board
    public BlockDisplay(int player, MyBoundedGrid<Block> board)
    {
        this.player = player;
        this.board = board;
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    createAndShowGUI();
                }
            });

        //Wait until display has been drawn
        try
        {
            while (frame == null || !frame.isVisible())
                Thread.sleep(1);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI()
    {
        //Create and set up the window.
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.addKeyListener(this);

        //Display the window.
        this.setPreferredSize(new Dimension(
                BLOCKSIZE * board.getColsReal(),
                BLOCKSIZE * board.getNumRows()
            ));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)((player + 0.5) * screenSize.width / (Tetris.PLAYERS) - BLOCKSIZE * board.getColsReal() / 2),
                (screenSize.height - BLOCKSIZE * board.getNumRows()) / 2);
        frame.pack();
        frame.setVisible(true);
    }

    public void paintComponent(Graphics g)
    {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(BORDER);
        g.fillRect(0, 0, BLOCKSIZE * board.getNumCols() + OUTLINE/2, BLOCKSIZE * board.getNumRows());
        for (int row = 0; row < board.getNumRows(); row++) {
            for (int col = 0; col < board.getColsReal(); col++) {
                Location loc = new Location(row, col);

                Block square = board.get(loc);

                if (square != null) {
                    g.setColor(square.getColor());
                    g.fillRect(col * BLOCKSIZE, row * BLOCKSIZE,
                            BLOCKSIZE, BLOCKSIZE);
                } else if (board.isMarked(new Location(row, col))) {
                    g.setColor(MARKED);
                    g.fillRect(col * BLOCKSIZE, row * BLOCKSIZE,
                            BLOCKSIZE, BLOCKSIZE);
                } else {
                    g.setColor(BACKGROUND);
                    g.fillRect(col * BLOCKSIZE + OUTLINE / 2, row * BLOCKSIZE + OUTLINE / 2,
                            BLOCKSIZE - OUTLINE, BLOCKSIZE - OUTLINE);
                }
            }
        }
        if (Tetris.hold[player] != -1) {
            Location[] locs = new Location[4];
            int center = board.getNumCols();
            int type = Tetris.hold[player];
            Color color;
            final Color[] colorList = {
                    new Color(117, 203, 174), // I
                    new Color(181, 89, 200), // T
                    new Color(204, 197, 100), // O
                    new Color(195, 132, 90), // L
                    new Color(99, 83, 199), // J
                    new Color(146, 204, 99), // S
                    new Color(191, 90, 92) // Z
            };

            color = colorList[type];
            if (type == 0) {
                locs[0] = new Location(1, center - 1);
                locs[1] = new Location(1, center);
                locs[2] = new Location(1, center + 1);
                locs[3] = new Location(1, center + 2);
            }
            if (type == 1) {
                locs[0] = new Location(0, center);
                locs[1] = new Location(1, center);
                locs[2] = new Location(1, center - 1);
                locs[3] = new Location(1, center + 1);
            }
            if (type == 2) {
                locs[0] = new Location(0, center);
                locs[1] = new Location(1, center);
                locs[2] = new Location(0, center + 1);
                locs[3] = new Location(1, center + 1);
            }
            if (type == 3) {
                locs[0] = new Location(1, center - 1);
                locs[1] = new Location(1, center);
                locs[2] = new Location(1, center + 1);
                locs[3] = new Location(0, center + 1);
            }
            if (type == 4) {
                locs[0] = new Location(1, center - 1);
                locs[1] = new Location(1, center);
                locs[2] = new Location(1, center + 1);
                locs[3] = new Location(0, center - 1);
            }
            if (type == 5) {
                locs[0] = new Location(1, center - 1);
                locs[1] = new Location(1, center);
                locs[2] = new Location(0, center);
                locs[3] = new Location(0, center + 1);
            }
            if (type == 6) {
                locs[0] = new Location(0, center - 1);
                locs[1] = new Location(1, center);
                locs[2] = new Location(0, center);
                locs[3] = new Location(1, center + 1);
            }

            for (int i = 0; i < 4; i++) {
                g.setColor(color);
                g.fillRect((int) ((locs[i].getCol() + centers[type]) * BLOCKSIZE), (int)((locs[i].getRow() + (type == 0 ? 0 : 0.5)) * BLOCKSIZE),
                        BLOCKSIZE, BLOCKSIZE);
            }
        }
        if (!win && !Tetris.alive[player]) {
            g.setColor(new Color(255, 255, 255, 64));
            g.fillRect(0, 0, BLOCKSIZE * board.getColsReal() + OUTLINE, BLOCKSIZE * board.getNumRows());
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            try {
                Image image = ImageIO.read(new File("skull.png"));
                int imageWidth = 200;
                int imageHeight = imageWidth;
                int x = (panelWidth - imageWidth) / 2;
                int y = (panelHeight - imageHeight) / 2;
                g.drawImage(image, x, y, imageWidth, imageHeight, this);
            } catch (IOException e) {

            }
        }
        else {
            if (!win) {
                for (int i = 0; i < Tetris.PLAYERS; i++) {
                    if ((i == player) != Tetris.alive[i]) {
                        return;
                    }
                }
                win = true;
            }
            
            Tetris.alive[player] = false;
            g.setColor(new Color(255, 255, 255, 64));
            g.fillRect(0, 0, BLOCKSIZE * board.getColsReal() + OUTLINE, BLOCKSIZE * board.getNumRows());
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            try {
                Image image = ImageIO.read(new File("trophy.png"));
                int imageWidth = 100;
                int imageHeight = imageWidth * image.getHeight(null) / image.getWidth(null);
                int x = (panelWidth - imageWidth * 336/image.getWidth(null)) / 2;
                int y = (panelHeight - imageHeight) / 2;
                g.drawImage(image, x, y, imageWidth, imageHeight, this);
            } catch (IOException e) {

            }
        }
    }

    //Redraws the board to include the pieces and border colors.
    public void showBlocks()
    {
        repaint();
    }

    // Sets the title of the window.
    public void setTitle(String title)
    {
        frame.setTitle(title);
    }

    public void keyTyped(KeyEvent e)
    {
    }

    public void keyReleased(KeyEvent e)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        if (listener == null)
            return;
        int code = e.getKeyCode();
    
        listener.keyPressed(code);
    }

    public void setArrowListener(ArrowListener listener)
    {
        this.listener = listener;
    }
}
