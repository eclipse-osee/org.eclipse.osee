/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import static org.eclipse.osee.framework.core.enums.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
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
 * @author Loren K. Ashley
 */

public class PlainTextRenderer extends FileSystemRenderer {

   /**
    * This is only relevant when {#link {@link #LOCAL} is <code>true</code>. When set to <code>true</code> the renderer
    * will write to the output stream provided to it via the renderer options from the {@link FileSystemRender} base
    * class. When set to <code>false</code> the renderer will render to a local buffer, when complete create an input
    * stream that reads from that buffer, and provide that input stream back to the {@link FileSystemRender} base class.
    */

   private static final boolean CAN_STREAM = false;

   /**
    * The context menu command title for the Edit command.
    */

   private static final String COMMAND_TITLE_EDIT = "Plain Text Editor";

   /**
    * The context menu command title for the Preview command.
    */

   private static final String COMMAND_TITLE_PREVIEW = "Preview Plain Text Editor";

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "txt";

   /**
    * The {@link ImageDescriptor} used to draw the icon for this renderer's command icon.
    */

   private static ImageDescriptor imageDescriptor;

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "Text";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = PlainTextRenderer.class.getCanonicalName();
   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Plain Text Edit";

   static {

      PlainTextRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(PlainTextRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION);

      //@formatter:off
      PlainTextRenderer.menuCommandDefinitions =
         List.of
            (
               new MenuCmdDef
                      (
                         CommandGroup.EDIT,
                         PresentationType.SPECIALIZED_EDIT,
                         PlainTextRenderer.COMMAND_TITLE_EDIT,
                         imageDescriptor
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.PREVIEW,
                         PresentationType.PREVIEW,
                         PlainTextRenderer.COMMAND_TITLE_PREVIEW,
                         imageDescriptor
                      )
            );
      //@formatter:on
   }

   private final IComparator comparator;

   public PlainTextRenderer() {
      super();
      this.comparator = new PlainTextDiffRenderer();
      this.menuCommands = PlainTextRenderer.menuCommandDefinitions;
      this.setRendererOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, PlainTextRenderer.CAN_STREAM);
   }

   public PlainTextRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
      this.comparator = new PlainTextDiffRenderer();
      this.menuCommands = PlainTextRenderer.menuCommandDefinitions;
      this.setRendererOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, PlainTextRenderer.CAN_STREAM);
   }

   /**
    * Renders the provided artifacts on the client by concatenating their plain text content. This render is not
    * recursive.
    *
    * @param artifacts the artifacts to be rendered.
    * @param writer the artifact content is appended to this {@link Writer}.
    */

   private void clientSideRenderer(List<Artifact> artifacts, Writer writer) {
      artifacts.stream().filter(Objects::nonNull).forEach((artifact) -> this.renderArtifact(artifact, writer));
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, RendererMap rendererOptions) {
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PlainTextContent)) {
         if (presentationType.matches(SPECIALIZED_EDIT, PREVIEW, DEFAULT_OPEN, PRODUCE_ATTRIBUTE, DIFF)) {
            return PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      Program program = Program.findProgram(PlainTextRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION);
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension *.txt found on your local machine.");
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
      return PlainTextRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return PlainTextRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return PlainTextRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return PlainTextRenderer.RENDERER_NAME;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {

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
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.PlainTextContent);
   }

   @Override
   public PlainTextRenderer newInstance() {
      return new PlainTextRenderer();
   }

   @Override
   public PlainTextRenderer newInstance(RendererMap rendererOptions) {
      return new PlainTextRenderer(rendererOptions);
   }

   /**
    * {@inheritDoc}
    * <p>
    * If more than one {@link Artifact} is provided, use the super class to open them one at a time.
    */

   @Override
   public void open(List<Artifact> artifacts, PresentationType presentationType) {

      Objects.requireNonNull(presentationType,
         "PlainTextRenderer::open, the parameter \"presentationType\" cannot be null.");

      if (Objects.isNull(artifacts) || artifacts.isEmpty()) {
         return;
      }

      //@formatter:off
      artifacts.stream()
         .filter(Objects::nonNull)
         .map(Collections::singletonList)
         .forEach( ( artifactList ) -> super.open( artifactList, presentationType ) );
      //@formatter:on
   }

   /**
    * Client side render of an <code>artifact</code>. The content of the artifact's plain text attribute is read as a
    * string and concatenated to the <code>writer</code>.
    *
    * @param artifact the artifact to be rendered.
    * @param writer the render content is appended to the {@link Writer}.
    */

   private void renderArtifact(Artifact artifact, Writer writer) {
      String content = artifact.getOrInitializeSoleAttributeValue(CoreAttributeTypes.PlainTextContent);
      try {
         writer.append(content);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }

   @Override
   public boolean supportsCompare() {
      return true;
   }

}
