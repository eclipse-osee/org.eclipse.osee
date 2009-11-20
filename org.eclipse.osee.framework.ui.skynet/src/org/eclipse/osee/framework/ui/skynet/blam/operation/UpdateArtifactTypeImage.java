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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.widgets.Display;

/**
 * @author Ryan D. Brooks
 */
public class UpdateArtifactTypeImage extends AbstractBlam {

   public static String ARTIFACT_TYPE_NAME = "Select Artifact Type";
   public static String SELECT_IMAGE = "Select Image GIF";

   @Override
   public String getName() {
      return "Update ArtifactType Image";
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String filename = variableMap.getString(SELECT_IMAGE);
      final ArtifactType artifactSubtypeDescriptor = variableMap.getArtifactType("Select Artifact Type");
      if (Strings.isValid(filename)) {
         File imageFile = new File(filename);
         if (!imageFile.exists()) {
            throw new OseeArgumentException("Invalid image filename.");
         }
         ImageManager.setArtifactTypeImageInDb(artifactSubtypeDescriptor, new ByteArrayInputStream(
               Lib.inputStreamToBytes(new FileInputStream(imageFile))));
      } else {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Clear Database Image?",
                        "No Image File Selected.\n\nSelect \"Ok\" to clear image from database (default image will be used).")) {
                     ImageManager.setArtifactTypeImageInDb(artifactSubtypeDescriptor, null);
                  }
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "This BLAM will import the selected 16x16 pixel gif image as the image for the selected artifact type.  Existing image will be overwritten.\nLeaving image filename blank will clear the image from the database.  Programatic default will be used instead.";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"" + SELECT_IMAGE + "\" />");
      buffer.append("<XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"" + ARTIFACT_TYPE_NAME + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}