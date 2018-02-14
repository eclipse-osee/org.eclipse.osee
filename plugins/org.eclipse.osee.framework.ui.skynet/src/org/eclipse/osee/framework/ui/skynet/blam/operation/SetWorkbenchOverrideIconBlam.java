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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("restriction")
public class SetWorkbenchOverrideIconBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Set Workbench Override Icon";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      final String filename = variableMap.getString("Image Filename");
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            // Set the application title

            if (Strings.isValid(filename)) {
               File imageFile = new File(filename);
               if (!imageFile.exists()) {
                  logf("Invalid image filename.");
               }
               try {
                  Image overrideImage = ImageDescriptor.createFromURL(imageFile.toURI().toURL()).createImage();
                  overrideImage(overrideImage);
                  File overrideFile = OseeData.getFile("workbenchOverride.gif");
                  Lib.copyFile(imageFile, overrideFile);
               } catch (Exception ex) {
                  log(ex);
               }
            }
         }
      });
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XFileSelectionDialog\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Image Filename\" /></xWidgets>";
   }

   public static void reloadOverrideImage() {
      try {
         File overrideFile = OseeData.getFile("workbenchOverride.gif");
         if (overrideFile.exists()) {
            Image overrideImage = ImageDescriptor.createFromURL(overrideFile.toURI().toURL()).createImage();
            overrideImage(overrideImage);
         } else if (ClientSessionManager.isSessionValid() && ClientSessionManager.getSession().getClientVersion().equals(
            "Development")) {
            Image overideImage = ImageManager.getImage(FrameworkImage.OSEE_32_RUN);
            overrideImage(overideImage);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error restoring .osee.data/workbenchOverride.gif image.", ex);
      }
   }

   public static void overrideImage(Image overrideImage) {
      for (IWorkbenchWindow window : Workbench.getInstance().getWorkbenchWindows()) {
         final Shell appShell = window.getShell();
         // Set the application icons
         final Image[] appIcons = {overrideImage, overrideImage, overrideImage};
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               appShell.setImages(appIcons);
            }
         });
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "Set the selected 32x32 pixel as the image for the workbench for this workspace.  This allows for easy recognition if multiple workbenches are running";
   }

}
