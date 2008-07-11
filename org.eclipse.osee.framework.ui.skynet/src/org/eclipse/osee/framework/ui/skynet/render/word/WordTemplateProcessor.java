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
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.collection.tree.Tree;
import org.eclipse.osee.framework.jdk.core.collection.tree.TreeNode;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class WordTemplateProcessor {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(WordTemplateProcessor.class);
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

   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

   private static final String[] NUMBER =
         new String[] {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};

   private String masterTemplate;
   private String slaveTemplate;
   private boolean outlining;
   private boolean recurseChildren;
   private String outlineNumber;
   private CoreRelationEnumeration outlineRelation;
   private String headingAttributeName;
   private List<AttributeElement> attributeElements;
   private boolean saveParagraphNumOnArtifact;
   private Set<String> ignoreAttributeExtensions;
   private int previousTemplateCopyIndex;
   private boolean isEditMode;

   public WordTemplateProcessor() throws CoreException {
      this(null, null);
   }

   public WordTemplateProcessor(String masterTemplate, String slaveTemplate) throws CoreException {
      super();
      this.masterTemplate = masterTemplate;
      this.slaveTemplate = slaveTemplate;
      this.attributeElements = new LinkedList<AttributeElement>();
      this.saveParagraphNumOnArtifact = false;
      this.isEditMode = false;
      this.ignoreAttributeExtensions = new HashSet<String>();

      loadIgnoreAttributeExtensions();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts
    * 
    * @throws IOException
    */
   public void applyTemplate(IFolder folder, BlamVariableMap variableMap) throws Exception {
      isEditMode = false;
      String fileName = variableMap.getString("MasterFileName");
      if (fileName == null) {
         fileName = "new file " + (new Date().toString().replaceAll(":", ";"));
      }
      AIFile.writeToFile(folder.getFile(fileName + ".xml"), applyTemplate(variableMap, masterTemplate, folder, null,
            null));
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts
    * 
    * @throws IOException
    */
   private InputStream applyTemplate(BlamVariableMap variableMap, String template, IFolder folder, String nextParagraphNumber, String outlineType) throws Exception {
      CharBackedInputStream charBak = new CharBackedInputStream();
      WordMLProducer wordMl = new WordMLProducer(charBak);

      template = handleSettingParagraphNumbers(variableMap, template, outlineType, nextParagraphNumber, wordMl);
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
            processArtifactSet(elementValue, variableMap, wordMl, outlineType);
         } else if (elementType.equals(EXTENSION_PROCESSOR)) {
            processExtensionTemplate(elementValue, variableMap, folder, wordMl);
         } else {
            throw new IllegalArgumentException("Invalid input: " + elementType);
         }
      }
      // Write out the last of the template
      wordMl.addWordMl(template.substring(lastEndIndex));
      return charBak;
   }

   /**
    * Parse through a template to find XML defining artifact sets and replace it with the result of publishing those
    * artifacts, it will also assign the artifacts paragraph number to the starting heading numbers in publishing.
    * 
    * @throws Exception
    */
   public InputStream applyTemplate(BlamVariableMap variableMap, String template, String outlineType, boolean isEditMode) throws Exception {
      this.isEditMode = isEditMode;
      CharBackedInputStream charBak = new CharBackedInputStream();
      WordMLProducer wordMl = new WordMLProducer(charBak);
      previousTemplateCopyIndex = 0;

      outlineNumber = peekAtFirstArtifactToGetParagraphNumber(template, null, variableMap);
      //modifications to the template must be done before the matcher
      template = wordMl.setHeadingNumbers(outlineNumber, template);
      template = WordUtil.stripSpellCheck(template);

      Matcher matcher = headElementsPattern.matcher(template);

      while (matcher.find()) {
         String elementType = matcher.group(3);
         String elementValue = matcher.group(4);

         if (elementType.equals(ARTIFACT)) {
            extractOutliningOptions(elementValue);

            // write out the template up to the start of the artifact element (but don't change copyIndex becuase there are nested elements in the artifact element
            wordMl.addWordMl(template.substring(previousTemplateCopyIndex, matcher.start()));
            previousTemplateCopyIndex = matcher.end();

            processArtifactSet(elementValue, variableMap, wordMl, outlineType);

         } else {
            throw new IllegalArgumentException("Invalid input: " + elementType);
         }
      }
      // Write out the last of the template
      wordMl.addWordMl(template.substring(previousTemplateCopyIndex));
      return charBak;
   }

   private void writeTemplateBetweenElements(WordMLProducer wordMl, String template, Matcher matcher) throws IOException {
      wordMl.addWordMl(template.substring(previousTemplateCopyIndex, matcher.start()));
      previousTemplateCopyIndex = matcher.end();
   }

   private String handleSettingParagraphNumbers(BlamVariableMap variableMap, String template, String outlineType, String nextParagraphNumber, WordMLProducer wordMl) throws CharacterCodingException {
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

   @SuppressWarnings("unchecked")
   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, BlamVariableMap variableMap) throws SQLException, MultipleAttributesExist, IllegalStateException, SQLException {
      String startParagraphNumber = "1";
      Matcher matcher = headElementsPattern.matcher(template);

      if (matcher.find()) {
         String elementType = matcher.group(3);
         String elementValue = matcher.group(4);

         if (elementType.equals(ARTIFACT)) {
            Matcher setNameMatcher = setNamePattern.matcher(elementValue);
            setNameMatcher.find();
            final String artifactSetName = WordUtil.textOnly(setNameMatcher.group(2));

            Collection<Artifact> artifacts = variableMap.getArtifacts(artifactSetName);

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

   protected void processTreeObjects(String artifactElement, final Tree<Object> tree, final WordMLProducer wordMl, final String outlineType) throws Exception {
      if (artifactElement != null && tree != null) {
         // extract Artifact set options
         Matcher setNameMatcher = setNamePattern.matcher(artifactElement);
         setNameMatcher.find();

         extractOutliningOptions(artifactElement);

         attributeElements.clear();
         extractSkynetAttributeReferences(getArtifactSetXml(artifactElement));

         processTreeHelper(tree, wordMl, outlineType);
      }
   }

   private void processTreeHelper(Tree<Object> tree, WordMLProducer wordMl, String outlineType) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      for (TreeNode<Object> treeNode : tree.getRoot().getChildren()) {

         if (treeNode.getSelf() instanceof Artifact) {
            Artifact artifact = (Artifact) treeNode.getSelf();

            processObject(artifact, wordMl, outlineType);
         } else if (treeNode.getSelf() instanceof String) {
            // process String
         }
      }
   }

   @SuppressWarnings("unchecked")
   protected void processArtifactSet(final String artifactElement, final BlamVariableMap variableMap, final WordMLProducer wordMl, final String outlineType) throws Exception {
      if (artifactElement != null && variableMap != null) {
         // extract Artifact set options
         Matcher setNameMatcher = setNamePattern.matcher(artifactElement);
         setNameMatcher.find();
         final String artifactSetName = WordUtil.textOnly(setNameMatcher.group(2));

         if (outlineNumber != null) {
            wordMl.setNextParagraphNumberTo(outlineNumber);
         }

         attributeElements.clear();
         extractSkynetAttributeReferences(getArtifactSetXml(artifactElement));

         processArtifactSetHelper(artifactSetName, variableMap, wordMl, outlineType);
      }
   }

   @SuppressWarnings("unchecked")
   private void processArtifactSetHelper(String artifactSetName, BlamVariableMap variableMap, WordMLProducer wordMl, String outlineType) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      for (Artifact artifact : variableMap.getArtifacts(artifactSetName)) {
         if (artifact != null) {
            processObjectArtifact(artifact, wordMl, outlineType);
         } else {
            wordMl.startOutlineSubSection("Times New Roman", "  ", outlineType);
            wordMl.endOutlineSubSection();
         }
      }
   }

   private void processExtensionTemplate(String elementValue, BlamVariableMap variableMap, IFolder folder, WordMLProducer wordMl) throws Exception {
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

            BlamVariableMap newVariableMap = doSubDocuments ? new BlamVariableMap() : null;

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
               AIFile.writeToFile(folder.getFile(subDocFileName), applyTemplate(newVariableMap, slaveTemplate, folder,
                     nextParagraphNumber, outlineType));

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
         outlineRelation = CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD;
         //         outlineNumber = null;

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
         outlineRelation = null;
         headingAttributeName = null;
      }
   }

   @SuppressWarnings("unchecked")
   private void processObject(Object object, WordMLProducer wordMl, String outlineType) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      processObjectArtifact((Artifact) object, wordMl, outlineType);

   }

   @SuppressWarnings("unchecked")
   private void processObjectArtifact(Artifact artifact, WordMLProducer wordMl, String outlineType) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      boolean performedOutLining = false;

      if (outlining) {
         performedOutLining = true;
         String headingText = artifact.getSoleAttributeValue(headingAttributeName, "");
         CharSequence paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);

         if (paragraphNumber != null && saveParagraphNumOnArtifact) {
            if (artifact.isAttributeTypeValid("Imported Paragraph Number")) {
               artifact.setSoleAttributeValue("Imported Paragraph Number", paragraphNumber.toString());
               artifact.persistAttributes();
            }
         }
      }

      processAttributes(artifact, wordMl);

      if (performedOutLining) {
         if (recurseChildren) {
            for (Artifact childArtifact : artifact.getRelatedArtifacts(outlineRelation)) {
               processObjectArtifact(childArtifact, wordMl, outlineType);
            }
         }

         if (performedOutLining) wordMl.endOutlineSubSection();
      }
   }

   private void processAttributes(Artifact artifact, WordMLProducer wordMl) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (attributeElement.getAttributeName().equals("*")) {
            for (AttributeType attributeType : artifact.getAttributeTypes()) {
               processAttribute(artifact, wordMl, attributeElement, attributeType.getName(), true);
            }
         } else {
            if (artifact.isAttributeTypeValid(attributeName)) {
               processAttribute(artifact, wordMl, attributeElement, attributeName, false);
            }
         }
      }

      wordMl.setPageLayout(artifact);
   }

   private void processAttribute(Artifact artifact, WordMLProducer wordMl, AttributeElement attributeElement, String attributeTypeName, boolean allAttrs) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
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

      attributeTypeName = AttributeTypeManager.getTypeWithWordContentCheck(artifact, attributeTypeName).getName();

      Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeTypeName);

      if (!attributes.isEmpty()) {
         Attribute<Object> attribute = attributes.iterator().next();
         AttributeType attributeType = attribute.getAttributeType();

         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         if (attributeTypeName.equals(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
               WordAttribute.CONTENT_NAME).getName())) {
            if (attributeElement.label.length() > 0) {
               wordMl.addParagraph(attributeElement.label);
            }

            Object value = attribute.getValue();
            if (value != null && value instanceof String) {
               String data = (String) value;
               String wordContent = WordUtil.stripSpellCheck(data);//TODO what is the best way to get at unknown attribute types? (because this isn't it)
               //Change the BinData Id so images do not get overriden by the other images
               wordContent = WordUtil.reassignBinDataID(wordContent);

               if (isEditMode) {
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

   public static void writeXMLMetaDataWrapper(WordMLProducer wordMl, String name, String guid, String attributeId, String contentString) {
      try {
         wordMl.addWordMl("<ns0:" + name + " xmlns:ns0=\"" + WordRenderer.ARTIFACT_SCHEMA + "\" " + guid + " " + attributeId + ">");
         wordMl.addWordMl(contentString);
         wordMl.addWordMl("</ns0:" + name + ">");
      } catch (IOException ex) {
         throw new RuntimeException(ex);
      }
   }

   /**
    * Returns the set of keys necessary for the template
    */
   @SuppressWarnings("unchecked")
   public Set<String> getTemplateKeys() {
      Set<String> keySet;
      keySet = new HashSet<String>();

      Matcher matcher = setNamePattern.matcher(masterTemplate);
      while (matcher.find()) {
         String key = WordUtil.textOnly(matcher.group(2));
         if (!keySet.add(key)) {
            logger.log(Level.WARNING, "The Set_Name " + key + " appears in template more than once");
         }
      }

      return keySet;
   }

   private String getArtifactSetXml(String artifactElement) {
      artifactElement = artifactElement.replaceAll("<(\\w+:)?Artifact/?>", "");
      artifactElement = artifactElement.replaceAll("<(\\w+:)?Set_Name>.*?</(\\w+:)?Set_Name>", "");

      return artifactElement;
   }

   private void extractSkynetAttributeReferences(String artifactElementTemplate) {
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
               logger.log(Level.WARNING, "Unexpected element read in Attribute:" + elementType);
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

   /**
    * @return Returns the slaveTemplate.
    */
   public String getSlaveTemplate() {
      return slaveTemplate;
   }

   private void loadIgnoreAttributeExtensions() throws CoreException {
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

   /**
    * @param saveParagraphNumOnArtifact the saveParagraphNumOnArtifact to set
    */
   public void setSaveParagraphNumOnArtifact(boolean saveParagraphNumOnArtifact) {
      this.saveParagraphNumOnArtifact = saveParagraphNumOnArtifact;
   }
}