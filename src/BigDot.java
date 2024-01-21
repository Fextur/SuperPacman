import java.io.FileNotFoundException;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class BigDot extends Dot
{
	public BigDot() throws FileNotFoundException
	{
		dotImage = new ImageIcon("Assets/Sprites/Dots/BigPoint.png");
		if(dotImage.getIconHeight() < 0)
			throw new FileNotFoundException("BigPoint not found");
	}

}