import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// Base Piece Class
abstract class Piece {
    private boolean killed = false;
    private boolean white = false;

    public Piece(boolean white) {
        this.white = white;
    }

    public boolean isWhite() {
        return this.white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

    public boolean isKilled() {
        return this.killed;
    }

    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    public abstract boolean canMove(Board board, Spot start, Spot end);
}

// King Class
class King extends Piece {
    public King(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false; // Can't move to a spot occupied by a same-color piece
        }
        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        return x + y == 1; // King moves one square
    }
}

// Queen Class
class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false; // Can't move to a spot occupied by a same-color piece
        }
        int dx = Math.abs(start.getX() - end.getX());
        int dy = Math.abs(start.getY() - end.getY());
        return dx == dy || start.getX() == end.getX() || start.getY() == end.getY();
    }
}

// Rook Class
class Rook extends Piece {
    public Rook(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false; // Can't move to a spot occupied by a same-color piece
        }
        return start.getX() == end.getX() || start.getY() == end.getY();
    }
}

// Bishop Class
class Bishop extends Piece {
    public Bishop(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false; // Can't move to a spot occupied by a same-color piece
        }
        int dx = Math.abs(start.getX() - end.getX());
        int dy = Math.abs(start.getY() - end.getY());
        return dx == dy; // Bishops move diagonally
    }
}

// Knight Class
class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false; // Can't move to a spot occupied by a same-color piece
        }
        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        return x * y == 2; // Valid knight move (L-shape)
    }
}

// Pawn Class
class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(Board board, Spot start, Spot end) {
        if (end.getPiece() != null && end.getPiece().isWhite() == this.isWhite()) {
            return false; // Can't move to a spot occupied by a same-color piece
        }
        int direction = this.isWhite() ? -1 : 1;
        int dx = Math.abs(start.getX() - end.getX());
        int dy = Math.abs(start.getY() - end.getY());

        // Regular movement (one square forward)
        if (dx == 0 && dy == direction) {
            return true;
        }

        // Capture movement (diagonal)
        if (dx == 1 && dy == direction && end.getPiece() != null) {
            return true;
        }
        return false;
    }
}

// Spot Class
class Spot {
    private int x, y;
    private Piece piece;

    public Spot(int x, int y, Piece piece) {
        this.x = x;
        this.y = y;
        this.piece = piece;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}

// Board Class
class Board {
    private Spot[][] spots = new Spot[8][8];

    public Board() {
        resetBoard();
    }

    public Spot getBox(int x, int y) {
        return spots[x][y];
    }

    public void resetBoard() {
        // Initialize white pieces
        spots[0][0] = new Spot(0, 0, new Rook(true));
        spots[0][1] = new Spot(0, 1, new Knight(true));
        spots[0][2] = new Spot(0, 2, new Bishop(true));
        spots[0][3] = new Spot(0, 3, new Queen(true));
        spots[0][4] = new Spot(0, 4, new King(true));
        spots[0][5] = new Spot(0, 5, new Bishop(true));
        spots[0][6] = new Spot(0, 6, new Knight(true));
        spots[0][7] = new Spot(0, 7, new Rook(true));

        for (int i = 0; i < 8; i++) {
            spots[1][i] = new Spot(1, i, new Pawn(true));
        }

        // Initialize black pieces
        spots[7][0] = new Spot(7, 0, new Rook(false));
        spots[7][1] = new Spot(7, 1, new Knight(false));
        spots[7][2] = new Spot(7, 2, new Bishop(false));
        spots[7][3] = new Spot(7, 3, new Queen(false));
        spots[7][4] = new Spot(7, 4, new King(false));
        spots[7][5] = new Spot(7, 5, new Bishop(false));
        spots[7][6] = new Spot(7, 6, new Knight(false));
        spots[7][7] = new Spot(7, 7, new Rook(false));

        for (int i = 0; i < 8; i++) {
            spots[6][i] = new Spot(6, i, new Pawn(false));
        }

        // Initialize empty spots
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                spots[i][j] = new Spot(i, j, null);
            }
        }
    }
}

