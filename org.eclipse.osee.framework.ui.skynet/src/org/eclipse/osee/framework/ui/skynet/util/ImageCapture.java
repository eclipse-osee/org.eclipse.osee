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
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ImageDialog;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class ImageCapture {

   final Image image;

   public ImageCapture(Control control) {
      GC gc = new GC(control.getDisplay());
      Rectangle bounds = control.getBounds();
      Point topLeft = control.toDisplay(0, 0);
      image = new Image(Display.getCurrent(), bounds.width, bounds.height);
      gc.copyArea(image, topLeft.x, topLeft.y);
      gc.dispose();
   }

   public Image getImage() {
      return image;
   }

   public void popupDialog() {
      ImageDialog diag = new ImageDialog(image, Display.getCurrent().getActiveShell());
      diag.open();
   }
}
