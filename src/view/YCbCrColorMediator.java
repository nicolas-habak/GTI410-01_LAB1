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
	private final int Y = 0;
	private final int CB = 1;
	private final int CR = 2;
	
	ColorSlider[] cs;
	
	int[] ycbcr;
	
	
	BufferedImage[] images;
	
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	YCbCrColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		
		ycbcr = convertRGBtoYCbCr(result.getPixel());		
		images = new BufferedImage[3];
		cs = new ColorSlider[3];
		
		this.result = result;
		result.addObserver(this);
		
		
		images[Y] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		images[CB] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		images[CR] = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		computeImage(Y);
		computeImage(CB);
		computeImage(CR);
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		//float normalizedV = (float) v / 255.0f;
		boolean updateY = false;
		boolean updateCb = false;
		boolean updateCr = false;
		
		if (s == cs[Y] && v != ycbcr[0]) {
			ycbcr[0] = v;
			updateCb = true;
			updateCr = true;
		}
		if (s == cs[CB] && v != ycbcr[1]) {
			ycbcr[1] = v;
			updateY = true;
			updateCr = true;
		}
		if (s == cs[CR] && v != ycbcr[2]) {
			ycbcr[2] = v;
			updateY = true;
			updateCb = true;
		}
		
		if (updateY) {
			computeImage(Y);
		}
		if (updateCb) {
			computeImage(CB);
		}
		if (updateCr) {
			computeImage(CR);
		}
		
		Pixel pixel = getPixelRGBA();
		result.setPixel(pixel);
	}
	
	private Pixel getPixelRGBA(){
		int[] rgba = convertYCbCrtoRGB(ycbcr);
		return new Pixel(rgba[0], rgba[1], rgba[2]);
	}
	
	public void computeImage(int index) {
		Pixel p = new Pixel();
		int[] ycbcr = this.ycbcr.clone();
		int[] rgba;
		for (int i = 0; i < imagesWidth; ++i) {
			ycbcr[index] = (int)(((double)i / (double)imagesWidth)*255.0);
			
			rgba = convertYCbCrtoRGB(ycbcr);
			
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
	public BufferedImage getYImage() {
		return images[Y];
	}
	
	/**
	 * @return
	 */
	public BufferedImage getCBImage() {
		return images[CB];
	}
	
	/**
	 * @return
	 */
	public BufferedImage getCRImage() {
		return images[CR];
	}

	/**
	 * @param slider
	 */
	public void setYCS(ColorSlider slider) {
		cs[Y] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setCbCS(ColorSlider slider) {
		cs[CB] = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setCrCS(ColorSlider slider) {
		cs[CR] = slider;
		slider.addObserver(this);
	}

	/**
	 * @return
	 */
	public double getY() {
		return ycbcr[0];
	}

	/**
	 * @return
	 */
	public double getCb() {
		return ycbcr[1];
	}

	/**
	 * @return
	 */
	public double getCr() {
		return ycbcr[2];
	}

	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = getPixelRGBA();
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		ycbcr = convertRGBtoYCbCr(result.getPixel());
		
		cs[Y].setValue(ycbcr[0]);
		cs[CB].setValue(ycbcr[1]);
		cs[CR].setValue(ycbcr[2]);
		
		computeImage(Y);
		computeImage(CB);
		computeImage(CR);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

	private int[] convertRGBtoYCbCr(int r, int g, int b) {
		
		int[] ycbcr = new int[3];
		
		ycbcr[0] = (int)(0.299*r + 0.587*g + 0.114*b + 0.5);
		ycbcr[1] = (int) (-0.1687*r-0.3313*g+0.5*b+128 + 0.5);
		ycbcr[2] = (int) (0.5*r-0.4187*g-0.0813*b+128 + 0.5);
	
	    for (int i=0; i<3; i++) {
        	if (ycbcr[i] > 255)
        		ycbcr[i] = 255;
        	else if (ycbcr[i] < 0)
    			ycbcr[i] = 0;
        }
        
        return ycbcr;
	}

	private int[] convertRGBtoYCbCr(Pixel p) {
		return convertRGBtoYCbCr(p.getRed(),p.getGreen(),p.getBlue());
	
	}
	
	private int[] convertYCbCrtoRGB(int y, int cb, int cr) {
		
		int[] rgb = new int[4];
		
		rgb[0] = (int) (y + 1.4*(cr-128) + 0.5);
		rgb[1] = (int) (y - 0.343*(cb-128) - 0.711*(cr-128) + 0.5);
		rgb[2] = (int) (y + 1.765*(cb-128) + 0.5);
		rgb[3] = 255;
		
	   	for (int i=0; i<3; i++) {
    		if (rgb[i] > 255)
    			rgb[i] = 255;
    		else if (rgb[i] < 0)
    			rgb[i] = 0;
    	}
				
		return rgb;
	}

	private int[] convertYCbCrtoRGB(int[] ycbcr) {
		return convertYCbCrtoRGB(ycbcr[0],ycbcr[1],ycbcr[2]);
	}
}
