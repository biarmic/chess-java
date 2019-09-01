import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class Board {
	private JLayeredPane panel;
	private Piece[][] piece;
	public Board() {
		JLabel boardBG = new JLabel();
		try {
			boardBG.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("images/board.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		boardBG.setBounds(0,0,900,900);
		panel = new JLayeredPane();
		panel.setLayout(null);
		panel.add(boardBG,JLayeredPane.DEFAULT_LAYER);
		piece = new Piece[2][16]; //piece[0] is the white piece array, piece[1] is the black piece array
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 8; j++) {//Placing pawns
				if(i==0) {
					piece[1][j] = new Pawn(false,1,j);
					panel.add(piece[1][j],new Integer(1));
				}else {
					piece[0][j] = new Pawn(true,6,j);
					panel.add(piece[0][j],new Integer(1));
				}
			}
			for(int j = 0; j < 2; j++) {//Placing rooks
				if(i==0) {
					piece[1][j+8] = new Rook(false,0,j*7);
					panel.add(piece[1][j+8],new Integer(1));
				}else {
					piece[0][j+8] = new Rook(true,7,j*7);
					panel.add(piece[0][j+8],new Integer(1));
				}
			}
			for(int j = 0; j < 2; j++) {//Placing knights
				if(i==0) {
					piece[1][j+10] = new Knight(false,0,1+j*5);
					panel.add(piece[1][j+10],new Integer(1));
				}else {
					piece[0][j+10] = new Knight(true,7,1+j*5);
					panel.add(piece[0][j+10],new Integer(1));
				}
			}
			for(int j = 0; j < 2; j++) {//Placing bishops
				if(i==0) {
					piece[1][j+12] = new Bishop(false,0,2+j*3);
					panel.add(piece[1][j+12],new Integer(1));
				}else {
					piece[0][j+12] = new Bishop(true,7,2+j*3);
					panel.add(piece[0][j+12],new Integer(1));
				}
			}
			if(i==0) {//Placing queens
				piece[1][14] = new Queen(false,0,3);
				panel.add(piece[1][14],new Integer(1));
			}else {
				piece[0][14] = new Queen(true,7,3);
				panel.add(piece[0][14],new Integer(1));
			}
			if(i==0) {//Placing kings
				piece[1][15] = new King(false,0,4);
				panel.add(piece[1][15],new Integer(1));
			}else {
				piece[0][15] = new King(true,7,4);
				panel.add(piece[0][15],new Integer(1));
			}
		}
		piece[0][0].setPieces(piece);
	}
	public JLayeredPane getPanel() {
		return panel;
	}
}
