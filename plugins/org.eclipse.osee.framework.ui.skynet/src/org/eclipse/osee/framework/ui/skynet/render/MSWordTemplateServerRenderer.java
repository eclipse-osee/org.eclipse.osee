/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.rest.api.publisher.publishing.MsWordPreviewRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * This Word Renderer is used for the purpose of making a REST Call to the PublishingEndpoint for previewing artifacts.
 *
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

public class MSWordTemplateServerRenderer extends FileSystemRenderer {

   /**
    * The context menu command title for the Preview command.
    */

   private static final String COMMAND_TITLE_PREVIEW = "MS Word Preview (Server)";

   /**
    * The context menu command title for the Preview No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES = "MS Word Preview No Attributes (Server)";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN = "MS Word Preview With Children (Server)";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES =
      "MS Word Preview With Children No Attributes (Server)";

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "xml";

   /**
    * The {@link ImageDescriptor} used to draw the icon for this renderer's command icon.
    */

   private static ImageDescriptor imageDescriptor;

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   /**
    * The program extension for MS Word documents.
    */

   private static final String PROGRAM_EXTENSION_WORD = "doc";

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "MS Word";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = "org.eclipse.osee.framework.ui.skynet.render.MSWordRestRenderer";

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Server Side MS Word Edit";

   /**
    * The {@link Program} used to invoke MS Word.
    */

   private static Program wordApplication;

   /*
    * Build menu commands
    */

