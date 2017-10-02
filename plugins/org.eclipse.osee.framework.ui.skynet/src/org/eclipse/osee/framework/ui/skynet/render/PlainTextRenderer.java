/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * Renderer for artifact with ASCII plain text file as its content. This is compared to the more traditional
 * WordRenderer which renders artifacts (e.g.: Software Requirements) content which is Word Documents.
 *
 * @author Shawn F. Cook
 */
public class PlainTextRenderer extends FileSystemRenderer {

   private final IComparator comparator;

   public PlainTextRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
      this.comparator = new PlainTextDiffRenderer();
   }

   public PlainTextRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = ImageManager.getProgramImageDescriptor("txt");
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "Plain Text Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Preview Plain Text Editor", imageDescriptor));
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts)  {
      InputStream stream = null;
      try {
         if (artifacts.isEmpty()) {
            Artifact artifact = artifacts.iterator().next();
            stream = artifact.getSoleAttributeValue(CoreAttributeTypes.PlainTextContent);
         } else {
            Artifact artifact = artifacts.iterator().next();
            String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.PlainTextContent);
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
   public Program getAssociatedProgram(Artifact artifact)  {
      Program program = Program.findProgram("txt");
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension *.txt found on your local machine.");
      }
      return program;
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PlainTextContent)) {
         if (presentationType.matches(SPECIALIZED_EDIT, PREVIEW, DEFAULT_OPEN, PRODUCE_ATTRIBUTE, DIFF)) {
            return PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "txt";
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.PlainTextContent);
   }

   @Override
   public String getName() {
      return "Plain Text Editor";
   }

   @Override
   public PlainTextRenderer newInstance() {
      return new PlainTextRenderer(new HashMap<RendererOption, Object>());
   }

   @Override
   public PlainTextRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new PlainTextRenderer(rendererOptions);
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType)  {
      for (Artifact artifact : artifacts) {
         super.open(Arrays.asList(artifact), presentationType);
      }
   }
}
