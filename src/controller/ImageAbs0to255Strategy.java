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
 * <p>Title: ImageClampStrategy</p>
 * <p>Description: Image-related strategy</p>
 * <p>Copyright: Copyright (c) 2004 Colin Barré-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.8 $
 */
public class ImageAbs0to255Strategy extends ImageConversionStrategy {
	/**
	 * Converts an ImageDouble to an ImageX using a clamping strategy (0-255).
	 */
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;
		
		PixelDouble valMin = new PixelDouble(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
		PixelDouble valMax = new PixelDouble(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
		
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				valMin.setRed(Math.min(curPixelDouble.getRed(), valMin.getRed()));
				valMin.setGreen(Math.min(curPixelDouble.getGreen(), valMin.getGreen()));
				valMin.setBlue(Math.min(curPixelDouble.getBlue(), valMin.getBlue()));
				valMin.setAlpha(Math.min(curPixelDouble.getAlpha(), valMin.getAlpha()));
				
				valMax.setRed(Math.max(curPixelDouble.getRed(), valMax.getRed()));
				valMax.setGreen(Math.max(curPixelDouble.getGreen(), valMax.getGreen()));
				valMax.setBlue(Math.max(curPixelDouble.getBlue(), valMax.getBlue()));
				valMax.setAlpha(Math.max(curPixelDouble.getAlpha(), valMax.getAlpha()));
			}
		}
		
		newImage.beginPixelUpdate();
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				newImage.setPixel(x, y, new Pixel((int)(correct(curPixelDouble.getRed(), valMin.getRed(), valMax.getRed())),
												  (int)(correct(curPixelDouble.getGreen(), valMin.getGreen(), valMax.getGreen())),
												  (int)(correct(curPixelDouble.getBlue(), valMin.getBlue(), valMax.getBlue())),
												  (int)(correct(curPixelDouble.getAlpha(), valMin.getAlpha(), valMax.getAlpha()))));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
	}
	
	private double correct(double value, double min, double max) {
		if(min < 0 || max > 255) {
			if(max - min <= 255) {
				if(min < 0) {
					value -= min;
				} else if (max > 255){
					value -= max - 255;
				}
			} else if (max - min > 255){
				value = (value / (max - min)) * 255;
			}
		}
			
		return value;
	}
}
