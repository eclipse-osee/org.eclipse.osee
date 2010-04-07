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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.linking.OseeLinkBuilder;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.compare.IComparator;
import org.eclipse.osee.framework.ui.skynet.render.compare.WordTemplateCompare;
import org.eclipse.osee.framework.ui.skynet.render.word.AttributeElement;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.w3c.dom.Element;

/**
 * Renders WordML content.
 *
 * @author Jeff C. Phillips
 */
public class WordTemplateRenderer extends WordRenderer implements ITemplateRenderer {
   private static final Pattern pattern =
         Pattern.compile("<v:imagedata[^>]*src=\"wordml://(\\d+\\.\\w+)\"[^>]*>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   public static final String RENDERER_EXTENSION = "org.eclipse.osee.framework.ui.skynet.word";
   public static final String DEFAULT_SET_NAME = "Default";
   public static final String ARTIFACT_NAME = "Word Renderer";
   public static final String TEMPLATE_ATTRIBUTE = "Word Template";
   public static final String ARTIFACT_SCHEMA = "http://eclipse.org/artifact.xsd";
   private static final String EMBEDDED_OBJECT_NO = "w:embeddedObjPresent=\"no\"";
   private static final String EMBEDDED_OBJECT_YES = "w:embeddedObjPresent=\"yes\"";
   private static final String STYLES_END = "</w:styles>";
   private static final String OLE_START = "<w:docOleData>";
   private static final String OLE_END = "</w:docOleData>";
   private static final QName fo = new QName("ns0", "unused_localname", ARTIFACT_SCHEMA);
   public static final String UPDATE_PARAGRAPH_NUMBER_OPTION = "updateParagraphNumber";

   private final WordTemplateProcessor templateProcessor = new WordTemplateProcessor(this);

   private final IComparator comparator;

   public WordTemplateRenderer() {
      super();
      this.comparator = new WordTemplateCompare(this);
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(2);

      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wordeditor.command");
      } else if (presentationType == PresentationType.PREVIEW) {
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wordpreview.command");
         commandIds.add("org.eclipse.osee.framework.ui.skynet.wordpreviewChildren.command");
      }

      return commandIds;
   }

   @Override
   public WordTemplateRenderer newInstance() throws OseeCoreException {
      return new WordTemplateRenderer();
   }

   public void publish(VariableMap variableMap, Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) throws OseeCoreException {
      templateProcessor.publishWithExtensionTemplates(variableMap, masterTemplateArtifact, slaveTemplateArtifact,
            artifacts);
   }

   /**
    * Displays a list of artifacts in the Artifact Explorer that could not be multi edited because they contained
    * artifacts that had an OLEData attribute.
    *
    * @param artifacts
    */
   private void displayNotMultiEditArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            public void run() {
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }

   public static QName getFoNamespace() {
      return fo;
   }

