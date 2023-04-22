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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.word.WordCoreUtilClient;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
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

   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";

   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";

   /**
    * The {@link ImageDescriptor} used to draw the icon for this renderer's command icon.
    */

   private static ImageDescriptor imageDescriptor;

   /**
    * A list of the {@link MenuCmdDef} for the right click context menu.
    */

   private static List<MenuCmdDef> menuCommandDefinitions;

   private static final String OLE_END = "</w:docOleData>";
   private static final String OLE_START = "<w:docOleData>";

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

   private static final String STYLES = "<w:styles>.*?</w:styles>";

   private static final String STYLES_END = "</w:styles>";

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

   private final WordTemplateProcessor templateProcessor;

   public MSWordTemplateClientRenderer() {
      super();
      this.comparator = new WordTemplateCompare(this);
      this.templateProcessor = new WordTemplateProcessor(this);
      this.menuCommands = MSWordTemplateClientRenderer.menuCommandDefinitions;
      this.setRendererOption(RendererOption.CLIENT_RENDERER_CAN_STREAM, MSWordTemplateClientRenderer.CAN_STREAM);
   }

   public MSWordTemplateClientRenderer(RendererMap options) {
      super(options);
      this.comparator = new WordTemplateCompare(this);
      this.templateProcessor = new WordTemplateProcessor(this);
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
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, RendererMap rendererOptions) {
      return MSWordTemplateRendererUtils.getApplicabilityRating(presentationType, artifact, rendererOptions);
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

      final List<Artifact> notMultiEditableArtifacts = new LinkedList<>();
      Artifact template;
      String templateContent = "";
      String templateOptions = "";
      String templateStyles = "";

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
         if (template != null) {
            templateContent = template.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            templateOptions = template.getSoleAttributeValue(CoreAttributeTypes.RendererOptions);

            List<Artifact> templateRelatedArtifacts =
               template.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);

            if (templateRelatedArtifacts.size() == 1) {
               templateStyles = templateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                  CoreAttributeTypes.WholeWordContent, "");
            } else if (templateRelatedArtifacts.size() > 1) {
               OseeLog.log(this.getClass(), Level.INFO,
                  "More than one style relation currently not supported. Defaulting to styles defined in the template.");
            }
         }
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);
         if (template != null) {
            templateContent = template.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            templateOptions = template.getSoleAttributeValue(CoreAttributeTypes.RendererOptions);

            List<Artifact> templateRelatedArtifacts =
               template.getRelatedArtifacts(CoreRelationTypes.SupportingInfo_SupportingInfo);

            if (templateRelatedArtifacts.size() == 1) {
               templateStyles = templateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                  CoreAttributeTypes.WholeWordContent, "");
            } else if (templateRelatedArtifacts.size() > 1) {
               OseeLog.log(this.getClass(), Level.INFO,
                  "More than one style relation currently not supported. Defaulting to styles defined in the template.");
            }
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() > 1) {
            // currently we can't support the editing of multiple artifacts with OLE data
            for (Artifact artifact : artifacts) {
               if (!artifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "").equals("")) {
                  notMultiEditableArtifacts.add(artifact);
               }
            }
            displayNotMultiEditArtifacts(notMultiEditableArtifacts,
               "Do not support editing of multiple artifacts with OLE data");
            artifacts.removeAll(notMultiEditableArtifacts);
         } else { // support OLE data when appropriate
            if (!firstArtifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData, "").equals("")) {
               templateContent = templateContent.replaceAll(EMBEDDED_OBJECT_NO, EMBEDDED_OBJECT_YES);

               //Add in new template styles now so OLE Data doesn't get lost
               if (!templateStyles.isEmpty()) {
                  templateContent = templateContent.replace(STYLES, templateStyles);
                  templateStyles = "";
               }

               templateContent = templateContent.replaceAll(STYLES_END,
                  STYLES_END + OLE_START + firstArtifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData,
                     "") + OLE_END);
            }
         }
      }

      templateContent = WordCoreUtilClient.removeGUIDFromTemplate(templateContent);

      //@formatter:off
      var outputStream = ( (Boolean) this.getRendererOptionValue(RendererOption.CLIENT_RENDERER_CAN_STREAM) )
                            ? (OutputStream) this.getRendererOptionValue(RendererOption.OUTPUT_STREAM)
                            : (OutputStream) null;

      return
         this.templateProcessor.applyTemplate
            (
               artifacts,                                                      /* Artifacts          */
               templateContent,                                                /* Template Content   */
               templateOptions,                                                /* Template Options   */
               templateStyles,                                                 /* Template Styles    */
               null,                                                           /* Folder, IContainer */
               null,                                                           /* Outline Number     */
               (String) getRendererOptionValue(RendererOption.OUTLINE_TYPE),   /* Outline Type       */
               presentationType,                                               /* Presentation Type  */
               outputStream                                                    /* Output Stream      */
            );
   }

   protected Artifact getTemplate(Artifact artifact, PresentationType presentationType) {
      // if USE_TEMPLATE_ONCE then only the first two artifacts will use the whole template (since they are diff'd with each other)
      // The settings from the template are stored previously and will be used, just not the content of the Word template

      boolean useTemplateOnce = (boolean) getRendererOptionValue(RendererOption.USE_TEMPLATE_ONCE);
      boolean firstTime = (boolean) getRendererOptionValue(RendererOption.FIRST_TIME);
      boolean secondTime = (boolean) getRendererOptionValue(RendererOption.SECOND_TIME);
      String option = (String) getRendererOptionValue(RendererOption.TEMPLATE_OPTION);
      ArtifactId templateArt = (ArtifactId) getRendererOptionValue(RendererOption.TEMPLATE_ARTIFACT);

      if (option != null && option.toString().isEmpty()) {
         option = null;
      }

      if (templateArt != null && templateArt.isValid() && (!useTemplateOnce || useTemplateOnce && (firstTime || secondTime))) {
         if (useTemplateOnce) {
            if (secondTime) {
               setRendererOption(RendererOption.SECOND_TIME, false);
            }
            if (firstTime) {
               setRendererOption(RendererOption.FIRST_TIME, false);
               setRendererOption(RendererOption.SECOND_TIME, true);
            }
         }

         if (templateArt instanceof Artifact) {
            return (Artifact) templateArt;
         } else {
            return ArtifactQuery.getArtifactFromId(templateArt, CoreBranches.COMMON);
         }

      }
      if (useTemplateOnce && !firstTime && !secondTime) {
         option = null;
      }
      return TemplateManager.getTemplate(this, artifact, presentationType, option);
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


   public void publish(Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) {
      this.templateProcessor.publishWithNestedTemplates(masterTemplateArtifact, slaveTemplateArtifact, artifacts);
   }

   @Override
   public void renderAttribute(AttributeTypeToken attributeType, Artifact artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer) {
      WordMLProducer wordMl = producer;

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
            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
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
