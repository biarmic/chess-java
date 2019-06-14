import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public abstract class Piece extends JLabel{
	private static Piece[][] pieces = null;
	private static Notation board = null;
	private boolean isWhite;
	private int row;
	private int column;
	private boolean isEaten;
	public Piece(boolean isWhite, int row, int column, String fileName) throws IOException {
		setIcon(new ImageIcon(ImageIO.read(getClass().getResource(fileName))));
		this.isWhite = isWhite;
		this.row = row;
		this.column = column;
		setBounds(column*100+50,row*100+50,100,100);
		addMouseListener(new ChessListener());
		addMouseMotionListener(new ChessListener());
	}
	public void setPieces(Piece[][] pieces) {
		Piece.pieces = pieces;
		board = new Notation(pieces);
	}
	public void setPosition(int row, int column) {
		board.move(this,row,column);
		this.row = row;
		this.column = column;
		setLocation(column*100+50,row*100+50);
	}
	public boolean isWhite() {
		return isWhite;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public Piece[][] getPieces(){
		return pieces;
	}
	public boolean isEaten() {
		return isEaten;
	}
	public Notation getBoard() {
		return new Notation(pieces);
	}
	public void returnPrevPosition() {
		setLocation(column*100+50,row*100+50);
	}
	public boolean isInBoard(int row, int column) {
		if(row<0 || row>7 || column<0 || column>7)
			return false;
		else
			return true;
	}
	public boolean isInRange(ArrayList<Position> range, int row, int column) {
		for(Position a : range) {
			if(a.getRow()==row && a.getColumn()==column)
				return true;
		}
		return false;
	}
	public void doesEat(int row, int column) {
		for(int i = 0; i < 16; i++) {
			if(!pieces[isWhite ? 1 : 0][i].isEaten() && pieces[isWhite ? 1 : 0][i].getRow()==row && pieces[isWhite ? 1 : 0][i].getColumn()==column) {
				pieces[isWhite ? 1 : 0][i].setEaten();
				break;
			}
		}
	}
	private void setEaten() {
		isEaten = true;
		setVisible(false);
		getParent().remove(this);
	}
	public Piece isTherePiece(int row, int column) {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 16; j++) {
				if(!pieces[i][j].isEaten && pieces[i][j].getRow()==row && pieces[i][j].getColumn()==column)
					return pieces[i][j];
			}
		}
		return null;
	}
	public ArrayList<Position> getRange(boolean isWhite) throws IOException {
		return board.getRange(this);
	}
}
