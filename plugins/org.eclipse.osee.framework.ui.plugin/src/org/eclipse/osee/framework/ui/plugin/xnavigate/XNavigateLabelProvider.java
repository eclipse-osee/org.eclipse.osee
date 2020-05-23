/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class XNavigateLabelProvider implements ILabelProvider {

   /**
    * ListViewers don't support images
    * 
    * @param arg0 the element
    */
   @Override
   public Image getImage(Object arg0) {
      return ((XNavigateItem) arg0).getImage();
   }

   /**
    * Gets the text for an element
    * 
    * @param arg0 the element
    */
   @Override
   public String getText(Object arg0) {
      if (arg0 == null) {
         return "";
      }
      return ((XNavigateItem) arg0).getName();
   }

   /**
    * Adds a listener
    * 
    * @param arg0 the listener
    */
   @Override
   public void addListener(ILabelProviderListener arg0) {
      // Throw it away
   }

   /**
    * Disposes any resources
    */
   @Override
   public void dispose() {
      // Nothing to dispose
   }

   /**
    * Returns whether changing the specified property for the specified element affect the label
    * 
    * @param arg0 the element
    * @param arg1 the property
    */
   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   /**
    * Removes a listener
    * 
    * @param arg0 the listener
    */
   @Override
   public void removeListener(ILabelProviderListener arg0) {
      // Ignore
   }
}
