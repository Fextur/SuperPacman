import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

@SuppressWarnings("serial")
public class RedGhost extends Ghost
{
	public RedGhost(int level) throws IOException
	{	
		super("Red", 181,	231, level, new Point(415, 15), new int[] {KeyEvent.VK_W, KeyEvent.VK_S,KeyEvent.VK_D,KeyEvent.VK_A});
	}
	@Override
	Point findChaseTarget() 
	{
		return pac;
	}
	
	@Override
	protected direction FindDirectionFromInput() throws AWTException
	{
		switch (keyCode)
		{
			case KeyEvent.VK_W:
				if(checkAvailability(direction.up))
					return direction.up;
				break;
			case KeyEvent.VK_S:
				if(checkAvailability(direction.down))
					return direction.down;
				break;
			case KeyEvent.VK_A:
				if(checkAvailability(direction.left))
					return direction.left;
				break;
			case KeyEvent.VK_D:
				if(checkAvailability(direction.right))
					return direction.right;
				break;
		}
		return dir;
	}
	
}

