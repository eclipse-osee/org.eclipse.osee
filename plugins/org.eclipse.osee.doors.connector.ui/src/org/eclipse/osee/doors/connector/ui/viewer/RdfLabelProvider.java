/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.doors.connector.ui.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class RdfLabelProvider extends LabelProvider {
   public RdfLabelProvider() {
      super();
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   @Override
   public Image getImage(Object element) {
      if (element instanceof RdfExplorerItem) {
         RdfExplorerItem item = (RdfExplorerItem) element;
         return item.getImage();
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   /*
    * @see ILabelProvider#getText(Object)
    */
   @Override
   public String getText(Object element) {
      if (element instanceof RdfExplorerItem) {
         RdfExplorerItem item = (RdfExplorerItem) element;
         return item.getName();
      }
      throw new IllegalArgumentException("wrong type: " + element.getClass().getName());
   }
}