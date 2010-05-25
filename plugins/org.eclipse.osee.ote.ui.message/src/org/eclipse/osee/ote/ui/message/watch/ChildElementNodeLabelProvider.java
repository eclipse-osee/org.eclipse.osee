/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 */
public class ChildElementNodeLabelProvider extends LabelProvider {
   private static final Image recordImg = ImageManager.getImage(OteMessageImage.DATABASE);
   private static final Image elementImg = ImageManager.getImage(OteMessageImage.PIPE);

   @Override
   public Image getImage(Object element) {
      if (element instanceof RecordElement) {
         return recordImg;
      } else if (element instanceof Element) {
         return elementImg;
      }
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof Element) {
         if (element instanceof RecordMap) {
            return ((Element) element).getDescriptiveName();
         } else {
            return String.format("%s: byte=%d, msb=%d, lsb=%d", ((Element) element).getDescriptiveName(),
                  ((Element) element).getByteOffset(), ((Element) element).getMsb(), ((Element) element).getLsb());
         }
      }
      return "<UNKNOWN>";
   }
}
