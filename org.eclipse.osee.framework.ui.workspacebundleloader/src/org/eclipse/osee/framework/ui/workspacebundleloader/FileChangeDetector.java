/*
 * Created on Dec 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.ChecksumUtil;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author b1528444
 *
 */
public class FileChangeDetector {

	private ConcurrentHashMap<URL, byte[]> bundleNameToMd5Map;

	public FileChangeDetector(){
		bundleNameToMd5Map = new ConcurrentHashMap<URL, byte[]>();
	}
	
	public boolean isChanged(URL url) {
		byte[] digest = getMd5Checksum(url);
		if (bundleNameToMd5Map.containsKey(url)) {
			// check for bundle binary equality
			if (!Arrays.equals(bundleNameToMd5Map.get(url), digest)) {
				bundleNameToMd5Map.put(url, digest);
				return true;
			} else {
				return false;
			}
		} else {
			bundleNameToMd5Map.put(url, digest);
			return true;
		}
	}

	private byte[] getMd5Checksum(URL url) {
		InputStream in = null;
		byte[] digest = new byte[0];
		try {
			in = url.openStream();
			digest = ChecksumUtil.createChecksum(url.openStream(), "MD5");
		} catch (IOException ex) {
			OseeLog.log(FileChangeDetector.class, Level.SEVERE, ex);
		} catch (NoSuchAlgorithmException ex) {
			OseeLog.log(FileChangeDetector.class, Level.SEVERE, ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					OseeLog.log(FileChangeDetector.class, Level.SEVERE, ex);
				}
			}
		}
		return digest;
	}

	/**
	 * @param url
	 * @return
	 */
	public boolean remove(URL url) {
		bundleNameToMd5Map.remove(url);
		return true;
	}

	/**
	 * 
	 */
	public void clear() {
		bundleNameToMd5Map.clear();
	}

}
