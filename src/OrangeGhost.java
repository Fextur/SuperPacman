import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

@SuppressWarnings("serial")
public class OrangeGhost extends Ghost
{
	public OrangeGhost(int level) throws IOException
	{	
		super("Orange",227,	231 , level, new Point(15, 465), new int[] {KeyEvent.VK_UP, KeyEvent.VK_DOWN,KeyEvent.VK_RIGHT,KeyEvent.VK_LEFT});
	}

	@Override
	Point findChaseTarget() 
	{
		if(16*8 < pac.distance(pos))
		{
			return pac;
		}
		else 
			return scatterCorner;
	}

	@Override
	protected direction FindDirectionFromInput() throws AWTException
	{
		switch (keyCode)
		{
			case KeyEvent.VK_UP:
				if(checkAvailability(direction.up))
					return direction.up;
				break;
			case KeyEvent.VK_DOWN:
				if(checkAvailability(direction.down))
					return direction.down;
				break;
			case KeyEvent.VK_LEFT:
				if(checkAvailability(direction.left))
					return direction.left;
				break;
			case KeyEvent.VK_RIGHT:
				if(checkAvailability(direction.right))
					return direction.right;
				break;
		}
		return dir;	
	}

}
