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
package org.eclipse.osee.ote.ui.message.watch.action;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.swt.graphics.Image;

public final class ElementLabelProvider extends LabelProvider {
   private static final Image Img = ImageManager.getImage(OteMessageImage.PIPE);

   @Override
   public Image getImage(Object element) {
      return Img;
   }

   @Override
   public String getText(Object element) {
      final Element elem = (Element) element;
      return String.format("%s: byte=%d, msb=%d, lsb=%d", elem.getName(), elem.getByteOffset(), elem.getMsb(),
            elem.getLsb());
   }
}