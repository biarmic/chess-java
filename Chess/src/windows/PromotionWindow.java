package windows;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import gamestuff.PieceType;
import gamestuff.pieces.Pawn;
import gamestuff.pieces.Piece;
import managers.GameManager;
import managers.PieceListener;

public class PromotionWindow extends Window {
	public PromotionWindow(Pawn pawn, int prevRow, int prevColumn, Board board, Piece eating) {
		super(615,250);
		JLabel label = new JLabel("<html><div align='center'>Choose a piece to promote your pawn</div></html>",JLabel.CENTER);
		label.setBounds(20,20,575,100);
		label.setFont(new Font(GameManager.font.getName(),Font.PLAIN,30));
		label.setForeground(Color.white);
		add(label,new Integer(0));
		JLabel[] pieces = new JLabel[4];
		try {
			for(int i = 0; i < 4; i++) {
				pieces[i] = new JLabel((new ImageIcon(ImageIO.read(getClass().getResource("/images/pieces/"+PieceType.values()[5-i]+"-black.png")))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < 4; i++) {
			pieces[i].setBounds(75+130*i,130,100,100);
			pieces[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			add(pieces[i],new Integer(1));
		}
		for(int i = 0; i < 4; i++) {
			final int a = i;
			pieces[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					Piece piece = board.promotePawn(pawn.getPieceColor().index(),PieceType.values()[5-a]);
					GameManager.checkGame();
					Screen.removeFromGlassPane(PromotionWindow.this);
					if(eating==null) {
						GameManager.writeRecord(PieceListener.getIndexLine(piece)+"."+false+"."+prevRow+"."+prevColumn+"."+pawn.getRow()+"."+pawn.getColumn()+".promote."+PieceType.values()[5-a]+"\n");
					}else {
						GameManager.writeRecord(PieceListener.getIndexLine(piece)+"."+false+"."+prevRow+"."+prevColumn+"."+pawn.getRow()+"."+pawn.getColumn()+".promote."+PieceType.values()[5-a]+"."+board.getPieceIndex(eating,eating.getPieceColor().index())+"\n");
					}
				}
			});
		}
	}
}
