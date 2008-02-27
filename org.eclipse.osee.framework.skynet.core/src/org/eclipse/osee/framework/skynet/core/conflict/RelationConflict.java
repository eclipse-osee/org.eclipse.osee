/*
 * Created on Feb 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 *
 */
public class RelationConflict{

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getDestGamma()
	 */
	public int getDestGamma() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getImage()
	 */
	public Image getImage() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getSourceGamma()
	 */
	public int getSourceGamma() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
