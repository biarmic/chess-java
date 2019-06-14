import java.io.IOException;
import java.util.ArrayList;

public class Notation {
	private static final int PAWN = 1;
	private static final int ROOK = 2;
	private static final int KNIGHT = 3;
	private static final int BISHOP = 4;
	private static final int QUEEN = 5;
	private static final int KING = 6;
	private int[][] board = new int[8][8];
	private boolean[] kingMoved = new boolean[2];
	private boolean[][] rookMoved = new boolean[2][2];
	public Notation(Piece[][] pieces) {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 16; j++) {
				Piece a = pieces[i][j];
				if(!a.isEaten()) {
					if(a instanceof Pawn) {
						board[a.getRow()][a.getColumn()] = PAWN + (a.isWhite() ? 10 : 20);
					}else if(a instanceof Rook) {
						board[a.getRow()][a.getColumn()] = ROOK + (a.isWhite() ? 10 : 20);
					}else if(a instanceof Knight) {
						board[a.getRow()][a.getColumn()] = KNIGHT + (a.isWhite() ? 10 : 20);
					}else if(a instanceof Bishop) {
						board[a.getRow()][a.getColumn()] = BISHOP + (a.isWhite() ? 10 : 20);
					}else if(a instanceof Queen) {
						board[a.getRow()][a.getColumn()] = QUEEN + (a.isWhite() ? 10 : 20);
					}else if(a instanceof King) {
						board[a.getRow()][a.getColumn()] = KING + (a.isWhite() ? 10 : 20);
					}
				}
			}
		}
	}
	public void move(Piece piece, int row, int column) {
		if(piece instanceof King) {
			kingMoved[piece.isWhite() ? 0 : 1] = true;
		}else if(piece instanceof Rook) {
			rookMoved[piece.isWhite() ? 0 : 1][column-piece.getColumn()<0 ? 0 : 1] = true;
		}
		int prevRow = piece.getRow();
		int prevColumn = piece.getColumn();
		int value = board[prevRow][prevColumn];
		board[prevRow][prevColumn] = 0;
		board[row][column] = value;
	}
	private boolean isInBoard(int row, int column) {
		if(row<0 || row>7 || column<0 || column>7)
			return false;
		else
			return true;
	}
	private ArrayList<Position> combineLists(ArrayList<Position> list1, ArrayList<Position> list2) {
		for(Position a : list2) {
			list1.add(a);
		}
		return list1;
	}
	private int findKingRow(boolean isWhite) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j]%10==6 && board[i][j]/10==(isWhite ? 1 : 2)) {
					return i;
				}
			}
		}
		return 0;
	}
	private int findKingColumn(boolean isWhite) {
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				if(board[i][j]%10==6 && board[i][j]/10==(isWhite ? 1 : 2)) {
					return j;
				}
			}
		}
		return 0;
	}
	public ArrayList<Position> getRange(Piece piece) throws IOException {
		int prevRow = piece.getRow();
		int prevColumn = piece.getColumn();
		boolean isWhite = piece.isWhite();
		int moving = board[prevRow][prevColumn];
		ArrayList<Position> positions = rangeFinder(prevRow,prevColumn,isWhite);
		for(int i = 0; i < positions.size(); i++) {
			if(piece instanceof King && prevRow==(isWhite ? 7 : 0)  &&Math.abs(positions.get(i).getColumn()-prevColumn)==2) {//Castling
				int row = positions.get(i).getRow();
				int column = positions.get(i).getColumn();
				int direction = column-prevColumn;
				int rookRow = isWhite ? 7 : 0;
				int rookPrevColumn = direction<0 ? 0 : 7;
				int rookColumn = direction<0 ? column+1 : column-1;
				int rook = board[rookRow][rookPrevColumn];
				board[prevRow][prevColumn] = 0;
				board[row][column] = moving;
				board[rookRow][rookPrevColumn] = 0;
				board[rookRow][rookColumn] = rook;
				if(isPositionThreatened(prevRow,prevColumn,isWhite) || isPositionThreatened(prevRow,prevColumn + (direction<0 ? -1 : 1),isWhite) || isPositionThreatened(prevRow,prevColumn + (direction<0 ? -2 : 2),isWhite)) {
					positions.remove(i);
					i--;
				}
				board[rookRow][rookColumn] = 0;
				board[rookRow][rookPrevColumn] = rook;
				board[row][column] = 0;
				board[prevRow][prevColumn] = moving;
			}else {
				int row = positions.get(i).getRow();
				int column = positions.get(i).getColumn();
				int eating = board[row][column];
				board[prevRow][prevColumn] = 0;
				board[row][column] = moving;
				if(isPositionThreatened(findKingRow(isWhite),findKingColumn(isWhite),isWhite)) {
					positions.remove(i);
					i--;
				}
				board[row][column] = eating;
				board[prevRow][prevColumn] = moving;
			}
		}
		return positions;
	}
	public ArrayList<Position> rangeFinder(int row, int column, boolean isWhite) throws IOException{
		int piece = board[row][column];
		if(piece%10==PAWN) {
			return pawnRangeFinder(row,column,isWhite);
		}else if(piece%10==ROOK) {
			return straightRangeFinder(row,column,isWhite,7);
		}else if(piece%10==KNIGHT) {
			return knightRangeFinder(row,column,isWhite);
		}else if(piece%10==BISHOP) {
			return diagonalRangeFinder(row,column,isWhite,7);
		}else if(piece%10==QUEEN) {
			return combineLists(straightRangeFinder(row,column,isWhite,7),diagonalRangeFinder(row,column,isWhite,7));
		}else if(piece%10==KING) {
			ArrayList<Position> positions = combineLists(straightRangeFinder(row,column,isWhite,1),diagonalRangeFinder(row,column,isWhite,1));
			if(!kingMoved[isWhite ? 0 : 1] && !rookMoved[isWhite ? 0 : 1][0] && board[isWhite ? 7 : 0][1]==0 && board[isWhite ? 7 : 0][2]==0 && board[isWhite ? 7 : 0][3]==0) {
				positions.add(new Position(isWhite ? 7 : 0,2,new ChessListener()));
			}
			if(!kingMoved[isWhite ? 0 : 1] && !rookMoved[isWhite ? 0 : 1][1] && board[isWhite ? 7 : 0][5]==0 && board[isWhite ? 7 : 0][6]==0) {
				positions.add(new Position(isWhite ? 7 : 0,6,new ChessListener()));
			}
			return positions;
		}
		return null;
	}
	private ArrayList<Position> pawnRangeFinder(int row, int column, boolean isWhite) throws IOException {
		ArrayList<Position> positions = new ArrayList<Position>();
		if(isInBoard(isWhite ? row-1 : row+1,column) && board[isWhite ? row-1 : row+1][column]==0) {
			positions.add(new Position((isWhite ? row-1 : row+1),column,new ChessListener()));
			if(row==(isWhite ? 6 : 1) && board[isWhite ? row-2 : row+2][column]==0) {
				positions.add(new Position((isWhite ? row-2 : row+2),column,new ChessListener()));
			}
		}
		if(isInBoard(isWhite ? row-1 : row+1,column-1) && board[isWhite ? row-1 : row+1][column-1]!=0 && board[isWhite ? row-1 : row+1][column-1]/10==(isWhite ? 2 : 1)) {
			positions.add(new Position((isWhite ? row-1 : row+1),column-1,new ChessListener()));
		}
		if(isInBoard(isWhite ? row-1 : row+1,column+1) && board[isWhite ? row-1 : row+1][column+1]!=0 && board[isWhite ? row-1 : row+1][column+1]/10==(isWhite ? 2 : 1)) {
			positions.add(new Position((isWhite ? row-1 : row+1),column+1,new ChessListener()));
		}
		return positions;
	}
	private ArrayList<Position> straightRangeFinder(int row, int column, boolean isWhite, int maxRange) throws IOException{
		ArrayList<Position> positions = new ArrayList<Position>();
		boolean left = true;
		boolean right = true;
		boolean up = true;
		boolean down = true;
		for(int i = 1; i < maxRange+1; i++) {
			boolean isInBoard = isInBoard(row,column-i);
			if(left && isInBoard && board[row][column-i]==0) {//LEFT
				positions.add(new Position(row,column-i,new ChessListener()));
			}else if(left && isInBoard && board[row][column-i]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row,column-i,new ChessListener()));
				left = false;
			}else if(left && isInBoard) {
				left = false;
			}
			isInBoard = isInBoard(row,column+i);
			if(right && isInBoard && board[row][column+i]==0) {//RIGHT
				positions.add(new Position(row,column+i,new ChessListener()));
			}else if(right && isInBoard && board[row][column+i]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row,column+i,new ChessListener()));
				right = false;
			}else if(right && isInBoard) {
				right = false;
			}
			isInBoard = isInBoard(row-i,column);
			if(up && isInBoard && board[row-i][column]==0) {//UP
				positions.add(new Position(row-i,column,new ChessListener()));
			}else if(up && isInBoard && board[row-i][column]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row-i,column,new ChessListener()));
				up = false;
			}else if(up && isInBoard) {
				up = false;
			}
			isInBoard = isInBoard(row+i,column);
			if(down && isInBoard && board[row+i][column]==0) {//DOWN
				positions.add(new Position(row+i,column,new ChessListener()));
			}else if(down && isInBoard && board[row+i][column]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row+i,column,new ChessListener()));
				down = false;
			}else if(down && isInBoard) {
				down = false;
			}
		}
		return positions;
	}
	private ArrayList<Position> diagonalRangeFinder(int row, int column, boolean isWhite, int maxRange) throws IOException{
		ArrayList<Position> positions = new ArrayList<Position>();
		boolean upLeft = true;
		boolean upRight = true;
		boolean downLeft = true;
		boolean downRight = true;
		for(int i = 1; i < maxRange+1; i++) {
			boolean isInBoard = isInBoard(row-i,column-i);
			if(upLeft && isInBoard && board[row-i][column-i]==0) {//UP LEFT
				positions.add(new Position(row-i,column-i,new ChessListener()));
			}else if(upLeft && isInBoard && board[row-i][column-i]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row-i,column-i,new ChessListener()));
				upLeft = false;
			}else if(upLeft && isInBoard) {
				upLeft = false;
			}
			isInBoard = isInBoard(row-i,column+i);
			if(upRight && isInBoard && board[row-i][column+i]==0) {//UP RIGHT
				positions.add(new Position(row-i,column+i,new ChessListener()));
			}else if(upRight && isInBoard && board[row-i][column+i]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row-i,column+i,new ChessListener()));
				upRight = false;
			}else if(upRight && isInBoard) {
				upRight = false;
			}
			isInBoard = isInBoard(row+i,column-i);
			if(downLeft && isInBoard && board[row+i][column-i]==0) {//DOWN LEFT
				positions.add(new Position(row+i,column-i,new ChessListener()));
			}else if(downLeft && isInBoard && board[row+i][column-i]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row+i,column-i,new ChessListener()));
				downLeft = false;
			}else if(downLeft && isInBoard) {
				downLeft = false;
			}
			isInBoard = isInBoard(row+i,column+i);
			if(downRight && isInBoard && board[row+i][column+i]==0) {//DOWN RIGHT
				positions.add(new Position(row+i,column+i,new ChessListener()));
			}else if(downRight && isInBoard && board[row+i][column+i]/10==(isWhite ? 2 : 1)){
				positions.add(new Position(row+i,column+i,new ChessListener()));
				downRight = false;
			}else if(downRight && isInBoard) {
				downRight = false;
			}
		}
		return positions;
	}
	private ArrayList<Position> knightRangeFinder(int row, int column, boolean isWhite) throws IOException {
		ArrayList<Position> positions = new ArrayList<Position>();
		for(int i = -1; i < 2; i+=2) {
			for(int j = -1; j < 2; j+=2) {
				if(isInBoard(row+2*i,column+1*j) && (board[row+2*i][column+1*j]==0 || board[row+2*i][column+1*j]/10==(isWhite ? 2 : 1))) {
					positions.add(new Position(row+2*i,column+1*j,new ChessListener()));
				}
				if(isInBoard(row+1*i,column+2*j) && (board[row+1*i][column+2*j]==0 || board[row+1*i][column+2*j]/10==(isWhite ? 2 : 1))) {
					positions.add(new Position(row+1*i,column+2*j,new ChessListener()));
				}
			}
		}
		return positions;
	}
	private boolean isPositionThreatened(int row, int column, boolean isWhite) throws IOException {
		ArrayList<Position> range = straightRangeFinder(row,column,isWhite,7);
		for(Position a : range) {
			int looking = board[a.getRow()][a.getColumn()];
			if(looking!=0 && looking/10==(isWhite ? 2 : 1) && (looking%10==ROOK || looking%10==QUEEN)) {
				return true;
			}else if(looking!=0 && looking/10==(isWhite ? 2 : 1) && looking%10==KING && (Math.abs(a.getRow()-row)==1 || Math.abs(a.getColumn()-column)==1)) {
				return true;
			}
		}
		range = diagonalRangeFinder(row,column,isWhite,7);
		for(Position a : range) {
			int looking = board[a.getRow()][a.getColumn()];
			if(looking!=0 && looking/10==(isWhite ? 2 : 1) && (looking%10==BISHOP || looking%10==QUEEN)) {
				return true;
			}else if(looking!=0 && looking/10==(isWhite ? 2 : 1) && looking%10==KING && (Math.abs(a.getRow()-row)==1 || Math.abs(a.getColumn()-column)==1)) {
				return true;
			}
		}
		range = knightRangeFinder(row,column,isWhite);
		for(Position a : range) {
			int looking = board[a.getRow()][a.getColumn()];
			if(looking!=0 && looking/10==(isWhite ? 2 : 1) && (looking%10==KNIGHT)) {
				return true;
			}
		}
		for(int i = -1; i < 2; i+=2) {
			int looking = board[isWhite ? row-1 : row+1][column+i];
			if(looking!=0 && looking/10==(isWhite ? 2 : 1) && (looking%10==PAWN)) {
				return true;
			}
		}
		return false;
	}
}
