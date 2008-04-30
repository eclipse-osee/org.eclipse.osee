/*
 * Created on Apr 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.osee.framework.resource.management.IResource;

/**
 * @author b1528444
 *
 */
public class DbResource implements IResource {

	private String name = "unknown";
	private InputStream inputStream;
	private boolean isCompressed = false;
	private URI uri;
	/**
	 * @param binaryStream
	 */
	public DbResource(InputStream inputStream) {
		this.inputStream = inputStream;
		try {
			uri = new URI("db://");
		} catch (URISyntaxException ex1) {
		}
		if(inputStream.markSupported()){
			inputStream.mark(1000);
			ZipInputStream in = new ZipInputStream(inputStream);
			// find out if it was compressed
			try {
				ZipEntry entry = in.getNextEntry();
				if (entry == null) {
					isCompressed = false;
				} else {
					name = entry.getName();
					isCompressed = true;
				}
			} catch (IOException ex) {
				isCompressed = false;
			}
			try {
				inputStream.reset();
			} catch (IOException ex) {
			}
		}		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.resource.management.IResource#getContent()
	 */
	@Override
	public InputStream getContent() throws IOException {
		return inputStream;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.resource.management.IResource#getLocation()
	 */
	@Override
	public URI getLocation() {
		return uri;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.resource.management.IResource#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.resource.management.IResource#isCompressed()
	 */
	@Override
	public boolean isCompressed() {
		return isCompressed;
	}

}
