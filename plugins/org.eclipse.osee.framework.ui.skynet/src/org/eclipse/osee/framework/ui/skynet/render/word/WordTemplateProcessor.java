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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.io.InputStream;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.linking.LinkType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.util.FileUiUtil;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @link WordTemplateProcessorTest
 */
public class WordTemplateProcessor {
   private static final String ARTIFACT = "Artifact";
   private static final String EXTENSION_PROCESSOR = "Extension_Processor";
   private static final String KEY = "Key";

   private static final Pattern outlineTypePattern = Pattern.compile("<((\\w+:)?(OutlineType))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern outlineNumberPattern = Pattern.compile("<((\\w+:)?(Number))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern argumentElementsPattern = Pattern.compile("<((\\w+:)?(Argument))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern keyValueElementsPattern = Pattern.compile("<((\\w+:)?(Key|Value))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern subDocElementsPattern = Pattern.compile("<((\\w+:)?(SubDoc))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern setNamePattern = Pattern.compile("<(\\w+:)?Set_Name>(.*?)</(\\w+:)?Set_Name>",
      Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern headElementsPattern = Pattern.compile(
      "<((\\w+:)?(" + ARTIFACT + "|" + EXTENSION_PROCESSOR + "))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern attributeElementsPattern = Pattern.compile("<((\\w+:)?(Attribute))>(.*?)</\\3>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern outlineElementsPattern = Pattern.compile("<((\\w+:)?(Outline))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern internalOutlineElementsPattern = Pattern.compile(
      "<((\\w+:)?(HeadingAttribute|RecurseChildren|Number))>(.*?)</\\1>",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Program wordApp = Program.findProgram("doc");

   private String slaveTemplate;
   private String slaveTemplateName;
   private boolean outlining;
   private boolean recurseChildren;
   private String outlineNumber;
   private IAttributeType headingAttributeType;
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
    */
   public void publishWithExtensionTemplates(VariableMap variableMap, Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) throws OseeCoreException {
      String masterTemplate = masterTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
      slaveTemplateName = "";
      slaveTemplate = "";

      if (slaveTemplateArtifact != null) {
         slaveTemplateName = slaveTemplateArtifact.getSafeName();
         slaveTemplate = slaveTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
      }

      IFolder folder = RenderingUtil.ensureRenderFolderExists(PresentationType.PREVIEW);
      String fileName = String.format("%s_%s.xml", masterTemplateArtifact.getSafeName(), Lib.getDateTimeString());
      IFile file = folder.getFile(fileName);
      AIFile.writeToFile(file,
         applyTemplate(variableMap, artifacts, masterTemplate, folder, null, null, PresentationType.PREVIEW));

      if (variableMap.getBoolean("OpenDocument") && file != null) {
         FileUiUtil.ensureFilenameLimit(file);
         wordApp.execute(file.getLocation().toFile().getAbsolutePath());
      }
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    * 
    * @param variableMap = will be filled with artifacts when specified in the template
    * @param artifacts = null if the template defines the artifacts to be used in the publishing
    * @param folder = null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    */
   public InputStream applyTemplate(VariableMap variableMap, List<Artifact> artifacts, String template, IFolder folder, String outlineNumber, String outlineType, PresentationType presentationType) throws OseeCoreException {
      WordMLProducer wordMl = null;
      CharBackedInputStream charBak = null;
      try {
         charBak = new CharBackedInputStream();
         wordMl = new WordMLProducer(charBak);
      } catch (CharacterCodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
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
               OseeExceptions.wrapAndThrow(ex);
            }
         } else {
            throw new OseeArgumentException("Invalid input [%s]", elementType);
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
                  if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                     String paragraphNum = artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
                     if (Strings.isValid(paragraphNum)) {
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
      if (Strings.isValid(outlineNumber)) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }

      extractSkynetAttributeReferences(getArtifactSetXml(artifactElement));

      for (Artifact artifact : artifacts) {
         processObjectArtifact(variableMap, artifact, wordMl, outlineType, presentationType, artifacts.size() > 1);
      }

      //maintain a list of artifacts that have been processed so we do not have duplicates.
      processedArtifacts.clear();
   }

   private void processExtensionTemplate(String elementValue, VariableMap variableMap, IFolder folder, WordMLProducer wordMl, PresentationType presentationType, String template) throws OseeCoreException, CoreException {
      String subdocumentName = null;
      boolean doSubDocuments = false;
      String nextParagraphNumber = null;
      String outlineType = null;
      boolean diff = variableMap.getBoolean("Publish As Diff");

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
         newVariableMap.setValue("linkType", variableMap.getValue("linkType"));

         if (diff) {
            generateDocumentDiff(newVariableMap, variableMap, subDocFileName, nextParagraphNumber, outlineType);
         } else {
            AIFile.writeToFile(
               folder.getFile(subDocFileName),
               applyTemplate(newVariableMap, null, slaveTemplate, folder, nextParagraphNumber, outlineType,
                  presentationType));
         }
         wordMl.createHyperLinkDoc(subDocFileName);

      } else {
         populateVariableMap(variableMap);
      }
   }

   private void generateDocumentDiff(VariableMap newVariableMap, VariableMap variableMap, String fileName, String nextParagraphNumber, String outlineType) throws OseeCoreException {
      newVariableMap.setValue("artifacts", newVariableMap.getArtifacts("srsProducer.objects"));
      newVariableMap.setValue("Diff from Baseline", variableMap.getBoolean("Diff from Baseline"));
      newVariableMap.setValue("compareBranch", variableMap.getBranch("compareBranch"));
      newVariableMap.setValue("template", slaveTemplateName);
      newVariableMap.setValue("linkType", variableMap.getValue("linkType"));
      WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer();
      templateFileDiffer.generateFileDifferences(fileName, newVariableMap, nextParagraphNumber, outlineType);
   }

   public void populateVariableMap(VariableMap variableMap) throws OseeCoreException {
      if (variableMap == null) {
         throw new IllegalArgumentException("variableMap must not be null");
      }

      String name = variableMap.getString("Name");
      Branch branch = variableMap.getBranch("Branch");
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromName(name, branch, EXCLUDE_DELETED);
      variableMap.setValue("srsProducer.objects", artifacts);
   }

   private void extractOutliningOptions(String artifactElement) throws OseeCoreException {
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
               headingAttributeType = AttributeTypeManager.getType(value);
            } else if (elementType.equals("RecurseChildren")) {
               recurseChildren = Boolean.parseBoolean(value);
            } else if (elementType.equals("Number")) {
               outlineNumber = value;
            }
         }
      } else {
         outlining = false;
         recurseChildren = false;
         headingAttributeType = null;
      }
   }

   private void processObjectArtifact(VariableMap variableMap, Artifact artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, boolean multipleArtifacts) throws OseeCoreException {
      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent) && !artifact.isAttributeTypeValid(CoreAttributeTypes.NativeContent)) {
         //If the artifact has not been processed
         if (!processedArtifacts.contains(artifact)) {

            handleLandscapeArtifactSectionBreak(artifact, wordMl, multipleArtifacts);
            boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);

            if (outlining) {
               String headingText = artifact.getSoleAttributeValue(headingAttributeType, "");

               CharSequence paragraphNumber = null;

               if (publishInline) {
                  paragraphNumber = wordMl.startOutlineSubSection();
               } else {
                  paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);
               }

               VariableMap options = renderer.getOptions();
               if (renderer.getBooleanOption(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION)) {
                  if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                     artifact.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, paragraphNumber.toString());
                     artifact.persist((SkynetTransaction) options.getValue(ITemplateRenderer.TRANSACTION_OPTION));
                  }
               }
            }

            processAttributes(variableMap, artifact, wordMl, presentationType, multipleArtifacts, publishInline);

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

   private void handleLandscapeArtifactSectionBreak(Artifact artifact, WordMLProducer wordMl, boolean multipleArtifacts) throws OseeCoreException {
      String pageTypeValue = null;
      //There is no reason to add an additional page break if there is a single artifacts
      if (multipleArtifacts) {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.PageType)) {
            pageTypeValue = artifact.getSoleAttributeValue(CoreAttributeTypes.PageType, "Portrait");
         }
         boolean landscape = pageTypeValue != null && pageTypeValue.equals("Landscape");

         if (landscape) {
            wordMl.setPageBreak();
         }
      }
   }

	private void processAttributes(VariableMap variableMap, Artifact artifact, WordMLProducer wordMl, PresentationType presentationType, boolean multipleArtifacts, boolean publishInLine) throws OseeCoreException {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (attributeName.equals("*")) {
            for (IAttributeType attributeType : RendererManager.getAttributeTypeOrderList(artifact)) {
               if (!outlining || !attributeType.equals(headingAttributeType)) {
                  processAttribute(variableMap, artifact, wordMl, attributeElement, attributeType, true,
                     presentationType, multipleArtifacts, publishInLine);
               }
            }
         } else {
            AttributeType attributeType = AttributeTypeManager.getType(attributeName);
            if (artifact.isAttributeTypeValid(attributeType)) {
               processAttribute(variableMap, artifact, wordMl, attributeElement, attributeType, false,
                  presentationType, multipleArtifacts, publishInLine);
            }

         }
      }
      wordMl.setPageLayout(artifact);
   }

   private void processAttribute(VariableMap variableMap, Artifact artifact, WordMLProducer wordMl, AttributeElement attributeElement, IAttributeType attributeType, boolean allAttrs, PresentationType presentationType, boolean multipleArtifacts, boolean publishInLine) throws OseeCoreException {
      // This is for SRS Publishing. Do not publish unspecified attributes
      if (!allAttrs && (attributeType.equals(CoreAttributeTypes.Partition) || attributeType.equals(CoreAttributeTypes.SafetyCriticality))) {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.Partition)) {
            for (Attribute<?> partition : artifact.getAttributes(CoreAttributeTypes.Partition)) {
               if (partition == null || partition.getValue() == null || partition.getValue().equals("Unspecified")) {
                  return;
               }
            }
         }
      }

      //Create a wordTemplateContent for new guys when opening them for edit.
      if (attributeType.equals(WordTemplateContent) && presentationType == PresentationType.SPECIALIZED_EDIT) {
         artifact.getOrInitializeSoleAttributeValue(attributeType);
      }

      Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeType);

      if (!attributes.isEmpty()) {
         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }
         variableMap = ensureMapIsSetForDocLinks(variableMap, allAttrs);

         Boolean isInPublishMode =
            variableMap != null ? Boolean.TRUE.equals(variableMap.getBoolean("inPublishMode")) : false;
         if (variableMap != null && isInPublishMode) {
            // Do not publish relation order during publishing
            if (CoreAttributeTypes.RelationOrder.equals(attributeType)) {
               return;
            }
         }

         if (!(publishInLine && artifact.isAttributeTypeValid(WordTemplateContent)) || attributeType.equals(WordTemplateContent)) {
            RendererManager.renderAttribute(attributeType, presentationType, artifact, variableMap, wordMl,
               attributeElement);
         }
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

   private void displayNonTemplateArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               ArrayList<Artifact> nonTempArtifacts = new ArrayList<Artifact>(artifacts.size());
               nonTempArtifacts.addAll(artifacts);
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }
}
