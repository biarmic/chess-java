package gamestuff;

public enum PieceType {
	empty, pawn, bishop, rook, knight, queen, king;
	public int index() {
		for (int i = 0; i < 7; i++)
			if (values()[i] == this)
				return i;
		return -1;
	}

	public String letter() {
		switch (this) {
		case pawn:
			return "";
		case knight:
			return "N";
		default:
			return "" + Character.toUpperCase(("" + this).charAt(0));
		}
	}
}
