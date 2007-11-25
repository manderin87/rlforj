package rlforj.los;

import java.util.Vector;

import rlforj.los.PrecisePermissive.FakeLosBoard;
import rlforj.los.PrecisePermissive.fovStateT;
import rlforj.math.Point2I;

/**
 * Given a set of squares that we are allowed to visit, and two points
 * A and B, calculates a monotonic path from A to B, if it exists.
 * Else it stops after as far as it can go
 * It is useful to calculate a path along which sight runs from A to B
 * given B is visible from A. An arrow or bolt can fly along this path.
 * @author sdatta
 *
 */
public class GenericCalculateProjection
{
	private Vector<Integer> pathx;
	private Vector<Integer> pathy;

	public void calculateProjecton(int startX, int startY, int adx, int ady,
			VisitedBoard fb, fovStateT state)
	{
		pathx = new Vector<Integer>();
		pathy = new Vector<Integer>();
		boolean axesSwapped = false;
		if (adx < ady)
		{
			axesSwapped = true;
			final int tmp = adx;
			adx = ady;
			ady = tmp;
		}

		// System.out.println("adx ady "+adx+" "+ady);
		int incE = 2 * ady;
		int incNE = 2 * ady - 2 * adx;
		int d = 2 * ady - adx;
		Point2I p = new Point2I(0, 0);
		int lasti = 0, lastj = 0;
		int j = 0;
		int signX = state.quadrant.x, signY = state.quadrant.y;
		for (int i = 0; i <= adx;)
		{
			// System.out.println(i+" "+j);
			if (axesSwapped)
			{
				pathx.add(j * signX + startX);
				pathy.add(i * signY + startY);
			} else
			{
				pathx.add(i * signX + startX);
				pathy.add(j * signY + startY);
			}
			lasti = i;
			lastj = j;
			boolean ippNotrecommended = false;
			if (d <= 0)
			{
				// try to just inc x
				if (axesSwapped)
				{
					p.y = i + 1;
					p.x = j;
				} else
				{
					p.x = i + 1;
					p.y = j;
				}
				if (fb.wasVisited(p.x, p.y))
				{
					d += incE;
					i++;
					continue;
				}
				// System.out.println("cannot i++ "+p+"
				// "+fb.visitedNotObs.contains(p));
			} else
			{
				// System.out.println("i++ not recommended ");
				ippNotrecommended = true;
			}

			// try to inc x and y
			if (axesSwapped)
			{
				p.y = i + 1;
				p.x = j + 1;
			} else
			{
				p.x = i + 1;
				p.y = j + 1;
			}
			if (fb.wasVisited(p.x, p.y))
			{
				d += incNE;
				j++;
				i++;
				continue;
			}
			// System.out.println("cannot i++ j++ "+p+"
			// "+fb.visitedNotObs.contains(p));
			if (ippNotrecommended)
			{ // try it even if not recommended
				if (axesSwapped)
				{
					p.y = i + 1;
					p.x = j;
				} else
				{
					p.x = i + 1;
					p.y = j;
				}
				if (fb.wasVisited(p.x, p.y))
				{
					d += incE;
					i++;
					continue;
				}
				// System.out.println("cannot i++ "+p+"
				// "+fb.visitedNotObs.contains(p));
			}
			// last resort
			// try to inc just y
			if (axesSwapped)
			{
				p.y = i;
				p.x = j + 1;
			} else
			{
				p.x = i;
				p.y = j + 1;
			}
			if (fb.wasVisited(p.x, p.y))
			{
				if (lasti == i - 1 && lastj == j)// last step was 1 to the
					// right
					System.out.println("<<-");// this step is 1 step to up,
				// together 1 diagonal
				// => we dont need last point
				d += -incE + incNE;// as if we went 1 step left then took 1
				// step up right
				j++;
				continue;
			}
			// System.out.println("cannot j++ "+p+"
			// "+fb.visitedNotObs.contains(p));
			// no path, end here.
			break;
		}
	}
	
	public static interface VisitedBoard {
		public boolean wasVisited(int x, int y);
	}
}