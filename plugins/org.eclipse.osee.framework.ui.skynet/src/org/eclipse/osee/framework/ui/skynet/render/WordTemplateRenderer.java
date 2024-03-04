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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.PublishingAppender;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.renderer.RenderLocation;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.word.WordCoreUtilClient;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessorClient;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;
import org.w3c.dom.Element;

/**
 * Renders WordML content.
 *
 * @author Jeff C. Phillips
 * @author Loren K. Ashley
 */

public class WordTemplateRenderer extends FileSystemRenderer {

   /**
    * To be applicable the artifact being tested must not be of one of these types.
    */

   //@formatter:off
   private static ArtifactTypeToken[] APPLICABILITY_TEST_ARTIFACT_TYPES =
      new ArtifactTypeToken[]
      {
         CoreArtifactTypes.HtmlArtifact,
         CoreArtifactTypes.Markdown,
         CoreArtifactTypes.PlainText,
         CoreArtifactTypes.UserGroup,
         AtsArtifactTypes.AtsArtifact,
         AtsArtifactTypes.AtsConfigArtifact
      };
   //@formatter:on

   /**
    * To be applicable the artifact being tested must not be of one of the types in
    * {@link #APPLICABILITY_TEST_ARTIFACT_TYPES}.
    */

   private static boolean ARTIFACT_TYPES_ARE_REQUIRED = false;

   /**
    * When set to <code>true</code> the renderer will write to the output stream provided to it via the renderer options
    * from the {@link FileSystemRender} base class. When set to <code>false</code> the renderer will render to a local
    * buffer, when complete create an input stream that reads from that buffer, and provide that input stream back to
    * the {@link FileSystemRender} base class.
    */

   private static final boolean CAN_STREAM = false;

   /**
    * The context menu command title for the Edit command.
    */

   private static final String COMMAND_TITLE_EDIT = "MS Word Edit";

   /**
    * The context menu command title for the Preview command.
    */

   private static final String COMMAND_TITLE_PREVIEW = "MS Word Preview";

   /**
    * The context menu command title for the Preview No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES = "MS Word Preview No Attributes";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN = "MS Word Preview With Children";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES =
      "MS Word Preview With Children No Attributes";

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
    * A delta value added to the renderer rating when the artifact being rendered is not of the type specified by
    * {@link #RENDERER_ARTIFACT_TYPE_TOKEN}.
    */

   private static final int NOT_RENDERER_ARTIFACT_TYPE_DELTA = 0;

   /**
    * The {@link PresentationType}s that this renderer is not applicable for.
    */

   //@formatter:off
   private static PresentationType[] PRESENTATION_TYPE_KNOCK_OUTS =
      new PresentationType[]
      {
         PresentationType.GENERALIZED_EDIT,
         PresentationType.GENERAL_REQUESTED
      };
   //@formatter:on

   /**
    * The attribute types that the renderer is not applicable for when the presentation type is
    * {@link PresentationType#PREVIEW}.
    */

   //@formatter:off
   private static AttributeTypeToken[] PREVIEW_ATTRIBUTE_TYPE_KNOCK_OUTS =
      new AttributeTypeToken[]
         {
            CoreAttributeTypes.WholeWordContent,
            CoreAttributeTypes.NativeContent
         };
   //@formatter:on

   /**
    * The program extension for MS Word documents.
    */

   private static final String PROGRAM_EXTENSION_WORD = "doc";

   /**
    * The preferred/expected type of the artifact being rendered.
    */

   private static final ArtifactTypeToken RENDERER_ARTIFACT_TYPE_TOKEN = CoreArtifactTypes.MsWord;

   /**
    * The main content attribute for the preferred/expected type of artifact being rendered.
    */

   private static final AttributeTypeToken RENDERER_CONTENT_ATTRIBUTE_TYPE = CoreAttributeTypes.WordTemplateContent;
   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "MS Word";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer";

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "MS Word";

