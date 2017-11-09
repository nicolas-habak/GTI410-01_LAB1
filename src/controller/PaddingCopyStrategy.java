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

import model.*;

/**
 * <p>Title: PaddingZeroStrategy</p>
 * <p>Description: Padding strategy where Zero values are returned if Pixel values are out of range.</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barré-Brisebois, Éric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.4 $
 */
public class PaddingCopyStrategy extends PaddingStrategy {
	PaddingCopyStrategy(){
		System.out.println("dans le copy padding");
	}
	
	/**
	 * Returns and validates the Pixel at the specified coordinate.
	 * If the Pixel is invalid, a new black (0,0,0,0) Pixel is returned.
	 * @param image source Image
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the validated Pixel value at the specified coordinates 
	 */
	public Pixel pixelAt(ImageX image, int x, int y) {
		Pixel copy = null;
		
		if ((x > 0) && (x < image.getImageWidth()) && (y > 0) && (y < image.getImageHeight())) {
				copy = image.getPixel(x, y);
		}
		else {
			if(x<=0) {
				if(y<=0) {
					copy = image.getPixel(1, 1);
				}
				else if(y>=image.getImageHeight()) {
					copy = image.getPixel(1, image.getImageHeight()-1);
				}
				else {
					copy = image.getPixel(1, y);
				}			
			}
			else if(x>=image.getImageWidth()) {
				if(y<=0) {
					copy = image.getPixel(image.getImageWidth()-1, 1);
				}
				else if(y>=image.getImageHeight()) {
					copy = image.getPixel(image.getImageWidth()-1, image.getImageHeight()-1);
				}
				else {
					copy = image.getPixel(image.getImageWidth()-1, y);
				}
			}
		}		
		
		return copy;		
	}

	/**
	 * Returns and validates the PixelDouble at the specified coordinate.
	 * Original Pixel is converted to PixelDouble.
	 * If the Pixel is invalid, a new black (0,0,0,0) PixelDouble is returned.
	 * @param image source ImageDouble
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the validated PixelDouble value at the specified coordinates
	 */	
	public PixelDouble pixelAt(ImageDouble image, int x, int y) {
		PixelDouble copy = null;
		
		if ((x > 0) && (x < image.getImageWidth()) && (y > 0) && (y < image.getImageHeight())) {
			copy = image.getPixel(x, y);
	}
	else {
		if(x<=0) {
			if(y<=0) {
				copy = image.getPixel(1, 1);
			}
			else if(y>=image.getImageHeight()) {
				copy = image.getPixel(1, image.getImageHeight()-1);
			}
			else {
				copy = image.getPixel(1, y);
			}			
		}
		else if(x>=image.getImageWidth()) {
			if(y<=0) {
				copy = image.getPixel(image.getImageWidth()-1, 1);
			}
			else if(y>=image.getImageHeight()) {
				copy = image.getPixel(image.getImageWidth()-1, image.getImageHeight()-1);
			}
			else {
				copy = image.getPixel(image.getImageWidth()-1, y);
			}
		}
	}	
	
		return copy;		
	}
}
