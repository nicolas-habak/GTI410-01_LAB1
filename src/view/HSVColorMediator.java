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

import java.awt.Color;
import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hCS;
	ColorSlider sCS;
	ColorSlider vCS;
	
	float[] hsv;
	
	int red;
	int green;
	int blue;
	
	BufferedImage HueImage;
	BufferedImage greenImage;
	BufferedImage blueImage;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		this.result = result;
		result.addObserver(this);
		
		hsv = Color.RGBtoHSB(red, green, blue, null);
		//int hue = (int) Math.round(360 * hsv[0]);
		
		HueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		greenImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeHueImage(hsv);
		computeSaturationImage(hsv);
		computeValueImage(hsv); 	
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateH = false;
		boolean updateS = false;
		boolean updateV = false;
		if (s == hCS && v != red) {
			red = v;
			updateS = true;
			updateV = true;
		}
		if (s == sCS && v != green) {
			green = v;
			updateH = true;
			updateV = true;
		}
		if (s == vCS && v != blue) {
			blue = v;
			updateH = true;
			updateS = true;
		}
		if (updateH) {
			computeHueImage(hsv);
		}
		if (updateS) {
			computeSaturationImage(hsv);
		}
		if (updateV) {
			computeValueImage(hsv);
		}
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
	}
	
	public void computeHueImage(float[] hsv) { 
		Pixel p = new Pixel((int)getRed(), (int)getBlue(), (int)getGreen(), 255); 
		
		
		
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				HueImage.setRGB(i, j, rgb);
			}
		}
		if (hCS != null) {
			hCS.update(HueImage);
		}
	}
	
	public void computeSaturationImage(float[] hsv) {
		Pixel p = new Pixel(red, green, blue, 255); 
				
		for (int i = 0; i<imagesWidth; ++i) {
			p.setGreen((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				greenImage.setRGB(i, j, rgb);
			}
		}
		if (sCS != null) {
			sCS.update(greenImage);
		}
	}
	
	public void computeValueImage(float[] hsv) { 
		Pixel p = new Pixel(red, green, blue, 255); 
		
		for (int i = 0; i<imagesWidth; ++i) {
			p.setBlue((int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blueImage.setRGB(i, j, rgb);
			}
		}
		if (vCS != null) {
			vCS.update(blueImage);
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
	public BufferedImage getHueImage() {
		return HueImage;
	}

	/**
	 * @param slider
	 */
	public void sethCS(ColorSlider slider) {
		hCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setsCS(ColorSlider slider) {
		sCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setvCS(ColorSlider slider) {
		vCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getBlue() {
		Color c = new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
		return c.getBlue();
	}

	/**
	 * @return
	 */
	public double getGreen() {
		Color c = new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
		return c.getGreen();
	}

	/**
	 * @return
	 */
	public double getRed() {
		Color c = new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
		return c.getRed();
	}

	public double getHue() {
		return hsv[0];
	}
	
	public double getSat() {
		return hsv[1];
	}
	
	public double getVal() {
		return hsv[2];
	}
	
	private int[] convertHSVtoRGB(float[] hsv) {
		return convertHSVtoRGB(hsv[0], hsv[1], hsv[2]);
	}
	
	private int[] convertHSVtoRGB(float h, float s, float v) {
		Color c = new Color(Color.HSBtoRGB(h, s, v));
		int[] rgb = new int[3];
		rgb[0] = c.getRed();
		rgb[1] = c.getGreen();
		rgb[2] = c.getBlue();
		
		return rgb;
	}

	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(red, green, blue, 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
		
		hCS.setValue(red);
		sCS.setValue(green);
		vCS.setValue(blue);
		computeHueImage(hsv);
		computeSaturationImage(hsv);
		computeValueImage(hsv);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

