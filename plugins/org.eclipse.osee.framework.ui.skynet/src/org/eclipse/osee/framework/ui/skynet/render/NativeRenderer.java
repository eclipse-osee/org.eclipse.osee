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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.NativeWordCompare;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.ProgramFinder;
import org.eclipse.swt.program.Program;

/**
 * Renders native content.
 *
 * @author Ryan D. Brooks
 */
public class NativeRenderer extends FileSystemRenderer {

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "xml";

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "XML";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = NativeRenderer.class.getCanonicalName();

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Native Editor";

   private final IComparator comparator;

   public NativeRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
      this.comparator = new NativeWordCompare(this);
   }

   public NativeRenderer() {
      this(null);
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

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Preview Native Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "Native Editor", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.EDIT, "org.eclipse.osee.framework.ui.skynet.othereditor.command",
         imageDescriptor));
   }

   @Override
   public NativeRenderer newInstance() {
      return new NativeRenderer();
   }

   @Override
   public NativeRenderer newInstance(RendererMap rendererOptions) {
      return new NativeRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.NativeContent)) {
         if (presentationType.matches(SPECIALIZED_EDIT, PREVIEW, DEFAULT_OPEN)) {
            return PRESENTATION_SUBTYPE_MATCH;
         } else if (presentationType.matches(DIFF)) {
            String extension = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.Extension, "xml");
            if (extension.contains("doc")) {
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

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return NativeRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return NativeRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return NativeRenderer.RENDERER_NAME;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return NativeRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
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
      Artifact artifact = artifacts.iterator().next();
      return artifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent);
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch,
      PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.NativeContent);
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