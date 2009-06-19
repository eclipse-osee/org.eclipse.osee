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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.swt.program.Program;

/**
 * Renders native content.
 * 
 * @author Ryan D. Brooks
 */
public class NativeRenderer extends FileRenderer {
   public static final String EXTENSION_ID = "org.eclipse.osee.framework.ui.skynet.render.NativeRenderer";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getCommandId()
    */
   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.nativeeditor.command");
         commandIds.add("org.eclipse.osee.framework.ui.skynet.othereditor.command");
      }

      return commandIds;
   }

   /**
    * @param rendererId
    */
   public NativeRenderer() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.Renderer#getName()
    */
   @Override
   public String getName() {
      return "Native Editor";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#newInstance()
    */
   @Override
   public NativeRenderer newInstance() throws OseeCoreException {
      return new NativeRenderer();
   }

   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if ((artifact.isOfType("Native") || artifact.isOfType("General Data")) && presentationType == PresentationType.SPECIALIZED_EDIT) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedExtension()
    */
   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return artifact.getSoleAttributeValue(NativeArtifact.EXTENSION, "xml");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getAssociatedProgram()
    */
   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      String extension = getAssociatedExtension(artifact);
      Program program = Program.findProgram(extension);
      if (program == null) {
         throw new OseeArgumentException(
               "No program associated with the extension " + extension + " found on your local machine.");
      }
      return program;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, java.util.List, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      return getRenderInputStream(artifacts.iterator().next(), presentationType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.FileRenderer#getRenderInputStream(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.lang.String, org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer.PresentationType)
    */
   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      Attribute<?> attribute = artifact.getSoleAttribute(NativeArtifact.CONTENT_NAME);
      //If the native artifact has been created without content create empty XML content.
      if (attribute == null) {
         attribute = artifact.createAttribute(AttributeTypeManager.getType(NativeArtifact.CONTENT_NAME), true);
         try {
            attribute.setValueFromInputStream(Streams.convertStringToInputStream(
                  WordWholeDocumentAttribute.getEmptyDocumentContent(), "UTF-8"));
         } catch (UnsupportedEncodingException e) {
            throw new OseeCoreException(e);
         }
      }
      return artifact.getSoleAttributeValue(NativeArtifact.CONTENT_NAME);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.IRenderer#minimumRanking()
    */
   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }
}