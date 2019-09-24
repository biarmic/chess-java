package managers;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import gamestuff.PieceColor;
import gamestuff.Dot;
import gamestuff.PieceType;
import gamestuff.Position;
import gamestuff.Tile;
import gamestuff.pieces.Pawn;
import gamestuff.pieces.Piece;
import windows.Board;
import windows.Button;
import windows.GameOverWindow;
import windows.LoadWindow;
import windows.SaveWindow;
import windows.Screen;
import windows.SettingsWindow;

public class GameManager {
	private static Screen screen;
	public static Board board;
	public static PieceColor turnColor = PieceColor.white;
	private static Button[] buttons = new Button[6];
	public static Font font;
	public static boolean LOAD_ANIMATION;
	public static boolean CONTINUE_GAME;
	public GameManager(Screen screen) {
		GameManager.screen = screen;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT,new File("Chess/src/others/font.ttf")).deriveFont(12f);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(font);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		final SettingsWindow settings = new SettingsWindow();
		buttons[0] = new Button("undo",883,176);
		buttons[1] = new Button("play",883,276);
		buttons[2] = new Button("save",883,376);
		buttons[3] = new Button("load",883,476);
		buttons[4] = new Button("document",883,576);
		buttons[5] = new Button("settings",883,676);
		buttons[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				undo();
			}
		});
		buttons[1].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				reset();
			}
		});
		buttons[2].addMouseListener(new MouseAdapter() {
			private SaveWindow saveWindow = new SaveWindow();
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.addToGlassPane(saveWindow);
			}
		});
		buttons[3].addMouseListener(new MouseAdapter() {
			private LoadWindow loadWindow = new LoadWindow();
			@Override
			public void mouseClicked(MouseEvent event) {
				loadWindow.updateSlots();
				Screen.addToGlassPane(loadWindow);
			}
		});
		buttons[4].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				print();
			}
		});
		buttons[5].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Screen.addToGlassPane(settings);
			}
		});
		Piece.updateIcons();
		board = new Board();
		for(Button button : buttons)
			board.add(button,Board.PIECE_LAYER);
		screen.setLayeredPane(board);
		PieceListener.board = board;
		Piece.board = board;
		Dot.board = board;
	}
	public static void start() {
		Screen.setClickable(false);
		boolean sound = AudioManager.SOUND;
		if(!LOAD_ANIMATION)
			AudioManager.SOUND = false;
		try {
			if(CONTINUE_GAME) {
				List<String> lines = Files.readAllLines(Paths.get("Chess/src/records/main.txt"),StandardCharsets.UTF_8);
				if(lines.size()>0)
					for(int i = 0; i < lines.size(); i++) {
						perform(lines.get(i));
						eraseLastRecordLine();
					}
			}else {
				FileWriter fw = new FileWriter(new File("Chess/src/records/main.txt"));
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Screen.setClickable(true);
		AudioManager.SOUND = sound;
	}
	public static void reset() {
		turnColor = PieceColor.white;
		board = new Board();
		for(Button button : buttons)
			board.add(button,new Integer(1));
		try {
			FileWriter fw = new FileWriter(new File("Chess/src/records/main.txt"));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		screen.setLayeredPane(board);
		PieceListener.board = board;
		Piece.board = board;
		Dot.board = board;
		board.revalidate();
	}
	public static void checkGame() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				boolean isKingThreatened = board.isKingThreatened();
				for(Piece piece : board.getPieces()[turnColor.index()-1])
					if(!piece.isEaten() && board.getRange(piece.getRow(),piece.getColumn()).size()!=0) {
						if(isKingThreatened)
							AudioManager.playSound("check");
						return;
					}
				AudioManager.playSound("checkmate");
				if(isKingThreatened) {
					if(turnColor==PieceColor.white)
						Screen.addToGlassPane(new GameOverWindow("Black wins!"));
					else
						Screen.addToGlassPane(new GameOverWindow("White wins!"));
				}else
					Screen.addToGlassPane(new GameOverWindow("Draw!"));
			}
		};
		thread.start();
	}
	public static void writeRecord(String appendix) {
		try {
			FileWriter fw = new FileWriter(new File("Chess/src/records/main.txt"),true);
			fw.append(appendix);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String eraseLastRecordLine() {
		String lastLine = "";
		try {
			String str = "";
			BufferedReader br = new BufferedReader(new FileReader(new File("Chess/src/records/main.txt")));
			Iterator<String> iterator = br.lines().iterator();
			while(iterator.hasNext()) {
				String line = iterator.next();
				if(iterator.hasNext())
					str += line+"\n";
				else
					lastLine = line;
			}
			br.close();
			FileWriter fw = new FileWriter(new File("Chess/src/records/main.txt"));
			fw.write(str);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lastLine;
	}
	public static String readLastRecordLine() {
		List<String> lines = Collections.emptyList();
	    try { 
	      lines = Files.readAllLines(Paths.get("Chess/src/records/main.txt"),StandardCharsets.UTF_8); 
	    } catch (IOException e) { 
	      e.printStackTrace(); 
	    }
	    if(lines.size()==0)
	    	return "";
	    return lines.get(lines.size()-1);
	}
	public static void load(int index) {
		Screen.setClickable(false);
		boolean sound = AudioManager.SOUND;
		if(!LOAD_ANIMATION)
			AudioManager.SOUND = false;
		reset();
		List<String> lines = Collections.emptyList();
		try { 
			lines = Files.readAllLines(Paths.get("Chess/src/records/save-slot-"+index+".txt"),StandardCharsets.UTF_8); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		if(lines.size()>1)
			for(int i = 1; i < lines.size(); i++)
				perform(lines.get(i));
		Screen.setClickable(true);
		AudioManager.SOUND = sound;
	}
	public static void print() {
		try { 
			List<String> lines = Files.readAllLines(Paths.get("Chess/src/records/main.txt"),StandardCharsets.UTF_8); 
			String notation = "";
			for(int i = 0; i < lines.size(); i++) {
				notation += i%2==0 ? (i/2+1)+". " : "";
				if(lines.get(i).length()<4)
					notation += (lines.get(i).length()==3 ? "O-O-O" : "O-O");
				else {
					StringTokenizer tokenizer = new StringTokenizer(lines.get(i),".");
					String letter = PieceType.valueOf(tokenizer.nextToken()).letter();
					tokenizer.nextToken();
					tokenizer.nextToken();
					int prevRank = 8-Integer.parseInt(tokenizer.nextToken());
					char prevFile = (char)(97+Integer.parseInt(tokenizer.nextToken()));
					int nextRank = 8-Integer.parseInt(tokenizer.nextToken());
					char nextFile = (char)(97+Integer.parseInt(tokenizer.nextToken()));
					if(tokenizer.hasMoreTokens()) {
						String move = tokenizer.nextToken();
						if(move.equals("eat") || move.equals("enpassant"))
							notation += letter+prevFile+""+prevRank+"x"+nextFile+""+nextRank;
						else {
							String promoteLetter = PieceType.valueOf(tokenizer.nextToken()).letter();
							notation += letter+prevFile+""+prevRank+(tokenizer.hasMoreTokens() ? "x" : "-")+nextFile+""+nextRank+promoteLetter;
						}
					}else
						notation += letter+prevFile+""+prevRank+"-"+nextFile+""+nextRank;
				}
				notation += i%2==0 ? " " : "\n";
			}
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
			chooser.showSaveDialog(null);
			File file = chooser.getSelectedFile();
			if(file!=null) {
				String name = file.getName();
				if(!name.endsWith(".txt"))
				    file = new File(file.toString()+".txt");
				FileWriter fw = new FileWriter(file);
				fw.write(notation);
				fw.close();
			}
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	}
	public static boolean isTwoStepAdvance() {
			String lastLine = GameManager.readLastRecordLine();
			if(lastLine.length()>0) {
				StringTokenizer tokenizer = new StringTokenizer(lastLine,".");
				if(tokenizer.nextToken().equals("pawn")) {
					int index = Integer.parseInt(tokenizer.nextToken());
					if(Boolean.parseBoolean(tokenizer.nextToken())) {
						int prevRow = Integer.parseInt(tokenizer.nextToken());
						int prevColumn = Integer.parseInt(tokenizer.nextToken());
						int curRow = Integer.parseInt(tokenizer.nextToken());
						int curColumn = Integer.parseInt(tokenizer.nextToken());
						if(Math.abs(prevRow-curRow)==2)
							return true;
					}
				}
			}
		return false;
	}
	private static void perform(String line) {
		if(line.length()<4)
			((PieceListener)board.getPieces()[turnColor.index()-1][15].getMouseListeners()[0]).movePiece(new Position(turnColor==PieceColor.white ? 7 : 0,line.length()==2 ? 6 : 2));
		else {
			StringTokenizer tokenizer = new StringTokenizer(line,".");
			tokenizer.nextToken();
			int index = Integer.parseInt(tokenizer.nextToken());
			boolean isFirstMove = Boolean.parseBoolean(tokenizer.nextToken());
			int prevRow = Integer.parseInt(tokenizer.nextToken());
			int prevColumn = Integer.parseInt(tokenizer.nextToken());
			int row = Integer.parseInt(tokenizer.nextToken());
			int column = Integer.parseInt(tokenizer.nextToken());
			if(tokenizer.hasMoreTokens() && tokenizer.nextToken().equals("promote")) {
				PieceType promoteType = PieceType.valueOf(tokenizer.nextToken());
				int eatingIndex = -1;
				if(tokenizer.hasMoreTokens()) {
					eatingIndex = Integer.parseInt(tokenizer.nextToken());
					Piece eating = board.getPieces()[turnColor.opposite().index()-1][eatingIndex];
					eating.setEaten(true);
					board.remove(eating);
				}
				Piece pawn = board.getPieces()[turnColor.index()-1][index];
				PieceListener.dotRange.clear();
				board.removeDots();
				pawn.setPosition(new Position(row,column));
				Piece piece = board.promotePawn(turnColor.index(),promoteType);
				GameManager.turnColor = GameManager.turnColor.opposite();
				AudioManager.playSound("move");
				if(eatingIndex==-1)
					GameManager.writeRecord(PieceListener.getIndexLine(piece)+"."+false+"."+prevRow+"."+prevColumn+"."+row+"."+column+".promote."+promoteType+"\n");
				else
					GameManager.writeRecord(PieceListener.getIndexLine(piece)+"."+false+"."+prevRow+"."+prevColumn+"."+row+"."+column+".promote."+promoteType+"."+eatingIndex+"\n");
			}else
				((PieceListener)board.getPieces()[turnColor.index()-1][index].getMouseListeners()[0]).movePiece(new Position(row,column));
		}
		if(LOAD_ANIMATION) {
			board.revalidate();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void undo() {
		String lastLine = eraseLastRecordLine();
		if(lastLine.length()==0)
			return;
		if(lastLine.length()<4) {
			Piece king = board.getPieces()[turnColor.opposite().index()-1][15];
			board.findTile(king.getRow(),king.getColumn()).removePiece();
			king.setPosition(turnColor==PieceColor.black ? 7 : 0,4);
			king.setMoved(false);
			board.findTile(king.getRow(),king.getColumn()).placePiece(PieceType.king,turnColor.opposite());
			Piece rook = board.getPieces()[turnColor.opposite().index()-1][9-(lastLine.length()%2)];
			board.findTile(rook.getRow(),rook.getColumn()).removePiece();
			rook.setPosition(turnColor==PieceColor.black ? 7 : 0,7*(lastLine.length()==2 ? 1 : 0));
			rook.setMoved(false);
			board.findTile(rook.getRow(),rook.getColumn()).placePiece(PieceType.rook,turnColor.opposite());
			turnColor = turnColor.opposite();
		}else {
			StringTokenizer tokenizer = new StringTokenizer(lastLine,".");
			PieceColor pieceColor = turnColor.opposite();
			PieceType movingType = PieceType.valueOf(tokenizer.nextToken());
			int movingIndex = Integer.parseInt(tokenizer.nextToken());
			boolean isFirstMove = Boolean.parseBoolean(tokenizer.nextToken());
			int prevRow = Integer.parseInt(tokenizer.nextToken());
			int prevColumn = Integer.parseInt(tokenizer.nextToken());
			int curRow = Integer.parseInt(tokenizer.nextToken());
			int curColumn = Integer.parseInt(tokenizer.nextToken());
			if(tokenizer.hasMoreTokens()) {
				String moveType = tokenizer.nextToken();
				if(moveType.equals("eat")) {
					int eatingIndex = Integer.parseInt(tokenizer.nextToken());
					Piece moving = board.getPieces()[pieceColor.index()-1][movingIndex];
					Piece eating = board.getPieces()[turnColor.index()-1][eatingIndex];
					Tile prevTile = board.findTile(prevRow,prevColumn);
					prevTile.placePiece(movingType,pieceColor);
					Tile curTile = board.findTile(curRow,curColumn);
					curTile.placePiece(eating.getPieceType(),turnColor);
					moving.setPosition(prevRow,prevColumn);
					moving.setMoved(!isFirstMove);
					eating.setEaten(false);
					board.add(eating,Board.PIECE_LAYER);
					turnColor = pieceColor;
				}else if(moveType.equals("promote")) {
					tokenizer.nextToken();
					if(tokenizer.hasMoreTokens()) {
						int eatingIndex = Integer.parseInt(tokenizer.nextToken());
						Piece eating = board.getPieces()[turnColor.index()-1][eatingIndex];
						board.remove(board.getPieces()[pieceColor.index()-1][movingIndex]);
						board.getPieces()[pieceColor.index()-1][movingIndex] = new Pawn(pieceColor,prevRow,prevColumn);
						Tile prevTile = board.findTile(prevRow,prevColumn);
						prevTile.placePiece(movingType,pieceColor);
						Tile curTile = board.findTile(curRow,curColumn);
						curTile.placePiece(eating.getPieceType(),turnColor);
						eating.setEaten(false);
						board.add(board.getPieces()[pieceColor.index()-1][movingIndex],Board.PIECE_LAYER);
						board.add(eating,Board.PIECE_LAYER);
						board.repaint();
						turnColor = pieceColor;
					}else {
						board.remove(board.getPieces()[pieceColor.index()-1][movingIndex]);
						board.getPieces()[pieceColor.index()-1][movingIndex] = new Pawn(pieceColor,prevRow,prevColumn);
						Tile prevTile = board.findTile(prevRow,prevColumn);
						prevTile.placePiece(movingType,pieceColor);
						Tile curTile = board.findTile(curRow,curColumn);
						curTile.removePiece();
						board.add(board.getPieces()[pieceColor.index()-1][movingIndex],Board.PIECE_LAYER);
						board.repaint();
						turnColor = pieceColor;
					}
				}else {
					int eatingIndex = Integer.parseInt(tokenizer.nextToken());
					Piece moving = board.getPieces()[pieceColor.index()-1][movingIndex];
					Piece eating = board.getPieces()[turnColor.index()-1][eatingIndex];
					Tile prevTile = board.findTile(prevRow,prevColumn);
					prevTile.placePiece(movingType,pieceColor);
					Tile curTile = board.findTile(curRow,curColumn);
					curTile.removePiece();
					Tile enpassanTile = board.findTile(eating.getRow(),eating.getColumn());
					enpassanTile.placePiece(eating.getPieceType(),turnColor);
					moving.setPosition(prevRow,prevColumn);
					moving.setMoved(!isFirstMove);
					eating.setEaten(false);
					board.add(eating,Board.PIECE_LAYER);
					turnColor = pieceColor;
				}
			}else {
				Tile prevTile = board.findTile(prevRow,prevColumn);
				prevTile.placePiece(movingType,pieceColor);
				Tile curTile = board.findTile(curRow,curColumn);
				curTile.removePiece();
				Piece moving = board.getPieces()[pieceColor.index()-1][movingIndex];
				moving.setPosition(prevRow,prevColumn);
				moving.setMoved(!isFirstMove);
				turnColor = pieceColor;
			}
		}
	}
}
