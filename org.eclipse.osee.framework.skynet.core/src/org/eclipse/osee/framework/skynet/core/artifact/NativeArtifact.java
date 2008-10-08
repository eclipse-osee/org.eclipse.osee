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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.swt.graphics.Image;

/**
 * Artifact type used to indicate that this artifact is to be rendered by a passing its binary data to a native program
 * for editing.
 * 
 * @author Ryan D. Brooks
 */
public class NativeArtifact extends Artifact {
   public static final String CONTENT_NAME = "Native Content";
   private static final SkynetActivator plugin = SkynetActivator.getInstance();

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    */
   public NativeArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public Image getImage() {
      try {
         Image image = plugin.getImageForProgram(getFileExtension());
         if (image == null) {
            image = plugin.getImage("laser_16_16.gif");
         }
         return image;
      } catch (Exception ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public String getFileName() throws OseeCoreException {
      return getDescriptiveName() + "." + getFileExtension();
   }

   public String getFileExtension() throws OseeCoreException {
      return getSoleAttributeValue("Extension", "");
   }

   public InputStream getNativeContent() throws IOException, OseeCoreException {
      return getSoleAttributeValue(CONTENT_NAME);
   }

   public void setNativeContent(File importFile) throws OseeCoreException, FileNotFoundException {
      setNativeContent(new FileInputStream(importFile));
   }

   public void setNativeContent(InputStream inputStream) throws OseeCoreException {
      setSoleAttributeValue(CONTENT_NAME, inputStream);
   }
}