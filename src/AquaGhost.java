import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.IOException;

@SuppressWarnings("serial")
public class AquaGhost extends Ghost 
{
	private Point red = new Point();
	
	public AquaGhost(int level) throws IOException
	{	
		super("Aqua", 204, 231, level, new Point(415,465), new int[] {KeyEvent.VK_I, KeyEvent.VK_K,KeyEvent.VK_J,KeyEvent.VK_L});
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
		return Opposite(pac, red, pac);
	}
	
	public static Point Opposite(Point pos, Point enemy, Point defaultTarget)
	{
		Point target = new Point();
		if(pos.x - enemy.x == 0)
		{
			return new Point(pos.x, pos.y + (pos.y - enemy.y));
		}
		double m = (double)(pos.y - enemy.y)/(double)(pos.x - enemy.x);
		double dist = enemy.distance(pos); 
		double n = enemy.y - m*enemy.x;
		double z = n - pos.y;
		double a = m*m + 1;
		double b = 2*m*z  - 2*pos.x;
		double c = pos.x*pos.x + z*z - dist*dist;
		double determinant = b * b - 4 * a * c;
		double x1, x2;
		if(determinant > 0)
		{
	            x1 = (-b + Math.sqrt(determinant)) / (2 * a);
	            x2 = (-b - Math.sqrt(determinant)) / (2 * a);
	            if(x1 == enemy.x) target.x = (int) x2;
	            else target.x = (int) x1;
	    		target.y = (int) (m*target.x + n);
		}
		else return defaultTarget;
		return target;
	}
	
	public void setRedPos(Point redPos)
	{
		red.x = redPos.x;
		red.y = redPos.y;
	}

	@Override
	protected direction FindDirectionFromInput() throws AWTException
	{
		switch (keyCode)
		{
			case KeyEvent.VK_I:
				if(checkAvailability(direction.up))
					return direction.up;
				break;
			case KeyEvent.VK_K:
				if(checkAvailability(direction.down))
					return direction.down;
				break;
			case KeyEvent.VK_J:
				if(checkAvailability(direction.left))
					return direction.left;
				break;
			case KeyEvent.VK_L:
				if(checkAvailability(direction.right))
					return direction.right;
				break;
		}
		return dir;
	}

}
