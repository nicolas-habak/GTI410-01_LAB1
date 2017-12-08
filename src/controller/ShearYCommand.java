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
package controller;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

import model.Shape;

/**
 * <p>Title: ShearYCommand</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004 Jean-François Barras, Éric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * <p>Created on: 2004-03-19</p>
 * @version $Revision: 1.4 $
 */
public class ShearYCommand extends AnchoredTransformationCommand {

	/**
	 * @param angleDegrees The angle to which the horizontal lines will be transformed.
	 * @param anchor one of the predefined positions for the anchor point
	 */
	public ShearYCommand(double angleDegrees, int anchor, List aObjects) {
		super(anchor);
		this.angleDegrees = angleDegrees;
		objects = aObjects;
	}
	
	/* (non-Javadoc)
	 * @see controller.Command#execute()
	 */
	public void execute() {
		System.out.println("command: shearing on y-axis to " + angleDegrees +
				           " degrees anchored on " + getAnchor());
		
		/* Récupération de l'angle fourni par l'utilisateur et conversion en radian */
		double radAngle = Math.toRadians(angleDegrees);		           		
		
		Iterator iter = objects.iterator();
		Shape shape;
		
		/* Récupération du point d'ancrage et sauvegarde dans variable p */		
		Point p = getAnchorPoint(objects);		
		
		while(iter.hasNext()){
			shape = (Shape)iter.next();
			mt.addMememto(shape);
			AffineTransform t = shape.getAffineTransform();
			
			/* Translation pour respecter le point d'ancrage, cisaillement en Y puis retour à la position d'origine */
			t.translate(p.getX(), p.getY());
			t.shear(0, radAngle);
			t.translate(-p.getX(), -p.getY());
			shape.setAffineTransform(t);
		}
	}

	/* (non-Javadoc)
	 * @see controller.Command#undo()
	 */
	public void undo() {
		mt.setBackMementos();
	}

	private MementoTracker mt = new MementoTracker();
	private List objects;
	private double angleDegrees;

}