   public static byte[] getFormattedContent(Element formattedItemElement) {
      ByteArrayOutputStream data = new ByteArrayOutputStream((int) Math.pow(2, 10));
      OutputFormat format = Jaxp.getCompactFormat(formattedItemElement.getOwnerDocument());
      format.setOmitDocumentType(true);
      format.setOmitXMLDeclaration(true);
      XMLSerializer serializer = new XMLSerializer(data, format);

      try {
         for (Element e : Jaxp.getChildDirects(formattedItemElement)) {
            serializer.serialize(e);
         }
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }

      return data.toByteArray();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (presentationType != PresentationType.GENERALIZED_EDIT) {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WORD_TEMPLATE_CONTENT)) {
            return PRESENTATION_SUBTYPE_MATCH;
         }
         if (!artifact.isAttributeTypeValid(CoreAttributeTypes.NATIVE_CONTENT) && !artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT)) {
            return PRESENTATION_TYPE;
         }
      }
      return NO_MATCH;
   }

   @Override
   public InputStream getRenderInputStream(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
      if (artifact != null) {
         artifacts.add(artifact);
      }
      return getRenderInputStream(artifacts, presentationType);
   }

   @Override
   public void renderAttribute(String attributeTypeName, Artifact artifact, PresentationType presentationType, Producer producer, VariableMap map, AttributeElement attributeElement) throws OseeCoreException {
      String value = "";
      WordMLProducer wordMl = (WordMLProducer) producer;

      if (attributeTypeName.equals(CoreAttributeTypes.WORD_TEMPLATE_CONTENT.getName())) {
         Attribute<?> wordTempConAttr = artifact.getSoleAttribute(attributeTypeName);
         String data = (String) wordTempConAttr.getValue();

         if (attributeElement.getLabel().length() > 0) {
            wordMl.addParagraph(attributeElement.getLabel());
         }

         if (data != null) {
            value = WordUtil.stripSpellCheck(data);//TODO what is the best way to get at unknown attribute types? (because this isn't it)
            //Change the BinData Id so images do not get overridden by the other images
            value = WordUtil.reassignBinDataID(value);

            LinkType linkType = (LinkType) map.getValue("linkType");
            value = WordMlLinkHandler.link(linkType, artifact, value);
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT) {
            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
            wordMl.addParagraphNoEscape(linkBuilder.getEditArtifactLink(artifact.getGuid(), artifact.getBranch(),
                  "OSEE_EDIT_START"));
            wordMl.addWordMl(value);
            wordMl.addParagraphNoEscape(linkBuilder.getEditArtifactLink(artifact.getGuid(), artifact.getBranch(),
                  "OSEE_EDIT_END"));
         } else {
            wordMl.addWordMl(value);
         }
         wordMl.resetListValue();

      } else {
         super.renderAttribute(attributeTypeName, artifact, PresentationType.SPECIALIZED_EDIT, wordMl, map,
               attributeElement);
      }
   }

   @Override
   public InputStream getRenderInputStream(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      final List<Artifact> notMultiEditableArtifacts = new LinkedList<Artifact>();
      String template;

      if (artifacts.isEmpty()) {
         //  Still need to get a default template with a null artifact list
         template = getTemplate(null, presentationType);
      } else {
         Artifact firstArtifact = artifacts.iterator().next();
         template = getTemplate(firstArtifact, presentationType);
         if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() > 1) {
            // currently we can't support the editing of multiple artifacts with OLE data
            for (Artifact artifact : artifacts) {
               if (!artifact.getSoleAttributeValue(CoreAttributeTypes.WORD_OLE_DATA, "").equals("")) {
                  notMultiEditableArtifacts.add(artifact);
               }
            }
            displayNotMultiEditArtifacts(notMultiEditableArtifacts,
                  "Do not support editing of multiple artifacts with OLE data");
            artifacts.removeAll(notMultiEditableArtifacts);
         } else { // support OLE data when appropriate
            if (!firstArtifact.getSoleAttributeValue(CoreAttributeTypes.WORD_OLE_DATA, "").equals("")) {
               template = template.replaceAll(EMBEDDED_OBJECT_NO, EMBEDDED_OBJECT_YES);
               template =
                     template.replaceAll(STYLES_END, STYLES_END + OLE_START + firstArtifact.getSoleAttributeValue(
                           CoreAttributeTypes.WORD_OLE_DATA, "") + OLE_END);
            }
         }
      }

      template = WordUtil.removeGUIDFromTemplate(template);
      return templateProcessor.applyTemplate(getOptions(), artifacts, template, null, null, null, presentationType);
   }

   protected String getTemplate(Artifact artifact, PresentationType presentationType) throws OseeCoreException {
      return TemplateManager.getTemplate(this, artifact, presentationType.name(), getStringOption(TEMPLATE_OPTION)).getSoleAttributeValue(
            CoreAttributeTypes.WHOLE_WORD_CONTENT);
   }

   @Override
   public IComparator getComparator() {
      return comparator;
   }
}