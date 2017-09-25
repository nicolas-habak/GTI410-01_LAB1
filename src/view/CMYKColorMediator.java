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

class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;
	
	float[] cmyk;
	
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		cmyk = convertRGBAtoCMYK(result.getPixel());
		
		this.result = result;
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

		computeCyanImage(cmyk);
		computeMagentaImage(cmyk);
		computeYellowImage(cmyk);
		computeBlackImage(cmyk);
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		
		if (s == cyanCS && v != cmyk[0]) {
			cmyk[0] = v;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == magentaCS && v != cmyk[1]) {
			cmyk[1] = v;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == yellowCS && v != cmyk[2]) {
			cmyk[2] = v;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == blackCS && v != cmyk[3]) {
			cmyk[3] = v;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		
		if (updateCyan) {
			computeCyanImage(cmyk);
		}
		if (updateMagenta) {
			computeMagentaImage(cmyk);
		}
		if (updateYellow) {
			computeYellowImage(cmyk);
		}
		if (updateBlack) {
			computeBlackImage(cmyk);
		}
		
		Pixel pixel = new Pixel((int)getRed(), (int)getGreen(), (int)getBlue(), 255);
		result.setPixel(pixel);
	}
	
	public void computeCyanImage(float[] cmyk) {
		computeCyanImage(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
	}
	
	public void computeCyanImage(float cyan, float magenta, float yellow, float black) { 
		Pixel p = new Pixel((int)getRed(), (int)getGreen(), (int)getBlue(), 255);
		float[] cmyk = convertRGBAtoCMYK(p);
		int[] rgba;
		for (int i = 0; i<imagesWidth; ++i) {
			cmyk[0] = i / (float)imagesWidth;
			
			rgba = convertCMYKtoRGBA(cmyk);
			
			p.setRed(rgba[0]);
			p.setGreen(rgba[1]);
			p.setBlue(rgba[2]);
			p.setAlpha(rgba[3]);
			
			int rgb = p.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
	}
	
	public void computeMagentaImage(float[] cmyk) {
		computeMagentaImage(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
	}
	
	public void computeMagentaImage(float cyan, float magenta, float yellow, float black) {
		Pixel p = new Pixel((int)getRed(), (int)getGreen(), (int)getBlue(), 255);
		float[] cmyk = convertRGBAtoCMYK(p);
		int[] rgba;
		for (int i = 0; i<imagesWidth; ++i) {
			cmyk[1] = i / (float)imagesWidth;
			
			rgba = convertCMYKtoRGBA(cmyk);
			
			p.setRed(rgba[0]);
			p.setGreen(rgba[1]);
			p.setBlue(rgba[2]);
			p.setAlpha(rgba[3]);
			
			int rgb = p.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}

	public void computeYellowImage(float[] cmyk) {
		computeYellowImage(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
	}
	
	public void computeYellowImage(float cyan, float magenta, float yellow, float black) {
		Pixel p = new Pixel((int)getRed(), (int)getGreen(), (int)getBlue(), 255);
		float[] cmyk = convertRGBAtoCMYK(p);
		int[] rgba;
		for (int i = 0; i<imagesWidth; ++i) {
			cmyk[2] = i / (float)imagesWidth;
			
			rgba = convertCMYKtoRGBA(cmyk);
			
			p.setRed(rgba[0]);
			p.setGreen(rgba[1]);
			p.setBlue(rgba[2]);
			p.setAlpha(rgba[3]);
			
			int rgb = p.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}
	
	public void computeBlackImage(float[] cmyk) {
		computeBlackImage(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
	}
	
	public void computeBlackImage(float cyan, float magenta, float yellow, float black) {
		Pixel p = new Pixel((int)getRed(), (int)getGreen(), (int)getBlue(), 255);
		float[] cmyk = convertRGBAtoCMYK(p);
		int[] rgba;
		for (int i = 0; i<imagesWidth; ++i) {
			cmyk[3] = i / (float)imagesWidth;
			
			rgba = convertCMYKtoRGBA(cmyk);
			
			p.setRed(rgba[0]);
			p.setGreen(rgba[1]);
			p.setBlue(rgba[2]);
			p.setAlpha(rgba[3]);
			
			int rgb = p.getARGB();
			
			for (int j = 0; j<imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}
	
	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return cyanImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getBlue() {
		return 255 - cmyk[0];
	}

	/**
	 * @return
	 */
	public double getGreen() {
		return 255 - cmyk[1];
	}

	/**
	 * @return
	 */
	public double getRed() {
		return 255 - cmyk[2];
	}

	/**
	 * @return
	 */
	public double getCyan() {
		return cmyk[0];
	}

	/**
	 * @return
	 */
	public double getMagenta() {
		return cmyk[1];
	}

	/**
	 * @return
	 */
	public double getYellow() {
		return cmyk[2];
	}

	/**
	 * @return
	 */
	public double getBlack() {
		return cmyk[3];
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel((int)getRed(), (int)getGreen(), (int)getBlue(), 255);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		cmyk = convertRGBAtoCMYK(result.getPixel());
		
		cyanCS.setValue((int)(255 * cmyk[0]));
		magentaCS.setValue((int)(255 * cmyk[1]));
		yellowCS.setValue((int)(255 * cmyk[2]));
		blackCS.setValue((int)(255 * cmyk[3]));
		
		computeCyanImage(cmyk);
		computeMagentaImage(cmyk);
		computeYellowImage(cmyk);
		computeBlackImage(cmyk);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
	
	private float[] convertRGBAtoCMYK(int red, int green, int blue) {
		red /= 255;
		green /= 255;
		blue /= 255;
		
		float[] cmyk = new float[4];
		cmyk[3] = 1 - Math.max(Math.max(red, green), blue);
		cmyk[0] = (1 - red - cmyk[3]);
		cmyk[1] = (1 - green - cmyk[3]);
		cmyk[2] = (1 - blue - cmyk[3]);
		
		if(cmyk[3] < 1) {
			cmyk[0] /= (1 - cmyk[3]);
			cmyk[0] /= (1 - cmyk[3]);
			cmyk[0] /= (1 - cmyk[3]);
		}
		return cmyk;
	}
	
	private float[] convertRGBAtoCMYK(Pixel p) {
		return convertRGBAtoCMYK(p.getRed(), p.getGreen(), p.getBlue());
	}
	
	private int[] convertCMYKtoRGBA(float[] cmyk) {
		if(cmyk.length > 3)
			return convertCMYKtoRGBA(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
		return null;
	}
	
	private int[] convertCMYKtoRGBA(float cyan, float magenta, float yellow, float black) {
		int[] rgba = new int[4];
		rgba[0] = (int) (255.0f * (1 - cyan) * (1 - black));
		rgba[1] = (int) (255.0f * (1 - magenta) * (1 - black));
		rgba[2] = (int) (255.0f * (1 - yellow) * (1 - black));
		rgba[3] = 255;
		return rgba;
	}
}

