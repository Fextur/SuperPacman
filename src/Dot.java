import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Dot extends JPanel
{
	protected ImageIcon dotImage;
	
	public Dot() throws FileNotFoundException
	{
		dotImage = new ImageIcon("Assets/Sprites/Dots/Dot.png");
		if(dotImage.getIconHeight() < 0)
			throw new FileNotFoundException("Dot not found");
	}
	
	public boolean drawDot(Graphics g, Point dotPos, Point pacPos)
	{
		if(checkPointIntersect(dotPos, pacPos)) return false;
		dotImage.paintIcon(this, g, dotPos.x, dotPos.y + 115);
		return true;
	}
	
	public static boolean checkPointIntersect(Point dot, Point pac)
	{
		Rectangle dotRec = new Rectangle(dot.x + 5,dot.y + 5, 6, 6);
		Rectangle pacRec = new Rectangle(pac.x,pac.y, 24, 24);
		if(dotRec.intersects(pacRec)) 
		{
			return true;
		}
		return false;
	}

}
