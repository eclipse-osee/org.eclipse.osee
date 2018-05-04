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

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.ProgramFinder;
import org.eclipse.swt.program.Program;

/**
 * Renders native content.
 *
 * @author Ryan D. Brooks
 */
public class NativeRenderer extends FileSystemRenderer {

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = null;
      try {
         imageDescriptor = ImageManager.getProgramImageDescriptor(getAssociatedExtension(artifact));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         imageDescriptor = ArtifactImageManager.getImageDescriptor(artifact);
      }

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Preview Native Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "Native Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.EDIT, "org.eclipse.osee.framework.ui.skynet.othereditor.command",
         imageDescriptor));
   }

   @Override
   public String getName() {
      return "Native Editor";
   }

   @Override
   public NativeRenderer newInstance() {
      return new NativeRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Object... objects) throws OseeCoreException {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.NativeContent)) {
         if (presentationType.matches(SPECIALIZED_EDIT, PREVIEW, DEFAULT_OPEN)) {
            return PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return getAssociatedExtension(artifact, "xml");
   }

   private String getAssociatedExtension(Artifact artifact, String defaultValue) throws OseeCoreException {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Extension, defaultValue);
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      String extension = getAssociatedExtension(artifact);
      Program program = ProgramFinder.findProgram(extension);
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension [%s] found on your local machine.",
            extension);
      }
      return program;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      Artifact artifact = artifacts.iterator().next();
      return artifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent);
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.NativeContent);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      for (Artifact artifact : artifacts) {
         super.open(Arrays.asList(artifact), presentationType);
      }
   }
}