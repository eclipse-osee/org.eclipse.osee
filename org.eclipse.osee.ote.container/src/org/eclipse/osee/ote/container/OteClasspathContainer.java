/*
 * Created on Aug 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.container;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * @author b1528444
 *
 */
public class OteClasspathContainer implements IClasspathContainer {

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
	 */
	@Override
	public IClasspathEntry[] getClasspathEntries() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
	 */
	@Override
	public int getKind() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
	 */
	@Override
	public IPath getPath() {
		return null;
	}

}
