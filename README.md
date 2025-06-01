# Project: Extended APCS Project from singleplayer to local multiplayer Tetris

# Instructions for Download:
Go to `<> Code` button, download files as zip and upzip the files, Run Main.class to start program and space to start the game.

# Gameplay
Use your set of keys (shown in below section) to control your own pieces. Clearing 2 lines will send 1 line to a random player (excluding yourself), 3 lines will send 2 lines, 4 lines will send 4 lines. Your die when the game can no longer spawn a piece in.

# Configuration
To Change Keybinds and other settings (such as the number of players), go to Tetris.java. The default keybinds are set as the following:
```java
private static final int[][] keyDict = {
      {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_Q, KeyEvent.VK_E}, 
      {KeyEvent.VK_T, KeyEvent.VK_F, KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_R, KeyEvent.VK_Y}, 
      {KeyEvent.VK_I, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L, KeyEvent.VK_U, KeyEvent.VK_O}, 
      {KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_SLASH, KeyEvent.VK_SHIFT}
  };
```
...meaning that the first player uses WASD for movement, Q to rotate clockwise, and E to hold. The following lines are for the other 3 players.

## TODO: 
- convert to .app
- use P2P or server networking
