/* -*- mode: java; c-basic-offset: 8; indent-tabs-mode: t; tab-width: 8 -*- */

package vib;

import amira.AmiraParameters;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

/* This plugin takes a binned image as input. It then reassigns equally spaced
   gray values to the pixels. */
public class Resample_ extends NaiveResampler implements PlugInFilter {
	ImagePlus image;
	boolean verbose=false;

	@Override
	public void run(ImageProcessor ip) {
		GenericDialog gd = new GenericDialog("Parameters");
		gd.addNumericField("Factor x", 2, 0);
		gd.addNumericField("Factor y", 2, 0);
		gd.addNumericField("Factor z", 2, 0);
		gd.addCheckbox("MinEntropy", false);
		gd.showDialog();
		if(gd.wasCanceled())
			return;

		int factorX = (int)gd.getNextNumber();
		int factorY = (int)gd.getNextNumber();
		int factorZ = (int)gd.getNextNumber();
		boolean minEntropy = gd.getNextBoolean();

		ImagePlus res = (minEntropy ?
			resampleMinEnt(image, factorX, factorY, factorZ) :
			resample(image, factorX, factorY, factorZ));

		if (AmiraParameters.isAmiraMesh(image))
			new AmiraParameters(image).setParameters(res);
		else {
			Object info = image.getProperty("Info");
			if (info != null)
				res.setProperty("Info", info);
		}

		res.show();
	}

	@Override
	public int setup(String arg, ImagePlus imp) {
		image = imp;
		// TODO: handle 32-bit
		return DOES_8G | DOES_8C | DOES_16 | DOES_RGB | NO_CHANGES;
	}
}
