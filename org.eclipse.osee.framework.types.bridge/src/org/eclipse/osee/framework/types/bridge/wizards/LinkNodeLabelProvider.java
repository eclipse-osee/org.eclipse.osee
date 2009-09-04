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
package org.eclipse.osee.framework.types.bridge.wizards;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.types.bridge.TypesImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class LinkNodeLabelProvider extends LabelProvider {

   @Override
   public Image getImage(Object element) {
      Image toReturn = super.getImage(element);
      LinkNode node = null;
      if (element instanceof LinkNode) {
         node = (LinkNode) element;
         toReturn = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
      }
      if (element instanceof LinkMessage) {
         toReturn = ImageManager.getImage(TypesImage.OSEE_TYPES_LINK);
      }
      if (node != null && !node.isResolved()) {
         toReturn = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
      }
      return toReturn;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof LinkMessage) {
         return ((LinkMessage) element).getImportEntry();
      } else if (element instanceof LinkNode) {
         LinkNode node = (LinkNode) element;
         return node.getUri().toFileString();
      }
      return super.getText(element);
   }

}
