/*
 * Created on Feb 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.conflict;

import java.io.InputStream;

import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 *
 */
public class AttributeConflict implements  IConflict {
	private int sourceGammaId;
	private int destGammaId;
	private String sourceValue;
	private String destValue;
	private InputStream sourceContent;
	private InputStream destContent;
	private Image image;
	private int artId;
	private int attrId;
	

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getDestGamma()
	 */
	public int getDestGamma() {
		return destGammaId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getImage()
	 */
	public Image getImage() {
		return image;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.ui.skynet.conflict.IConflict#getSourceGamma()
	 */
	public int getSourceGamma() {
		return sourceGammaId;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
}