// Player Class (Abstract for Human and Computer)
abstract class Player {
    protected boolean whiteSide;

    public boolean isWhiteSide() {
        return this.whiteSide;
    }

    public abstract boolean isHumanPlayer();
}

// Human Player Class
class HumanPlayer extends Player {
    public HumanPlayer(boolean whiteSide) {
        this.whiteSide = whiteSide;
    }

    @Override
    public boolean isHumanPlayer() {
        return true;
    }
}

// Computer Player Class
class ComputerPlayer extends Player {
    public ComputerPlayer(boolean whiteSide) {
        this.whiteSide = whiteSide;
    }

    @Override
    public boolean isHumanPlayer() {
        return false;
    }
}

// Chess Game Class
public class ChessGame extends JFrame {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = TILE_SIZE * 8;
    private Board board;
    private Player currentPlayer;
    private boolean isGameOver;
    private Player whitePlayer;
    private Player blackPlayer;
    private String selectedPiece;
    private int selectedX, selectedY;

    public ChessGame(boolean playWithComputer) {
        board = new Board();
        whitePlayer = new HumanPlayer(true);
        blackPlayer = playWithComputer ? new ComputerPlayer(false) : new HumanPlayer(false);
        currentPlayer = whitePlayer;
        isGameOver = false;

        setTitle("Chess Game");
        setSize(BOARD_SIZE + 16, BOARD_SIZE + 39);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isGameOver) return;

                int x = e.getX() / TILE_SIZE;
                int y = e.getY() / TILE_SIZE;

                if (selectedPiece == null) {
                    if (board.getBox(x, y).getPiece() != null &&
                        board.getBox(x, y).getPiece().isWhite() == currentPlayer.isWhiteSide()) {
                        selectedPiece = (x + "," + y);
                        selectedX = x;
                        selectedY = y;
                    }
                } else {
                    Spot startSpot = board.getBox(selectedX, selectedY);
                    Spot endSpot = board.getBox(x, y);

                    if (startSpot.getPiece().canMove(board, startSpot, endSpot)) {
                        endSpot.setPiece(startSpot.getPiece());
                        startSpot.setPiece(null);
                        switchPlayer();
                    }
                    selectedPiece = null;
                }
            }
        });
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawBoard(g);
        drawPieces(g);
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }
                g.fillRect(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawPieces(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getBox(i, j).getPiece();
                if (piece != null) {
                    if (piece instanceof King) {
                        g.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
                        g.fillOval(i * TILE_SIZE + 20, j * TILE_SIZE + 20, TILE_SIZE - 40, TILE_SIZE - 40);
                    } else if (piece instanceof Queen) {
                        g.setColor(piece.isWhite() ? Color.WHITE : Color.BLACK);
                        g.fillRect(i * TILE_SIZE + 20, j * TILE_SIZE + 20, TILE_SIZE - 40, TILE_SIZE - 40);
                    }
                    // Additional piece drawings can be added for other pieces...
                }
            }
        }
    }

    public static void main(String[] args) {
        new StartScreen();
    }
}

// Start Screen Class (Initial Game Options)
class StartScreen extends JFrame {
    public StartScreen() {
        setTitle("Chess Game - Start");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton humanVsHumanButton = new JButton("Human vs. Human");
        JButton humanVsComputerButton = new JButton("Human vs. Computer");
        JButton quitButton = new JButton("Quit");

        humanVsHumanButton.addActionListener(e -> {
            dispose();
            new ChessGame(false);  // False for Human vs Human
        });

        humanVsComputerButton.addActionListener(e -> {
            dispose();
            new ChessGame(true);  // True for Human vs Computer
        });

        quitButton.addActionListener(e -> System.exit(0));

        panel.add(humanVsHumanButton);
        panel.add(humanVsComputerButton);
        panel.add(quitButton);

        add(panel);
        setVisible(true);
    }
}
