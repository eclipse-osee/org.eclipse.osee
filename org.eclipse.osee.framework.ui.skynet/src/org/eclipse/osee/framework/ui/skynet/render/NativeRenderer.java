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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.swt.program.Program;

/**
 * Renders native content.
 * 
 * @author Ryan D. Brooks
 */
public class NativeRenderer extends FileRenderer {
   public static final String EXTENSION_ID = "org.eclipse.osee.framework.ui.skynet.render.NativeRenderer";

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.nativeeditor.command");
         commandIds.add("org.eclipse.osee.framework.ui.skynet.othereditor.command");
      }

      if (presentationType == PresentationType.PREVIEW) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.nativeprevieweditor.command");
      }

      return commandIds;
   }

   /**
    * @param rendererId
    */
   public NativeRenderer() {
      super();
   }

   @Override
   public String getName() {
      return "Native Editor";
   }

   @Override
   public NativeRenderer newInstance() throws OseeCoreException {
      return new NativeRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact.isAttributeTypeValid(CoreAttributes.NATIVE_CONTENT.getName())) {
         switch (presentationType) {
            case SPECIALIZED_EDIT:
            case PREVIEW:
               return PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return artifact.getSoleAttributeValue(CoreAttributes.NATIVE_EXTENSION.getName(), "xml");
   }

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

   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      return getRenderInputStream(artifacts.iterator().next(), presentationType);
   }

   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return artifact.getSoleAttributeValue(CoreAttributes.NATIVE_CONTENT.getName());
   }

   @Override
   public int minimumRanking() throws OseeCoreException {
      return NO_MATCH;
   }
}