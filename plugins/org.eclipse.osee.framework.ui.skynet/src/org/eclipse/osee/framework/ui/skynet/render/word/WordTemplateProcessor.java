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
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PREVIEW;
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
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.report.api.DataRightInput;
import org.eclipse.osee.define.report.api.DataRightResult;
import org.eclipse.osee.define.report.api.PageOrientation;
import org.eclipse.osee.define.report.api.ReportConstants;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.render.DataRightProvider;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
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

   public static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";

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
   public static final String ATTRIBUTE_NAME = "AttributeName";
   public static final String ALL_ATTRIBUTES = "AllAttributes";
   public static final String RECURSE_ON_LOAD = "Recurse On Load";
   public static final String RECURSE_CHILDREN = "RecurseChildren";
   public static final String PUBLISH_AS_DIFF = "Publish As Diff";

   private String slaveTemplate;
   private boolean outlining;
   private boolean recurseChildren;
   private String outlineNumber;
   private IAttributeType headingAttributeType;
   private final List<AttributeElement> attributeElements = new LinkedList<AttributeElement>();
   final List<Artifact> nonTemplateArtifacts = new LinkedList<Artifact>();
   private final Set<String> ignoreAttributeExtensions = new HashSet<String>();
   private final Set<Artifact> processedArtifacts = new HashSet<Artifact>();
   private final WordTemplateRenderer renderer;
   private boolean isDiff;
   private boolean excludeFolders;
   private CharSequence paragraphNumber = null;
   private final DataRightInput request;

   private final DataRightProvider provider;

   public WordTemplateProcessor(WordTemplateRenderer renderer, DataRightProvider provider) {
      this.renderer = renderer;
      this.provider = provider;
      loadIgnoreAttributeExtensions();
      request = provider.createRequest();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    */
   public void publishWithExtensionTemplates(Artifact masterTemplateArtifact, Artifact slaveTemplateArtifact, List<Artifact> artifacts) throws OseeCoreException {
      String masterTemplate = masterTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
      slaveTemplate = "";
      isDiff = renderer.getBooleanOption(PUBLISH_AS_DIFF);
      renderer.setOption(ITemplateRenderer.TEMPLATE_OPTION, masterTemplateArtifact);

      if (slaveTemplateArtifact != null) {
         renderer.setOption(ITemplateRenderer.TEMPLATE_OPTION, slaveTemplateArtifact);
         slaveTemplate = slaveTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
      }
      // Process the settings from the template at the beginning
      Matcher matcher = headElementsPattern.matcher(masterTemplate);
      matcher.find();
      String artifactElement = matcher.group(4);
      artifactElement = artifactElement.replaceAll("<(\\w+:)?Artifact/?>", "");
      artifactElement = artifactElement.replaceAll("<(\\w+:)?Set_Name>.*?</(\\w+:)?Set_Name>", "");
      extractSkynetAttributeReferences(artifactElement);

      // Need to check if all attributes will be published.  If so set the AllAttributes option.
      // Assumes that all (*) will not be used when other attributes are specified
      renderer.setOption(ALL_ATTRIBUTES, false);
      if (attributeElements.size() == 1) {
         String attributeName = attributeElements.get(0).getAttributeName();
         if (attributeName.equals("*")) {
            renderer.setOption(ALL_ATTRIBUTES, true);
         }
      }
      IFile file =
         RenderingUtil.getRenderFile(renderer, COMMON, PREVIEW, "/", masterTemplateArtifact.getSafeName(), ".xml");
      AIFile.writeToFile(file, applyTemplate(artifacts, masterTemplate, file.getParent(), null, null, PREVIEW));

      if (!renderer.getBooleanOption(IRenderer.NO_DISPLAY) && !isDiff) {
         RenderingUtil.ensureFilenameLimit(file);
         wordApp.execute(file.getLocation().toFile().getAbsolutePath());
      }
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    * 
    * @param artifacts = null if the template defines the artifacts to be used in the publishing
    * @param folder = null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    */
   public InputStream applyTemplate(List<Artifact> artifacts, String template, IContainer folder, String outlineNumber, String outlineType, PresentationType presentationType) throws OseeCoreException {
      excludeFolders = renderer.getBooleanOption("Exclude Folders");
      WordMLProducer wordMl = null;
      CharBackedInputStream charBak = null;

      try {
         charBak = new CharBackedInputStream();
         wordMl = new WordMLProducer(charBak);
      } catch (CharacterCodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }

      template = template.replaceAll(PGNUMTYPE_START_1, "");
      this.outlineNumber =
         outlineNumber == null ? peekAtFirstArtifactToGetParagraphNumber(template, null, artifacts) : outlineNumber;
      template = wordMl.setHeadingNumbers(this.outlineNumber, template, outlineType);
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
            if (artifacts == null) {
               // This handles the case where artifacts selected in the template
               Matcher setNameMatcher = setNamePattern.matcher(elementValue);
               setNameMatcher.find();
               artifacts = renderer.getArtifactsOption(WordUtil.textOnly(setNameMatcher.group(2)));
            }
            if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() == 1) {
               // for single edit override outlining options
               outlining = false;
            }
            processArtifactSet(elementValue, artifacts, wordMl, outlineType, presentationType);
         } else if (elementType.equals(EXTENSION_PROCESSOR)) {
            processExtensionTemplate(elementValue, folder, wordMl, presentationType, template);
         } else {
            throw new OseeArgumentException("Invalid input [%s]", elementType);
         }
      }
      String endOfTemplate = template.substring(lastEndIndex);
      // Write out the last of the template
      wordMl.addWordMl(updateFooter(endOfTemplate));
      displayNonTemplateArtifacts(nonTemplateArtifacts,
         "Only artifacts of type Word Template Content are supported in this case.");
      return charBak;
   }

   private String updateFooter(String endOfTemplate) {
      // footer cleanup
      endOfTemplate = endOfTemplate.replaceAll(ReportConstants.FTR, "");
      endOfTemplate =
         endOfTemplate.replaceFirst(ReportConstants.PAGE_SZ, ReportConstants.CONTINUOUS + ReportConstants.PG_SZ);
      return endOfTemplate;
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<Artifact> artifacts) throws OseeCoreException {
      String startParagraphNumber = "1";
      if (artifacts != null) {
         Matcher matcher = headElementsPattern.matcher(template);

         if (matcher.find()) {
            String elementType = matcher.group(3);

            if (elementType.equals(ARTIFACT) && !artifacts.isEmpty()) {
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
      return startParagraphNumber;
   }

   private void processArtifactSet(String artifactElement, List<Artifact> artifacts, WordMLProducer wordMl, String outlineType, PresentationType presentationType) throws OseeCoreException {
      nonTemplateArtifacts.clear();
      if (Strings.isValid(outlineNumber)) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }
      // Don't extract the settings from the template if already done.
      if (attributeElements.isEmpty()) {
         extractSkynetAttributeReferences(getArtifactSetXml(artifactElement));
      }

      if (renderer.getBooleanOption(PUBLISH_AS_DIFF)) {
         WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
         templateFileDiffer.generateFileDifferences(artifacts, "/results/", outlineNumber, outlineType, recurseChildren);
      } else {
         populateRequest(artifacts, request);
         DataRightResult response = provider.getDataRights(request);

         for (Artifact artifact : artifacts) {
            processObjectArtifact(artifact, wordMl, outlineType, presentationType, response);

         }
      }
      // maintain a list of artifacts that have been processed so we do not
      // have duplicates.
      request.clear();
      processedArtifacts.clear();
   }

   private void processExtensionTemplate(String elementValue, IContainer folder, WordMLProducer wordMl, PresentationType presentationType, String template) throws OseeCoreException {
      String subdocumentName = null;
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
      }

      matcher = argumentElementsPattern.matcher(elementValue);

      while (matcher.find()) {
         matcher = keyValueElementsPattern.matcher(matcher.group(4));

         String key = null;
         while (matcher.find()) {
            String type = WordUtil.textOnly(matcher.group(3));

            if (type.equalsIgnoreCase(KEY)) {
               key = WordUtil.textOnly(matcher.group(4));
            } else {
               String value = WordUtil.textOnly(matcher.group(4));
               renderer.setOption(key, value);
            }
         }
      }

      String artifactName = renderer.getStringOption("Name");
      String artifactId = renderer.getStringOption("Id");
      Branch branch = renderer.getBranchOption("Branch");
      List<Artifact> artifacts = null;

      if (Strings.isValid(artifactId)) {
         artifacts =
            ArtifactQuery.getArtifactListFromIds(Arrays.asList(Integer.valueOf(artifactId)), branch, EXCLUDE_DELETED);
      } else if (Strings.isValid(artifactName)) {
         artifacts = ArtifactQuery.getArtifactListFromName(artifactName, branch, EXCLUDE_DELETED);
      }

      String subDocFileName = subdocumentName + ".xml";

      if (isDiff) {
         WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
         templateFileDiffer.generateFileDifferences(artifacts, "/results/" + subDocFileName, nextParagraphNumber,
            outlineType, recurseChildren);
      } else {
         IFile file = folder.getFile(new Path(subDocFileName));
         AIFile.writeToFile(file,
            applyTemplate(artifacts, slaveTemplate, folder, nextParagraphNumber, outlineType, presentationType));
      }
      wordMl.createHyperLinkDoc(subDocFileName);
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
            } else if (elementType.equals(RECURSE_CHILDREN)) {
               recurseChildren = renderer.getBooleanOption(RECURSE_CHILDREN);

               if (!recurseChildren) {
                  recurseChildren = Boolean.parseBoolean(value);
               }
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

   private void processObjectArtifact(Artifact artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, DataRightResult data) throws OseeCoreException {
      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent) && !artifact.isAttributeTypeValid(CoreAttributeTypes.NativeContent)) {
         // If the artifact has not been processed
         if (!processedArtifacts.contains(artifact)) {

            boolean ignoreArtifact = excludeFolders && artifact.isOfType(CoreArtifactTypes.Folder);
            boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);
            boolean startedSection = false;
            boolean templateOnly = renderer.getBooleanOption("TEMPLATE ONLY");
            boolean includeUUIDs = renderer.getBooleanOption("INCLUDE UUIDS");

            if (!ignoreArtifact) {
               if (outlining && !templateOnly) {
                  String headingText = artifact.getSoleAttributeValue(headingAttributeType, "");

                  if (includeUUIDs) {
                     String UUIDtext = String.format(" <UUID = %s>", artifact.getArtId());
                     headingText = headingText.concat(UUIDtext);
                  }

                  Boolean mergeTag = (Boolean) renderer.getOption(ITemplateRenderer.ADD_MERGE_TAG);
                  if (mergeTag != null && mergeTag) {
                     headingText = headingText.concat(" [MERGED]");
                  }

                  if (!publishInline && !templateOnly) {
                     paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);
                     startedSection = true;
                  }

                  if (paragraphNumber == null) {
                     paragraphNumber = wordMl.startOutlineSubSection();
                     startedSection = true;
                  }

                  if (renderer.getBooleanOption(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION) && !publishInline) {
                     if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                        artifact.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, paragraphNumber.toString());

                        SkynetTransaction transaction =
                           (SkynetTransaction) renderer.getOption(ITemplateRenderer.TRANSACTION_OPTION);
                        if (transaction != null) {
                           artifact.persist(transaction);
                        } else {
                           artifact.persist(getClass().getSimpleName());
                        }
                     }
                  }
               }
               PageOrientation orientation = WordRendererUtil.getPageOrientation(artifact);

               String footer = data.getContent(artifact.getGuid(), orientation);
               processAttributes(artifact, wordMl, presentationType, publishInline, footer);

            }
            // Check for option that may have been set from Publish with Diff BLAM to recurse
            if ((recurseChildren && !renderer.getBooleanOption(RECURSE_ON_LOAD)) || (renderer.getBooleanOption(RECURSE_ON_LOAD) && !renderer.getBooleanOption("Orig Publish As Diff"))) {
               for (Artifact childArtifact : artifact.getChildren()) {
                  processObjectArtifact(childArtifact, wordMl, outlineType, presentationType, data);
               }
            }

            if (startedSection) {
               wordMl.endOutlineSubSection();
            }
            processedArtifacts.add(artifact);
         }
      } else {
         nonTemplateArtifacts.add(artifact);
      }
   }

   private void populateRequest(List<Artifact> artifacts, DataRightInput request) {
      boolean isDataRightsPresent = false;
      if (request.isEmpty()) {
         List<Artifact> allArtifacts = new ArrayList<Artifact>();
         if (recurseChildren) {
            for (Artifact art : artifacts) {
               allArtifacts.add(art);
               if (!art.isHistorical()) {
                  allArtifacts.addAll(art.getDescendants());
               }
            }
         } else {
            allArtifacts.addAll(artifacts);
         }

         int index = 0;
         for (Artifact artifact : allArtifacts) {
            String classification = WordRendererUtil.getDataRightsClassification(artifact);

            PageOrientation orientation = WordRendererUtil.getPageOrientation(artifact);
            request.addData(artifact.getGuid(), classification, orientation, index);
            index++;

            if (Strings.isValid(classification)) {
               isDataRightsPresent = true;
            }
         }
      }

      if (!isDataRightsPresent) {
         request.clear();
      }
   }

   private void processAttributes(Artifact artifact, WordMLProducer wordMl, PresentationType presentationType, boolean publishInLine, String footer) throws OseeCoreException {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (renderer.getBooleanOption(ALL_ATTRIBUTES) || (attributeName.equals("*"))) {
            for (IAttributeType attributeType : RendererManager.getAttributeTypeOrderList(artifact)) {
               if (!outlining || !attributeType.equals(headingAttributeType)) {
                  processAttribute(artifact, wordMl, attributeElement, attributeType, true, presentationType,
                     publishInLine, footer);
               }
            }
         } else {
            AttributeType attributeType = AttributeTypeManager.getType(attributeName);

            if (artifact.isAttributeTypeValid(attributeType)) {
               processAttribute(artifact, wordMl, attributeElement, attributeType, false, presentationType,
                  publishInLine, footer);
            }
         }
      }
   }

   private void processAttribute(Artifact artifact, WordMLProducer wordMl, AttributeElement attributeElement, IAttributeType attributeType, boolean allAttrs, PresentationType presentationType, boolean publishInLine, String footer) throws OseeCoreException {
      renderer.setOption("allAttrs", allAttrs);
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
      boolean templateOnly = renderer.getBooleanOption("TEMPLATE ONLY");
      if (templateOnly && !attributeType.equals(WordTemplateContent)) {
         return;
      }

      // Create a wordTemplateContent for new guys when opening them for edit.
      if (attributeType.equals(WordTemplateContent) && presentationType == PresentationType.SPECIALIZED_EDIT) {
         artifact.getOrInitializeSoleAttributeValue(attributeType);
      }

      /**
       * In some cases this returns no attributes at all, including no wordTemplateContent, even though it exists This
       * happens when wordTemplateContent is blank, so the else if condition takes this into account.
       */
      Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeType);

      if (!attributes.isEmpty()) {
         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         // Do not publish relation order during publishing
         if (renderer.getBooleanOption("inPublishMode") && CoreAttributeTypes.RelationOrder.equals(attributeType)) {
            return;
         }

         if (!(publishInLine && artifact.isAttributeTypeValid(WordTemplateContent)) || attributeType.equals(WordTemplateContent)) {
            RendererManager.renderAttribute(attributeType, presentationType, artifact, wordMl, attributeElement,
               footer, renderer.getValues());
         }
      } else if (attributeType.equals(WordTemplateContent)) {
         RendererManager.renderAttribute(attributeType, presentationType, artifact, wordMl, attributeElement, footer,
            renderer.getValues());
      }
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
