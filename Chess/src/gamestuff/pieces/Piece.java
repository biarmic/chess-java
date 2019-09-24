package gamestuff.pieces;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import gamestuff.PieceColor;
import gamestuff.PieceType;
import gamestuff.Position;
import managers.PieceListener;
import windows.Board;
import windows.ColorWindow;

public abstract class Piece extends JLabel {
	public static ImageIcon[][] icons = new ImageIcon[2][6];
	public static Board board;
	private PieceType type;
	private PieceColor color;
	private int row;
	private int column;
	private boolean isEaten = false;
	private boolean hasMoved = false;
	public Piece(PieceType type, PieceColor color, int row, int column) {
		this.type = type;
		this.color = color;
		this.row = row;
		this.column = column;
		updateIcon();
		setBounds(50+column*100,50+row*100,100,100);
		PieceListener listener = new PieceListener(this);
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}
	public PieceType getPieceType() {
		return type;
	}
	public PieceColor getPieceColor() {
		return color;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public boolean isEaten() {
		return isEaten;
	}
	public boolean hasMoved() {
		return hasMoved;
	}
	public void updateIcon() {
		setIcon(icons[color.index()-1][type.index()-1]);
	}
	public void setPosition(Position position) {
		setPosition(position.getRow(),position.getColumn());
	}
	public void setPosition(int row, int column) {
		hasMoved = true;
		this.row = row;
		this.column = column;
		resetPosition();
	}
	public void resetPosition() {
		setLocation(50+column*100,50+row*100);
		board.setLayer(this,new Integer(1));
	}
	public void setEaten(boolean isEaten) {
		this.isEaten = isEaten;
	}
	public void setMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	public static void updateIcons() {
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 6; j++) {
				try {
					BufferedImage img = ImageIO.read(Piece.class.getResource("/images/pieces/"+PieceType.values()[j+1]+"-"+PieceColor.values()[i+1]+".png"));
					for(int w = 0; w < img.getWidth(); w++) {
						for(int h = 0; h < img.getHeight(); h++) {
							Color current = new Color(img.getRGB(w,h),true);
							if(current.getAlpha()>0) {
								int rgb = current.getRed()==255 ? ColorWindow.THEME_COLOR.getRGB() : ColorWindow.OPPOSITE_COLOR.getRGB();
								img.setRGB(w,h,rgb);
							}
						}
					}
					icons[i][j] = new ImageIcon(img);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
