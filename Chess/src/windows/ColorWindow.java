package windows;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class ColorWindow extends Window {
	private static final LineBorder FOCUS_BORDER = new LineBorder(Color.green,4);
	private static SettingsWindow settings;
	public static Color THEME_COLOR;
	public static Color OPPOSITE_COLOR;
	public static ColorMode COLOR_MODE;
	private JLabel mapPicker;
	private JLabel barPicker;
	private JLabel mapBorder;
	private JLabel themeColor;
	private JLabel oppositeColor;
	private Button colorMode;
	public ColorWindow(SettingsWindow settings) {
		super(595,384);
		ColorWindow.settings = settings;
		JLabel hueMap = null;
		JLabel colorBar = null;
		try {
			hueMap = new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/color/map-hue.png"))));
			colorBar = new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/color/bar-hue.png"))));
			mapPicker = new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/color/map-picker.png"))));
			barPicker = new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/images/color/bar-picker.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		hueMap.setBounds(24,24,256,256);
		hueMap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		mapBorder = new JLabel();
		mapBorder.setBounds(20,20,264,264);
		mapBorder.setBorder(BORDER);
		mapBorder.setOpaque(true);
		mapBorder.setBackground(ColorWindow.THEME_COLOR==null ? Color.red : null);
		add(hueMap,new Integer(1));
		add(mapBorder,new Integer(0));
		colorBar.setBounds(318,24,20,256);
		colorBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		JLabel colorBorder = new JLabel();
		colorBorder.setBounds(314,20,28,264);
		colorBorder.setBorder(BORDER);
		add(colorBar,new Integer(1));
		add(colorBorder,new Integer(0));
		mapPicker.setBounds(20,20,14,14);
		barPicker.setBounds(314,20,28,12);
		mapPicker.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		barPicker.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		add(mapPicker,new Integer(2));
		add(barPicker,new Integer(2));
		themeColor = new JLabel();
		themeColor.setBounds(378,20,132,50);
		themeColor.setBorder(BORDER);
		themeColor.setOpaque(true);
		oppositeColor = new JLabel();
		oppositeColor.setBounds(378,90,132,50);
		oppositeColor.setBorder(BORDER);
		oppositeColor.setOpaque(true);
		updateColor();
		add(themeColor,new Integer(0));
		add(oppositeColor,new Integer(0));
		themeColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if(ColorWindow.COLOR_MODE==ColorMode.custom && themeColor.getBorder()==BORDER) {
					themeColor.setBorder(FOCUS_BORDER);
					oppositeColor.setBorder(BORDER);
				}
			}
		});
		oppositeColor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if(ColorWindow.COLOR_MODE==ColorMode.custom && oppositeColor.getBorder()==BORDER) {
					oppositeColor.setBorder(FOCUS_BORDER);
					themeColor.setBorder(BORDER);
				}
			}
		});
		MouseAdapter mapListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				move(event);
			}
			@Override
			public void mouseDragged(MouseEvent event) {
				move(event);
			}
			private void move(MouseEvent event) {
				int eventX = (int)event.getPoint().getX();
				int eventY = (int)event.getPoint().getY();
				mapPicker.setLocation(eventX<3 ? 20 : eventX>253 ? 270 : eventX+17,eventY<3 ? 20 : eventY>253 ? 270 : eventY+17);
				updateColor();
			}
		};
		hueMap.addMouseListener(mapListener);
		hueMap.addMouseMotionListener(mapListener);
		MouseAdapter barListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				move(event);
			}
			@Override
			public void mouseDragged(MouseEvent event) {
				move(event);
			}
			private void move(MouseEvent event) {
				int eventY = (int)event.getPoint().getY();
				barPicker.setLocation(barPicker.getX(),eventY<3 ? 20 : eventY>256 ? 272 : eventY+16);
				updateColor();
			}
		};
		colorBar.addMouseListener(barListener);
		colorBar.addMouseMotionListener(barListener);
		colorMode = new Button(COLOR_MODE.index(),378,160,200,50,"Negative","Mono","Custom");
		add(colorMode,new Integer(1));
		colorMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				COLOR_MODE = ColorMode.values()[(COLOR_MODE.index()+1)%3];
				if(COLOR_MODE!=ColorMode.custom) {
					themeColor.setBorder(BORDER);
					oppositeColor.setBorder(BORDER);
				}
				updateColor();
			}
		});
		Button choose = new Button("Select",20,304,160,60);
		add(choose,new Integer(1));
		choose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				THEME_COLOR = themeColor.getBackground();
				OPPOSITE_COLOR = oppositeColor.getBackground();
				writeSetting();
				Screen.removeFromGlassPane(ColorWindow.this);
			}
		});
		Button reset = new Button("Reset",200,304,150,60);
		add(reset,new Integer(1));
		reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				THEME_COLOR = Color.white;
				OPPOSITE_COLOR = Color.black;
				COLOR_MODE = ColorMode.negative;
				writeSetting();
				Screen.removeFromGlassPane(ColorWindow.this);
			}
		});
		Button close = new Button("Close",370,304,150,60);
		add(close,new Integer(1));
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.removeFromGlassPane(ColorWindow.this);
			}
		});
	}
	private void updateColor() {
		float hue = 1-(float)(barPicker.getY()-16)/256;
		float saturation = (float)(mapPicker.getX()-16)/256;
		float brightness = 1-(float)(mapPicker.getY()-16)/256;
		Color chosenColor = new Color(Color.HSBtoRGB(hue,saturation,brightness));
		if(COLOR_MODE==ColorMode.negative) {
			themeColor.setBackground(chosenColor);
			oppositeColor.setBackground(new Color(255-chosenColor.getRed(),255-chosenColor.getGreen(),255-chosenColor.getBlue()));
		}else if(COLOR_MODE==ColorMode.mono) {
			themeColor.setBackground(chosenColor.brighter().brighter());
			oppositeColor.setBackground(chosenColor.darker().darker());
		}else {
			if(themeColor.getBorder()==FOCUS_BORDER) {
				themeColor.setBackground(chosenColor);
			}else if(oppositeColor.getBorder()==FOCUS_BORDER) {
				oppositeColor.setBackground(chosenColor);
			}
		}
		mapBorder.setBackground(new Color(Color.HSBtoRGB(hue,1,1)));
	}
	private void writeSetting() {
		settings.updateColor();
		Board.updateColors();
		try { 
			String str = "";
			List<String> lines = Files.readAllLines(Paths.get("Chess/src/others/settings.txt"),StandardCharsets.UTF_8);
			for(int i = 0; i < 4; i++) {
				str += lines.get(i)+"\n";
			}
			str += COLOR_MODE.index()+"\n";
			if(THEME_COLOR==Color.white) {
				str += "0,0,1\n";
			}else {
				float[] hsb = new float[3];
				Color chosen = themeColor.getBackground();
				Color.RGBtoHSB(chosen.getRed(),chosen.getGreen(),chosen.getBlue(),hsb);
				str += hsb[0]+","+hsb[1]+","+hsb[2]+"\n";
				if(COLOR_MODE==ColorMode.custom) {
					chosen = oppositeColor.getBackground();
					Color.RGBtoHSB(chosen.getRed(),chosen.getGreen(),chosen.getBlue(),hsb);
					str += hsb[0]+","+hsb[1]+","+hsb[2]+"\n";
				}
			}
			FileWriter fw = new FileWriter(new File("Chess/src/others/settings.txt"));
			fw.write(str);
			fw.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
}
