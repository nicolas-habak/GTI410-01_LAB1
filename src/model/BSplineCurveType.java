/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: BezierCurveType</p>
 * <p>Description: ... (CurveType)</p>
 * <p>Copyright: Copyright (c) 2004 Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author Eric Paquette
 * @version $Revision: 1.3 $
 */
public class BSplineCurveType extends CurveType {

	public BSplineCurveType(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see model.CurveType#getNumberOfSegments(int)
	 */
	public int getNumberOfSegments(int numberOfControlPoints) {
		if (numberOfControlPoints >= 4) {
			return numberOfControlPoints - 3;
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see model.CurveType#getNumberOfControlPointsPerSegment()
	 */
	public int getNumberOfControlPointsPerSegment() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see model.CurveType#getControlPoint(java.util.List, int, int)
	 */
	public ControlPoint getControlPoint(
		List controlPoints,
		int segmentNumber,
		int controlPointNumber) {
		int controlPointIndex = segmentNumber + controlPointNumber;
		return (ControlPoint)controlPoints.get(controlPointIndex);
	}

	/* (non-Javadoc)
	 * @see model.CurveType#evalCurveAt(java.util.List, double)
	 */
	public Point evalCurveAt(List controlPoints, double t) {
		List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);
		List gVector = Matrix.buildColumnVector4(((ControlPoint)controlPoints.get(0)).getCenter(), 
			((ControlPoint)controlPoints.get(1)).getCenter(), 
			((ControlPoint)controlPoints.get(2)).getCenter(),
			((ControlPoint)controlPoints.get(3)).getCenter());
		
		Point p = Matrix.eval(tVector, matrix, gVector);
		
		/* L'algorithme pour Matrix.eval est basé sur celui-ci mais était fourni dans l'application
		
		Point p = new Point();
		List T = Matrix.buildRowVector4(t*t*t, t*t, t, 1);
		List Gs = Matrix.buildColumnVector4(((ControlPoint)controlPoints.get(0)).getCenter(), 
				((ControlPoint)controlPoints.get(1)).getCenter(), 
				((ControlPoint)controlPoints.get(2)).getCenter(),
				((ControlPoint)controlPoints.get(3)).getCenter());
		
		List Bs = Matrix.buildRowVector4(0, 0, 0, 0);
		
		for(int i = 0; i < ((List)Bs.get(0)).size(); i++) {
			for(int j = 0; j < matrix.size(); j++) {
				double Bsi = ((Double)((List)Bs.get(0)).get(i)).doubleValue();
				Bsi += ((Double)((List)T.get(0)).get(j)).doubleValue() * ((Double)((List)matrix.get(j)).get(i)).doubleValue();
				((List)Bs.get(0)).set(i, Bsi);
			}
			double Bsi = ((Double)((List)Bs.get(0)).get(i)).doubleValue();
			Bsi /= 6.0f;
			((List)Bs.get(0)).set(i, Bsi);
		}
		double x = 0, y = 0;
		for(int i = 0; i < ((List)Bs.get(0)).size(); i++) {
			x += ((Double)((List)Bs.get(0)).get(i)).doubleValue() * ((Point)((List)Gs.get(i)).get(0)).x;
			y += ((Double)((List)Bs.get(0)).get(i)).doubleValue() * ((Point)((List)Gs.get(i)).get(0)).y;
		}
		
		
		p.setLocation(x, y);
		*/
		
		/* il faut diviser par 6 car la matrice de coefficients est
		 1/6 * -1.0f,  3.0f, -3.0f, 1.0f, 
				3.0f, -6.0f,  3.0f, 0.0f, 
			   -3.0f,  0.0f,  3.0f, 0.0f, 
				1.0f,  4.0f,  1.0f, 0.0f */
		p.setLocation(p.getX() / 6, p.getY() / 6);
		
		return p;
	}

	private List bSplineMatrix = 
		Matrix.buildMatrix4(-1.0f,  3.0f, -3.0f, 1.0f, 
							 3.0f, -6.0f,  3.0f, 0.0f, 
							-3.0f,  0.0f,  3.0f, 0.0f, 
							 1.0f,  4.0f,  1.0f, 0.0f);
							 
	private List matrix = bSplineMatrix;
}
