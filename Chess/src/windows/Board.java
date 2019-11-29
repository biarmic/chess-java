package windows;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import gamestuff.PieceColor;
import gamestuff.PieceType;
import gamestuff.Position;
import gamestuff.Tile;
import gamestuff.pieces.*;
import managers.GameManager;

public class Board extends JLayeredPane {
	public static final Integer GROUND_LAYER = new Integer(0);
	public static final Integer PIECE_LAYER = new Integer(1);
	public static final Integer DOT_LAYER = new Integer(2);
	public static final Integer DRAG_LAYER = new Integer(3);
	private static Board board;
	private Tile[][] tiles = new Tile[8][8];
	private Piece[][] pieces = new Piece[2][16];

	public Board() {
		board = this;
		setOpaque(true);
		setBackground(ColorWindow.OPPOSITE_COLOR);
		prepare();
	}

	public static void updateColors() {
		board.setBackground(ColorWindow.OPPOSITE_COLOR);
		for (Component comp : board.getComponentsInLayer(0)) {
			JLabel label = (JLabel) comp;
			if (label.getText().length() == 0)
				label.setBackground(
						((label.getX() - 50) / 100 + (label.getY() - 50) / 100) % 2 == 0 ? ColorWindow.THEME_COLOR
								: ColorWindow.OPPOSITE_COLOR);
			else
				label.setForeground(ColorWindow.THEME_COLOR);
		}
		Piece.updateIcons();
		for (Component comp : board.getComponentsInLayer(1)) {
			if (comp instanceof Piece)
				((Piece) comp).updateIcon();
			else
				((Button) comp).updateIcon();
		}
	}

