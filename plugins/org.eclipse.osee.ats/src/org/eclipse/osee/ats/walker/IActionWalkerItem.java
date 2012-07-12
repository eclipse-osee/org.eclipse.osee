/*
 * Created on Jul 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.walker;

import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public interface IActionWalkerItem {

   public Image getImage();

   public String getName();

   public void handleDoubleClick();
}
