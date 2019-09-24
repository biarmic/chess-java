package windows;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import managers.GameManager;

public class Button extends JLabel {
	private String fileName;
	private int index;
	public Button(String fileName, int x, int y) {
		this.fileName = fileName;
		updateIcon();
		setBounds(x,y,getIcon().getIconWidth(),getIcon().getIconHeight());
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	public Button(String text, int x, int y, int width, int height) {
		super(text,JLabel.CENTER);
		setBorder(Window.BORDER);
		setBounds(x,y,width,height);
		setFont(new Font(GameManager.font.getName(),Font.PLAIN,30));
		setForeground(Color.white);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	public Button(int index, int x, int y, int width, int height, String... array) {
		this(array[index],x,y,width,height);
		this.index = index;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Button.this.index = (Button.this.index+1)%array.length;
				Button.this.setText(array[Button.this.index]);
			}
		});
	}
	public Button(Color color, int x, int y, int width, int height) {
		setBackground(color);
		setBorder(Window.BORDER);
		setBounds(x,y,width,height);
		setOpaque(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	public void updateIcon() {
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("/images/buttons/"+fileName+".png"));
			for(int w = 0; w < img.getWidth(); w++) {
				for(int h = 0; h < img.getHeight(); h++) {
					Color current = new Color(img.getRGB(w,h),true);
					if(current.getAlpha()>0) {
						int rgb = ColorWindow.THEME_COLOR.getRGB();
						img.setRGB(w,h,rgb);
					}
				}
			}
			setIcon(new ImageIcon(img));
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}
}
