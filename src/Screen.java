import java.awt.Graphics;
import java.io.FileNotFoundException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Screen extends JPanel
{
	private ImageIcon screenImage;
	public Screen() 
	{
		
	}
	
	public void drawScreen(Graphics g, String sName) throws FileNotFoundException
	{
		screenImage = new ImageIcon("Assets/Sprites/Screens/"+sName+".png");
		if(screenImage.getIconHeight() < 0)
			throw new FileNotFoundException(sName + " not found");
		screenImage.paintIcon(this, g, 0, 0);
	}
}
