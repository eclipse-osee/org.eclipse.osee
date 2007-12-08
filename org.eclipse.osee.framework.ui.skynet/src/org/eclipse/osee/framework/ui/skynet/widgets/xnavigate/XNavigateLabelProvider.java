/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class XNavigateLabelProvider implements ILabelProvider {

   /**
    * ListViewers don't support images
    * 
    * @param arg0 the element
    * @return Image
    */
   public Image getImage(Object arg0) {
      return ((XNavigateItem) arg0).getImage();
   }

   /**
    * Gets the text for an element
    * 
    * @param arg0 the element
    * @return String
    */
   public String getText(Object arg0) {
      return ((XNavigateItem) arg0).getName();
   }

   /**
    * Adds a listener
    * 
    * @param arg0 the listener
    */
   public void addListener(ILabelProviderListener arg0) {
      // Throw it away
   }

   /**
    * Disposes any resources
    */
   public void dispose() {
      // Nothing to dispose
   }

   /**
    * Returns whether changing the specified property for the specified element affect the label
    * 
    * @param arg0 the element
    * @param arg1 the property
    * @return boolean
    */
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   /**
    * Removes a listener
    * 
    * @param arg0 the listener
    */
   public void removeListener(ILabelProviderListener arg0) {
      // Ignore
   }
}
