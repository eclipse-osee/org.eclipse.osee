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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

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
   private final List<AttributeElement> attributeElements = new LinkedList<AttributeElement>();
   final List<Artifact> nonTemplateArtifacts = new LinkedList<Artifact>();
   private final Set<String> ignoreAttributeExtensions = new HashSet<String>();
   private final Set<Artifact> processedArtifacts = new HashSet<Artifact>();
   private final IRenderer renderer;

   public WordTemplateProcessor(IRenderer renderer) {
      this.renderer = renderer;
      loadIgnoreAttributeExtensions();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    * 
    * @throws IOException
    */
   public void publishWithExtensionTemplates(VariableMap variableMap, Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) throws OseeCoreException {
      String masterTemplate = masterTemplateArtifact.getSoleAttributeValue(WordAttribute.WHOLE_WORD_CONTENT, "");
      slaveTemplate =
            slaveTemplateArtifact != null ? slaveTemplateArtifact.getSoleAttributeValue(
                  WordAttribute.WHOLE_WORD_CONTENT, "") : "";

      IFolder folder = FileSystemRenderer.ensureRenderFolderExists(PresentationType.PREVIEW);
      String fileName = String.format("%s_%s.xml", masterTemplateArtifact.getSafeName(), Lib.getDateTimeString());
      AIFile.writeToFile(folder.getFile(fileName), applyTemplate(variableMap, artifacts, masterTemplate, folder, null,
            null, PresentationType.PREVIEW));
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    * 
    * @param variableMap = will be filled with artifacts when specified in the template
    * @param artifacts = null if the template defines the artifacts to be used in the publishing
    * @param template
    * @param folder = null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    * @param outlineType
    * @return InputStream
    * @throws OseeCoreException
    */
   public InputStream applyTemplate(VariableMap variableMap, List<Artifact> artifacts, String template, IFolder folder, String outlineNumber, String outlineType, PresentationType presentationType) throws OseeCoreException {
      WordMLProducer wordMl;
      CharBackedInputStream charBak;
      try {
         charBak = new CharBackedInputStream();
         wordMl = new WordMLProducer(charBak);
      } catch (CharacterCodingException ex) {
         throw new OseeWrappedException(ex);
      }

      this.outlineNumber =
            outlineNumber == null ? peekAtFirstArtifactToGetParagraphNumber(template, null, artifacts) : outlineNumber;
      template = wordMl.setHeadingNumbers(this.outlineNumber, template, outlineType);
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
            if (artifacts == null) { //This handles the case where artifacts selected in the template
               Matcher setNameMatcher = setNamePattern.matcher(elementValue);
               setNameMatcher.find();
               artifacts = variableMap.getArtifacts(WordUtil.textOnly(setNameMatcher.group(2)));
            }
            if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() == 1) {
               // for single edit override outlining options
               outlining = false;
            }
            processArtifactSet(variableMap, elementValue, artifacts, wordMl, outlineType, presentationType);
         } else if (elementType.equals(EXTENSION_PROCESSOR)) {
            try {
               processExtensionTemplate(elementValue, variableMap, folder, wordMl, presentationType, template);
            } catch (CoreException ex) {
               throw new OseeWrappedException(ex);
            }
         } else {
            throw new OseeArgumentException("Invalid input: " + elementType);
         }
      }
      // Write out the last of the template
      wordMl.addWordMl(template.substring(lastEndIndex));
      displayNonTemplateArtifacts(nonTemplateArtifacts,
            "Only artifacts of type Word Template Content are supported in this case.");
      return charBak;
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<Artifact> artifacts) throws OseeCoreException {
      String startParagraphNumber = "1";
      if (artifacts != null) {
         Matcher matcher = headElementsPattern.matcher(template);

         if (matcher.find()) {
            String elementType = matcher.group(3);

            if (elementType.equals(ARTIFACT)) {
               if (!artifacts.isEmpty()) {
                  Artifact artifact = artifacts.iterator().next();
                  if (artifact.isAttributeTypeValid(CoreAttributeTypes.PARAGRAPH_NUMBER)) {
                     String paragraphNum = artifact.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, "");
                     if (paragraphNum != null && !paragraphNum.equals("")) {
                        startParagraphNumber = paragraphNum;
                     }
                  }
               }
            }
         }
      }
      return startParagraphNumber;
   }

   private void processArtifactSet(VariableMap variableMap, final String artifactElement, final List<Artifact> artifacts, final WordMLProducer wordMl, final String outlineType, PresentationType presentationType) throws OseeCoreException {
      nonTemplateArtifacts.clear();
      if (outlineNumber != null) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }

      extractSkynetAttributeReferences(getArtifactSetXml(artifactElement));

      for (Artifact artifact : artifacts) {
         processObjectArtifact(variableMap, artifact, wordMl, outlineType, presentationType, artifacts.size() > 1);
      }
      //maintain a list of artifacts that have been processed so we do not have duplicates.
      processedArtifacts.clear();
   }

   /**
    * @throws CoreException
    */
   private void processExtensionTemplate(String elementValue, VariableMap variableMap, IFolder folder, WordMLProducer wordMl, PresentationType presentationType, String template) throws OseeCoreException, CoreException {
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

      matcher = subDocElementsPattern.matcher(elementValue);

      if (matcher.find()) {
         subdocumentName = WordUtil.textOnly(matcher.group(4));
         doSubDocuments = true;
      }

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
         populateVariableMap(newVariableMap);
         AIFile.writeToFile(folder.getFile(subDocFileName), applyTemplate(newVariableMap, null, slaveTemplate, folder,
               nextParagraphNumber, outlineType, presentationType));

         wordMl.createHyperLinkDoc(subDocFileName);
      } else {
         populateVariableMap(variableMap);
      }
   }

   public void populateVariableMap(VariableMap variableMap) throws OseeCoreException {
      if (variableMap == null) {
         throw new IllegalArgumentException("variableMap must not be null");
      }

      String name = variableMap.getString("Name");
      Branch branch = variableMap.getBranch("Branch");

      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(name, branch, false);

      variableMap.setValue("srsProducer.objects", artifacts);
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

   private void processObjectArtifact(VariableMap variableMap, Artifact artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.WHOLE_WORD_CONTENT.getName()) && !artifact.isAttributeTypeValid(CoreAttributeTypes.NATIVE_CONTENT.getName())) {
         //If the artifact has not been processed
         if (!processedArtifacts.contains(artifact)) {
            if (outlining) {
               String headingText = artifact.getSoleAttributeValue(headingAttributeName, "");
               CharSequence paragraphNumber =
                     wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);

               VariableMap options = renderer.getOptions();
               if (renderer.getBooleanOption(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION)) {
                  if (artifact.isAttributeTypeValid(CoreAttributeTypes.PARAGRAPH_NUMBER)) {
                     artifact.setSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, paragraphNumber.toString());
                     artifact.persist((SkynetTransaction) options.getValue(ITemplateRenderer.TRANSACTION_OPTION));
                  }
               }
            }
            processAttributes(variableMap, artifact, wordMl, presentationType, multipleArtifacts);
            if (recurseChildren) {
               for (Artifact childArtifact : artifact.getChildren()) {
                  processObjectArtifact(variableMap, childArtifact, wordMl, outlineType, presentationType,
                        multipleArtifacts);
               }
            }
            if (outlining) {
               wordMl.endOutlineSubSection();
            }
            processedArtifacts.add(artifact);
         }
      } else {
         nonTemplateArtifacts.add(artifact);
      }
   }

   private void processAttributes(VariableMap variableMap, Artifact artifact, WordMLProducer wordMl, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (attributeElement.getAttributeName().equals("*")) {
            for (String attributeTypeName : orderAttributeNames(artifact.getAttributeTypes())) {
               if (!outlining || !attributeTypeName.equals(headingAttributeName)) {
                  processAttribute(variableMap, artifact, wordMl, attributeElement, attributeTypeName, true,
                        presentationType, multipleArtifacts);
               }
            }
         } else {

            if (artifact.isAttributeTypeValid(attributeName)) {
               processAttribute(variableMap, artifact, wordMl, attributeElement, attributeName, false,
                     presentationType, multipleArtifacts);
            }

         }
      }

      wordMl.setPageLayout(artifact);
   }

   private void processAttribute(VariableMap variableMap, Artifact artifact, WordMLProducer wordMl, AttributeElement attributeElement, String attributeTypeName, boolean allAttrs, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      // This is for SRS Publishing. Do not publish unspecified attributes
      if (!allAttrs && (attributeTypeName.equals(Requirements.PARTITION) || attributeTypeName.equals("Safety Criticality"))) {
         if (artifact.isAttributeTypeValid(Requirements.PARTITION)) {
            for (Attribute<?> partition : artifact.getAttributes(Requirements.PARTITION)) {
               if (partition == null || partition.getValue() == null || partition.getValue().equals("Unspecified")) {
                  return;
               }
            }
         }
      }

      if (attributeTypeName.equals("TIS Traceability")) {
         for (Artifact requirement : artifact.getRelatedArtifacts(CoreRelationTypes.Verification__Requirement)) {
            wordMl.addParagraph(requirement.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER) + "\t" + requirement.getName());
         }
         return;
      }

      attributeTypeName = AttributeTypeManager.getType(attributeTypeName).getName();

      //create wordTemplateContent for new guys
      if (attributeTypeName.equals(WordAttribute.WORD_TEMPLATE_CONTENT)) {
         artifact.getOrInitializeSoleAttributeValue(attributeTypeName);
      }

      Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeTypeName);

      if (!attributes.isEmpty()) {
         Attribute<Object> attribute = attributes.iterator().next();
         AttributeType attributeType = attribute.getAttributeType();

         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }
         variableMap = ensureMapIsSetForDocLinks(variableMap, allAttrs);

         Boolean isInPublishMode =
               variableMap != null ? Boolean.TRUE.equals(variableMap.getBoolean("inPublishMode")) : false;
         if (variableMap != null && isInPublishMode) {
            // Do not publish relation order during publishing
            if (CoreAttributeTypes.RELATION_ORDER.equals(attributeType.getName())) {
               return;
            }
         }

         RendererManager.renderAttribute(attributeTypeName, presentationType, artifact, variableMap, wordMl,
               attributeElement);
      }
   }

   private VariableMap ensureMapIsSetForDocLinks(VariableMap variableMap, boolean allAttrs) throws OseeArgumentException {
      //Do not try to use a null map
      VariableMap theMap = variableMap;
      if (theMap == null) {
         theMap = new VariableMap();
      }
      //If someone else set the link leave it set else set it to OSEE server link
      if (theMap.getValue("linkType") == null) {
         theMap.setValue("linkType", LinkType.OSEE_SERVER_LINK);
      }
      //set all attrs
      theMap.setValue("allAttrs", allAttrs);

      return theMap;
   }

   public static String elementNameFor(String artifactName) {
      // Since artifact names are free text it is important to reformat the name
      // to ensure it is suitable as an element name
      // NOTE: The current program.launch has a tokenizing bug that causes an error if consecutive
      // spaces are in the name
      String elementName = artifactName.trim().replaceAll("[^A-Za-z0-9]", "_");

      // Ensure the name did not end up empty
      if (elementName.equals("")) {
         elementName = "nameless";
      }

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

   private void displayNonTemplateArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            public void run() {
               ArrayList<Artifact> nonTempArtifacts = new ArrayList<Artifact>(artifacts.size());
               nonTempArtifacts.addAll(artifacts);
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }
}