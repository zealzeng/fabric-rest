/**
 * 
 */
package com.whlylc.fabricrest.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zeal
 *
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
	
	/**
	 * Write fully 
	 * @param file
	 * @param in
	 * @param totalSize
	 * @throws IOException
	 */
	public static void writeFully(File file, InputStream in, int totalSize) throws IOException {
		 //FileOutputStream fos = null;
		 try (FileOutputStream fos = new FileOutputStream(file)) {
			 
			 int writeCount = 0;
			 byte[] buf = new byte[1024];
			 while (writeCount < totalSize) {
				 int readCount = in.read(buf);
				 if (readCount == -1) {
					 break;
				 }
				 else if (readCount == 0) {
					 continue;
				 }
				 fos.write(buf, 0, readCount);
				 writeCount += readCount;
			 }
			 if (writeCount < totalSize) {
				 throw new IOException("Expected total size is " + totalSize + ", but only write" + writeCount);
			 }
		 }
		 finally {
//			 IOUtils.closeQuietly(fos);
		 }
	}

}
