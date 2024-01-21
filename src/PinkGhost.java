import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

@SuppressWarnings("serial")
public class PinkGhost extends Ghost
{
	public PinkGhost(int level) throws IOException
	{	
		super("Pink", 250,	231, level, new Point(12, 8 ), new int[] {KeyEvent.VK_T, KeyEvent.VK_G,KeyEvent.VK_H,KeyEvent.VK_F});
	}
	
	@Override
	Point findChaseTarget() 
	{
		switch(pacDir)
		{
			case up:
				pac.y -= moveOffset*4;
				break;
			case down:
				pac.y += moveOffset*4;
				break;
			case left:
				pac.x -= moveOffset*4;
				break;
			case right:
				pac.x += moveOffset*4;
				break;
			default:
				break;
		}
		return pac;
	}
	
	protected direction FindDirectionFromInput() throws AWTException
	{
		switch (keyCode)
		{
			case KeyEvent.VK_T:
				if(checkAvailability(direction.up))
					return direction.up;
				break;
			case KeyEvent.VK_G:
				if(checkAvailability(direction.down))
					return direction.down;
				break;
			case KeyEvent.VK_F:
				if(checkAvailability(direction.left))
					return direction.left;
				break;
			case KeyEvent.VK_H:
				if(checkAvailability(direction.right))
					return direction.right;
				break;
		}
		return dir;
	}
	
}
