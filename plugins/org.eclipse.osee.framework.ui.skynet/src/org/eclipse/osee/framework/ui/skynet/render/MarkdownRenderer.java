/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.api.synchronization.ExportRequest;
import org.eclipse.osee.define.api.synchronization.Root;
import org.eclipse.osee.define.api.synchronization.SynchronizationEndpoint;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
 * @author Loren K. Ashley
 */

public class MarkdownRenderer extends FileSystemRenderer {

   /**
    * This is only relevant when {#link {@link #LOCAL} is <code>true</code>. When set to <code>true</code> the renderer
    * will write to the output stream provided to it via the renderer options from the {@link FileSystemRender} base
    * class. When set to <code>false</code> the renderer will render to a local buffer, when complete create an input
    * stream that reads from that buffer, and provide that input stream back to the {@link FileSystemRender} base class.
    */

   private static final boolean CAN_STREAM = true;

   /**
    * The context menu command identifier for the Other Editor.
    */

   private static final String COMMAND_ID_OTHER_EDITOR = "org.eclipse.osee.framework.ui.skynet.othereditor.command";

   /**
    * The context menu command title for the Edit command.
    */

   private static final String COMMAND_TITLE_EDIT = "Makrdown Editor";

   /**
    * The context menu command title for the Preview command.
    */

   private static final String COMMAND_TITLE_PREVIEW = "Preview Markdown Editor";

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "md";

   /**
    * The {@link ImageDescriptor} used to draw the icon for this renderer's command icon.
    */

   private static ImageDescriptor imageDescriptor;
   /**
    * Set this to true to perform basic mark down rendering on the client. Set this to false to use the Synchronization
    * Artifact API to perform the mark down rendering on the server.
    */

   private static boolean LOCAL = true;

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "Markdown";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = MarkdownRenderer.class.getCanonicalName();

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Markdown Editor";

   /**
    * The Synchronization Artifact type name for requesting a mark down render from the Synchronization Artifact API.
    */

   private static final String SYNCHRONIATION_ARTIFACT_TYPE = "markdown";

   static {

      MarkdownRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(MarkdownRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION);

      //@formatter:off
      MarkdownRenderer.menuCommandDefinitions =
         List.of
            (
               new MenuCmdDef
                      (
                         CommandGroup.PREVIEW,
                         PresentationType.PREVIEW,
                         MarkdownRenderer.COMMAND_TITLE_PREVIEW,
                         MarkdownRenderer.imageDescriptor
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.EDIT,
                         PresentationType.SPECIALIZED_EDIT,
                         MarkdownRenderer.COMMAND_TITLE_EDIT,
                         MarkdownRenderer.imageDescriptor
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.EDIT,
                         MarkdownRenderer.COMMAND_ID_OTHER_EDITOR,
                         MarkdownRenderer.imageDescriptor
                      )
            );
      //@formatter:on
   }

   private final IComparator comparator;

   /**
    * Synchronization Artifact API endpoint for server side rendering.
    */

   private final SynchronizationEndpoint synchronizationEndpoint;

