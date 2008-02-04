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
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.CompressedContentAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks Artifact type used to indicate that this artifact is to be rendered by a passing its binary
 *         data to a native program for editing.
 */
public class NativeArtifact extends Artifact {
   public static final String CONTENT_NAME = "Native Content";
   private static final SkynetActivator plugin = SkynetActivator.getInstance();

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    * @throws SQLException
    */
   public NativeArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   @Override
   public Image getImage() {
      Image image = plugin.getImageForProgram(getExtension());
      if (image == null) {
         image = plugin.getImage("laser_16_16.gif");
      }

      return image;
   }

   public String getFileName() {
      return getDescriptiveName() + "." + getExtension();
   }

   public String getExtension() {
      return getSoleAttributeValue("Extension");
   }

   public InputStream getNativeContent() throws IOException, SQLException {
      CompressedContentAttribute attribute =
            (CompressedContentAttribute) getAttributeManager("Native Content").getSoleAttribute();
      return attribute.getUncompressedStream();
   }

   public void setNativeContent(File importFile) throws IOException, SQLException {
      DynamicAttributeManager attributeManager = getAttributeManager(CONTENT_NAME);
      attributeManager.setData(new FileInputStream(importFile));
   }

   public void setNativeContent(InputStream inputStream) throws IOException, SQLException {
      DynamicAttributeManager attributeManager = getAttributeManager(CONTENT_NAME);
      attributeManager.setData(inputStream);
   }

   /**
    * Creates a new artifact and duplicates all of its attribute data.
    */
   @Override
   public Artifact duplicate(Branch branch) {
      NativeArtifact newArtifact = null;
      try {
         ArtifactSubtypeDescriptor newDescriptor = getDescriptor(branch);

         newArtifact = (NativeArtifact) newDescriptor.makeNewArtifact();
         duplicateAttributes(newArtifact);
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (IOException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return newArtifact;
   }

   private Artifact duplicateAttributes(NativeArtifact artifact) throws IOException, IllegalStateException, SQLException {
      if (artifact == null) throw new IllegalArgumentException("Artifact can not be null.");

      for (DynamicAttributeManager attrManager : getAttributes()) {
         for (Attribute attribute : attrManager.getAttributes()) {

            if (attribute instanceof CompressedContentAttribute) {
               CompressedContentAttribute contentAttribute = (CompressedContentAttribute) attribute;
               artifact.setNativeContent(contentAttribute.getUncompressedStream());

            } else {
               artifact.setSoleAttributeValue(attribute.getName(), attribute.getStringData());
            }
         }
      }
      return artifact;
   }

}