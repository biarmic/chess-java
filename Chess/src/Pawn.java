import java.io.IOException;

public class Pawn extends Piece{
	public Pawn(boolean isWhite, int row, int column) throws IOException {
		super(isWhite,row,column,isWhite ? "pawn_white.png" : "pawn_black.png");
	}
}
