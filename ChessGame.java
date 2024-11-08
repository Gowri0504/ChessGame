import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.io.File;

public class ChessGame extends Frame implements MouseListener {
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

    public ChessGame() {
        board = new String[8][8];
        whitePieces = new HashSet<>(Arrays.asList("P", "R", "N", "B", "Q", "K"));
        blackPieces = new HashSet<>(Arrays.asList("p", "r", "n", "b", "q", "k"));
        initializeBoard();

        // Load piece images from the same directory as the source code
        loadPieceImages();

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

    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = INITIAL_PIECES[i * 8 + j];
            }
        }
    }

    private void loadPieceImages() {
        pieceImages = new HashMap<>();

        // Define relative file paths for all pieces
        pieceImages.put("wP", Toolkit.getDefaultToolkit().getImage("wp.png")); // White Pawn
        pieceImages.put("wR", Toolkit.getDefaultToolkit().getImage("wr.png")); // White Rook
        pieceImages.put("wN", Toolkit.getDefaultToolkit().getImage("wn.png")); // White Knight
        pieceImages.put("wB", Toolkit.getDefaultToolkit().getImage("wb.png")); // White Bishop
        pieceImages.put("wQ", Toolkit.getDefaultToolkit().getImage("wq.png")); // White Queen
        pieceImages.put("wK", Toolkit.getDefaultToolkit().getImage("wk.png")); // White King

        pieceImages.put("bP", Toolkit.getDefaultToolkit().getImage("bp.png")); // Black Pawn
        pieceImages.put("bR", Toolkit.getDefaultToolkit().getImage("br.png")); // Black Rook
        pieceImages.put("bN", Toolkit.getDefaultToolkit().getImage("bn.png")); // Black Knight
        pieceImages.put("bB", Toolkit.getDefaultToolkit().getImage("bb.png")); // Black Bishop
        pieceImages.put("bQ", Toolkit.getDefaultToolkit().getImage("bq.png")); // Black Queen
        pieceImages.put("bK", Toolkit.getDefaultToolkit().getImage("bk.png")); // Black King
    }

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

                // Handle capture (capturing the opponent's piece)
                if (!capturedPiece.equals(".")) {
                    System.out.println("Captured: " + capturedPiece);
                }
            } else {
                selectedPiece = null;
                validMoves.clear();
                repaint();
            }
        }
    }

    // Simplified valid moves calculation for all pieces (adjust to your needs)
    private Set<String> calculateValidMoves(String piece, int x, int y) {
        Set<String> moves = new HashSet<>();
        if (piece.equals("P") || piece.equals("p")) {
            int direction = piece.equals("P") ? -1 : 1;
            if (y + direction >= 0 && y + direction < 8) {
                if (board[y + direction][x].equals(".")) {
                    moves.add(x + "," + (y + direction));
                }
                // Capture diagonally
                if (x + 1 < 8 && !board[y + direction][x + 1].equals(".")) {
                    moves.add((x + 1) + "," + (y + direction));
                }
                if (x - 1 >= 0 && !board[y + direction][x - 1].equals(".")) {
                    moves.add((x - 1) + "," + (y + direction));
                }
            }
        }
        return moves;
    }

    // Unused mouse events
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        new ChessGame();
    }
}
