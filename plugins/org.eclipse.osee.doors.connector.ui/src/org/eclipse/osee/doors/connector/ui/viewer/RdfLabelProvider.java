/*********************************************************************
 * Copyright (c) 2016 Boeing
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