import java.io.IOException;
import java.util.ArrayList;

public class Knight extends Piece{
	public Knight(boolean isWhite, int row, int column) throws IOException {
		super(isWhite,row,column,isWhite ? "knight_white.png" : "knight_black.png");
	}
}
