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

package org.eclipse.osee.framework.ui.skynet.render.word;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class WordTemplateProcessor {
   private static final String ARTIFACT = "Artifact";
   private static final String EXTENSION_PROCESSOR = "Extension_Processor";
   private static final String KEY = "Key";

   private static final Pattern namePattern =
         Pattern.compile("<((\\w+:)?(Name))>(.*?)</\\1>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern outlineTypePattern =
         Pattern.compile("<((\\w+:)?(OutlineType))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern outlineNumberPattern =
         Pattern.compile("<((\\w+:)?(Number))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern argumentElementsPattern =
         Pattern.compile("<((\\w+:)?(Argument))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern keyValueElementsPattern =
         Pattern.compile("<((\\w+:)?(Key|Value))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern subDocElementsPattern =
         Pattern.compile("<((\\w+:)?(SubDoc))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern setNamePattern =
         Pattern.compile("<(\\w+:)?Set_Name>(.*?)</(\\w+:)?Set_Name>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern headElementsPattern =
         Pattern.compile("<((\\w+:)?(" + ARTIFACT + "|" + EXTENSION_PROCESSOR + "))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern attributeElementsPattern =
         Pattern.compile("<((\\w+:)?(Attribute))>(.*?)</\\3>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern internalAttributeElementsPattern =
         Pattern.compile("<((\\w+:)?(Label|Outline|Name|Format|Editable))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern outlineElementsPattern =
         Pattern.compile("<((\\w+:)?(Outline))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern internalOutlineElementsPattern =
         Pattern.compile("<((\\w+:)?(HeadingAttribute|RecurseChildren|Number))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final String[] NUMBER =
         new String[] {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};

   private String slaveTemplate;
   private boolean outlining;
   private boolean recurseChildren;
   private String outlineNumber;
   private String headingAttributeName;
   private List<AttributeElement> attributeElements = new LinkedList<AttributeElement>();
   final List<Artifact> nonTemplateArtifacts = new LinkedList<Artifact>();
   private Set<String> ignoreAttributeExtensions = new HashSet<String>();
   private int previousTemplateCopyIndex;
   private WordTemplateRenderer renderer;

   public WordTemplateProcessor(WordTemplateRenderer renderer) {
      this.renderer = renderer;
      loadIgnoreAttributeExtensions();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    * 
    * @throws IOException
    */
   public void publishSRS(VariableMap variableMap) throws OseeCoreException {
      Artifact srsMasterTemplate =
            ArtifactQuery.getArtifactFromTypeAndName("Renderer Template", "srsMasterTemplate",
                  BranchManager.getCommonBranch());
      String masterTemplate = srsMasterTemplate.getSoleAttributeValue(WordAttribute.WHOLE_WORD_CONTENT, "");

      Artifact srsSlaveTemplate =
            ArtifactQuery.getArtifactFromTypeAndName("Renderer Template", "srsSlaveTemplate",
                  BranchManager.getCommonBranch());
      slaveTemplate = srsSlaveTemplate.getSoleAttributeValue(WordAttribute.WHOLE_WORD_CONTENT, "");

      IFolder folder = FileSystemRenderer.ensureRenderFolderExists(PresentationType.PREVIEW);
      String fileName = "SRS_" + Lib.getDateTimeString() + ".xml";
      AIFile.writeToFile(folder.getFile(fileName), applySRSTemplate(variableMap, masterTemplate, folder, null, null));
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    * 
    * @throws IOException
    */
   private InputStream applySRSTemplate(VariableMap variableMap, String template, IFolder folder, String nextParagraphNumber, String outlineType) throws OseeCoreException {
      WordMLProducer wordMl;
      CharBackedInputStream charBak;
      try {
         charBak = new CharBackedInputStream();
         wordMl = new WordMLProducer(charBak);
         template = handleSettingParagraphNumbersForSRS(template, outlineType, nextParagraphNumber, wordMl);
      } catch (CharacterCodingException ex) {
         throw new OseeWrappedException(ex);
      }
      template = WordUtil.stripSpellCheck(template);

      Matcher matcher = headElementsPattern.matcher(template);
      int lastEndIndex = 0;
      while (matcher.find()) {
         // Write the part of the template between the elements
         wordMl.addWordMl(template.substring(lastEndIndex, matcher.start()));

         lastEndIndex = matcher.end();
         String elementType = matcher.group(3);
         String elementValue = matcher.group(4);

         if (elementType.equals(ARTIFACT)) {
            extractOutliningOptions(elementValue);
            Matcher setNameMatcher = setNamePattern.matcher(elementValue);
            setNameMatcher.find();
            String artifactSetName = WordUtil.textOnly(setNameMatcher.group(2));
            processArtifactSet(elementValue, variableMap.getArtifacts(artifactSetName), wordMl, outlineType,
                  PresentationType.PREVIEW);
         } else if (elementType.equals(EXTENSION_PROCESSOR)) {
            try {
               processExtensionTemplate(elementValue, variableMap, folder, wordMl);
            } catch (CoreException ex) {
               throw new OseeWrappedException(ex);
            }
         } else {
            throw new OseeArgumentException("Invalid input: " + elementType);
         }
      }
      // Write out the last of the template
      wordMl.addWordMl(template.substring(lastEndIndex));

      displayNonTemplateArtifacts(nonTemplateArtifacts);
      return charBak;
   }

   /**
    * Parse through a template to find XML defining artifact sets and replace it with the result of publishing those
    * artifacts, it will also assign the artifacts paragraph number to the starting heading numbers in publishing.
    * 
    * @throws Exception
    */
   public InputStream applyTemplate(List<Artifact> artifacts, String template, String outlineType, PresentationType presentationType) throws OseeCoreException {
      CharBackedInputStream charBak;
      try {
         charBak = new CharBackedInputStream();
      } catch (CharacterCodingException ex) {
         throw new OseeWrappedException(ex);
      }
      WordMLProducer wordMl = new WordMLProducer(charBak);
      previousTemplateCopyIndex = 0;

      outlineNumber = peekAtFirstArtifactToGetParagraphNumber(template, null, artifacts);
      //modifications to the template must be done before the matcher
      template = wordMl.setHeadingNumbers(outlineNumber, template);

      template = WordUtil.stripSpellCheck(template);

      Matcher matcher = headElementsPattern.matcher(template);

      while (matcher.find()) {
         String elementType = matcher.group(3);
         String elementValue = matcher.group(4);

         if (elementType.equals(ARTIFACT)) {
            extractOutliningOptions(elementValue);

            if (presentationType == PresentationType.EDIT && artifacts.size() == 1) {
               // for single edit override outlining options
               outlining = false;
            }

            // write out the template up to the start of the artifact element (but don't change copyIndex because there are nested elements in the artifact element
            wordMl.addWordMl(template.substring(previousTemplateCopyIndex, matcher.start()));
            previousTemplateCopyIndex = matcher.end();
            processArtifactSet(elementValue, artifacts, wordMl, outlineType, presentationType);

         } else {
            throw new IllegalArgumentException("Invalid input: " + elementType);
         }
      }
      // Write out the last of the template
      wordMl.addWordMl(template.substring(previousTemplateCopyIndex));
      displayNonTemplateArtifacts(nonTemplateArtifacts);
      return charBak;
   }

   private void writeTemplateBetweenElements(WordMLProducer wordMl, String template, Matcher matcher) throws OseeWrappedException {
      wordMl.addWordMl(template.substring(previousTemplateCopyIndex, matcher.start()));
      previousTemplateCopyIndex = matcher.end();
   }

   private String handleSettingParagraphNumbersForSRS(String template, String outlineType, String nextParagraphNumber, WordMLProducer wordMl) throws CharacterCodingException {
      boolean appendixOutlineType = outlineType != null && outlineType.equalsIgnoreCase("APPENDIX");

      if (appendixOutlineType) {
         // Example of appendix number: A.0
         char[] chars = nextParagraphNumber.toCharArray();
         template = wordMl.setAppendixStartLetter(chars[0], template);
      } else {
         template = wordMl.setHeadingNumbers(nextParagraphNumber, template);
      }

      if (nextParagraphNumber != null && !appendixOutlineType) {
         wordMl.setNextParagraphNumberTo(nextParagraphNumber);
      }
      return template;
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<Artifact> artifacts) throws OseeCoreException {
      String startParagraphNumber = "1";
      Matcher matcher = headElementsPattern.matcher(template);

      if (matcher.find()) {
         String elementType = matcher.group(3);

         if (elementType.equals(ARTIFACT)) {
            if (!artifacts.isEmpty()) {
               Artifact artifact = artifacts.iterator().next();
               if (artifact.isAttributeTypeValid("Imported Paragraph Number")) {
                  String paragraphNum = artifact.getSoleAttributeValue("Imported Paragraph Number", "");
                  if (paragraphNum != null && !paragraphNum.equals("")) {
                     startParagraphNumber = paragraphNum;
                  }
               }
            }
         }
      }
      return startParagraphNumber;
   }

   private void processArtifactSet(final String artifactElement, final List<Artifact> artifacts, final WordMLProducer wordMl, final String outlineType, PresentationType presentationType) throws OseeCoreException {
      nonTemplateArtifacts.clear();
      if (outlineNumber != null) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }

      extractSkynetAttributeReferences(getArtifactSetXml(artifactElement));

      for (Artifact artifact : artifacts) {
         processObjectArtifact(artifact, wordMl, outlineType, presentationType, artifacts.size() > 1);
      }
   }

   /**
    * Only used by Publish SRS
    * 
    * @throws CoreException
    */
   private void processExtensionTemplate(String elementValue, VariableMap variableMap, IFolder folder, WordMLProducer wordMl) throws OseeCoreException, CoreException {
      String extensionName;
      String subdocumentName = null;
      boolean doSubDocuments = false;
      String nextParagraphNumber = null;
      String outlineType = null;

      Matcher matcher = outlineNumberPattern.matcher(elementValue);
      if (matcher.find()) {
         nextParagraphNumber = WordUtil.textOnly(matcher.group(4));
      }

      matcher = outlineTypePattern.matcher(elementValue);
      if (matcher.find()) {
         outlineType = WordUtil.textOnly(matcher.group(4));
      }

      matcher = namePattern.matcher(elementValue);
      if (matcher.find()) {
         extensionName = WordUtil.textOnly(matcher.group(4));
      } else {
         throw new IllegalArgumentException("Schema must contain an extension name.");
      }

      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IExtension extension =
            registry.getExtension("org.eclipse.osee.framework.ui.skynet.WordMlProducer", extensionName);

      if (extension != null) {
         matcher = subDocElementsPattern.matcher(elementValue);

         if (matcher.find()) {
            subdocumentName = WordUtil.textOnly(matcher.group(4));
            doSubDocuments = true;
         }

         IConfigurationElement[] configElements = null;
         configElements = extension.getConfigurationElements();
         for (int j = 0; j < configElements.length; j++) {
            IWordMlProducer producer = (IWordMlProducer) configElements[j].createExecutableExtension("class");

            matcher = argumentElementsPattern.matcher(elementValue);

            VariableMap newVariableMap = doSubDocuments ? new VariableMap() : null;

            while (matcher.find()) {
               matcher = keyValueElementsPattern.matcher(matcher.group(4));

               String key = null;
               while (matcher.find()) {
                  String type = WordUtil.textOnly(matcher.group(3));

                  if (type.equalsIgnoreCase(KEY)) {
                     key = WordUtil.textOnly(matcher.group(4));
                  } else {
                     String value = WordUtil.textOnly(matcher.group(4));

                     if (doSubDocuments) {
                        newVariableMap.setValue(key, value);
                     } else {
                        variableMap.setValue(key, value);
                     }
                  }
               }
            }

            if (doSubDocuments) {
               newVariableMap.setValue("Branch", variableMap.getBranch("Branch"));
               String subDocFileName = subdocumentName + ".xml";
               producer.process(newVariableMap);
               AIFile.writeToFile(folder.getFile(subDocFileName), applySRSTemplate(newVariableMap, slaveTemplate,
                     folder, nextParagraphNumber, outlineType));

               wordMl.createHyperLinkDoc(subDocFileName);
               // wordMl.createSubDoc(subDocFileName);
            } else {
               producer.process(variableMap);
            }
         }
      }
   }

   private void extractOutliningOptions(String artifactElement) {
      Matcher matcher = outlineElementsPattern.matcher(artifactElement);

      if (matcher.find()) {
         matcher = internalOutlineElementsPattern.matcher(matcher.group(4));
         outlining = true;

         // Default values for optional/unspecified parameters
         recurseChildren = false;

         while (matcher.find()) {
            String elementType = matcher.group(3);
            String value = WordUtil.textOnly(matcher.group(4));

            if (elementType.equals("HeadingAttribute")) {
               headingAttributeName = value;
            } else if (elementType.equals("RecurseChildren")) {
               recurseChildren = Boolean.parseBoolean(value);
            } else if (elementType.equals("Number")) {
               outlineNumber = value;
            }
         }
      } else {
         outlining = false;
         recurseChildren = false;
         headingAttributeName = null;
      }
   }

   private void processObjectArtifact(Artifact artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      if (artifact instanceof WordArtifact && !((WordArtifact) artifact).isWholeWordArtifact()) {
         if (outlining) {
            String headingText = artifact.getSoleAttributeValue(headingAttributeName, "");
            CharSequence paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);

            VariableMap options = renderer.getOptions();
            if (renderer.getBooleanOption(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION)) {
               if (artifact.isAttributeTypeValid("Imported Paragraph Number")) {
                  artifact.setSoleAttributeValue("Imported Paragraph Number", paragraphNumber.toString());
                  artifact.persistAttributes((SkynetTransaction) options.getValue(WordTemplateRenderer.TRANSACTION_OPTION));
               }
            }
         }
         processAttributes(artifact, wordMl, presentationType, multipleArtifacts);
         if (recurseChildren) {
            for (Artifact childArtifact : artifact.getChildren()) {
               processObjectArtifact(childArtifact, wordMl, outlineType, presentationType, multipleArtifacts);
            }
         }
         if (outlining) {
            wordMl.endOutlineSubSection();
         }
      } else {
         nonTemplateArtifacts.add(artifact);
      }
   }

   private void processAttributes(Artifact artifact, WordMLProducer wordMl, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (attributeElement.getAttributeName().equals("*")) {
            for (String attributeTypeName : orderAttributeNames(artifact.getAttributeTypes())) {
               if (!outlining || !attributeTypeName.equals(headingAttributeName)) {
                  processAttribute(artifact, wordMl, attributeElement, attributeTypeName, true, presentationType,
                        multipleArtifacts);
               }
            }
         } else {
            if (artifact.isAttributeTypeValid(attributeName)) {
               processAttribute(artifact, wordMl, attributeElement, attributeName, false, presentationType,
                     multipleArtifacts);
            } else {
               throw new OseeCoreException(String.format("Invalid attribute type [%s]", attributeName));
            }
         }
      }

      wordMl.setPageLayout(artifact);
   }

   private void processAttribute(Artifact artifact, WordMLProducer wordMl, AttributeElement attributeElement, String attributeTypeName, boolean allAttrs, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      String format = attributeElement.getFormat();

      // This is for SRS Publishing. Do not publish unspecified attributes
      if (!allAttrs && (attributeTypeName.equals(Requirements.PARTITION) || attributeTypeName.equals("Safety Criticality"))) {
         if (artifact.isAttributeTypeValid(Requirements.PARTITION)) {
            for (Attribute<?> partition : artifact.getAttributes(Requirements.PARTITION)) {
               if (partition.getValue().equals("Unspecified")) {
                  return;
               }
            }
         }
      }

      if (attributeTypeName.equals("TIS Traceability")) {
         for (Artifact requirement : artifact.getRelatedArtifacts(CoreRelationEnumeration.Verification__Requirement)) {
            wordMl.addParagraph(requirement.getSoleAttributeValue("Imported Paragraph Number") + "\t" + requirement.getDescriptiveName());
         }
         return;
      }

      attributeTypeName = AttributeTypeManager.getType(attributeTypeName).getName();

      Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeTypeName);

      if (!attributes.isEmpty()) {
         Attribute<Object> attribute = attributes.iterator().next();
         AttributeType attributeType = attribute.getAttributeType();

         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         if (attributeTypeName.equals(WordAttribute.WORD_TEMPLATE_CONTENT)) {
            if (attributeElement.label.length() > 0) {
               wordMl.addParagraph(attributeElement.label);
            }

            Object value = attribute.getValue();
            if (value != null && value instanceof String) {
               String data = (String) value;
               String wordContent = WordUtil.stripSpellCheck(data);//TODO what is the best way to get at unknown attribute types? (because this isn't it)
               //Change the BinData Id so images do not get overridden by the other images
               wordContent = WordUtil.reassignBinDataID(wordContent);

               if (presentationType == PresentationType.EDIT) {
                  writeXMLMetaDataWrapper(wordMl, elementNameFor(attributeType.getName()),
                        "ns0:guid=\"" + artifact.getGuid() + "\"",
                        "ns0:attrId=\"" + attributeType.getAttrTypeId() + "\"", wordContent);
               } else {
                  wordMl.addWordMl(wordContent);
               }
            } else {
               System.out.println(artifact.getArtifactType().getName() + " : " + artifact.getSoleAttributeValue("Name") + " : " + attributeType.getName() + " == null");
            }

            wordMl.resetListValue();
         } else {
            wordMl.startParagraph();
            // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
            if (allAttrs) {
               wordMl.addWordMl("<w:r><w:t> " + attributeTypeName + ": </w:t></w:r>");
            } else {
               wordMl.addWordMl(attributeElement.label);
            }

            String valueList = Collections.toString(", ", artifact.getAttributes(attributeTypeName));
            if (attributeElement.format.contains(">x<")) {
               wordMl.addWordMl(format.replace(">x<", ">" + valueList + "<"));
            } else {
               wordMl.addTextInsideParagraph(valueList);
            }
            wordMl.endParagraph();
         }
      }
   }

   public static String elementNameFor(String artifactName) {
      // Since artifact names are free text it is important to reformat the name
      // to ensure it is suitable as an element name
      // NOTE: The current program.launch has a tokenizing bug that causes an error if consecutive
      // spaces are in the name
      String elementName = artifactName.trim().replaceAll("[^A-Za-z0-9]", "_");

      // Ensure the name did not end up empty
      if (elementName.equals("")) elementName = "nameless";

      // Fix the first character if it is a number by replacing it with its name
      char firstChar = elementName.charAt(0);
      if (firstChar >= '0' && firstChar <= '9') {
         elementName = NUMBER[firstChar - '0'] + elementName.substring(1);
      }

      return elementName;
   }

   public static void writeXMLMetaDataWrapper(WordMLProducer wordMl, String name, String guid, String attributeId, String contentString) throws OseeWrappedException {
      wordMl.addWordMl("<ns0:" + name + " xmlns:ns0=\"" + WordTemplateRenderer.ARTIFACT_SCHEMA + "\" " + guid + " " + attributeId + ">");
      wordMl.addWordMl(contentString);
      wordMl.addWordMl("</ns0:" + name + "><w:p/>");
   }

   private String getArtifactSetXml(String artifactElement) {
      artifactElement = artifactElement.replaceAll("<(\\w+:)?Artifact/?>", "");
      artifactElement = artifactElement.replaceAll("<(\\w+:)?Set_Name>.*?</(\\w+:)?Set_Name>", "");

      return artifactElement;
   }

   private void extractSkynetAttributeReferences(String artifactElementTemplate) {
      attributeElements.clear();
      Matcher matcher = attributeElementsPattern.matcher(artifactElementTemplate);

      while (matcher.find()) {
         attributeElements.add(new AttributeElement(matcher.group(4)));
      }
   }

   private static class AttributeElement {
      private String outlineNumber;
      private String label;
      private String attributeName;
      private String format;

      public AttributeElement(String element) {
         Matcher matcher = internalAttributeElementsPattern.matcher(element);

         this.outlineNumber = "";
         this.label = "";
         this.attributeName = "";
         this.format = "";

         while (matcher.find()) {
            String elementType = matcher.group(3);
            String value = matcher.group(4).trim();
            if (elementType.equals("Outline")) {
               value = WordUtil.textOnly(value);
               if (value.length() > 0) {
                  outlineNumber = value;
               } else {
                  outlineNumber = "1.0";
               }
            } else if (elementType.equals("Label")) {
               label = value;
            } else if (elementType.equals("Name")) {
               attributeName = WordUtil.textOnly(value);
            } else if (elementType.equals("Format")) {
               format = value;
            } else {
               OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Unexpected element read in Attribute:" + elementType);
            }
         }
      }

      public String getAttributeName() {
         return attributeName;
      }

      public String getFormat() {
         return format;
      }

      public String getLabel() {
         return label;
      }

      public String getOutlineNumber() {
         return outlineNumber;
      }
   }

   private void loadIgnoreAttributeExtensions() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      if (extensionRegistry != null) {
         IExtensionPoint point =
               extensionRegistry.getExtensionPoint("org.eclipse.osee.framework.ui.skynet.IgnorePublishAttribute");
         if (point != null) {
            IExtension[] extensions = point.getExtensions();
            for (IExtension extension : extensions) {
               IConfigurationElement[] elements = extension.getConfigurationElements();
               for (IConfigurationElement element : elements) {
                  ignoreAttributeExtensions.add(element.getAttribute("name"));
               }
            }
         }
      }
   }

   private Collection<String> orderAttributeNames(Collection<AttributeType> attributeTypes) {
      ArrayList<String> orderedNames = new ArrayList<String>(attributeTypes.size());
      String contentName = null;

      for (AttributeType attributeType : attributeTypes) {
         if (attributeType.getName().equals(WordAttribute.WHOLE_WORD_CONTENT) || attributeType.getName().equals(
               WordAttribute.WORD_TEMPLATE_CONTENT)) {
            contentName = attributeType.getName();
         } else {
            orderedNames.add(attributeType.getName());
         }
      }
      Arrays.sort(orderedNames.toArray(new String[0]));
      if (contentName != null) {
         orderedNames.add(contentName);
      }
      return orderedNames;
   }

   private void displayNonTemplateArtifacts(final Collection<Artifact> artifacts) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            public void run() {
               ArrayList<Artifact> nonTempArtifacts = new ArrayList<Artifact>(artifacts.size());
               nonTempArtifacts.addAll(artifacts);
               ArtifactExplorer.explore(nonTempArtifacts);
            }
         });
      }
   }
}