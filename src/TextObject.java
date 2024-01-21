import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class TextObject extends JPanel 
{
	public Font font;
	protected Point pos;
	
	public TextObject(int x, int y) throws FileNotFoundException
	{
		String fName = "Assets/Misc/8bit16.ttf";
	    try 
	    {
			font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fName));
		}
	    catch (FontFormatException | IOException e)
	    {
	    	throw new FileNotFoundException("font not found");
		}
	    font = font.deriveFont(30f);
	    pos = new Point(x,y);
	}
	
	protected abstract String definePrint() throws IOException;
	
	public void draw(Graphics g) throws IOException
	{
		String s = definePrint();
		g.setFont(font); 
		g.setColor(Color.RED);
		g.drawString(s, (int) (pos.x - String.valueOf(s).length()* 10) , pos.y);
	}
	
}
