/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.ProgramFinder;
import org.eclipse.swt.program.Program;

/**
 * Renders native content.
 *
 * @author Murshed Alam
 */
public class MarkdownRenderer extends FileSystemRenderer {

   private final IComparator comparator;

   public MarkdownRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
      this.comparator = new PlainTextDiffRenderer();
   }

   public MarkdownRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = null;
      try {
         imageDescriptor = ImageManager.getProgramImageDescriptor(getAssociatedExtension(artifact));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         imageDescriptor = ArtifactImageManager.getImageDescriptor(artifact);
      }

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Preview Markdown Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "Markdown Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.EDIT, "org.eclipse.osee.framework.ui.skynet.othereditor.command",
         imageDescriptor));
   }

   @Override
   public String getName() {
      return "Markdown Editor";
   }

   @Override
   public MarkdownRenderer newInstance() {
      return new MarkdownRenderer();
   }

   @Override
   public MarkdownRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new MarkdownRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PrimaryAttribute)) {
         if (presentationType.matches(SPECIALIZED_EDIT, PREVIEW, DEFAULT_OPEN)) {
            return PRESENTATION_SUBTYPE_MATCH;
         } else if (presentationType.matches(DIFF)) {
            String extension = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.Extension, "md");
            if (extension.contains("md")) {
               return PRESENTATION_SUBTYPE_MATCH;
            }
         }
      }

      return NO_MATCH;
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return getAssociatedExtension(artifact, "md");
   }

   private String getAssociatedExtension(Artifact artifact, String defaultValue) {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Extension, defaultValue);
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      String extension = getAssociatedExtension(artifact);
      Program program = ProgramFinder.findProgram(extension);
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension [%s] found on your local machine.",
            extension);
      }
      return program;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      InputStream stream = null;
      try {
         if (artifacts.isEmpty()) {
            Artifact artifact = artifacts.iterator().next();
            stream = artifact.getSoleAttributeValue(CoreAttributeTypes.MarkdownContent);
         } else {
            Artifact artifact = artifacts.iterator().next();
            String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.MarkdownContent);
            if (presentationType == PresentationType.DIFF && WordCoreUtil.containsWordAnnotations(content)) {
               throw new OseeStateException(
                  "Trying to diff the [%s] artifact on the [%s] branch, which has tracked changes turned on.  All tracked changes must be removed before the artifacts can be compared.",
                  artifact.getName(), artifact.getBranchToken().getName());
            }
            stream = Streams.convertStringToInputStream(content, "UTF-8");
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return stream;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.MarkdownContent);
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {
      for (Artifact artifact : artifacts) {
         super.open(Arrays.asList(artifact), presentationType);
      }
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }
}