import java.io.IOException;
import java.util.ArrayList;

public class Pawn extends Piece{
	public Pawn(boolean isWhite, int row, int column) throws IOException {
		super(isWhite,row,column,isWhite ? "pawn_white.png" : "pawn_black.png");
	}
}