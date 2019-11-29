package gamestuff;

public enum PieceColor {
	empty, white, black;
	public int index() {
		for (int i = 0; i < 3; i++)
			if (values()[i] == this)
				return i;
		return -1;
	}

	public PieceColor opposite() {
		if (this == white)
			return black;
		else if (this == black)
			return white;
		return null;
	}
}
