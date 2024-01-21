import java.awt.Graphics;
import java.io.FileNotFoundException;
import javax.swing.*; 

@SuppressWarnings("serial")
public class Life extends JFrame
{
	private ImageIcon lifeImage;
	
	public Life()
	{
	}
	
	public void drawLife(Graphics g, int lifes) throws FileNotFoundException
	{
		lifeImage = new ImageIcon("Assets/Sprites/Lifes/"+lifes+".png");
		if(lifeImage.getIconHeight() < 0)
			throw new FileNotFoundException("Life" + lifes + " not found");
		lifeImage.paintIcon(this, g, 10, 620);
	}
	
}
