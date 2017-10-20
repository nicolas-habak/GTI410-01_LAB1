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

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barré-Brisebois, Éric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.12 $
 */
public class ImageLineFiller extends AbstractTransformer {
	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageLineFiller() {
		setFloodFill(true);
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();
					int colorGerme = currentImage.getPixelInt(ptTransformed.x, ptTransformed.y);
					if(floodFill)doFloodFill(ptTransformed, colorGerme);
					if(!floodFill)doBorderFill(ptTransformed);
					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void doFloodFill(Point ptClicked, int germe) {

		Pixel pGerme = new Pixel(germe);
		float[] hsvGerme = convertRGBAtoHSV(pGerme);
		float[] hsvThresholds = new float[] { getHueThreshold(), getSaturationThreshold(), getValueThreshold() };
		float[] hsvMaxThresholds = new float[] { 180.0f, 255.0f, 255.0f };
		
		Stack stack = new Stack();
		stack.push(ptClicked);
		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() && 0 <= current.y && current.y < currentImage.getImageHeight() 
					&& !currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				Pixel p = new Pixel(currentImage.getPixelInt(current.x, current.y));
				float[] hsvPixel = convertRGBAtoHSV(p);
				
				boolean inRange = true;
				
				for(int i = 0; i < hsvGerme.length && inRange; i++) {
					inRange = hsvPixel[i] <= hsvGerme[i] * (1 + hsvThresholds[i] / hsvMaxThresholds[i]) && hsvPixel[i] >= hsvGerme[i] * (1 - hsvThresholds[i] / hsvMaxThresholds[i]);
				}
				
				if(inRange){
					currentImage.setPixel(current.x, current.y, fillColor);
					
					// Next points to fill.
					Point nextLeft = new Point(current.x-1, current.y);
					Point nextRight = new Point(current.x+1, current.y);
					Point nextUp = new Point(current.x, current.y+1);
					Point nextDown = new Point(current.x, current.y-1);
					stack.push(nextLeft);
					stack.push(nextRight);
					stack.push(nextUp);
					stack.push(nextDown);
				}
			}
		}
	}
	
	private void doBorderFill(Point ptClicked) {
		Stack stack = new Stack();
		stack.push(ptClicked);
		while (!stack.empty()) {
			Point current = (Point)stack.pop();
			if (0 <= current.x && current.x < currentImage.getImageWidth() && 0 <= current.y && current.y < currentImage.getImageHeight() 
					&& !currentImage.getPixel(current.x, current.y).equals(borderColor) && !currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				
				currentImage.setPixel(current.x, current.y, fillColor);
				
				// Next points to fill.
				Point nextLeft = new Point(current.x-1, current.y);
				Point nextRight = new Point(current.x+1, current.y);
				Point nextUp = new Point(current.x, current.y+1);
				Point nextDown = new Point(current.x, current.y-1);
				stack.push(nextLeft);
				stack.push(nextRight);
				stack.push(nextUp);
				stack.push(nextDown);
			}
		}
	}
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}

	private float[] convertRGBAtoHSV(int[] rgb) {
		float[] normRGB = new float[rgb.length];
		float[] hsv = new float[3];
		
		for(int i = 0; i < rgb.length; i++) {
			normRGB[i] = (float)rgb[i] / 255;
		}
		
		float cmax = Math.max(Math.max(normRGB[0], normRGB[1]), normRGB[2]);
		float cmin = Math.min(Math.min(normRGB[0], normRGB[1]), normRGB[2]);
		float delta = cmax - cmin;
		
		if (delta == 0)
			hsv[0] = 0;
		else if (cmax == normRGB[0])
			hsv[0] = 60.0f * (((normRGB[1] - normRGB[2]) / delta) % 60);
		else if (cmax == normRGB[1])
			hsv[0] = 60.0f * ((normRGB[2] - normRGB[0]) / delta + 2);
		else
			hsv[0] = 60.0f * ((normRGB[0] - normRGB[1]) / delta + 4);
		
		if(hsv[0] < 0)
			hsv[0] += 360;
		else if (hsv[0] > 360)
			hsv[0] -= 360;
		
		if(cmax == 0)
			hsv[1] = 0;
		else
			hsv[1] = delta / cmax;
		
		hsv[2] = cmax;
		
		return hsv;
	}
	private float[] convertRGBAtoHSV(Pixel p) {
		return convertRGBAtoHSV(new int[] { p.getRed(), p.getGreen(), p.getBlue() });
	}
}
