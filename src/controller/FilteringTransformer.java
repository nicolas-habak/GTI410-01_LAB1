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

import java.awt.event.MouseEvent;
import java.util.List;

import model.ImageDouble;
import model.ImageX;
import model.Shape;

/**
 * 
 * <p>Title: FilteringTransformer</p>
 * <p>Description: ... (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 Sébastien Bois, Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.6 $
 */
public class FilteringTransformer extends AbstractTransformer{
	PaddingStrategy ps = new PaddingZeroStrategy();
	ImageConversionStrategy ics = new ImageClampStrategy();
	CustomFilter3x3 filter = new CustomFilter3x3(ps, ics);
	double matrix[][] = new double[3][3];
	
	/**
	 * @param _coordinates
	 * @param _value
	 */
	public void updateKernel(Coordinates _coordinates, float _value) {
		//System.out.println("[" + (_coordinates.getColumn() - 1) + "][" + (_coordinates.getRow() - 1) + "] = " + _value);		
		matrix[_coordinates.getColumn() - 1][_coordinates.getRow() - 1] = _value;		
		
		filter.setFilterMatrix(matrix);		
	}
		
	/**
	 * 
	 * @param e
	 * @return
	 */
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {			
			Shape shape = (Shape)intersectedObjects.get(0);			
			if (shape instanceof ImageX) {				
				ImageX currentImage = (ImageX)shape;
				ImageDouble filteredImage = filter.filterToImageDouble(currentImage);
				ImageX filteredDisplayableImage = filter.getImageConversionStrategy().convert(filteredImage);
				currentImage.beginPixelUpdate();
				
				for (int i = 0; i < currentImage.getImageWidth(); ++i) {
					for (int j = 0; j < currentImage.getImageHeight(); ++j) {
						currentImage.setPixel(i, j, filteredDisplayableImage.getPixelInt(i, j));
					}
				}
				currentImage.endPixelUpdate();
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FILTER; }

	/**
	 * @param string
	 */
	public void setBorder(String string) {
		//System.out.println(string);
		switch(string.trim().toLowerCase()) {
		case "0":System.out.println("zone 0");ps = new PaddingZeroStrategy();break;
		case "none":System.out.println("zone none");break;
		case "copy":System.out.println("zone copy");ps = new PaddingCopyStrategy();break;
		case "mirror":System.out.println("zone mirror");ps = new PaddingMirrorStrategy();break;
		case "circular":System.out.println("zone circular");break;		
		}
		
		filter = new CustomFilter3x3(ps, ics);
	}

	/**
	 * @param string
	 */
	public void setClamp(String string) {
		System.out.println(string);
		
		switch(string.trim().toLowerCase()) {
		case "clamp 0...255":ics = new ImageClampStrategy();break;
		case "abs and normalize to 255":break;
		case "abs and normalize 0 to 255":ics = new ImageAbs0to255Strategy();break;
		case "normalize 0 to 255":break;		
		}
		
		filter = new CustomFilter3x3(ps, ics);
	}
}
