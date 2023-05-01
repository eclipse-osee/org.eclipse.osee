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
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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

public class MSWordTemplateClientRenderer extends FileSystemRenderer {

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

   private static final String COMMAND_TITLE_PREVIEW = "MS Word Preview (Client)";

   /**
    * The context menu command title for the Preview No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES = "MS Word Preview No Attributes (Client)";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN = "MS Word Preview With Children (Client)";

   /**
    * The context menu command title for the Preview With Children No Attributes command.
    */

   private static final String COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES =
      "MS Word Preview With Children No Attributes (Client)";

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

   private static final String RENDERER_IDENTIFIER = "org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer";

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Client Side MS Word Edit";

   /**
    * The {@link Program} used to invoke MS Word.
    */

   private static Program wordApplication;

   /*
    * Build menu commands
    */

   static {

      MSWordTemplateClientRenderer.imageDescriptor =
         ImageManager.getProgramImageDescriptor(MSWordTemplateClientRenderer.PROGRAM_EXTENSION_WORD);

      MSWordTemplateClientRenderer.wordApplication =
         Program.findProgram(MSWordTemplateClientRenderer.PROGRAM_EXTENSION_WORD);

      //@formatter:off
      MSWordTemplateClientRenderer.menuCommandDefinitions =
      List.of
         (
            new MenuCmdDef
                   (
                     CommandGroup.EDIT,
                     MSWordTemplateClientRenderer.COMMAND_TITLE_EDIT,
                     MSWordTemplateClientRenderer.imageDescriptor,
                     Map.of
                        (
                          RendererOption.OPEN_OPTION.getKey(), RendererOption.OPEN_IN_MS_WORD_VALUE.getKey()
                        )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      MSWordTemplateClientRenderer.COMMAND_TITLE_PREVIEW,
                      MSWordTemplateClientRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      MSWordTemplateClientRenderer.COMMAND_TITLE_PREVIEW_NO_ATTRIBUTES,
                      MSWordTemplateClientRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_NO_ATTRIBUTES_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      MSWordTemplateClientRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN,
                      MSWordTemplateClientRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_VALUE.getKey()
                         )
                   ),

            new MenuCmdDef
                   (
                      CommandGroup.PREVIEW,
                      MSWordTemplateClientRenderer.COMMAND_TITLE_PREVIEW_WITH_CHILDREN_NO_ATTRIBUTES,
                      MSWordTemplateClientRenderer.imageDescriptor,
                      Map.of
                         (
                            RendererOption.OPEN_OPTION.getKey(),     RendererOption.OPEN_IN_MS_WORD_VALUE.getKey(),
                            RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE.getKey()
                         )
                   )
         );
   //@formatter:on
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

   public MSWordTemplateClientRenderer() {
      this(null);
   }

   public MSWordTemplateClientRenderer(RendererMap options) {
      super(options);
      this.comparator = new WordTemplateCompare(this);
      this.templateProcessor = new WordTemplateProcessorClient();
      this.menuCommands = MSWordTemplateClientRenderer.menuCommandDefinitions;
      this.setRendererOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, MSWordTemplateClientRenderer.CAN_STREAM);
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

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {
      var rating = MSWordTemplateRendererUtils.getApplicabilityRating(presentationType, artifact, rendererOptions);
      if (!PresentationType.PREVIEW.equals(presentationType)) {
         rating--;
      }
      return rating;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      return MSWordTemplateClientRenderer.wordApplication;
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
      return MSWordTemplateClientRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return MSWordTemplateClientRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return MSWordTemplateClientRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return MSWordTemplateClientRenderer.RENDERER_NAME;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {

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

      boolean useTemplateOnce = (boolean) getRendererOptionValue(RendererOption.USE_TEMPLATE_ONCE);
      boolean firstTime = (boolean) getRendererOptionValue(RendererOption.FIRST_TIME);
      boolean secondTime = (boolean) getRendererOptionValue(RendererOption.SECOND_TIME);

      var publishingTemplateIdentifier = (String) getRendererOptionValue(RendererOption.PUBLISHING_TEMPLATE_IDENTIFIER);

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

         var publishingTemplateRequest = new PublishingTemplateRequest(publishingTemplateIdentifier);

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
                   this.getIdentifier(),               /* Renderer Id                */
                   artifactTypeName,                   /* Publish Artifact Type Name */
                   presentationType.name(),            /* Presentation Type          */
                   option                              /* Option                     */
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
   public MSWordTemplateClientRenderer newInstance() {
      return new MSWordTemplateClientRenderer();
   }

   @Override
   public MSWordTemplateClientRenderer newInstance(RendererMap rendererOptions) {
      return new MSWordTemplateClientRenderer(rendererOptions);
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
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer wordMl, String format, String label, String footer) {


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
            wordMl.addWordMl(data);
            wordMl.addEditParagraphNoEscape(WordCoreUtil.getEndEditImage(artifact.getGuid()));

         } else if (data != null) {
            wordMl.addWordMl(data);
         } else if (footer != null) {
            wordMl.addWordMl(footer);
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
