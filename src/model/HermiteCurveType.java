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
import java.util.List;

/**
 * <p>Title: BezierCurveType</p>
 * <p>Description: ... (CurveType)</p>
 * <p>Copyright: Copyright (c) 2004 Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author Eric Paquette
 * @version $Revision: 1.3 $
 */
public class HermiteCurveType extends CurveType {

	public HermiteCurveType(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see model.CurveType#getNumberOfSegments(int)
	 */
	public int getNumberOfSegments(int numberOfControlPoints) {
		if (numberOfControlPoints >= 4) {
			return (numberOfControlPoints - 1) / 3;
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
	public ControlPoint getControlPoint(List controlPoints, int segmentNumber, int controlPointNumber) {
		int controlPointIndex = segmentNumber * 3 + controlPointNumber;
		return (ControlPoint)controlPoints.get(controlPointIndex);
	}

	/* (non-Javadoc)
	 * @see model.CurveType#evalCurveAt(java.util.List, double)
	 * Récupération des points dans la fenêtre et calcul des tangentes à l'aide des points
	 * 
	 */
	public Point evalCurveAt(List controlPoints, double t) {
		List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);
		
		Point p1 = ((ControlPoint)controlPoints.get(0)).getCenter();
		Point p2 = ((ControlPoint)controlPoints.get(1)).getCenter();
		Point p3 = ((ControlPoint)controlPoints.get(2)).getCenter();
		Point p4 = ((ControlPoint)controlPoints.get(3)).getCenter();
		
		Point r1 = (new ControlPoint((p2.getX()-p1.getX()),(p2.getY()-p1.getY()))).getCenter();
		Point r4 = (new ControlPoint((p3.getX()-p4.getX()),(p3.getY()-p4.getY()))).getCenter();
		
		List gVector = Matrix.buildColumnVector4(p1,p4,r1,r4);
		Point p = Matrix.eval(tVector, matrix, gVector);
					
		return p;
	}

	//Modification de la matrice pour respecter la matrice de l'Hermite
	private List HermiteMatrix = 
		Matrix.buildMatrix4(2, -2, 1, 1, 
							-3, 3, -2, -1, 
							 0, 0, 1, 0, 
							 1, 0, 0, 0);
							 
	private List matrix = HermiteMatrix;
}
