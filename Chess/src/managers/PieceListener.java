package managers;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import gamestuff.PieceColor;
import gamestuff.Dot;
import gamestuff.Position;
import gamestuff.pieces.King;
import gamestuff.pieces.Pawn;
import gamestuff.pieces.Piece;
import windows.Board;
import windows.PromotionWindow;
import windows.Screen;

public class PieceListener extends MouseAdapter {
	public static Board board;
	private Piece source;
	private ArrayList<Position> range = null;
	public static ArrayList<Dot> dotRange = new ArrayList<Dot>();
	private boolean canMove = false;

	public PieceListener(Piece source) {
		this.source = source;
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		if (source.getPieceColor() == GameManager.turnColor) {
			source.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			canMove = true;
		} else {
			source.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			canMove = false;
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (canMove) {
			board.setLayer(source, Board.DRAG_LAYER);
			range = board.getRange(source.getRow(), source.getColumn());
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (canMove) {
			source.setLocation((int) event.getPoint().getX() + source.getX() - 50,
					(int) event.getPoint().getY() + source.getY() - 50);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (canMove) {
			Position destination = isInRange(source.getY() / 100, source.getX() / 100);
			if (destination != null) {
				movePiece(destination);
			} else {
				source.resetPosition();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (canMove) {
			if (dotRange.size() == 0) {
				dotRange.clear();
				for (Position position : range) {
					Dot dot = new Dot(position, this);
					board.add(dot, Board.DOT_LAYER);
					dotRange.add(dot);
				}
			} else {
				range = null;
				dotRange.clear();
				board.removeDots();
			}
		}
	}

	private Position isInRange(int row, int column) {
		for (Position position : range)
			if (position.getRow() == row && position.getColumn() == column)
				return position;
		return null;
	}

	public void movePiece(Position destination) {
		int prevRow = source.getRow();
		int prevColumn = source.getColumn();
		boolean isFirstMove = !source.hasMoved();
		dotRange.clear();
		board.removeDots();
		Piece eating = board.movePiece(source, destination);
		source.setPosition(destination);
		int difference = prevColumn - destination.getColumn();
		if (source instanceof King && Math.abs(difference) == 2) {
			((PieceListener) board.findPiece(prevRow, difference < 0 ? 7 : 0, source.getPieceColor().index())
					.getMouseListeners()[0]).movePiece(new Position(prevRow, difference < 0 ? 5 : 3));
			GameManager.eraseLastRecordLine();
			GameManager.writeRecord(difference < 0 ? "oo\n" : "ooo\n");
		} else {
			if (source instanceof Pawn && source.getRow() == (GameManager.turnColor == PieceColor.white ? 0 : 7)) {
				Screen.addToGlassPane(new PromotionWindow((Pawn) source, prevRow, prevColumn, board, eating));
			} else if (source instanceof Pawn && Math.abs(prevColumn - source.getColumn()) == 1 && eating == null
					&& GameManager.isTwoStepAdvance()) {
				Piece prevPawn = board.getPieces()[GameManager.turnColor.opposite().index() - 1][Integer
						.parseInt(GameManager.readLastRecordLine().substring(5, 6))];
				prevPawn.setEaten(true);
				board.findTile(prevPawn.getRow(), prevPawn.getColumn()).removePiece();
				board.remove(prevPawn);
				GameManager.writeRecord(getIndexLine(source) + ".false." + prevRow + "." + prevColumn + "."
						+ source.getRow() + "." + source.getColumn() + ".enpassant."
						+ board.getPieceIndex(prevPawn, prevPawn.getPieceColor().index()) + "\n");
			} else {
				if (eating == null) {
					GameManager.writeRecord(getIndexLine(source) + "." + isFirstMove + "." + prevRow + "." + prevColumn
							+ "." + source.getRow() + "." + source.getColumn() + "\n");
				} else {
					GameManager.writeRecord(getIndexLine(source) + "." + isFirstMove + "." + prevRow + "." + prevColumn
							+ "." + source.getRow() + "." + source.getColumn() + ".eat."
							+ board.getPieceIndex(eating, eating.getPieceColor().index()) + "\n");
				}
			}
			if (eating == null)
				AudioManager.playSound("move");
			else
				AudioManager.playSound("eat");
			GameManager.turnColor = GameManager.turnColor.opposite();
			GameManager.checkGame();
		}
	}

	public static String getIndexLine(Piece piece) {
		return piece.getPieceType() + "." + board.getPieceIndex(piece, piece.getPieceColor().index());
	}
}
