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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ImageDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

/**
 * Capture an image within the bounds of OSEE workbench.
 *
 * @author Donald G. Dunne
 */
public class ImageCaptureBlam extends AbstractBlam {

   public Point topLeftPoint;
   public Point botRightPoint;
   public boolean listenerAdded = false;

   @Override
   public String getName() {
      return "Image Capture";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            log("Starting Image Capture...");
            topLeftPoint = null;
            botRightPoint = null;
            Display.getDefault().addFilter(SWT.MouseUp, displayKeysListener);
         }
      });
   }
   Listener displayKeysListener = new Listener() {
      @Override
      public void handleEvent(org.eclipse.swt.widgets.Event event) {
         if (event.type == SWT.MouseUp) {
            if (topLeftPoint == null) {
               topLeftPoint = event.display.getCursorLocation();
               logf("\nFirst Mouse Event " + topLeftPoint);
            } else {
               botRightPoint = event.display.getCursorLocation();
               logf("Second Mouse Event " + botRightPoint);
               GC gc = new GC(Display.getCurrent());
               Image image =
                  new Image(Display.getCurrent(), botRightPoint.x - topLeftPoint.x, botRightPoint.y - topLeftPoint.y);
               gc.copyArea(image, topLeftPoint.x, topLeftPoint.y);
               gc.dispose();
               Display.getDefault().removeFilter(SWT.MouseUp, displayKeysListener);
               ImageDialog diag = new ImageDialog(image, Displays.getActiveShell());
               diag.open();
            }
         }
      }
   };

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Mouse Down on top left location, Mouse Up on bottom right.  Only works within bounds of workbench window.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}