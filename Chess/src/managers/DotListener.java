package managers;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gamestuff.Position;

public class DotListener extends MouseAdapter {
	private PieceListener source;
	private Position position;
	public DotListener(Position position, PieceListener source) {
		this.position = position;
		this.source = source;
	}
	@Override
	public void mouseClicked(MouseEvent event) {
		source.movePiece(position);
	}
}
