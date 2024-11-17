import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class cg extends Frame implements MouseListener {
    private static final int TILE_SIZE = 80;
    private static final int BOARD_SIZE = TILE_SIZE * 8;
    private static final String[] INITIAL_PIECES = {
        "r", "n", "b", "q", "k", "b", "n", "r",
        "p", "p", "p", "p", "p", "p", "p", "p",
        ".", ".", ".", ".", ".", ".", ".", ".",
        ".", ".", ".", ".", ".", ".", ".", ".",
        ".", ".", ".", ".", ".", ".", ".", ".",
        ".", ".", ".", ".", ".", ".", ".", ".",
        "P", "P", "P", "P", "P", "P", "P", "P",
        "R", "N", "B", "Q", "K", "B", "N", "R"
    };

    private String[][] board;
    private String selectedPiece;
    private int selectedX, selectedY;
    private boolean isWhiteTurn = true;
    private Set<String> whitePieces;
    private Set<String> blackPieces;
    private Set<String> validMoves;
    private Map<String, Image> pieceImages;
    private boolean playWithComputer;
    private boolean isGameOver = false;
    private Player currentPlayer;
    private Player whitePlayer;
    private Player blackPlayer;
    
    // Create a JLabel for move count display
    private JLabel whiteScoreLabel = new JLabel("White: 0 moves");
    private JLabel blackScoreLabel = new JLabel("Black: 0 moves");
    
    // Create an instance of ChessGameFunctions to handle move counts and game logic
    private ChessGameFunctions gameFunctions;

    // Constructor to initialize the game
    public cg(boolean playWithComputer) {
        this.playWithComputer = playWithComputer;
        board = new String[8][8];
        whitePieces = new HashSet<>(Arrays.asList("P", "R", "N", "B", "Q", "K"));
        blackPieces = new HashSet<>(Arrays.asList("p", "r", "n", "b", "q", "k"));
        initializeBoard();

        // Load piece images from the same directory as the source code
        loadPieceImages();

        // Initialize players (Human vs Human or Human vs Computer)
        whitePlayer = new HumanPlayer(true);
        blackPlayer = playWithComputer ? new ComputerPlayer(false) : new HumanPlayer(false);
        currentPlayer = whitePlayer;

        // Initialize ChessGameFunctions with the move count labels
        gameFunctions = new ChessGameFunctions(whiteScoreLabel, blackScoreLabel);
        
        setSize(BOARD_SIZE, BOARD_SIZE);
        setTitle("Chess Game");
        setVisible(true);
        addMouseListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    // Initialize the chessboard
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = INITIAL_PIECES[i * 8 + j];
            }
        }
    }

    // Load piece images
    private void loadPieceImages() {
        pieceImages = new HashMap<>();
        pieceImages.put("wP", Toolkit.getDefaultToolkit().getImage("wp.png"));
        pieceImages.put("wR", Toolkit.getDefaultToolkit().getImage("wr.png"));
        pieceImages.put("wN", Toolkit.getDefaultToolkit().getImage("wn.png"));
        pieceImages.put("wB", Toolkit.getDefaultToolkit().getImage("wb.png"));
        pieceImages.put("wQ", Toolkit.getDefaultToolkit().getImage("wq.png"));
        pieceImages.put("wK", Toolkit.getDefaultToolkit().getImage("wk.png"));
        pieceImages.put("bP", Toolkit.getDefaultToolkit().getImage("bp.png"));
        pieceImages.put("bR", Toolkit.getDefaultToolkit().getImage("br.png"));
        pieceImages.put("bN", Toolkit.getDefaultToolkit().getImage("bn.png"));
        pieceImages.put("bB", Toolkit.getDefaultToolkit().getImage("bb.png"));
        pieceImages.put("bQ", Toolkit.getDefaultToolkit().getImage("bq.png"));
        pieceImages.put("bK", Toolkit.getDefaultToolkit().getImage("bk.png"));
    }

    // Paint the board and pieces
    public void paint(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Draw the board tiles
                g.setColor((i + j) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY);
                g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Draw the pieces
                String piece = board[i][j];
                if (!piece.equals(".")) {
                    Image pieceImage = pieceImages.get(getPieceKey(piece));
                    if (pieceImage != null) {
                        g.drawImage(pieceImage, j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                    }
                }
            }
        }

        // Highlight valid moves (if a piece is selected)
        if (selectedPiece != null) {
            g.setColor(Color.GREEN);
            for (String move : validMoves) {
                int x = Integer.parseInt(move.split(",")[0]);
                int y = Integer.parseInt(move.split(",")[1]);
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    // Utility method to map the piece letter to a key for loading images
    private String getPieceKey(String piece) {
        if (piece.equals("P") || piece.equals("p")) {
            return (piece.equals("P") ? "wP" : "bP");
        }
        if (piece.equals("R") || piece.equals("r")) {
            return (piece.equals("R") ? "wR" : "bR");
        }
        if (piece.equals("N") || piece.equals("n")) {
            return (piece.equals("N") ? "wN" : "bN");
        }
        if (piece.equals("B") || piece.equals("b")) {
            return (piece.equals("B") ? "wB" : "bB");
        }
        if (piece.equals("Q") || piece.equals("q")) {
            return (piece.equals("Q") ? "wQ" : "bQ");
        }
        if (piece.equals("K") || piece.equals("k")) {
            return (piece.equals("K") ? "wK" : "bK");
        }
        return null; // For empty tiles
    }

    // Mouse click event handler to select and move pieces
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / TILE_SIZE;
        int y = e.getY() / TILE_SIZE;

        if (selectedPiece == null) {
            // Select a piece
            if (!board[y][x].equals(".")) {
                String piece = board[y][x];
                if (isWhiteTurn && whitePieces.contains(piece) || !isWhiteTurn && blackPieces.contains(piece)) {
                    selectedPiece = piece;
                    selectedX = x;
                    selectedY = y;
                    validMoves = calculateValidMoves(selectedPiece, selectedX, selectedY);
                    repaint();
                }
            }
        } else {
            // Move the piece
            if (validMoves.contains(x + "," + y)) {
                String capturedPiece = board[y][x];
                board[y][x] = selectedPiece;
                board[selectedY][selectedX] = ".";
                selectedPiece = null;
                isWhiteTurn = !isWhiteTurn;
                validMoves.clear();
                repaint();
                // Update move count using ChessGameFunctions
                gameFunctions.updateMoveCount(isWhiteTurn);
                // Handle capture (capturing the opponent's piece)
                if (!capturedPiece.equals(".")) {
                    System.out.println("Captured: " + capturedPiece);
                }
            } else {                    
                selectedPiece = null;
                isWhiteTurn = !isWhiteTurn;
                validMoves.clear();
                repaint();

                // Handle capture (capturing the opponent's piece)
                if (!capturedPiece.equals(".")) {
                    System.out.println("Captured: " + capturedPiece);
                }

                // Check if the game is over
                if (gameFunctions.isGameOver(board)) {
                    gameFunctions.announceWinner(isWhiteTurn ? "White" : "Black");
                    isGameOver = true;
                }

                // If it's the computer's turn, let it play
                if (playWithComputer && !isWhiteTurn && !isGameOver) {
                    computerMove();
                }
            }


    // Calculate valid moves for a piece (simplified for demonstration)
// Calculate valid moves for a piece (simplified for demonstration)
private Set<String> calculateValidMoves(String piece, int x, int y) {
    Set<String> moves = new HashSet<>();

    // Pawn movement
    if (piece.equals("P")) {
        // White pawn moves forward
        if (y > 0 && board[y - 1][x].equals(".")) {
            moves.add(x + "," + (y - 1)); // Move 1 square forward
            // Double move on first turn
            if (y == 6 && board[y - 2][x].equals(".")) {
                moves.add(x + "," + (y - 2)); // Move 2 squares forward
            }
        }
        // White pawn captures diagonally
        if (y > 0 && x > 0 && board[y - 1][x - 1].equals("p")) {
            moves.add((x - 1) + "," + (y - 1)); // Capture diagonally left
        }
        if (y > 0 && x < 7 && board[y - 1][x + 1].equals("p")) {
            moves.add((x + 1) + "," + (y - 1)); // Capture diagonally right
        }
    }

    // Rook movement (straight lines horizontally and vertically)
    if (piece.equals("R") || piece.equals("r")) {
        // Horizontal moves
        for (int i = x + 1; i < 8; i++) {
            if (board[y][i].equals(".")) {
                moves.add(i + "," + y); // Empty space
            } else if (isOpponent(piece, board[y][i])) {
                moves.add(i + "," + y); // Capture opponent's piece
                break;
            } else {
                break; // Blocked by own piece
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            if (board[y][i].equals(".")) {
                moves.add(i + "," + y);
            } else if (isOpponent(piece, board[y][i])) {
                moves.add(i + "," + y);
                break;
            } else {
                break;
            }
        }
        // Vertical moves
        for (int i = y + 1; i < 8; i++) {
            if (board[i][x].equals(".")) {
                moves.add(x + "," + i);
            } else if (isOpponent(piece, board[i][x])) {
                moves.add(x + "," + i);
                break;
            } else {
                break;
            }
        }
        for (int i = y - 1; i >= 0; i--) {
            if (board[i][x].equals(".")) {
                moves.add(x + "," + i);
            } else if (isOpponent(piece, board[i][x])) {
                moves.add(x + "," + i);
                break;
            } else {
                break;
            }
        }
    }

    // Knight movement (L-shaped moves)
    if (piece.equals("N") || piece.equals("n")) {
        int[][] knightMoves = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, 
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : knightMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                if (board[newY][newX].equals(".") || isOpponent(piece, board[newY][newX])) {
                    moves.add(newX + "," + newY);
                }
            }
        }
    }

    // Bishop movement (diagonal moves)
    if (piece.equals("B") || piece.equals("b")) {
        // Diagonal moves (top-right, top-left, bottom-right, bottom-left)
        for (int i = 1; i < 8; i++) {
            // Top-right diagonal
            if (x + i < 8 && y - i >= 0) {
                if (board[y - i][x + i].equals(".")) {
                    moves.add((x + i) + "," + (y - i));
                } else if (isOpponent(piece, board[y - i][x + i])) {
                    moves.add((x + i) + "," + (y - i));
                    break;
                } else {
                    break;
                }
            }
            // Top-left diagonal
            if (x - i >= 0 && y - i >= 0) {
                if (board[y - i][x - i].equals(".")) {
                    moves.add((x - i) + "," + (y - i));
                } else if (isOpponent(piece, board[y - i][x - i])) {
                    moves.add((x - i) + "," + (y - i));
                    break;
                } else {
                    break;
                }
            }
            // Bottom-right diagonal
            if (x + i < 8 && y + i < 8) {
                if (board[y + i][x + i].equals(".")) {
                    moves.add((x + i) + "," + (y + i));
                } else if (isOpponent(piece, board[y + i][x + i])) {
                    moves.add((x + i) + "," + (y + i));
                    break;
                } else {
                    break;
                }
            }
            // Bottom-left diagonal
            if (x - i >= 0 && y + i < 8) {
                if (board[y + i][x - i].equals(".")) {
                    moves.add((x - i) + "," + (y + i));
                } else if (isOpponent(piece, board[y + i][x - i])) {
                    moves.add((x - i) + "," + (y + i));
                    break;
                } else {
                    break;
                }
            }
        }
    }

    // Queen movement (rook + bishop)
    if (piece.equals("Q") || piece.equals("q")) {
        moves.addAll(calculateValidMoves("R", x, y)); // Rook moves
        moves.addAll(calculateValidMoves("B", x, y)); // Bishop moves
    }

    // King movement (1 square in any direction)
    if (piece.equals("K") || piece.equals("k")) {
        int[][] kingMoves = {
            {1, 0}, {0, 1}, {-1, 0}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] move : kingMoves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {
                if (board[newY][newX].equals(".") || isOpponent(piece, board[newY][newX])) {
                    moves.add(newX + "," + newY);
                }
            }
        }
    }

    return moves;
}
    // Helper methods to calculate diagonal-moving pieces (Bishop, Queen)
    private Set<String> calculateDiagonalMoves(int x, int y, String piece) {
        Set<String> moves = new HashSet<>();
        String[] directions = {"up-left", "up-right", "down-left", "down-right"};
        for (String direction : directions) {
            int i = 1;
            while (true) {
                int newX = x, newY = y;
                if (direction.equals("up-left")) {
                    newX -= i;
                    newY -= i;
                }
                if (direction.equals("up-right")) {
                    newX += i;
                    newY -= i;
                }
                if (direction.equals("down-left")) {
                    newX -= i;
                    newY += i;
                }
                if (direction.equals("down-right")) {
                    newX += i;
                    newY += i;
                }
                if (newX < 0 || newY < 0 || newX >= 8 || newY >= 8) break;

                String target = board[newY][newX];
                if (target.equals(".")) {
                    moves.add(newX + "," + newY);
                } else {
                    if ((Character.isLowerCase(target.charAt(0)) != Character.isLowerCase(piece.charAt(0)))) {
                        moves.add(newX + "," + newY);
                    }
                    break;
                }
                i++;
            }
        }
        return moves;
    }
// Helper function to check if a piece is an opponent
private boolean isOpponent(String piece, String target) {
    if (piece.equals("P") || piece.equals("R") || piece.equals("N") || piece.equals("B") || piece.equals("Q") || piece.equals("K")) {
        return target.equals(target.toLowerCase()); // Target is black
    } else {
        return target.equals(target.toUpperCase()); // Target is white
    }
}


    // Handle computer's move (basic random move for demonstration)
    private void computerMove() {
        // Simplified: Make a random valid move (to be expanded)
        Random rand = new Random();
        int x = rand.nextInt(8);
        int y = rand.nextInt(8);

        // Simplified move: move a random piece
        if (!board[y][x].equals(".")) {
            selectedPiece = board[y][x];
            validMoves = calculateValidMoves(selectedPiece, x, y);
            mouseClicked(new MouseEvent(this, 0, 0, 0, x * TILE_SIZE, y * TILE_SIZE, 1, false));
        }
    }

    // Unused MouseListener methods
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
        // Player class and subclasses for Human and Computer players
    abstract class Player {
        boolean isWhite;

        Player(boolean isWhite) {
            this.isWhite = isWhite;
        }

        abstract void makeMove();
    }

    class HumanPlayer extends Player {
        HumanPlayer(boolean isWhite) {
            super(isWhite);
        }

        @Override
        void makeMove() {
            // Human player makes move (already handled by mouse events)
        }
    }

    class ComputerPlayer extends Player {
        ComputerPlayer(boolean isWhite) {
            super(isWhite);
        }

        @Override
        void makeMove() {
            // Implement simple AI for computer move here
        }
    }

    // Main method to start the game
    public static void main(String[] args) {
        new cg(true); // Change to `new cg(false)` for Human vs Human
    }
}


