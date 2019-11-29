package gamestuff.pieces;

import gamestuff.PieceColor;
import gamestuff.PieceType;

public class Pawn extends Piece {
	public Pawn(PieceColor color, int row, int column) {
		super(PieceType.pawn, color, row, column);
	}
}