   static {

      MSWordTemplateServerRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(MSWordTemplateServerRenderer.PROGRAM_EXTENSION_WORD);

      MSWordTemplateServerRenderer.wordApplication =
         Program.findProgram(MSWordTemplateServerRenderer.PROGRAM_EXTENSION_WORD);

      //@formatter:off
      MSWordTemplateServerRenderer.menuCommandDefinitions =
      List.of
         (
            new MenuCmdDef
                   (
                     CommandGroup.PREVIEW_SERVER,
                     MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW,
                     MSWordTemplateServerRenderer.imageDescriptor,
                     Map.of
                        (
                           RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                           RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_VALUE.getKey()
                        )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW_SERVER,
                      MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES,
                      MSWordTemplateServerRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW_SERVER,
                      MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN,
                      MSWordTemplateServerRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW_SERVER,
                      MSWordTemplateServerRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES,
                      MSWordTemplateServerRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()
                         )
                   )
         );
      //@formatter:on
   }

   private final IComparator comparator;

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} without any options. This constructor is used by the
    * right-click context menu to create the renderer.
    */

   public MSWordTemplateServerRenderer() {
      this(null);
   }

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} with the provided options.
    *
    * @param options map of {@link RendererOption}s.
    */

   public MSWordTemplateServerRenderer(RendererMap options) {
      super(options);
      this.comparator = new WordTemplateCompare(this);
   }

   /**
    * Adds the context menu command entries for this renderer to the specified list of {@link MenuCmdDef} objects for
    * the specified artifact.
    *
    * @param commands the {@link List} of {@link MenuCmdDef} objects to be appended to. This parameter maybe an empty
    * list but should not be <code>null</code>.
    * @param the {@link Artifact} context menu commands are to be offered for. This parameter is not used.
    * @throws NullPointerException when the parameter <code>commands</code> is <code>null</code>.
    */

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {

      Objects.requireNonNull(commands,
         "MSWordTemplateServerRenderer::addMenuCommandDefinitions, the parameter \"commands\" is null.");

      MSWordTemplateServerRenderer.menuCommandDefinitions.forEach(commands::add);
   }

   /**
    * Determines the applicability rating for the {@link IRenderer} implementation.
    * <p>
    * During menu building this renderer must declare itself as applicable or else the method
    * {@link #addMenuCommandDefinitions} will not get invoked.
    * <p>
    * During command execution this renderer must return the highest applicability rating to be selected and used to
    * process the command. This renderer determines it's applicability in the same manner as the client side renderer.
    * However, when the {@link PresentationType} is {@link PresentationType#PREVIEW_SERVER} the applicability rating is
    * incremented so that this (server side) render will win out over the client side renderer.
    * <p>
    *
    * @param presentationType the publishing request {@link PresentationType}.
    * @param artifact this parameter is not used and may be <code>null</code>.
    * @param options this parameter is not used and may be <code>null</code>.
    * @return the applicability rating
    */

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, RendererMap rendererOptions) {
      var rating = MSWordTemplateRendererUtils.getApplicabilityRating(presentationType, artifact, rendererOptions);
      if (!PresentationType.PREVIEW_SERVER.equals(presentationType)) {
         rating--;
      }
      return rating;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return MSWordTemplateServerRenderer.wordApplication;
   }

   /**
    * Gets the {@link IComparator} implementation used to compare {@link Artifact}s.
    *
    * @return {@link IComparator} implementation for {@link Artifact}s.
    */

   @Override
   public IComparator getComparator() {
      return this.comparator;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return MSWordTemplateServerRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return MSWordTemplateServerRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return MSWordTemplateServerRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return MSWordTemplateServerRenderer.RENDERER_NAME;
   }

   /**
    * Unsupported operation.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public List<AttributeTypeToken> getOrderedAttributeTypes(Artifact artifact, Collection<? extends AttributeTypeToken> attributeTypes) {
      throw new UnsupportedOperationException();
   }

   /**
    * Sends a MsWord document publishing request to the OSEE server and returns the response content as an
    * {@link Attachment}.
    *
    * @param presentationType enumeration describing how the results will be presented to the user and used for the
    * publishing template selection.
    * @param publishArtifacts a list of the {@link Artifact} objects to be published.
    * @return an {@link Attachment} containing the MsWord content of the published artifacts.
    * @throws OseeCoreException when:
    * <ul>
    * <li>inputs are invalid,</li>
    * <li>failed OSEE server response,</li>
    * <li>no Word content is returned from the server,</li>
    * <li>the Word content is not properly terminated, or</li>
    * <li>the Word content was not properly encoded for the {@link InputStream}.</li>
    * </ul>
    */

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> publishArtifacts) {

      /*
       * Validate Inputs
       */

      //@formatter:off
      if(    Objects.isNull(presentationType)
          || Objects.isNull(publishArtifacts)
          || (publishArtifacts.size() == 0) ) {

         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "MsWord Renderer invalid inputs." )
                             .indentInc()
                             .segment( "PresentationType", Objects.nonNull( presentationType )
                                                              ? presentationType
                                                              : "(no presentation type specified)"  )
                             .segment( "Publish Artifacts ", publishArtifacts, Artifact::getIdString )
                             .toString()
                   );
      }

      /*
       * Create the server request data
       */

      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   this.getIdentifier(),                                                 /* Renderer Id                */
                   publishArtifacts.get(0).getArtifactTypeName(),                        /* Publish Artifact Type Name */
                   presentationType.name(),                                              /* Presentation Type          */
                   (String) this.getRendererOptionValue(RendererOption.TEMPLATE_OPTION)  /* Option                     */
                );

      var branchId =
         BranchId.create
            (
               publishArtifacts.get(0).getBranch().getId(),                 /* Branch Id */
               ((ArtifactId) getRendererOptionValue(RendererOption.VIEW))   /* View Id   */
            );
      //@formatter:on

      /*
       * Make list of Artifact Identifiers from the list of Artifacts to be published
       */

      var publishArtifactIds =
         publishArtifacts.stream().map(Artifact::getId).map(ArtifactId::valueOf).collect(Collectors.toList());

      var msWordPreviewRequestData =
         new MsWordPreviewRequestData(publishingTemplateRequest, branchId, publishArtifactIds);

      /*
       * Make the server call for the publish
       */

      var attachment = PublishingRequestHandler.msWordPreview(msWordPreviewRequestData);

      try {
         return attachment.getDataHandler().getInputStream();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new UpdateArtifactOperation(file, artifacts, branch, false);
   }

   /**
    * Gets the minimum applicability ranking for this {@link IRenderer} implementation.
    *
    * @return {@link IRenderer#NO_MATCH}.
    */

   @Override
   public int minimumRanking() {
      return IRenderer.NO_MATCH;
   }

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} without any options.
    *
    * @return an {@link MSWordTemplateServerRenderer} object.
    */

   @Override
   public MSWordTemplateServerRenderer newInstance() {
      return new MSWordTemplateServerRenderer();
   }

   /**
    * Creates a new {@link MSWordTemplateServerRenderer} with the map of provided {@link RendererOption}s.
    *
    * @return an {@link MSWordTemplateServerRenderer} object.
    */

   @Override
   public MSWordTemplateServerRenderer newInstance(RendererMap rendererOptions) {
      return new MSWordTemplateServerRenderer(rendererOptions);
   }

   /**
    * Rendering the attributes of an artifact are not supported by this renderer.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer) {
      throw new UnsupportedOperationException();
   }

   /**
    * Rendering the attributes of an artifact are not supported by this renderer.
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public String renderAttributeAsString(AttributeTypeId attributeType, Artifact artifact, PresentationType presentationType, String defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * This {@link IRenderer} implementation uses the default {@link IComparator} implementation for {@link Artifacts}.
    *
    * @return <code>true</code>.
    */

   @Override
   public boolean supportsCompare() {
      return true;
   }

}

/* EOF */
