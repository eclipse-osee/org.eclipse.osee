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

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.SPECIALIZED_EDIT;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.define.report.api.ReportConstants;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.DataRightProviderImpl;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
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
public class WordTemplateRenderer extends WordRenderer implements ITemplateRenderer {
   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";
   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";
   private static final String STYLES_END = "</w:styles>";
   private static final String OLE_START = "<w:docOleData>";
   private static final String OLE_END = "</w:docOleData>";
   public static final String UPDATE_PARAGRAPH_NUMBER_OPTION = "updateParagraphNumber";
   public static final String FIRST_TIME = "FirstTime";
   public static final String SECOND_TIME = "SecondTime";

   private final WordTemplateProcessor templateProcessor;

   private final IComparator comparator;

   public WordTemplateRenderer() {
      this.comparator = new WordTemplateCompare(this);
      DataRightProvider provider = new DataRightProviderImpl();
      this.templateProcessor = new WordTemplateProcessor(this, provider);
   }

   @Override
   public WordTemplateRenderer newInstance() {
      return new WordTemplateRenderer();
   }

   public void publish(Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts, Object... options) throws OseeCoreException {
      setOptions(options);
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
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact, Object... objects) throws OseeCoreException {
      int rating = NO_MATCH;
      Artifact aArtifact = artifact.getFullArtifact();
      if (!presentationType.matches(PresentationType.GENERALIZED_EDIT, PresentationType.GENERAL_REQUESTED)) {
         if (aArtifact.isAttributeTypeValid(CoreAttributeTypes.WordTemplateContent)) {
            rating = PRESENTATION_SUBTYPE_MATCH;
         } else if (presentationType.matches(PresentationType.PREVIEW, PresentationType.DIFF)) {
            rating = BASE_MATCH;
         }
      }
      return rating;
   }

   @Override
   public void renderAttribute(IAttributeType attributeType, Artifact artifact, PresentationType presentationType, Producer producer, AttributeElement attributeElement, String footer) throws OseeCoreException {
      WordMLProducer wordMl = (WordMLProducer) producer;

      if (attributeType.equals(CoreAttributeTypes.WordTemplateContent)) {
         Attribute<?> wordTempConAttr = artifact.getSoleAttribute(attributeType);
         String data = null;
         if (wordTempConAttr != null) {
            data = (String) wordTempConAttr.getValue();
         }

         if (attributeElement.getLabel().length() > 0) {
            wordMl.addParagraph(attributeElement.getLabel());
         }

         if (data != null) {
            //Change the BinData Id so images do not get overridden by the other images
            data = WordUtil.reassignBinDataID(data);

            LinkType linkType = (LinkType) getOption("linkType");
            Set<String> unknownGuids = new HashSet<>();
            data = WordMlLinkHandler.link(linkType, artifact, data, unknownGuids);
            WordUiUtil.displayUnknownGuids(artifact, unknownGuids);

            data = WordUtil.reassignBookMarkID(data);
            data = removeSectionBreakParagraph(data);

            // remove any existing footers and replace with the current one
            data = data.replaceAll(ReportConstants.ENTIRE_FTR + ReportConstants.FULL_PARA_END, "");
            data = data.replaceAll(ReportConstants.NO_DATA_RIGHTS, "");
            data = data.concat(footer);
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT) {
            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
            wordMl.addEditParagraphNoEscape(linkBuilder.getStartEditImage(artifact.getGuid()));
            wordMl.addWordMl(data);
            wordMl.addEditParagraphNoEscape(linkBuilder.getEndEditImage(artifact.getGuid()));

         } else if (data != null) {
            data = data.replaceAll(WordTemplateProcessor.PGNUMTYPE_START_1, "");
            wordMl.addWordMl(data);
         }
         wordMl.resetListValue();
      } else {
         super.renderAttribute(attributeType, artifact, PresentationType.SPECIALIZED_EDIT, wordMl, attributeElement,
            footer);
      }
   }

   private String removeSectionBreakParagraph(String data) {
      Pattern pattern = Pattern.compile(ReportConstants.ENTIRE_FTR);
      Pattern paragraphMatch = Pattern.compile("<w:p.*?/w:p>");
      Matcher matcher = pattern.matcher(data);
      if (matcher.find()) {

         int startIndex = matcher.start();
         String replace = data.substring(0, startIndex);

         // Matches everything but last paragraph before section break
         Matcher matchParagraphs = paragraphMatch.matcher(replace);
         replace = "";
         while (matchParagraphs.find()) {
            replace += matchParagraphs.group(0);
         }
         data = replace + data.substring(startIndex);
      }

      return data;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      final List<Artifact> notMultiEditableArtifacts = new LinkedList<>();
      Artifact template;
      String templateContent = "";
      String templateOptions = "";

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
         if (template != null) {
            templateContent = template.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            templateOptions = template.getSoleAttributeValue(CoreAttributeTypes.RendererOptions);
         }
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);
         if (template != null) {
            templateContent = template.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent);
            templateOptions = template.getSoleAttributeValue(CoreAttributeTypes.RendererOptions);
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
               templateContent = templateContent.replaceAll(STYLES_END,
                  STYLES_END + OLE_START + firstArtifact.getSoleAttributeValue(CoreAttributeTypes.WordOleData,
                     "") + OLE_END);
            }
         }
      }

      templateContent = WordUtil.removeGUIDFromTemplate(templateContent);
      return templateProcessor.applyTemplate(artifacts, templateContent, templateOptions, null, null,
         getStringOption("outlineType"), presentationType);
   }

   protected Artifact getTemplate(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      // if USE_TEMPLATE_ONCE then only the first two artifacts will use the whole template (since they are diff'd with each other)
      // The settings from the template are stored previously and will be used, just not the content of the Word template
      boolean useTemplateOnce = getBooleanOption(USE_TEMPLATE_ONCE);
      boolean firstTime = getBooleanOption(FIRST_TIME);
      boolean secondTime = getBooleanOption(SECOND_TIME);
      Object option = getOption(TEMPLATE_OPTION);

      if (option instanceof Artifact && (!useTemplateOnce || useTemplateOnce && (firstTime || secondTime))) {
         Artifact template = (Artifact) option;
         if (useTemplateOnce) {
            if (secondTime) {
               setOption(SECOND_TIME, false);
            }
            if (firstTime) {
               setOption(FIRST_TIME, false);
               setOption(SECOND_TIME, true);
            }
         }

         return template;
      } else if ((option == null || option instanceof String) || (useTemplateOnce && !firstTime)) {
         if (useTemplateOnce && !firstTime && !secondTime) {
            option = null;
         }
         Artifact templateArtifact = TemplateManager.getTemplate(this, artifact, presentationType, (String) option);
         return templateArtifact;
      }
      return null;
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
         TEMPLATE_OPTION, PREVIEW_WITH_RECURSE_VALUE));
   }
}
