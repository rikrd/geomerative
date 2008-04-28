package org.apache.batik.svggen.font;

import java.io.*;

public class RandomAccessFileEmulator extends DataInputStream{
	
	public RandomAccessFileEmulator(byte[] byteArray, String accesMode){
		super(new ByteArrayInputStream(byteArray));
	}

	public void seek(long n) throws IOException{
		reset();
		skip(n);
	}
}