	private void prepare() {
		Font font = new Font(GameManager.font.getName(), Font.PLAIN, 38);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				JLabel groundTile = new JLabel();
				groundTile.setOpaque(true);
				groundTile.setBackground((i + j) % 2 == 0 ? ColorWindow.THEME_COLOR : ColorWindow.OPPOSITE_COLOR);
				groundTile.setBounds(50 + j * 100, 50 + i * 100, 100, 100);
				add(groundTile, GROUND_LAYER);
				tiles[i][j] = new Tile(i, j);
			}
			JLabel letter = new JLabel("" + (char) (65 + i));
			letter.setBounds(88 + 100 * i, 853, 50, 50);
			letter.setForeground(ColorWindow.THEME_COLOR);
			letter.setFont(font);
			add(letter, GROUND_LAYER);
			JLabel number = new JLabel("" + (8 - i));
			number.setBounds(12, 82 + 100 * i, 50, 50);
			number.setForeground(ColorWindow.THEME_COLOR);
			number.setFont(font);
			add(number, GROUND_LAYER);
		}
		JLabel frame = new JLabel();
		frame.setOpaque(true);
		frame.setBackground(ColorWindow.THEME_COLOR);
		frame.setBounds(45, 45, 810, 810);
		add(frame, GROUND_LAYER);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				pieces[i][j] = new Pawn(PieceColor.values()[i + 1], i == 0 ? 6 : 1, j);
				add(pieces[i][j], PIECE_LAYER);
				tiles[i == 0 ? 6 : 1][j].placePiece(PieceType.pawn, PieceColor.values()[i + 1]);
			}
			for (int j = 0; j < 2; j++) {
				pieces[i][8 + j] = new Rook(PieceColor.values()[i + 1], i == 0 ? 7 : 0, j * 7);
				pieces[i][10 + j] = new Knight(PieceColor.values()[i + 1], i == 0 ? 7 : 0, 1 + j * 5);
				pieces[i][12 + j] = new Bishop(PieceColor.values()[i + 1], i == 0 ? 7 : 0, 2 + j * 3);
				add(pieces[i][8 + j], PIECE_LAYER);
				add(pieces[i][10 + j], PIECE_LAYER);
				add(pieces[i][12 + j], PIECE_LAYER);
				tiles[i == 0 ? 7 : 0][j * 7].placePiece(PieceType.rook, PieceColor.values()[i + 1]);
				tiles[i == 0 ? 7 : 0][1 + j * 5].placePiece(PieceType.knight, PieceColor.values()[i + 1]);
				tiles[i == 0 ? 7 : 0][2 + j * 3].placePiece(PieceType.bishop, PieceColor.values()[i + 1]);
			}
			pieces[i][14] = new Queen(PieceColor.values()[i + 1], i == 0 ? 7 : 0, 3);
			pieces[i][15] = new King(PieceColor.values()[i + 1], i == 0 ? 7 : 0, 4);
			add(pieces[i][14], PIECE_LAYER);
			add(pieces[i][15], PIECE_LAYER);
			tiles[i == 0 ? 7 : 0][3].placePiece(PieceType.queen, PieceColor.values()[i + 1]);
			tiles[i == 0 ? 7 : 0][4].placePiece(PieceType.king, PieceColor.values()[i + 1]);
		}
	}

	public Piece[][] getPieces() {
		return pieces;
	}

	public Tile findTile(int row, int column) {
		return tiles[row][column];
	}

	private Tile findTile(PieceType type, PieceColor color) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (tiles[i][j].getPieceType() == type && tiles[i][j].getColor() == color)
					return tiles[i][j];
			}
		}
		return null;
	}

	public Piece findPiece(int row, int column, int colorIndex) {
		for (int i = 0; i < 16; i++) {
			Piece piece = pieces[colorIndex - 1][i];
			if (!piece.isEaten() && piece.getRow() == row && piece.getColumn() == column)
				return piece;
		}
		return null;
	}

	public int getPieceIndex(Piece piece, int colorIndex) {
		for (int i = 0; i < 16; i++) {
			if (pieces[colorIndex - 1][i] == piece)
				return i;
		}
		return -1;
	}

	public void removeDots() {
		for (Component comp : getComponentsInLayer(2))
			remove(comp);
		repaint();
	}

	public Piece promotePawn(int colorIndex, PieceType type) {
		for (int i = 0; i < 8; i++) {
			Piece piece = pieces[colorIndex - 1][i];
			if (piece instanceof Pawn && piece.getRow() == (colorIndex == 1 ? 0 : 7)) {
				remove(piece);
				if (type == PieceType.queen)
					pieces[colorIndex - 1][i] = new Queen(PieceColor.values()[colorIndex], piece.getRow(),
							piece.getColumn());
				else if (type == PieceType.knight)
					pieces[colorIndex - 1][i] = new Knight(PieceColor.values()[colorIndex], piece.getRow(),
							piece.getColumn());
				else if (type == PieceType.rook)
					pieces[colorIndex - 1][i] = new Rook(PieceColor.values()[colorIndex], piece.getRow(),
							piece.getColumn());
				else
					pieces[colorIndex - 1][i] = new Bishop(PieceColor.values()[colorIndex], piece.getRow(),
							piece.getColumn());
				tiles[piece.getRow()][piece.getColumn()].placePiece(type, piece.getPieceColor());
				add(pieces[colorIndex - 1][i], PIECE_LAYER);
				return pieces[colorIndex - 1][i];
			}
		}
		return null;
	}

	public Piece movePiece(Piece piece, Position destination) {
		tiles[piece.getRow()][piece.getColumn()].removePiece();
		if (tiles[destination.getRow()][destination.getColumn()].placePiece(piece.getPieceType(),
				piece.getPieceColor()) != PieceType.empty) {
			Piece eaten = findPiece(destination.getRow(), destination.getColumn(),
					GameManager.turnColor.opposite().index());
			remove(eaten);
			eaten.setEaten(true);
			return eaten;
		}
		return null;
	}

	private boolean isInBoard(int row, int column) {
		return !(row < 0 || row > 7 || column < 0 || column > 7);
	}

	public ArrayList<Position> getRange(int row, int column) {
		ArrayList<Position> range = null;
		if (tiles[row][column].getPieceType() == PieceType.pawn) {
			range = getPawnRange(row, column);
			range.addAll(getEnPassantRange(row, column));
		} else if (tiles[row][column].getPieceType() == PieceType.rook)
			range = getStraightRange(row, column, 7);
		else if (tiles[row][column].getPieceType() == PieceType.knight)
			range = getKnightRange(row, column);
		else if (tiles[row][column].getPieceType() == PieceType.bishop)
			range = getDiagonalRange(row, column, 7);
		else if (tiles[row][column].getPieceType() == PieceType.queen) {
			range = getStraightRange(row, column, 7);
			range.addAll(getDiagonalRange(row, column, 7));
		} else if (tiles[row][column].getPieceType() == PieceType.king) {
			range = getStraightRange(row, column, 1);
			range.addAll(getDiagonalRange(row, column, 1));
			range.addAll(getCastlingRange(row, column));
		}
		return removeThreatenedPositions(range, row, column);
	}

	private ArrayList<Position> getStraightRange(int row, int column, int maxRange) {
		ArrayList<Position> positions = new ArrayList<Position>();
		PieceColor color = tiles[row][column].getColor();
		for (int rotation = 0; rotation < 2; rotation++) {
			for (int direction = -1; direction < 2; direction += 2) {
				for (int i = 1; i <= maxRange; i++) {
					if (isInBoard(row + i * direction * (rotation == 0 ? 0 : 1),
							column + i * direction * (rotation == 0 ? 1 : 0))) {
						Tile current = tiles[row + i * direction * (rotation == 0 ? 0 : 1)][column
								+ i * direction * (rotation == 0 ? 1 : 0)];
						if (current.getPieceType() == PieceType.empty || current.getColor() != color)
							positions.add(new Position(row + i * direction * (rotation == 0 ? 0 : 1),
									column + i * direction * (rotation == 0 ? 1 : 0)));
						if (current.getColor() != PieceColor.empty)
							break;
					} else
						break;
				}
			}
		}
		return positions;
	}

	private ArrayList<Position> getDiagonalRange(int row, int column, int maxRange) {
		ArrayList<Position> positions = new ArrayList<Position>();
		PieceColor color = tiles[row][column].getColor();
		for (int rotation = -1; rotation < 2; rotation += 2) {
			for (int direction = -1; direction < 2; direction += 2) {
				for (int i = 1; i <= maxRange; i++) {
					if (isInBoard(row + i * direction, column + i * rotation)) {
						Tile current = tiles[row + i * direction][column + i * rotation];
						if (current.getPieceType() == PieceType.empty || current.getColor() != color)
							positions.add(new Position(row + i * direction, column + i * rotation));
						if (current.getColor() != PieceColor.empty)
							break;
					} else
						break;
				}
			}
		}
		return positions;
	}

	private ArrayList<Position> getKnightRange(int row, int column) {
		ArrayList<Position> positions = new ArrayList<Position>();
		PieceColor color = tiles[row][column].getColor();
		for (int rotation = -1; rotation < 2; rotation += 2) {
			for (int direction = -1; direction < 2; direction += 2) {
				for (int i = 0; i < 2; i++) {
					if (isInBoard(row + (i == 0 ? 1 : 2) * direction, column + (i == 0 ? 2 : 1) * rotation)) {
						Tile current = tiles[row + (i == 0 ? 1 : 2) * direction][column + (i == 0 ? 2 : 1) * rotation];
						if (current.getPieceType() == PieceType.empty || current.getColor() != color)
							positions.add(new Position(row + (i == 0 ? 1 : 2) * direction,
									column + (i == 0 ? 2 : 1) * rotation));
					}
				}
			}
		}
		return positions;
	}

	private ArrayList<Position> getPawnRange(int row, int column) {
		ArrayList<Position> positions = new ArrayList<Position>();
		PieceColor color = tiles[row][column].getColor();
		for (int rotation = -1; rotation < 2; rotation += 2) {
			if (isInBoard(row + (color == PieceColor.white ? -1 : 1), column + rotation)) {
				Tile current = tiles[row + (color == PieceColor.white ? -1 : 1)][column + rotation];
				if (current.getPieceType() != PieceType.empty && current.getColor() != color)
					positions.add(new Position(row + (color == PieceColor.white ? -1 : 1), column + rotation));
			}
		}
		if (isInBoard(row + (color == PieceColor.white ? -1 : 1), column)) {
			Tile current = tiles[row + (color == PieceColor.white ? -1 : 1)][column];
			if (current.getPieceType() == PieceType.empty) {
				positions.add(new Position(row + (color == PieceColor.white ? -1 : 1), column));
				if (isInBoard(row + (color == PieceColor.white ? -2 : 2), column)) {
					current = tiles[row + (color == PieceColor.white ? -2 : 2)][column];
					if ((color == PieceColor.white ? row == 6 : row == 1) && current.getPieceType() == PieceType.empty)
						positions.add(new Position(row + (color == PieceColor.white ? -2 : 2), column));
				}
			}
		}
		return positions;
	}

	private ArrayList<Position> getCastlingRange(int row, int column) {
		ArrayList<Position> positions = new ArrayList<Position>();
		PieceColor color = tiles[row][column].getColor();
		if (!findPiece(row, column, color.index()).hasMoved()) {
			if (findPiece(row, column - 4, color.index()) != null
					&& !findPiece(row, column - 4, color.index()).hasMoved()) {
				boolean areEmpty = true;
				for (int i = 1; i <= 3; i++) {
					if (tiles[row][column - i].getPieceType() != PieceType.empty) {
						areEmpty = false;
						break;
					}
				}
				if (areEmpty) {
					boolean isThreatened = false;
					for (int i = 0; i < 3; i++) {
						if (isKingThreatened(PieceType.king, row, column, row, column - i)) {
							isThreatened = true;
							break;
						}
					}
					if (!isThreatened) {
						positions.add(new Position(row, column - 2));
					}
				}
			}
			if (findPiece(row, column + 3, color.index()) != null
					&& !findPiece(row, column + 3, color.index()).hasMoved()) {
				for (int i = 1; i <= 2; i++) {
					if (tiles[row][column + i].getPieceType() != PieceType.empty)
						return positions;
				}
				for (int i = 0; i < 3; i++) {
					if (isKingThreatened(PieceType.king, row, column, row, column + i))
						return positions;
				}
				positions.add(new Position(row, column + 2));
			}
		}
		return positions;
	}

	private ArrayList<Position> getEnPassantRange(int row, int column) {
		ArrayList<Position> positions = new ArrayList<Position>();
		if (row == (GameManager.turnColor == PieceColor.white ? 3 : 4)) {
			String lastLine = GameManager.readLastRecordLine();
			if (lastLine.length() > 0) {
				StringTokenizer tokenizer = new StringTokenizer(lastLine, ".");
				if (tokenizer.nextToken().equals("pawn")) {
					int index = Integer.parseInt(tokenizer.nextToken());
					if (Boolean.parseBoolean(tokenizer.nextToken())) {
						int prevRow = Integer.parseInt(tokenizer.nextToken());
						int prevColumn = Integer.parseInt(tokenizer.nextToken());
						int curRow = Integer.parseInt(tokenizer.nextToken());
						int curColumn = Integer.parseInt(tokenizer.nextToken());
						if (Math.abs(prevRow - curRow) == 2 && Math.abs(column - curColumn) == 1)
							positions.add(new Position(row + (GameManager.turnColor == PieceColor.white ? -1 : 1),
									curColumn));
					}
				}
			}
		}
		return positions;
	}

	private ArrayList<Position> removeThreatenedPositions(ArrayList<Position> range, int currentRow,
			int currentColumn) {
		PieceType type = tiles[currentRow][currentColumn].getPieceType();
		Iterator<Position> iterator = range.iterator();
		while (iterator.hasNext()) {
			Position destination = iterator.next();
			if (isKingThreatened(type, currentRow, currentColumn, destination.getRow(), destination.getColumn()))
				iterator.remove();
		}
		return range;
	}

	public boolean isKingThreatened(PieceType movingType, int currentRow, int currentColumn, int destinationRow,
			int destinationColumn) {
		Tile currentTile = tiles[currentRow][currentColumn];
		currentTile.removePiece();
		PieceType previousType = tiles[destinationRow][destinationColumn].placePiece(movingType, GameManager.turnColor);
		boolean isThreatened = isKingThreatened();
		tiles[destinationRow][destinationColumn].placePiece(previousType,
				previousType == PieceType.empty ? PieceColor.empty : GameManager.turnColor.opposite());
		currentTile.placePiece(movingType, GameManager.turnColor);
		return isThreatened;
	}

	public boolean isKingThreatened() {
		boolean isThreatened = false;
		Tile kingTile = findTile(PieceType.king, GameManager.turnColor);
		int kingRow = kingTile.getRow();
		int kingColumn = kingTile.getColumn();
		ArrayList<Position> straight = getStraightRange(kingRow, kingColumn, 7);
		for (Position position : straight) {
			Tile tile = tiles[position.getRow()][position.getColumn()];
			if (tile.getPieceType() == PieceType.rook || tile.getPieceType() == PieceType.queen) {
				isThreatened = true;
				break;
			} else if (tile.getPieceType() == PieceType.king && Math.abs(kingRow - position.getRow()) <= 1
					&& Math.abs(kingColumn - position.getColumn()) <= 1) {
				isThreatened = true;
				break;
			}
		}
		if (!isThreatened) {
			ArrayList<Position> diagonal = getDiagonalRange(kingRow, kingColumn, 7);
			for (Position position : diagonal) {
				Tile tile = tiles[position.getRow()][position.getColumn()];
				if (tile.getPieceType() == PieceType.bishop || tile.getPieceType() == PieceType.queen) {
					isThreatened = true;
					break;
				} else if (tile.getPieceType() == PieceType.king && Math.abs(kingRow - position.getRow()) <= 1
						&& Math.abs(kingColumn - position.getColumn()) <= 1) {
					isThreatened = true;
					break;
				}
			}
			if (!isThreatened) {
				ArrayList<Position> knight = getKnightRange(kingRow, kingColumn);
				for (Position position : knight) {
					Tile tile = tiles[position.getRow()][position.getColumn()];
					if (tile.getPieceType() == PieceType.knight) {
						isThreatened = true;
						break;
					}
				}
				if (!isThreatened) {
					for (int rotation = -1; rotation < 2; rotation += 2) {
						if (isInBoard(kingRow + (GameManager.turnColor == PieceColor.white ? -1 : 1),
								kingColumn + rotation)) {
							Tile tile = tiles[kingRow + (GameManager.turnColor == PieceColor.white ? -1 : 1)][kingColumn
									+ rotation];
							if (tile.getPieceType() == PieceType.pawn && tile.getColor() != GameManager.turnColor) {
								isThreatened = true;
								break;
							}
						}
					}
				}
			}
		}
		return isThreatened;
	}
}
