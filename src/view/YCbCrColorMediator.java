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

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class YCbCrColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider yCS;
	ColorSlider cbCS;
	ColorSlider crCS;
	
	/*int red;
	int green;
	int blue;*/
	
	int[] ycbcr;
	
	BufferedImage redImage;
	BufferedImage greenImage;
	BufferedImage blueImage;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	YCbCrColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		int red = result.getPixel().getRed();
		int green = result.getPixel().getGreen();
		int blue = result.getPixel().getBlue();
		
		ycbcr = convertRGBtoYCbCr(red, green, blue);
		
		this.result = result;
		result.addObserver(this);
		
		redImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		greenImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeRedImage(ycbcr);
		computeGreenImage(ycbcr);
		computeBlueImage(ycbcr); 	
		
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateRed = false;
		boolean updateGreen = false;
		boolean updateBlue = false;
		if (s == yCS && v != ycbcr[0]) {
			ycbcr[0] = v;
			updateGreen = true;
			updateBlue = true;
		}
		if (s == cbCS && v != ycbcr[1]) {
			ycbcr[1] = v;
			updateRed = true;
			updateBlue = true;
		}
		if (s == crCS && v != ycbcr[2]) {
			ycbcr[2] = v;
			updateRed = true;
			updateGreen = true;
		}
		if (updateRed) {
			computeRedImage(ycbcr);
		}
		if (updateGreen) {
			computeGreenImage(ycbcr);
		}
		if (updateBlue) {
			computeBlueImage(ycbcr);
		}
		
		Pixel pixel = new Pixel(getRed(), getGreen(), getBlue(), 255);
		result.setPixel(pixel);
		
		//int[] ycbcr = convertRGBtoYCbCr(red,green,blue);
		
		//System.out.println("y:"+ycbcr[0]+" cb:"+ycbcr[1]+" cr:"+ycbcr[2]);
	}
	
	public void computeRedImage(int[] ycbcr) { 
		Pixel p = new Pixel(getRed(), getGreen(), getBlue(), 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				redImage.setRGB(i, j, rgb);
			}
		}
		if (yCS != null) {
			yCS.update(redImage);
		}
	}
	
	public void computeGreenImage(int[] ycbcr) {
		Pixel p = new Pixel(getRed(), getGreen(), getBlue(), 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setGreen((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				greenImage.setRGB(i, j, rgb);
			}
		}
		if (cbCS != null) {
			cbCS.update(greenImage);
		}
	}
	
	public void computeBlueImage(int[] ycbcr) { 
		Pixel p = new Pixel(getRed(), getGreen(), getBlue(), 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setBlue((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blueImage.setRGB(i, j, rgb);
			}
		}
		if (crCS != null) {
			crCS.update(blueImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getBlueImage() {
		return blueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getGreenImage() {
		return greenImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getRedImage() {
		return redImage;
	}

	/**
	 * @param slider
	 */
	public void setRedCS(ColorSlider slider) {
		yCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setGreenCS(ColorSlider slider) {
		cbCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setBlueCS(ColorSlider slider) {
		crCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public int getBlue() {
		return convertYCbCrtoRGB(ycbcr)[2];
	}

	/**
	 * @return
	 */
	public int getGreen() {
		return convertYCbCrtoRGB(ycbcr)[1];
	}

	/**
	 * @return
	 */
	public int getRed() {
		return convertYCbCrtoRGB(ycbcr)[1];
	}
	
	private int[] convertRGBtoYCbCr(int red, int green, int blue) {
		
		int[] ycbcr = new int[3];
		
		ycbcr[0] = (int)(0.299*red + 0.587*green + 0.114*blue + 0.5);
		ycbcr[1] = (int) (-0.1687*red-0.3313*green+0.5*blue+128 + 0.5);
		ycbcr[2] = (int) (0.5*red-0.4187*green-0.0813*blue+128 + 0.5);
		
		return ycbcr;
	}
	
	private int[] convertYCbCrtoRGB(int[] ycbcr) {
		
		int[] rgb = new int[3];
		
		int y = ycbcr[0];
		int cb = ycbcr[1];
		int cr = ycbcr[2];
		
		rgb[0] = (int) (y + 1.4*(cr-128) + 0.5);
		rgb[1] = (int) (y - 0.343*(cb-128) - 0.711*(cr-128) + 0.5);
		rgb[2] = (int) (y + 1.765*(cb-128) + 0.5);
				
		return rgb;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(getRed(), getGreen(), getBlue(), 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		ycbcr = convertRGBtoYCbCr(result.getPixel().getRed(),result.getPixel().getGreen(),result.getPixel().getBlue());
		
		int red = result.getPixel().getRed();
		int green = result.getPixel().getGreen();
		int blue = result.getPixel().getBlue();
		
		yCS.setValue(red);
		cbCS.setValue(green);
		crCS.setValue(blue);
		computeRedImage(ycbcr);
		computeGreenImage(ycbcr);
		computeBlueImage(ycbcr);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

