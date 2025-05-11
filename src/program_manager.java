import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class  program_manager  {
    private final int SIZE = 8;
    private final int TILE_SIZE = 80;
    private final JButton[][] board = new JButton[SIZE][SIZE];
    private final Stack<Move> moveStack = new Stack<>();
    private final JPanel board_panel = new JPanel(new GridLayout(SIZE, SIZE));
    private final JPanel control_panel = new JPanel();
    private final JLabel score_label = new JLabel("Score: 0");
    private final JLabel move_label = new JLabel("Moves: 0");
    private final JButton startBtn = new JButton("Start");
    private final JButton restartBtn = new JButton("Restart");
    private final JButton undoBtn = new JButton("Undo (3)");

    private int score = 0;
    private int moves = 1;
    private int lives = 3;
    private Point knightPos = null;

    public  program_manager () {
        board();
        setup_controls();
    }

    private void board() {
        board_panel.setPreferredSize(new Dimension(SIZE * TILE_SIZE, SIZE * TILE_SIZE));
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                JButton tile = new JButton();
                tile.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                tile.setFont(new Font("Arial", Font.BOLD, 24));
                int r = row, c = col;
                tile.addActionListener(e -> handle_move(r, c));
                board[row][col] = tile;
                board_panel.add(tile);
            }
        }
    }

    private void setup_controls() {
        control_panel.setLayout(new FlowLayout());
        control_panel.add(startBtn);
        control_panel.add(restartBtn);
        control_panel.add(undoBtn);
        control_panel.add(score_label);
        control_panel.add(move_label);

        startBtn.addActionListener(e -> start_game());
        restartBtn.addActionListener(e -> reset_game());
        undoBtn.addActionListener(e -> undo_move());
    }

    public JPanel getBoard() {
        JPanel main = new JPanel(new BorderLayout());
        main.add(board_panel, BorderLayout.CENTER);
        main.add(control_panel, BorderLayout.SOUTH);
        main.add(rules_panel(),BorderLayout.EAST);
        return main;
    }

    private void start_game() {
        reset_game();
        // Do nothing — wait for user to click a tile to start
        JOptionPane.showMessageDialog(null, "Click any square to start the knight's Tour.");
        startBtn.setEnabled(false); // disable Start after first click

    }

    private void reset_game() {
        moveStack.clear();
        score = 0;
        moves = 1;
        lives = 3;
        knightPos = null;
        updateScoreLabel();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton tile = board[i][j];
                tile.setText("");
                tile.setIcon(null);
                tile.setEnabled(true);
                tile.setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
            }
        }
        undoBtn.setText("Undo (3)");
        startBtn.setEnabled(true);

    }

    private static class Move {
        Point position;
        int moveNumber;

        Move(Point position, int moveNumber) {
            this.position = position;
            this.moveNumber = moveNumber;
        }
    }
    private JPanel rules_panel() {
        JPanel rulesPanel = new JPanel();
        rulesPanel.setPreferredSize(new Dimension(250, 640));
        rulesPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("How to Play");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setFont(new Font("Arial", Font.PLAIN, 14));
        rulesText.setText(
                "1. Click any square to start.\n" +
                        "2. Move like a knight (L shape).\n" +
                        "3. Visit all 64 squares without repeating.\n" +
                        "4. You gain points for each move.\n" +
                        "5. Undo costs 1 life. You have 3 lives.\n" +
                        "6. Complete the tour to win!"
        );

        JScrollPane scrollPane = new JScrollPane(rulesText);

        rulesPanel.add(title, BorderLayout.NORTH);
        rulesPanel.add(scrollPane, BorderLayout.CENTER);

        return rulesPanel;
    }



    private void place_knight(int row, int col, boolean increaseScore) {
        if (knightPos != null) {
            // Mark the previous position with move number
            JButton prevTile = board[knightPos.x][knightPos.y];
            prevTile.setIcon(null); // remove the knight image
            Move lastMove = moveStack.peek();
            prevTile.setText(String.valueOf(lastMove.moveNumber));
            prevTile.setForeground(Color.BLACK);




            // Set background and foreground based on tile color
            if ((knightPos.x + knightPos.y) % 2 == 0) {
                prevTile.setBackground(Color.WHITE);
                prevTile.setForeground(Color.BLACK);  // good contrast on white
            } else {
                prevTile.setBackground(Color.GRAY);
                prevTile.setForeground(Color.WHITE);  // good contrast on gray
            }
        }

        knightPos = new Point(row, col);
        JButton currTile = board[row][col];
        ImageIcon knightIcon = new ImageIcon("knight.png");
        Image scaledImage = knightIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        currTile.setIcon(new ImageIcon(scaledImage));
        currTile.setText("");

        currTile.setBackground(Color.GREEN);
        currTile.setEnabled(false);

        moveStack.push(new Move(new Point(row, col), moves));

        if (increaseScore) {
            score += 20;
            moves += 1;
        }
        updateScoreLabel();
        highlight_moves(row, col);

    }

    private void handle_move(int row, int col) {
        if (knightPos == null) {
            // This is the first move
            place_knight(row, col, true);
            return;
        }
        if ( is_legal_move(knightPos, row, col)) {
            place_knight(row, col, true);
        }
    }


    private boolean is_legal_move(Point from, int toRow, int toCol) {
        if (from == null) return false;
        int dx = Math.abs(from.x - toRow);
        int dy = Math.abs(from.y - toCol);
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    private void highlight_moves(int row, int col) {
        // Reset all unvisited/enabled tiles to base color
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JButton tile = board[r][c];
                if (tile.isEnabled()) {
                    tile.setBackground((r + c) % 2 == 0 ? Color.WHITE : Color.GRAY);
                }
            }
        }

        // Highlight new legal moves in yellow
        int[][] moves = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] move : moves) {
            int r = row + move[0];
            int c = col + move[1];
            if (isInBounds(r, c) && board[r][c].isEnabled()) {
                board[r][c].setBackground(Color.YELLOW);
            }
        }
    }


    // Checks whether the given row and column are within the bounds of the board.

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private void undo_move() {
        if (moveStack.size() <= 1 || lives == 0) return;

        // Remove current knight's tile info
        Move current = moveStack.pop();
        JButton currentTile = board[current.position.x][current.position.y];
        currentTile.setIcon(null);
        currentTile.setText("");
        currentTile.setEnabled(true);
        currentTile.setBackground((current.position.x + current.position.y) % 2 == 0 ? Color.WHITE : Color.GRAY);

        // Restore previous move
        Move previous = moveStack.peek();
        knightPos = previous.position;

        JButton prevTile = board[previous.position.x][previous.position.y];
        ImageIcon knightIcon = new ImageIcon("knight.png");
        Image scaledImage = knightIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        prevTile.setIcon(new ImageIcon(scaledImage));
        prevTile.setText("");
        prevTile.setBackground(Color.GREEN);

        // Update stats
        score -= 15;
        moves -= 1;
        lives -= 1;
        updateScoreLabel();
        undoBtn.setText("Undo (" + lives + ")");
        highlight_moves(previous.position.x, previous.position.y);
    }


    // updating moves and score
    private void updateScoreLabel() {
        score_label.setText("Score: " + score);
        move_label.setText("Moves: " + moves);
    }
}
