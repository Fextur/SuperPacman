import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ChooseGhost extends JPanel
{
	private ImageIcon chooseImage;
	public boolean aquaState = false;
	public boolean redState = false;
	public boolean pinkState = false;
	public boolean orangeState = false;
	public int keyCode = 0;
	public ChooseGhost() 
	{
		
	}
	
	public void drawChooseScreen(Graphics g) throws FileNotFoundException
	{
		chooseImage = new ImageIcon("Assets/Sprites/Screens/ChooseGhost.png");
		if(chooseImage.getIconHeight() < 0)
			throw new FileNotFoundException("ChooseGhost not found");
		chooseImage.paintIcon(this, g, 0, 0);
		checkInput();
		if(aquaState)
		{
			chooseImage = new ImageIcon("Assets/Sprites/Screens/AquaChoosen.png");
			if(chooseImage.getIconHeight() < 0)
				throw new FileNotFoundException("AquaChoosen not found");
			chooseImage.paintIcon(this, g, 232, 349);
		}
		if(pinkState)
		{
			chooseImage = new ImageIcon("Assets/Sprites/Screens/PinkChoosen.png");
			if(chooseImage.getIconHeight() < 0)
				throw new FileNotFoundException("PinkChoosen not found");
			chooseImage.paintIcon(this, g, 2, 349);
		}
		if(redState)
		{
			chooseImage = new ImageIcon("Assets/Sprites/Screens/RedChoosen.png");
			if(chooseImage.getIconHeight() < 0)
				throw new FileNotFoundException("RedChoosen not found");
			chooseImage.paintIcon(this, g, 2, 178);
		}
		if(orangeState)
		{
			chooseImage = new ImageIcon("Assets/Sprites/Screens/OrangeChoosen.png");
			if(chooseImage.getIconHeight() < 0)
				throw new FileNotFoundException("OrangeChoosen not found");
			chooseImage.paintIcon(this, g, 232, 178);
		}
	}
	
	private void checkInput()
	{
		switch(keyCode)
		{
			case KeyEvent.VK_W:
				redState = !redState;
				break;
			case KeyEvent.VK_UP:
				orangeState = !orangeState;
				break;
			case KeyEvent.VK_T:
				pinkState = !pinkState;
				break;
			case KeyEvent.VK_I:
				aquaState = !aquaState;
				break;
		}
		keyCode = 0;
	}
	
}