   /**
    * The preferred/expected {@link RendererOption#OPEN_OPTION} for the renderer.
    */

   private static final String RENDERER_OPTION_OPEN_IN_VALUE = RendererOption.OPEN_IN_MS_WORD_VALUE.getKey();

   /*
    * Build menu commands
    */

   /**
    * The {@link Program} used to invoke MS Word.
    */

   private static Program wordApplication;

   static {

      WordTemplateRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(WordTemplateRenderer.PROGRAM_EXTENSION_WORD);

      WordTemplateRenderer.wordApplication = Program.findProgram(WordTemplateRenderer.PROGRAM_EXTENSION_WORD);

      //@formatter:off
      WordTemplateRenderer.menuCommandDefinitions =
      List.of
         (
            new MenuCmdDef
                   (
                     CommandGroup.EDIT,
                     WordTemplateRenderer.COMMAND_TITLE_EDIT,
                     WordTemplateRenderer.imageDescriptor,
                     Map.of
                        (
                          RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                          RendererOption.RENDER_LOCATION.getKey(), RenderLocation.CLIENT.name()
                        )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW, RenderLocation.CLIENT ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.CLIENT.name()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES, RenderLocation.CLIENT ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.CLIENT.name()
                         )
                   ),


            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN, RenderLocation.CLIENT ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.CLIENT.name()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES, RenderLocation.CLIENT ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.CLIENT.name()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW, RenderLocation.SERVER ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.SERVER.name()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES, RenderLocation.SERVER ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.SERVER.name()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN, RenderLocation.SERVER ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.SERVER.name()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      WordTemplateRenderer.commandTitle( WordTemplateRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES, RenderLocation.SERVER ),
                      WordTemplateRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey(),
                            RendererOption.RENDER_LOCATION.getKey(), RenderLocation.SERVER.name()
                         )
                   )

         );
   //@formatter:on
   }

   /**
    * Adds an annotation to the menu command for where the render is to be performed.
    *
    * @param commandTitle the title of the menu command.
    * @param renderLocation the location where the render will be performed.
    * @return an annotated command title.
    */

   private static String commandTitle(String commandTitle, RenderLocation renderLocation) {
      return commandTitle + renderLocation.getMenuCommandAnnotation();
   }

   public static byte[] getFormattedContent(Element formattedItemElement) throws XMLStreamException {
      ByteArrayOutputStream data = new ByteArrayOutputStream((int) Math.pow(2, 10));
      XMLStreamWriter xmlWriter = null;
      try {
         xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(data, "UTF-8");
         for (Element e : Jaxp.getChildDirects(formattedItemElement)) {
            Jaxp.writeNode(xmlWriter, e, false);
         }
      } finally {
         if (xmlWriter != null) {
            xmlWriter.flush();
            xmlWriter.close();
         }
      }
      return data.toByteArray();
   }

   private final IComparator comparator;

   private final WordTemplateProcessorClient templateProcessor;

   public WordTemplateRenderer() {
      this(null);
   }

   public WordTemplateRenderer(RendererMap options) {
      super(options);
      this.comparator = new WordTemplateCompare(this);
      this.templateProcessor = new WordTemplateProcessorClient();
      this.menuCommands = WordTemplateRenderer.menuCommandDefinitions;
      this.setRendererOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, WordTemplateRenderer.CAN_STREAM);

      super.presentationTypeKnockOuts = WordTemplateRenderer.PRESENTATION_TYPE_KNOCK_OUTS;
      super.previewAttributeTypeKnockOuts = WordTemplateRenderer.PREVIEW_ATTRIBUTE_TYPE_KNOCK_OUTS;
      super.artifactTypesAreRequired = WordTemplateRenderer.ARTIFACT_TYPES_ARE_REQUIRED;
      super.applicabilityTestArtifactTypes = WordTemplateRenderer.APPLICABILITY_TEST_ARTIFACT_TYPES;
      super.defaultFileExtension = WordTemplateRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
      super.openInRendererOptionValue = WordTemplateRenderer.RENDERER_OPTION_OPEN_IN_VALUE;
      super.rendererArtifactTypeToken = WordTemplateRenderer.RENDERER_ARTIFACT_TYPE_TOKEN;
      super.notRendererArtifactTypeDelta = WordTemplateRenderer.NOT_RENDERER_ARTIFACT_TYPE_DELTA;
      super.rendererContentAttributeType = WordTemplateRenderer.RENDERER_CONTENT_ATTRIBUTE_TYPE;
   }

   /**
    * Displays a list of artifacts in the Artifact Explorer that could not be multi edited because they contained
    * artifacts that had an OLEData attribute.
    */

   private void displayNotMultiEditArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {

      var rating = this.getBaseApplicabilityRating(presentationType, artifact, rendererOptions);

      return rating;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   protected @NonNull Optional<ImageDescriptor> getArtifactBasedImageDescriptor(@Nullable Artifact artifact) {
      return Optional.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return WordTemplateRenderer.wordApplication;
   }

   @Override
   public IComparator getComparator() {
      return this.comparator;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return WordTemplateRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return WordTemplateRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return WordTemplateRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return WordTemplateRenderer.RENDERER_NAME;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {

      if (this.isRendererOptionSet(RendererOption.RENDER_LOCATION)) {

         RenderLocation renderLocation = this.getRendererOptionValue(RendererOption.RENDER_LOCATION);

         switch (renderLocation) {
            default:
            case CLIENT:
               return this.getRenderInputStreamClient(presentationType, artifacts);
            case SERVER:
               return this.getRenderInputStreamServer(presentationType, artifacts);
         }

      }

      return this.getRenderInputStreamClient(presentationType, artifacts);
   }

   public InputStream getRenderInputStreamClient(PresentationType presentationType, List<Artifact> artifacts) {

      /*
       * Validate Inputs
       */

      //@formatter:off
      Conditions.require
         (
            presentationType,
            ValueType.PARAMETER,
            "presentationType",
            "must be non-null",
            Objects::isNull,
            NullPointerException::new
         );

      Conditions.require
         (
            artifacts,
            ValueType.PARAMETER,
            "artifacts",
            "must be non-null and and not contain null elements",
            Conditions.or( Objects::isNull, Conditions::collectionContainsNull ),
            IllegalArgumentException::new
         );
      //@formatter:on

      /*
       * Set Publishing Format
       */

      this.setRendererOption(RendererOption.PUBLISHING_FORMAT, FormatIndicator.WORD_ML);

      //@formatter:off
      var firstArtifactOrNull =
         ( Objects.nonNull(artifacts) && !artifacts.isEmpty() )
            ? artifacts.get(0)
            : null;
      //@formatter:on

      var publishingTemplate = this.getTemplate(firstArtifactOrNull, presentationType);

      if (Objects.nonNull(firstArtifactOrNull)) {

         if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() > 1) {

            // currently we can't support the editing of multiple artifacts with OLE data

            //@formatter:off
            var notMultiEditableArtifacts =
               artifacts
                  .stream()
                  .filter( ( artifact ) -> !artifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "").equals("") )
                  .collect( Collectors.toList() );

            this.displayNotMultiEditArtifacts
               (
                  notMultiEditableArtifacts,
                  "Do not support editing of multiple artifacts with OLE data"
               );
            //@formatter:on

            artifacts.removeAll(notMultiEditableArtifacts);

         } else { // support OLE data when appropriate

            if (!firstArtifactOrNull.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "").equals("")) {

               var oleData = firstArtifactOrNull.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "");

               publishingTemplate.update(WordCoreUtil::replaceEmbeddedObjectNoWithYes);

               publishingTemplate.update((tc) -> WordCoreUtil.addOleDataToEndOfStyle(tc, oleData));
            }
         }

      }

      publishingTemplate.update(WordCoreUtilClient::removeGUIDFromTemplate);

      //@formatter:off
      var outputStream = ( (Boolean) this.getRendererOptionValue(RendererOption.CLIENT_RENDERER_CAN_STREAM) )
                            ? (OutputStream) this.getRendererOptionValue(RendererOption.OUTPUT_STREAM)
                            : (OutputStream) null;

      return
         this.templateProcessor
            .configure
               (
                  this,                                                           /* Renderer                      */
                  publishingTemplate,                                             /* Primary Publishing Template   */
                  null,                                                           /* Secondary Publishing Template */
                  null,                                                           /* Folder, IContainer            */
                  null,                                                           /* Outline Number                */
                  presentationType                                                /* Presentation Type             */
               )
            .applyTemplate
               (
                  artifacts,                                                      /* Artifacts     */
                  outputStream                                                    /* Output Stream */
               );
      //@formatter:on
   }

   /**
    * Sends a MsWord document publishing request to the OSEE server and returns the response content as an
    * {@link Attachment}.
    *
    * @param presentationType enumeration describing how the results will be presented to the user and used for the
    * publishing template selection.
    * @param publishArtifacts a list of the {@link Artifact} objects to be published.
    * @return an {@link Attachment} containing the MsWord content of the published artifacts.
    * @throws NullPointerException when <code>presentationType</code> is <code>null</code>, <code>artifacts</code> is
    * <code>null</code>, or <code>artifacts</code> contains a <code>null</code> element.
    * @throws NullPointerException when the parameter <code>presentationType</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>artifacts</code> is <code>null</code>, empty, or
    * contains a <code>null</code>.
    * @throws OseeCoreException when the publish fails.
    */

   private InputStream getRenderInputStreamServer(PresentationType presentationType, List<Artifact> artifacts) {

      /*
       * Validate Inputs
       */

      //@formatter:off
      Conditions.require
         (
            presentationType,
            ValueType.PARAMETER,
            "presentationType",
            "must be non-null",
            Objects::isNull,
            NullPointerException::new
         );

      Conditions.require
         (
            artifacts,
            ValueType.PARAMETER,
            "artifacts",
            "must be non-null and not contain null elements",
            Conditions.or( Objects::isNull, Conditions::collectionContainsNull ),
            IllegalArgumentException::new
         );
      //@formatter:on

      /*
       * Set Publishing Format
       */

      var formatIndicator = FormatIndicator.WORD_ML;
      this.setRendererOption(RendererOption.PUBLISHING_FORMAT, formatIndicator);

      /*
       * Create the server request data
       */

      //@formatter:off
      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   this.getIdentifier(),                                                 /* Match Criteria: Renderer Id                */
                   artifacts.get(0).getArtifactTypeName(),                               /* Match Criteria: Publish Artifact Type Name */
                   presentationType.name(),                                              /* Match Criteria: Presentation Type          */
                   (String) this.getRendererOptionValue(RendererOption.TEMPLATE_OPTION), /* Match Criteria: Option                     */
                   formatIndicator                                                       /* Publish Format Indicator                   */
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
                   artifactIdentifiers        /* Artifact To Publish         */
                );
      //@formatter:on

      /*
       * Make the server call for the publish
       */

      var attachment = PublishingRequestHandler.msWordPreview(publishingRequestData);

      try {

         return attachment.getDataHandler().getInputStream();

      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "MSWordTemplateServerRenderer::getRenderInputStream, failed to obtain publishing stream." )
                             .reasonFollows( e )
                             .toString(),
                      e
                   );
         //@formatter:on
      }

   }

   /**
    * When the renderer option {@RendererOption#USE_TEMPLATE_ONCE} is <code>true</code> then only the first two
    * artifacts will use the whole template, since the first two are diff'd with each other.
    *
    * <pre>
    * Request Publishing Template By:
    *
    * Identifier when:
    *
    *      Template Artifact is Valid
    *  &&  Use Template Once is False
    *
    *            --OR--
    *
    *      Template Artifact is Valid
    *  &&  Use Template Once is True
    *  &&  First Time or Second Time flag is True
    *
    * Match Criteria when:
    *
    *      Template Artifact is Invalid
    *
    *            --OR--
    *
    *      Template Artifact is Valid
    *  &&  Template Use Once is True
    *  &&  On at least the Third Iteration
    * </pre>
    *
    * @param artifact the name of the artifact is used in the template match criteria. This parameter may be
    * <code>null</code>.
    * @param presentationType the type of presentation being made to the user.
    * @return The selected {@link PublishingTemplate}.
    * @throws OseeCoreException when a Publishing Template is not found.
    */

   protected PublishingTemplate getTemplate(Artifact artifact, PresentationType presentationType) {

      boolean useTemplateOnce = (boolean) this.getRendererOptionValue(RendererOption.USE_TEMPLATE_ONCE);
      boolean firstTime = (boolean) this.getRendererOptionValue(RendererOption.FIRST_TIME);
      boolean secondTime = (boolean) this.getRendererOptionValue(RendererOption.SECOND_TIME);

      var publishingTemplateIdentifier =
         (String) this.getRendererOptionValue(RendererOption.PUBLISHING_TEMPLATE_IDENTIFIER);
      var formatIndicator = (FormatIndicator) this.getRendererOptionValue(RendererOption.PUBLISHING_FORMAT);
      /*
       * Determine if selection is by identifier or match criteria
       */

      //@formatter:off
      if (

              Strings.isValidAndNonBlank( publishingTemplateIdentifier )
           && ( !useTemplateOnce || firstTime || secondTime )

         ) {
      //@formatter:on

         if (useTemplateOnce) {

            if (secondTime) {
               this.setRendererOption(RendererOption.SECOND_TIME, false);
            }

            if (firstTime) {
               this.setRendererOption(RendererOption.FIRST_TIME, false);
               this.setRendererOption(RendererOption.SECOND_TIME, true);
            }

         }

         /*
          * Request the publishing template using the saved identifier.
          */

         var publishingTemplateRequest = new PublishingTemplateRequest(publishingTemplateIdentifier, formatIndicator);

         var publishingTemplate = this.getTemplateCallServer(publishingTemplateRequest);

         return publishingTemplate;
      }

      /*
       * Remove option from the Publishing Template match criteria when it is null, blank, or useTemplateOnce is enable
       * and this is at least the third template to be fetched.
       */

      //@formatter:off
      var option =
         ( !useTemplateOnce || firstTime || secondTime )
            ? (String) getRendererOptionValue(RendererOption.TEMPLATE_OPTION)
            : null;
            //@formatter:on

      option = Strings.isValidAndNonBlank(option) ? option : null;

      /*
       * Artifact type name is part of the template match criteria strings.
       */

      var artifactTypeName = Objects.nonNull(artifact) ? artifact.getArtifactTypeName() : "";

      /*
       * Request the publishing template using match criteria.
       */

      //@formatter:off
      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   this.getIdentifier(),               /* Match Criteria: Renderer Id                */
                   artifactTypeName,                   /* Match Criteria: Publish Artifact Type Name */
                   presentationType.name(),            /* Match Criteria: Presentation Type          */
                   option,                             /* Match Criteria: Option                     */
                   formatIndicator                     /* Publishing Format Indicator                */
                );
      //@formatter:on

      var publishingTemplate = this.getTemplateCallServer(publishingTemplateRequest);

      return publishingTemplate;
   }

   /**
    * Makes the server call to obtain a publishing template. Server exceptions are handled in PublishingRequestHandler.
    * An OseeCoreException is throw here when a Publishing Template was not found.
    *
    * @param publishingTemplateRequest the request data
    * @return the publishing template.
    * @throws OseeCoreException when a Publishing Template is not found.
    */

   private PublishingTemplate getTemplateCallServer(PublishingTemplateRequest publishingTemplateRequest) {

      var publishingTemplate = PublishingRequestHandler.getPublishingTemplate(publishingTemplateRequest);

      if (publishingTemplate.isSentinel()) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "MSWordTemplateClientRenderer:getTemplateCallServer, Failed to find a Publishing Template." )
                             .indentInc()
                             .segment( "Publishing Template Request", publishingTemplateRequest )
                             .toString()
                   );
      }

      return publishingTemplate;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new UpdateArtifactOperation(file, artifacts, branch, false);
   }

   @Override
   public WordTemplateRenderer newInstance() {
      return new WordTemplateRenderer();
   }

   @Override
   public WordTemplateRenderer newInstance(RendererMap rendererOptions) {
      return new WordTemplateRenderer(rendererOptions);
   }


   public void publish( PublishingTemplate primaryPublishingTemplate, PublishingTemplate secondaryPublishingTemplate, List<Artifact> artifacts) {
      //@formatter:off
      this.templateProcessor
         .configure
            (
               this,
               primaryPublishingTemplate,
               secondaryPublishingTemplate,
               null,
               null,
               null
            )
         .publishWithNestedTemplates
            (
               artifacts
            );
   }

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, PublishingAppender wordMl, String format, String label, String footer) {


      if (attributeType.equals(CoreAttributeTypes.WordTemplateContent)) {
         String data = null;
         LinkType linkType = (LinkType) getRendererOptionValue(RendererOption.LINK_TYPE);

         if (label.length() > 0) {
            wordMl.addParagraph(label);
         }

         TransactionToken txId = null;
         if (artifact.isHistorical()) {
            txId = artifact.getTransaction();
         } else {
            txId = TransactionToken.SENTINEL;
         }

         WordTemplateContentData wtcData = new WordTemplateContentData();
         wtcData.setArtId(artifact);
         wtcData.setBranch(artifact.getBranch());
         wtcData.setFooter(presentationType != PresentationType.SPECIALIZED_EDIT ? footer : "");
         wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
         wtcData.setLinkType(linkType);
         wtcData.setTxId(txId);
         wtcData.setPresentationType(presentationType);
         ArtifactId view = (ArtifactId) getRendererOptionValue(RendererOption.VIEW);
         wtcData.setViewId(view == null ? ArtifactId.SENTINEL : view);
         wtcData.setPermanentLinkUrl(
            String.format("http://%s:%s/", ClientSessionManager.getClientName(), ClientSessionManager.getClientPort()));

         Pair<String, Set<String>> content = null;
         try {
            content = PublishingRequestHandler.renderWordTemplateContent(wtcData);
         } catch (Exception ex) {
            WordUiUtil.displayErrorMessage(artifact, ex.toString());
            wordMl.addParagraph(String.format(
               "There as a problem parsing content for this artifact - Artifact: %s - Branch: %s.  See OSEE for details.",
               artifact.toStringWithId(), artifact.getBranch().toString()));
         }

         if (content != null) {
            data = content.getFirst();
            WordUiUtil.displayUnknownGuids(artifact, content.getSecond());
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT) {

            wordMl.addEditParagraphNoEscape(WordCoreUtil.getStartEditImage(artifact.getGuid()));
            wordMl.append(data);
            wordMl.addEditParagraphNoEscape(WordCoreUtil.getEndEditImage(artifact.getGuid()));

         } else if (data != null) {
            wordMl.append(data);
         } else if (footer != null) {
            wordMl.append(footer);
         }
         if (data != null && WordCoreUtil.containsLists(data)) {
            wordMl.resetListValue();
         }
      } else {
         super.renderAttribute(attributeType, artifact, PresentationType.SPECIALIZED_EDIT, wordMl, format, label,
            footer);
      }
   }

   /**
    * This {@link IRenderer} implementation uses the default {@link IComparator} implementation for {@link Artifact}.
    *
    * @return <code>true</code>.
    */

   @Override
   public boolean supportsCompare() {
      return true;
   }

}

/* EOF */