   public MarkdownRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   public MarkdownRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
      this.comparator = new MarkdownDiffRenderer();
      this.menuCommands = MarkdownRenderer.menuCommandDefinitions;
      if (MarkdownRenderer.LOCAL & MarkdownRenderer.CAN_STREAM) {
         this.synchronizationEndpoint =
            OsgiUtil.getService(this.getClass(), OseeClient.class).getSynchronizationEndpoint();
         this.updateOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, true);
      } else {
         this.synchronizationEndpoint = null;
         this.updateOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, false);
      }
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {

      Objects.requireNonNull(commands,
         "MarkdownRenderer::addMenuCommandDefinitions, the parameter \"commands\" cannot be null.");

      ImageDescriptor unfinalImageDescriptor = null;
      if (Objects.nonNull(artifact)) {
         try {
            unfinalImageDescriptor = ImageManager.getProgramImageDescriptor(this.getAssociatedExtension(artifact));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            unfinalImageDescriptor = ArtifactImageManager.getImageDescriptor(artifact);
         }
      }

      var imageDescriptor = unfinalImageDescriptor;
      var menuStream = this.menuCommands.stream();
      if (Objects.nonNull(artifact)) {
         menuStream = menuStream.map((menuCommand) -> menuCommand.newInstance(imageDescriptor));
      }
      menuStream.forEach(commands::add);
   }

   /**
    * Renders the provided artifacts on the client by concatenating their mark down content. This render is not
    * recursive.
    *
    * @param artifacts the artifacts to be rendered.
    * @param writer the artifact content is appended to this {@link Writer}.
    */

   void clientSideRenderer(List<Artifact> artifacts, Writer writer) {

      artifacts.stream().forEach((artifact) -> this.renderArtifact(artifact, writer));
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (artifact.isOfType(CoreArtifactTypes.Markdown) || artifact.isAttributeTypeValid(
         CoreAttributeTypes.PrimaryAttribute)) {
         if (presentationType.matches(SPECIALIZED_EDIT, PREVIEW, DEFAULT_OPEN)) {
            return PRESENTATION_SUBTYPE_MATCH;
         } else if (presentationType.matches(DIFF)) {
            String extension = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.Extension,
               MarkdownRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION);
            if (extension.contains(MarkdownRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION)) {
               return PRESENTATION_SUBTYPE_MATCH;
            }
         }
      }

      return NO_MATCH;
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
   public IComparator getComparator() {
      return comparator;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return MarkdownRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return MarkdownRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return MarkdownRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return MarkdownRenderer.RENDERER_NAME;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {

      if (MarkdownRenderer.LOCAL) {

         /*
          * Client Side Render
          */

         //@formatter:off
         if( (Boolean) this.getRendererOptionValue(RendererOption.CLIENT_RENDERER_CAN_STREAM) ) {

            /*
             * Write to the provided PipedOutputStream
             */

            var outputStream       = (OutputStream) this.getRendererOptionValue(RendererOption.OUTPUT_STREAM);
            var outputStreamWriter = new OutputStreamWriter(outputStream);
            var bufferedWriter     = new BufferedWriter(outputStreamWriter);

            this.clientSideRenderer(artifacts, bufferedWriter);

            try {
               bufferedWriter.close();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }

            return null;

         } else {

            /*
             * Write to a buffer and return a stream that reads the buffer.
             */

            var outputStream       = new ByteArrayOutputStream() {
                                            public byte[] getBuff() {
                                               return this.buf;
                                            }
                                         };
            var outputStreamWriter = new OutputStreamWriter(outputStream);
            var bufferedWriter     = new BufferedWriter(outputStreamWriter);

            this.clientSideRenderer(artifacts, bufferedWriter);

            try {
               bufferedWriter.close();
            } catch (Exception e) {
               throw new RuntimeException(e);
            }

            return new ByteArrayInputStream(outputStream.getBuff());
         }
         //@formatter:on

      } else {

         /*
          * Server Side Render
          */

         return this.serverSideRenderer(artifacts);
      }
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.MarkdownContent);
   }

   @Override
   public MarkdownRenderer newInstance() {
      return new MarkdownRenderer();
   }

   @Override
   public MarkdownRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new MarkdownRenderer(rendererOptions);
   }

   /**
    * Client side render of an <code>artifact</code>. The content of the artifact's mark down attribute is read as a
    * string and concatenated to the <code>writer</code>.
    *
    * @param artifact the artifact to be rendered.
    * @param writer the render content is appended to the {@link Writer}.
    */

   void renderArtifact(Artifact artifact, Writer writer) {
      String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.MarkdownContent);
      try {
         writer.append(content);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Calls the Synchronization Artifact API to render the <code>artifacts</code> to mark down on the server. This
    * render is recursive, all hierarchical children under the provide artifacts will be rendered.
    *
    * @param artifacts the artifacts to be rendered.
    * @return an {@link InputStream} the server data can be read from.
    */

   InputStream serverSideRenderer(List<Artifact> artifacts) {

      var branchToken = artifacts.get(0).getBranchToken();

      var rootArray =
         artifacts.stream().filter(Objects::nonNull).map((artifactId) -> new Root(branchToken, artifactId)).toArray(
            (size) -> new Root[size]);

      var exportRequest = new ExportRequest(MarkdownRenderer.SYNCHRONIATION_ARTIFACT_TYPE, rootArray);

      var inputStream = this.synchronizationEndpoint.exporter(exportRequest);

      return inputStream;
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }
}