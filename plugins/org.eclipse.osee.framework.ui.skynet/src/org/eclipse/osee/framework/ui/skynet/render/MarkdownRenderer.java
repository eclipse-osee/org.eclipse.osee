/**********************************************************************
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

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.MarkdownCompare;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.ProgramFinder;
import org.eclipse.swt.program.Program;

/**
 * Renders a hierarchical tree of markdown artifacts.
 *
 * @author Murshed Alam
 * @author Loren K. Ashley
 */

public class MarkdownRenderer extends FileSystemRenderer {

   /**
    * The context menu command identifier for the Other Editor.
    */

   private static final String COMMAND_ID_OTHER_EDITOR = "org.eclipse.osee.framework.ui.skynet.othereditor.command";

   /**
    * The context menu command title for the Edit command.
    */

   private static final String COMMAND_TITLE_EDIT = "Markdown Editor";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN = "Markdown Preview With Children";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES =
      "Markdown Preview With Children No Attributes";

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

   /*
    * Build menu commands
    */

   static {

      MarkdownRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(MarkdownRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION);

      //@formatter:off
      MarkdownRenderer.menuCommandDefinitions =
         List.of
            (
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
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.PREVIEW,
                         MarkdownRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN,
                         MarkdownRenderer.imageDescriptor,
                         Map.of
                            (
                               RendererOption.OPEN_OPTION.getKey(),       RendererOption.OPEN_IN_MARKDOWN_EDITOR_VALUE.getKey(),
                               RendererOption.PUBLISHING_FORMAT.getKey(), FormatIndicator.MARKDOWN.name(),
                               RendererOption.TEMPLATE_OPTION.getKey(),   RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()
                            )
                      ),

               new MenuCmdDef
                      (
                         CommandGroup.PREVIEW,
                         MarkdownRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES,
                         MarkdownRenderer.imageDescriptor,
                         Map.of
                            (
                               RendererOption.OPEN_OPTION.getKey(),       RendererOption.OPEN_IN_MARKDOWN_EDITOR_VALUE.getKey(),
                               RendererOption.PUBLISHING_FORMAT.getKey(), FormatIndicator.MARKDOWN.name(),
                               RendererOption.TEMPLATE_OPTION.getKey(),   RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()
                            )
                      )

            );
      //@formatter:on
   }

   private final IComparator comparator;

   public MarkdownRenderer() {
      this(null);
   }

   public MarkdownRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
      this.comparator = new MarkdownCompare();
      this.menuCommands = MarkdownRenderer.menuCommandDefinitions;
      //@formatter:off
      super.presentationTypeKnockOuts      = MarkdownRenderer.PRESENTATION_TYPE_KNOCK_OUTS;
      super.previewAttributeTypeKnockOuts  = MarkdownRenderer.PREVIEW_ATTRIBUTE_TYPE_KNOCK_OUTS;
      super.publishingFormat               = FormatIndicator.MARKDOWN;
      super.artifactTypesAreRequired       = MarkdownRenderer.ARTIFACT_TYPES_ARE_REQUIRED;
      super.applicabilityTestArtifactTypes = MarkdownRenderer.APPLICABILITY_TEST_ARTIFACT_TYPES;
      super.defaultFileExtension           = MarkdownRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
      super.openInRendererOptionValue      = MarkdownRenderer.RENDERER_OPTION_OPEN_IN_VALUE;
      super.rendererArtifactTypeToken      = MarkdownRenderer.RENDERER_ARTIFACT_TYPE_TOKEN;
      super.notRendererArtifactTypeDelta   = MarkdownRenderer.NOT_RENDERER_ARTIFACT_TYPE_DELTA;
      super.rendererContentAttributeType   = MarkdownRenderer.RENDERER_CONTENT_ATTRIBUTE_TYPE;
      //@formatter:on
   }

   /**
    * The {@link PresentationType}s the renderer is not applicable for.
    */

   //@formatter:off
   private static final PresentationType[]   PRESENTATION_TYPE_KNOCK_OUTS =
      new PresentationType[]
      {
         PresentationType.GENERALIZED_EDIT,
         PresentationType.GENERAL_REQUESTED
      };
   //@formatter:on

   /**
    * No attribute types are invalid when the presentation type is {@link PresentationType#PREVIEW}.
    */

   //@formatter:off
   private static final AttributeTypeToken[] PREVIEW_ATTRIBUTE_TYPE_KNOCK_OUTS =
      new AttributeTypeToken[] {};
   //@formatter:on

   /**
    * The type of the artifact being rendered must be in the {@link APPLICABILITY_TEST_ARTIFACT_TYPES} array.
    */

   private static final boolean ARTIFACT_TYPES_ARE_REQUIRED = true;

   /**
    * The renderer will only be applicable for artifacts of the artifact types in this array.
    */

   //@formatter:off
   private static final ArtifactTypeToken[]  APPLICABILITY_TEST_ARTIFACT_TYPES =
      new ArtifactTypeToken[]
      {
         CoreArtifactTypes.Folder,
         CoreArtifactTypes.Markdown
      };
   //@formatter:on

   /**
    * The value of the {@link RendererOption#OPEN_OPTION} that will boost the renderer's applicability rating when the
    * presentation type is {@link PresentationType#DIFF}.
    */

   private static final String RENDERER_OPTION_OPEN_IN_VALUE = RendererOption.OPEN_IN_MARKDOWN_EDITOR_VALUE.getKey();

   /**
    * The preferred/expected type of artifact the renderer is for.
    */

   private static final ArtifactTypeToken RENDERER_ARTIFACT_TYPE_TOKEN = CoreArtifactTypes.Markdown;

   /**
    * A delta applied to the renderer applicability rating when the artifact the rating was generated for is not of the
    * {@link #RENDERER_ARTIFACT_TYPE_TOKEN} type.
    */

   private static final int NOT_RENDERER_ARTIFACT_TYPE_DELTA = -2;

   /**
    * The expected main content attribute for the artifact being rendered.
    */

   private static final AttributeTypeToken RENDERER_CONTENT_ATTRIBUTE_TYPE = CoreAttributeTypes.MarkdownContent;

   /**
    * {@inheritDoc}
    */

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {

      var rating = this.getBaseApplicabilityRating(presentationType, artifact, rendererOptions);

      return rating;
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
    * Calls the Synchronization Artifact API to render the <code>artifacts</code> to mark down on the server. This
    * render is recursive, all hierarchical children under the provide artifacts will be rendered.
    *
    * @param artifacts the artifacts to be rendered.
    * @return an {@link InputStream} the server data can be read from.
    * @throws NullPointerException when <code>presentationType</code> is <code>null</code>, <code>artifacts</code> is
    * <code>null</code>, or <code>artifacts</code> contains a <code>null</code> element.
    */

   @Override
   public InputStream getRenderInputStream(@NonNull PresentationType presentationType,
      @NonNull List<@NonNull Artifact> artifacts) {

      /*
       * Validate Inputs
       */

      //@formatter:off
      Conditions.require
         (
            presentationType,
            ValueType.PARAMETER,
            MarkdownRenderer.class.getName(),
            "getRenderInputStream",
            "presentationType",
            "must be non-null",
            Objects::isNull,
            NullPointerException::new
         );

      Conditions.require
         (
            artifacts,
            ValueType.PARAMETER,
            MarkdownRenderer.class.getName(),
            "getRenderInputStream",
            "artifacts",
            "must be non-null",
            Objects::isNull,
            "must not contain null elements",
            Conditions::collectionContainsNull,
            NullPointerException::new
         );
      //@formatter:on

      /*
       * Set Publishing Format
       */

      var formatIndicator = FormatIndicator.MARKDOWN;
      this.setRendererOption(RendererOption.PUBLISHING_FORMAT, formatIndicator);

      /*
       * Temporary check to prevent editing of multiple artifacts
       */

      if (artifacts.size() > 1) {
         //@formatter:off
         var message =
            RenderingUtil.displayErrorDocument
               (
                  this,
                  presentationType,
                  null, /* no button actions */
                  artifacts.get( 0 ).getBranch(),
                  artifacts,
                  "Editing of multiple Markdown artifacts is not implemented."
               );
         //@formatter:on
         throw new OseeCoreException(message);
      }

      /*
       * Create the server request data
       */

      //@formatter:off
      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   this.getIdentifier(),                                        /* Match Criteria: Renderer Id                */
                   artifacts.get(0).getArtifactTypeName(),                      /* Match Criteria: Publish Artifact Type Name */
                   presentationType.name(),                                     /* Match Criteria: Presentation Type          */
                   this.getRendererOptionValue(RendererOption.TEMPLATE_OPTION), /* Match Criteria: Option                     */
                   formatIndicator                                              /* Publish Format Indicator                   */
                );

      var artifactIdentifiers =
         artifacts
            .stream()
            .map( Artifact::getId )
            .map( ArtifactId::valueOf )
            .collect( Collectors.toList() );

      var publishingRequestData =
         new PublishingRequestData
                (
                   publishingTemplateRequest, /* Publishing Template Request */
                   this,                      /* Renderer Options            */
                   artifactIdentifiers        /* Artifacts To Publish        */
                );
      //@formatter:on

      /*
       * Make the server call for the publish
       */

      var attachment = PublishingRequestHandler.msWordPreview(publishingRequestData);

      try {
         return attachment.getDataHandler().getInputStream();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch,
      PresentationType presentationType) {
      return new FileToAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.MarkdownContent);
   }

   @Override
   public MarkdownRenderer newInstance() {
      return new MarkdownRenderer();
   }

   @Override
   public MarkdownRenderer newInstance(RendererMap rendererOptions) {
      return new MarkdownRenderer(rendererOptions);
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }
}