/*
 * Created on Feb 21, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.conflict;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips 
 *
 */
public interface IConflict extends IAdaptable {
	
	public Image getImage();
	
	public int getSourceGamma();
	
	public int getDestGamma();
}
