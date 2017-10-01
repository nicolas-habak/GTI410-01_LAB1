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
	
	private final int HUE = 0;
	private final int SATURATION = 1;
	private final int VALUE = 2;
	
	ColorSlider[] cs;
	float[] hsv;
	
	BufferedImage[] images;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		hsv = convertRGBAtoHSV(result.getPixel());
		images = new BufferedImage[3];
		cs = new ColorSlider[3];
		
		this.result = result;
		result.addObserver(this);
		
		//hsv = Color.RGBtoHSB(red, green, blue, null);
		//int hue = (int) Math.round(360 * hsv[0]);
		
		for(int i = 0; i < images.length; i ++)
		{
			images[i] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
			computeImage(i);
		}	
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		float normalizedV = (float)v / 255;
		boolean updateH = false;
		boolean updateS = false;
		boolean updateV = false;
		
		if (s == cs[HUE] && v != hsv[HUE]) {
			hsv[HUE] = normalizedV * 360;
			updateS = true;
			updateV = true;
		}
		if (s == cs[SATURATION] && normalizedV != hsv[SATURATION]) {
			hsv[SATURATION] = normalizedV;
			updateH = true;
			updateV = true;
		}
		if (s == cs[VALUE] && normalizedV != hsv[VALUE]) {
			hsv[VALUE] = normalizedV;
			updateH = true;
			updateS = true;
		}
		if (updateH) {
			computeImage(HUE);
		}
		if (updateS) {
			computeImage(SATURATION);
		}
		if (updateV) {
			computeImage(VALUE);
		}
		
		Pixel pixel = getPixelRGBA();
		result.setPixel(pixel);
	}
	
	private Pixel getPixelRGBA(){
		int[] rgba = convertHSVtoRGBA(hsv);
		return new Pixel(rgba[0], rgba[1], rgba[2], rgba[3]);
	}


	public void computeImage(int index) {
		Pixel p = new Pixel();
		float[] hsv = this.hsv.clone();
		int[] rgba;
		for (int i = 0; i < imagesWidth; ++i) {
			hsv[index] = (float)i / (float)imagesWidth * (index == HUE ? 360 : 1);
			
			rgba = convertHSVtoRGBA(hsv);
			
			p.setRed(rgba[0]);
			p.setGreen(rgba[1]);
			p.setBlue(rgba[2]);
			p.setAlpha(rgba[3]);
			
			int rgb = p.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				images[index].setRGB(i, j, rgb);
			}
		}
		if (cs[index] != null) {
			cs[index].update(images[index]);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return images[HUE];
	}
	
	/**
	 * @return
	 */
	public BufferedImage getSaturationImage() {
		return images[SATURATION];
	}
	
	/**
	 * @return
	 */
	public BufferedImage getValueImage() {
		return images[VALUE];
	}	

	/**
	 * @param slider
	 */
	public void sethCS(ColorSlider slider) {
		cs[HUE] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setsCS(ColorSlider slider) {
		cs[SATURATION] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setvCS(ColorSlider slider) {
		cs[VALUE] = slider;
		slider.addObserver(this);
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
	
	private int[] convertHSVtoRGBA(float[] hsv) {
		return convertHSVtoRGBA(hsv[0], hsv[1], hsv[2]);
	}
	
	private int[] convertHSVtoRGBA(float h, float s, float v) {
		float c = s * v;
		float x = c * (1.0f - Math.abs((h / 60) % 2.0f - 1.0f));
		float m = v - c;
		
		float[] normRGB = new float[3];
		int[] rgb = new int[4];
		
		if(h < 60) normRGB = new float[] { c, x, 0 };		// 0 <= H < 60 
		else if(h < 120) normRGB = new float[] { x, c, 0 };	// 60 <= H < 120
		else if(h < 180) normRGB = new float[] { 0, c, x };	// 120 <= H < 180
		else if(h < 240) normRGB = new float[] { 0, x, c };	// 180 <= H < 240
		else if(h < 300) normRGB = new float[] { x, 0, c };	// 240 <= H < 300
		else normRGB = new float[] { c, 0, x };				// 300 <= H < 360
		
		for(int i = 0; i < normRGB.length; i++) {
			rgb[i] = (int)((normRGB[i] + m) * 255);
		}
		
		rgb[3] = 255;
		
		return rgb;
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
	
	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = getPixelRGBA();
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		hsv = convertRGBAtoHSV(result.getPixel());
		
		cs[HUE].setValue((int)(hsv[HUE] / 360 * 255));
		cs[SATURATION].setValue((int)(255.0f * hsv[SATURATION]));
		cs[VALUE].setValue((int)(255.0f * hsv[VALUE]));
		
		computeImage(HUE);
		computeImage(SATURATION);
		computeImage(VALUE);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

