import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class ChessListener implements MouseMotionListener, MouseListener{
	private static ArrayList<Notation> prevBoards = new ArrayList<Notation>();
	private static boolean turnOfWhite = true;
	private Piece source;
	private static ArrayList<Position> range;
	private static Piece rangePiece;
	private static boolean rangeRevealed = false;
	private int x;
	private int y;
	private static boolean dragged;
	@Override
	public void mouseClicked(MouseEvent event) {
		if(event.getSource() instanceof Piece && ((Piece) event.getSource()).isWhite()==turnOfWhite) {
			setSource(event);
			try {
				if(!rangeRevealed) {
					range = source.getRange(turnOfWhite);
					rangePiece = source;
					revealRange();
				}else {
					hideRange();
					rangeRevealed = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(event.getSource() instanceof Position && rangeRevealed){
			Position source = (Position) event.getSource();
			int x = source.getX();
			int y = source.getY();
			addPrevBoard(rangePiece.getBoard());
			rangePiece.doesEat(y/100,x/100);
			if(rangePiece instanceof King)
				((King)rangePiece).doesCastle(y/100,x/100,turnOfWhite);
			((JLayeredPane) rangePiece.getParent()).setLayer(rangePiece,JLayeredPane.PALETTE_LAYER);
			rangePiece.setPosition(y/100,x/100);
			hideRange();
			range = null;
			if(turnOfWhite)
				turnOfWhite = false;
			else
				turnOfWhite = true;
		}
	}
	@Override
	public void mouseDragged(MouseEvent event) {
		if(event.getSource() instanceof Piece && ((Piece) event.getSource()).isWhite()==turnOfWhite) {
			setSource(event);
			((JLayeredPane) source.getParent()).setLayer(source,JLayeredPane.POPUP_LAYER);
			source.setLocation(x,y);
			dragged = true;
		}
	}
	@Override
	public void mouseReleased(MouseEvent event) {
		if(dragged && event.getSource() instanceof Piece && ((Piece) event.getSource()).isWhite()==turnOfWhite) {
			setSource(event);
			try {
				if(rangePiece!=source) {
					if(rangeRevealed) {
						hideRange();
					}
					range = source.getRange(turnOfWhite);
					rangePiece = source;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(source.isInBoard(y/100,x/100) && source.isInRange(range,y/100,x/100)) {
				addPrevBoard(rangePiece.getBoard());
				source.doesEat(y/100,x/100);
				if(source instanceof King)
					((King)source).doesCastle(y/100,x/100,turnOfWhite);
				source.setPosition(y/100,x/100);
				if(rangeRevealed)
					hideRange();
				range = null;
				if(turnOfWhite)
					turnOfWhite = false;
				else
					turnOfWhite = true;
			}else {
				source.returnPrevPosition();
			}
			((JLayeredPane) source.getParent()).setLayer(source,JLayeredPane.PALETTE_LAYER);
			dragged = false;
		}
	}
	private void revealRange() {
		for(Position a : range) {
			((JLayeredPane)source.getParent()).add(a,JLayeredPane.MODAL_LAYER);
		}
		rangeRevealed = true;
	}
	private void hideRange() {
		for(Position a : range) {
			a.setVisible(false);
			((JLayeredPane)rangePiece.getParent()).remove(a);
		}
		rangeRevealed = false;
		rangePiece = null;
	}
	private void setSource(MouseEvent event) {
		source = (Piece) event.getSource();
		x = (int)event.getPoint().getX()+source.getX()-50;
		y = (int)event.getPoint().getY()+source.getY()-50;
	}
	private void addPrevBoard(Notation board) {
		prevBoards.add(board);
	}
	//EMPTY BODIES, these methods will not be used
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
