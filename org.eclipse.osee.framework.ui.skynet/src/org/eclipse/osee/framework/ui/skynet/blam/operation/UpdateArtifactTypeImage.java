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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.InputStreamImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 */
public class UpdateArtifactTypeImage extends AbstractBlam {

   public static String ARTIFACT_TYPE_NAME = "Select Artifact Type";
   public static String SELECT_IMAGE = "Select Image GIF";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               String filename = variableMap.getString(SELECT_IMAGE);
               if (filename == null) {
                  AWorkbench.popup("ERROR", "Must enter full path to image.");
                  return;
               }
               File imageFile = new File(filename);
               if (!imageFile.exists()) {
                  AWorkbench.popup("ERROR", "Invalid image filename.");
                  return;
               }
               ArtifactType artifactSubtypeDescriptor =
                     variableMap.getArtifactType("Select Artifact Type");
               if (!MessageDialog.openConfirm(
                     Display.getCurrent().getActiveShell(),
                     "Update Artifact Type Image",
                     "Set Image for Artifact Type \"" + artifactSubtypeDescriptor.getName() + "\" to \"" + filename + "?")) {
                  return;
               }

               InputStreamImageDescriptor imageDescriptor =
                     new InputStreamImageDescriptor(imageFile.toURL().openStream());
               ArtifactTypeManager.updateArtifactTypeImage(artifactSubtypeDescriptor, imageDescriptor);
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         };
      });

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "This BLAM will import the selected 16x16 pixel gif image as the image for the selected artifact type.  Existing image will be overwritten.\n\nNOTE: Change default branch for other Artifact Types.";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"" + SELECT_IMAGE + "\" />");
      buffer.append("<XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"" + ARTIFACT_TYPE_NAME + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }
}