package net.ion.radon.impl.let.extract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.ion.framework.rest.RopeRepresentation;
import net.ion.framework.rope.Rope;
import net.ion.icss.export.Exporter;
import net.ion.radon.core.TreeContext;

import org.apache.commons.io.IOUtils;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class ExtractHelper {
	
	public static ExtractConfigLoader getConfigLoader(TreeContext context) {
		ExtractConfigLoader loader = context.getAttributeObject("extract.config", ExtractConfigLoader.class);
		return loader;
	}
	
	private static File makeTempFile(InputStream input, String suffix) throws IOException {
		OutputStream output = null ;
		try {
 
			File tempFile = File.createTempFile("extract", suffix);
			output = new FileOutputStream(tempFile);
			IOUtils.copy(input, output);

			input.close();
			output.close();
			
			return tempFile ;
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}
	}
	
	private static Rope makeRope(File tempFile, String configFile, String exePath) throws FileNotFoundException, IOException {
		
		Exporter exporter = Exporter.make(new File(configFile), exePath);
		Rope rope = exporter.convert(tempFile);
		
		return rope;
	}
	
	
	
	public static  Representation extractorFile(InputStream stream, String configFile, String exePath, String suffix) throws IOException {
		File tempFile = null;
		try {
			//long start = System.currentTimeMillis();
			tempFile =  ExtractHelper.makeTempFile(stream, suffix) ;
			Rope rope = makeRope(tempFile, configFile, exePath);
			
			//ExtractorTrace.add( ExportInfo.createInputStream(APP_NAME, start, System.currentTimeMillis(), tempFile.length()));
			
			Representation result = new RopeRepresentation(rope, MediaType.TEXT_PLAIN);
			result.setCharacterSet(CharacterSet.UTF_8);
			return result;
		} finally {
			if (tempFile != null)
				tempFile.delete();
		}
	}

}
