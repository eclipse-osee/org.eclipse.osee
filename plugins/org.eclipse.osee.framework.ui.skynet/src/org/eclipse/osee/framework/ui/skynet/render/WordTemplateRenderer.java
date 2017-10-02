/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.DIFF;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.report.api.WordTemplateContentData;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpWordUpdateRequest;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.w3c.dom.Element;

/**
 * Renders WordML content.
 *
 * @author Jeff C. Phillips
 */
public class WordTemplateRenderer extends WordRenderer {
   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";
   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";
   private static final String STYLES = "<w:styles>.*?</w:styles>";
   private static final String STYLES_END = "</w:styles>";
   private static final String OLE_START = "<w:docOleData>";
   private static final String OLE_END = "</w:docOleData>";

   private final WordTemplateProcessor templateProcessor;
   private final IComparator comparator;

   public WordTemplateRenderer(Map<RendererOption, Object> options) {
      super(options);
      this.comparator = new WordTemplateCompare(this);
      this.templateProcessor = new WordTemplateProcessor(this);
   }

   public WordTemplateRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public WordTemplateRenderer newInstance() {
      return new WordTemplateRenderer(new HashMap<RendererOption, Object>());
   }

   @Override
   public WordTemplateRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new WordTemplateRenderer(rendererOptions);
   }

   public void publish(Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) {
      templateProcessor.publishWithNestedTemplates(masterTemplateArtifact, slaveTemplateArtifact, artifacts);
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

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int rating = NO_MATCH;
      if (!presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED)) {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WordTemplateContent)) {
            if (presentationType.matches(DEFAULT_OPEN, PREVIEW)) {
               if (artifact.getAttributeCount(WordTemplateContent) > 0) {
                  rating = PRESENTATION_SUBTYPE_MATCH;
               } else {
                  rating = SUBTYPE_TYPE_MATCH;
               }
            } else {
               rating = PRESENTATION_SUBTYPE_MATCH;
            }
         } else if (presentationType.matches(PREVIEW, DIFF)) {
            rating = BASE_MATCH;
         }
      }
      return rating;
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

         TransactionId txId = null;
         if (artifact.isHistorical()) {
            txId = artifact.getTransaction();
         } else {
            txId = TransactionId.SENTINEL;
         }

         WordTemplateContentData wtcData = new WordTemplateContentData();
         wtcData.setArtId(artifact.getUuid());
         wtcData.setBranch(artifact.getBranch());
         wtcData.setFooter(footer);
         wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
         wtcData.setLinkType(linkType != null ? linkType.toString() : null);
         wtcData.setTxId(txId);
         wtcData.setSessionId(ClientSessionManager.getSessionId());
         wtcData.setPresentationType(presentationType);
         ArtifactId view = (ArtifactId) getRendererOptionValue(RendererOption.VIEW);
         wtcData.setViewId(view == null ? ArtifactId.SENTINEL : view);
         wtcData.setPermanentLinkUrl(ArtifactURL.getSelectedPermanenrLinkUrl());

         Pair<String, Set<String>> content = null;
         try {
            content = HttpWordUpdateRequest.renderWordTemplateContent(wtcData);
         } catch (Exception e) {
            WordUiUtil.displayErrorMessage(e.toString());
         }

         if (content != null) {
            data = content.getFirst();
            WordUiUtil.displayUnknownGuids(artifact, content.getSecond());
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT) {
            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
            wordMl.addEditParagraphNoEscape(linkBuilder.getStartEditImage(artifact.getGuid()));
            wordMl.addWordMl(data);
            wordMl.addEditParagraphNoEscape(linkBuilder.getEndEditImage(artifact.getGuid()));

         } else if (data != null) {
            wordMl.addWordMl(data);
         }
         wordMl.resetListValue();
      } else {
         super.renderAttribute(attributeType, artifact, PresentationType.SPECIALIZED_EDIT, wordMl, format, label,
            footer);
      }
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

            if (templateRelatedArtifacts != null) {

               if (templateRelatedArtifacts.size() == 1) {
                  templateStyles = templateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                     CoreAttributeTypes.WholeWordContent, "");
               } else if (templateRelatedArtifacts.size() > 1) {
                  OseeLog.log(this.getClass(), Level.INFO,
                     "More than one style relation currently not supported. Defaulting to styles defined in the template.");
               }
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

            if (templateRelatedArtifacts != null) {
               if (templateRelatedArtifacts.size() == 1) {
                  templateStyles = templateRelatedArtifacts.get(0).getSoleAttributeValueAsString(
                     CoreAttributeTypes.WholeWordContent, "");
               } else {
                  OseeLog.log(this.getClass(), Level.INFO,
                     "More than one style relation currently not supported. Defaulting to styles defined in the template.");
               }
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

      templateContent = WordUtil.removeGUIDFromTemplate(templateContent);

      return templateProcessor.applyTemplate(artifacts, templateContent, templateOptions, templateStyles, null, null,
         (String) getRendererOptionValue(RendererOption.OUTLINE_TYPE), presentationType);
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
               updateOption(RendererOption.SECOND_TIME, false);
            }
            if (firstTime) {
               updateOption(RendererOption.FIRST_TIME, false);
               updateOption(RendererOption.SECOND_TIME, true);
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
   public IComparator getComparator() {
      return comparator;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new UpdateArtifactOperation(file, artifacts, branch, false);
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      ImageDescriptor imageDescriptor = ImageManager.getProgramImageDescriptor("doc");
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "MS Word Edit", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "MS Word Preview", imageDescriptor));
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "MS Word Preview with children", imageDescriptor,
         RendererOption.TEMPLATE_OPTION.getKey(), RendererOption.PREVIEW_WITH_RECURSE_VALUE.getKey()));
   }

}
