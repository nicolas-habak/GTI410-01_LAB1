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
	
	private final int CYAN = 0;
	private final int MAGENTA = 1;
	private final int YELLOW = 2;
	private final int BLACK = 3;
	
	ColorSlider[] cs;
	
	float[] cmyk;
	
	
	BufferedImage[] images;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		cmyk = convertRGBAtoCMYK(result.getPixel());
		
		images = new BufferedImage[4];
		cs = new ColorSlider[4];
		
		this.result = result;
		result.addObserver(this);
		
		images[CYAN] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		images[MAGENTA] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		images[YELLOW] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		images[BLACK] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

		computeImage(CYAN);
		computeImage(MAGENTA);
		computeImage(YELLOW);
		computeImage(BLACK);
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		float normalizedV = (float) v / 255.0f;
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		
		if (s == cs[CYAN] && normalizedV != cmyk[0]) {
			cmyk[0] = normalizedV;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == cs[MAGENTA] && normalizedV != cmyk[1]) {
			cmyk[1] = normalizedV;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == cs[YELLOW] && normalizedV != cmyk[2]) {
			cmyk[2] = normalizedV;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == cs[BLACK] && normalizedV != cmyk[3]) {
			cmyk[3] = normalizedV;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		
		if (updateCyan) {
			computeImage(CYAN);
		}
		if (updateMagenta) {
			computeImage(MAGENTA);
		}
		if (updateYellow) {
			computeImage(YELLOW);
		}
		if (updateBlack) {
			computeImage(BLACK);
		}
		
		Pixel pixel = getPixelRGBA();
		result.setPixel(pixel);
	}
	
	private Pixel getPixelRGBA(){
		int[] rgba = convertCMYKtoRGBA(cmyk);
		return new Pixel(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	public void computeImage(int index) {
		Pixel p = new Pixel();
		float[] cmyk = this.cmyk.clone();
		int[] rgba;
		for (int i = 0; i < imagesWidth; ++i) {
			cmyk[index] = i / (float)imagesWidth;
			
			rgba = convertCMYKtoRGBA(cmyk);
			
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
	public BufferedImage getYellowImage() {
		return images[YELLOW];
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return images[MAGENTA];
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return images[CYAN];
	}
	
	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return images[BLACK];
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cs[CYAN] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		cs[MAGENTA] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		cs[YELLOW] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		cs[BLACK] = slider;
		slider.addObserver(this);
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
		Pixel currentColor = getPixelRGBA();
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		cmyk = convertRGBAtoCMYK(result.getPixel());
		
		cs[CYAN].setValue((int)(255.0f * cmyk[0]));
		cs[MAGENTA].setValue((int)(255.0f * cmyk[1]));
		cs[YELLOW].setValue((int)(255.0f * cmyk[2]));
		cs[BLACK].setValue((int)(255.0f * cmyk[3]));

		computeImage(CYAN);
		computeImage(MAGENTA);
		computeImage(YELLOW);
		computeImage(BLACK);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}
	
	private float[] convertRGBAtoCMYK(int red, int green, int blue) {
		float normalizedRed = (float) red / 255;
		float normalizedGreen = (float) green / 255;
		float normalizedBlue = (float) red / 255;
		
		float[] cmyk = new float[4];
		cmyk[3] = 1.0f - Math.max(Math.max(normalizedRed, normalizedGreen), normalizedBlue);
		cmyk[0] = 1.0f - normalizedRed - cmyk[3];
		cmyk[1] = 1.0f - normalizedGreen - cmyk[3];
		cmyk[2] = 1.0f - normalizedBlue - cmyk[3];
		
		if(cmyk[3] < 1) {
			cmyk[0] /= (1.0f - cmyk[3]);
			cmyk[0] /= (1.0f - cmyk[3]);
			cmyk[0] /= (1.0f - cmyk[3]);
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