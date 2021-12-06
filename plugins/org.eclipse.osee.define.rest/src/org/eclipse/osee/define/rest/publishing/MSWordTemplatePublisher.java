/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.define.rest.publishing;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HeadingMsWord;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.NativeContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParagraphNumber;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PlainTextContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PublishInline;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RelationOrder;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RendererOptions;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordOleData;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.util.ReportConstants.CONTINUOUS;
import static org.eclipse.osee.framework.core.util.ReportConstants.FTR;
import static org.eclipse.osee.framework.core.util.ReportConstants.PAGE_SZ;
import static org.eclipse.osee.framework.core.util.ReportConstants.PG_SZ;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.ArtifactUrlServer;
import org.eclipse.osee.define.api.AttributeAlphabeticalComparator;
import org.eclipse.osee.define.api.AttributeElement;
import org.eclipse.osee.define.api.MetadataElement;
import org.eclipse.osee.define.api.OseeHierarchyComparator;
import org.eclipse.osee.define.api.OseeLinkBuilder;
import org.eclipse.osee.define.api.PublishingArtifactError;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.rest.DataRightsOperationsImpl;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUtilities;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.core.util.WordMLWriter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Branden W. Phillips
 */
public class MSWordTemplatePublisher {

   //Constants
   protected static final String ARTIFACT = "Artifact";
   protected static final Object ARTIFACT_ID = "Artifact Id";
   protected static final String ARTIFACT_TYPE = "Artifact Type";
   protected static final String APPLICABILITY = "Applicability";
   protected static final String INSERT_ARTIFACT_HERE = "INSERT_ARTIFACT_HERE";
   protected static final String INSERT_LINK = "INSERT_LINK_HERE";
   protected static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";
   protected static final String FONT = "Times New Roman";
   protected static final String LANDSCAPE = "Landscape";
   protected static final String CHANGE_TAG = WordUtilities.CHANGE_TAG;
   protected static final OseeLinkBuilder linkBuilder = new OseeLinkBuilder();

   //Patterns
   protected static final Pattern headElementsPattern =
      Pattern.compile("(" + INSERT_ARTIFACT_HERE + ")" + "|" + INSERT_LINK,
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   //Template
   protected String elementType;

   //Outlining Options
   protected AttributeTypeToken headingAttributeType;
   protected boolean outlining;
   protected boolean recurseChildren;
   protected String outlineNumber = "";
   protected boolean includeEmptyHeaders = false;

   //DataRights
   protected DataRightResult response = null;
   protected String overrideClassification;

   //Data Structures
   protected final PublishingOptions publishingOptions;
   protected final List<AttributeElement> attributeElements = new LinkedList<>();
   protected final List<MetadataElement> metadataElements = new LinkedList<>();
   protected final List<ArtifactReadable> nonTemplateArtifacts = new LinkedList<>();
   protected final Set<ArtifactReadable> processedArtifacts = new HashSet<>();
   protected final Set<ArtifactId> emptyFolders = new HashSet<>();
   protected final Map<ArtifactReadable, CharSequence> artParagraphNumbers = new HashMap<>();
   protected final List<ArtifactTypeToken> excludeArtifactTypes = new LinkedList<>();
   protected final Map<ApplicabilityId, Boolean> applicabilityMap = new HashMap<>();
   protected final List<ArtifactReadable> headerArtifacts = new LinkedList<>();
   protected final Map<ArtifactTypeToken, List<ArtifactReadable>> oseeLinkedArtifactMap = new HashMap<>();
   protected List<ArtifactId> changedArtifacts = new LinkedList<>();
   protected final Map<ArtifactId, String> wordContentMap = new HashMap<>();
   protected final Set<String> headerGuids = new HashSet<>();
   protected HashMap<ApplicabilityId, ApplicabilityToken> applicabilityTokens;

   //Error Variables
   protected final List<PublishingArtifactError> errorLog = new LinkedList<>();
   protected Set<String> bookmarkedIds = new HashSet<>();
   protected HashMap<String, ArtifactReadable> hyperlinkedIds = new HashMap<>();

   protected final Writer writer;
   protected final OrcsApi orcsApi;
   protected final AtsApi atsApi;
   protected final Log logger;
   protected final ActivityLog activityLog;
   protected final OrcsTokenService tokenService;
   protected final OseeHierarchyComparator hierarchyComparator;

   public MSWordTemplatePublisher(PublishingOptions publishingOptions, Writer writer, OrcsApi orcsApi, AtsApi atsApi) {
      this.publishingOptions = publishingOptions;
      this.writer = writer;
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.logger = atsApi.getLogger();
      this.activityLog = orcsApi.getActivityLog();
      this.tokenService = orcsApi.tokenService();
      this.hierarchyComparator = new OseeHierarchyComparator(activityLog);
   }

   /**
    * Beginning method of the publishing process. Default version takes in the list of artifact ids to be published, and
    * then the artifact id for the template. This method is where the artifact readable's are gathered, and the template
    * options are set up. If everything is valid, move onto the next step for publishing.
    */
   public void publish(ArtifactId templateArtId, List<ArtifactId> artifactIds) {
      List<ArtifactReadable> artifacts = getSelectedArtifacts(artifactIds);
      String template = setUpTemplateWithOptions(templateArtId);

      if (!artifacts.isEmpty() && Strings.isValid(template) && elementType.equals(ARTIFACT)) {
         applyContentToTemplate(artifacts, template);
      }
   }

   /**
    * Second step of the publishing process. This method is where the WordMLWriter is set up and the word xml starts to
    * be written. The default version changes some elements of the template first. Then is the start of the template up
    * until the marking where the artifact content should be. The artifacts/content is then inserted in the middle via
    * processContent. Finally the rest of the template's word content is placed at the end to finish off the published
    * document.
    */
   protected void applyContentToTemplate(List<ArtifactReadable> artifacts, String templateContent) {
      WordMLWriter wordMl = new WordMLWriter(writer);

      templateContent =
         setUpTemplateContent(wordMl, artifacts.get(0), templateContent, publishingOptions.msWordHeadingDepth);

      int lastEndIndex = 0;
      Matcher matcher = headElementsPattern.matcher(templateContent);
      while (matcher.find()) {
         lastEndIndex = handleStartOfTemplate(wordMl, templateContent, matcher);
         processContent(artifacts, wordMl);
         addErrorLogToWordMl(wordMl);
      }

      handleEndOfTemplate(wordMl, templateContent, lastEndIndex);
   }

   /**
    * Third step of the publishing process, this is where the processed content of the publish is handled in between the
    * beginning and end of the render template. In the default implementation, the artifact hierarchy is processed
    * starting from our head artifact, then any errors are added in their own final section. This section is likely to
    * be overriden by subclasses.
    */
   protected void processContent(List<ArtifactReadable> artifacts, WordMLWriter wordMl) {
      for (ArtifactReadable artifact : artifacts) {
         processArtifact(artifact, wordMl);
      }
   }

   /**
    * This method processes each artifact individually. A running list of processed artifacts is kept so no artifact is
    * processed multiple times. In the default implementation, artifacts are processed in hierarchy order so it traces
    * through each artifacts' child recursively if the option is enabled. Within each artifact, the metadata and
    * attributes are published.
    */
   protected void processArtifact(ArtifactReadable artifact, WordMLWriter wordMl) {
      if (!processedArtifacts.contains(artifact)) {
         boolean startedSection = false;

         if (checkIncluded(artifact)) {
            startedSection = renderArtifact(artifact, wordMl);
         }

         if (recurseChildren) {
            List<ArtifactReadable> children = new LinkedList<>();
            try {
               children = artifact.getChildren();
            } catch (OseeCoreException ex) {
               errorLog.add(new PublishingArtifactError(artifact.getId(), artifact.getName(),
                  artifact.getArtifactType(),
                  "There is an error when finding children for this artifact. Possible Cause: Empty Relation Order Attribute"));
            }
            for (ArtifactReadable childArtifact : children) {
               if (childArtifact != null) {
                  processArtifact(childArtifact, wordMl);
               } else {
                  errorLog.add(new PublishingArtifactError(artifact.getId(), artifact.getName(),
                     artifact.getArtifactType(), "Artifact has an empty child relation"));
               }
            }
         }
         if (startedSection) {
            wordMl.endOutlineSubSection();
         }
         processedArtifacts.add(artifact);
      }
   }

   /**
    * This is the fifth level in the main process of publishing. This is where any processing needed happens once it is
    * determined that the artifact will be included in the publish. In the default implementation, we just check
    * outlining and publishInLine to see whether or not to print the header and start the outlining. Then metadata and
    * attributes are processed. The reason this method returns a boolean is to say whether or not the MS Word section
    * was begun with a header.
    */
   protected boolean renderArtifact(ArtifactReadable artifact, WordMLWriter wordMl) {
      boolean publishInline = artifact.getSoleAttributeValue(PublishInline, false);
      boolean startedSection = false;
      if (outlining && !publishInline) {
         setArtifactOutlining(artifact, wordMl);
         startedSection = true;
      }
      renderMetadata(artifact, wordMl);
      renderAttributes(artifact, wordMl);
      return startedSection;
   }

   //--- Publish Helper Methods ---//

   /**
    * Grabs artifact readable's for the artifact ids passed in at the start.
    */
   protected List<ArtifactReadable> getSelectedArtifacts(List<ArtifactId> artifactIds) {
      List<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory().fromBranch(publishingOptions.branch).andIds(artifactIds).getResults().getList();
      artifacts.sort(hierarchyComparator);
      return artifacts;
   }

   /**
    * Using the template artifact id, gets the template content and its' options. This default method then will go and
    * parse the renderOptions attribute.
    */
   protected String setUpTemplateWithOptions(ArtifactId templateArtId) {
      String template = "", options = "";
      ArtifactReadable templateArtifact;
      if (templateArtId != ArtifactId.SENTINEL) {
         templateArtifact = orcsApi.getQueryFactory().fromBranch(COMMON).andId(templateArtId).getArtifact();
         template = templateArtifact.getSoleAttributeAsString(WholeWordContent, "");
         options = templateArtifact.getSoleAttributeAsString(RendererOptions, "");
         parseRenderOptions(options);
      }
      return template;
   }

   /**
    * This default version of the method goes through each section of the renderOptions, parsing the json and setting
    * the class variables. In the default publish, if element type is not artifact an error should be thrown because
    * then the template is not valid
    */
   protected void parseRenderOptions(String options) {
      setElementType(options);
      if (elementType.equals(ARTIFACT)) {
         parseAttributeOptions(options);
         parseMetadataOptions(options);
         parseOutliningOptions(options);
      }
   }

   protected void setElementType(String templateOptions) {
      try {
         ObjectMapper OM = new ObjectMapper();
         JsonNode jsonObject = OM.readTree(templateOptions);

         elementType = jsonObject.findValue("ElementType").asText();
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected void parseOutliningOptions(String templateOptions) {
      try {
         ObjectMapper objMap = new ObjectMapper();
         JsonNode jsonObject = objMap.readTree(templateOptions);
         JsonNode optionsArray = jsonObject.findValue("OutliningOptions");

         outlining = optionsArray.findValue("Outlining").asBoolean();
         recurseChildren = optionsArray.findValue("RecurseChildren").asBoolean();
         try {
            includeEmptyHeaders = optionsArray.findValue("IncludeEmptyHeaders").asBoolean();
         } catch (Exception ex) {
            // The template file json may not have this defined, default is false
            includeEmptyHeaders = false;
         }
         outlineNumber = optionsArray.findValue("OutlineNumber").asText();
         String headingAttrType = optionsArray.findValue("HeadingAttributeType").asText();
         headingAttributeType = tokenService.getAttributeType(headingAttrType);
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected void parseAttributeOptions(String templateOptions) {
      try {
         attributeElements.clear();

         ObjectMapper objMap = new ObjectMapper();
         JsonNode jsonObject = objMap.readTree(templateOptions);
         JsonNode attributeOptions = jsonObject.findValue("AttributeOptions");
         JsonNode options = null;

         for (int i = 0; i < attributeOptions.size(); i++) {
            options = attributeOptions.get(i);
            String attributeType = options.findValue("AttrType").asText();
            String attributeLabel = options.findValue("Label").asText();
            String formatPre = options.findValue("FormatPre").asText();
            String formatPost = options.findValue("FormatPost").asText();

            AttributeElement attrElement = new AttributeElement();
            boolean typeExists = tokenService.attributeTypeExists(attributeType);
            if (attributeType.equals("*") || typeExists) {
               attrElement.setElements(attributeType, attributeLabel, formatPre, formatPost);
               attributeElements.add(attrElement);
            }
         }
         // Need to check if all attributes will be published.  If so set the AllAttributes option.
         // Assumes that all (*) will not be used when other attributes are specified
         publishingOptions.allAttributes = false;
         if (attributeElements.size() == 1) {
            String attributeName = attributeElements.get(0).getAttributeName();
            if (attributeName.equals("*")) {
               publishingOptions.allAttributes = true;
            }
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected void parseMetadataOptions(String metadataOptions) {
      try {
         ObjectMapper objMap = new ObjectMapper();
         JsonNode jsonObject = objMap.readTree(metadataOptions);

         if (!jsonObject.has("MetadataOptions")) {
            return;
         }

         JsonNode optionsArray = jsonObject.findValue("MetadataOptions");
         JsonNode options = null;
         for (int i = 0; i < optionsArray.size(); i++) {
            options = optionsArray.get(i);

            String metadataType = optionsArray.findValue("Type").asText();
            String metadataFormat = optionsArray.findValue("Format").asText();
            String metadataLabel = optionsArray.findValue("Label").asText();

            MetadataElement metadataElement = new MetadataElement();

            metadataElement.setElements(metadataType, metadataFormat, metadataLabel);
            metadataElements.add(metadataElement);
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   //--- ApplyContentToTemplate Helper Methods ---//

   /**
    * This default version of the method cleans up some pieces of the render template's whole word content, and grabs
    * the paragraph number if needed.
    */
   protected String setUpTemplateContent(WordMLWriter wordMl, ArtifactReadable artifact, String templateContent, int maxOutline) {
      templateContent = templateContent.replaceAll(PGNUMTYPE_START_1, "");
      if (outlineNumber.equals("")) {
         outlineNumber = getParagraphNumber(artifact, templateContent);
      }
      templateContent = wordMl.setHeadingNumbers(outlineNumber, templateContent, null);
      if (maxOutline != 9) {
         wordMl.setMaxOutlineLevel(maxOutline);
      }

      return templateContent;
   }

   protected String setUpTemplateContent(WordMLWriter wordMl, ArtifactReadable artifact, String templateContent) {
      return setUpTemplateContent(wordMl, artifact, templateContent, 9);
   }

   /**
    * If the render template has the "insert_artifact_here" marking, the head artifacts paragraph number attribute is
    * used as the start of the outline number. Otherwise the default is to start at 1
    */
   protected String getParagraphNumber(ArtifactReadable artifact, String templateContent) {
      String startParagraphNumber = "1";
      Matcher matcher = headElementsPattern.matcher(templateContent);

      if (matcher.find()) {
         String elementType = matcher.group(0);

         if (elementType != null && elementType.equals(INSERT_ARTIFACT_HERE)) {
            String paragraphNum = artifact.getSoleAttributeValue(ParagraphNumber, "");
            if (Strings.isValid(paragraphNum)) {
               startParagraphNumber = paragraphNum;
            }

         }
      }

      return startParagraphNumber;
   }

   /**
    * Adds the beginning section of the template to our wordml builder, the end index of the matcher is returned as the
    * lastEndIndex for the default implementation
    */
   protected int handleStartOfTemplate(WordMLWriter wordMl, String templateContent, Matcher matcher) {
      wordMl.addWordMl(templateContent.substring(0, matcher.start()));
      return matcher.end();
   }

   /**
    * For the default version, now that the content has been inserted the rest of the render template's whole word
    * content is appended to the end after the footer is updated
    */
   protected void handleEndOfTemplate(WordMLWriter wordMl, String templateContent, int lastEndIndex) {
      String endOfTemplate = templateContent.substring(lastEndIndex);
      // Write out the last of the template
      wordMl.addWordMl(updateFooter(endOfTemplate));
   }

   /**
    * Cleans up the final section of the template to fix styling
    */
   protected String updateFooter(String endOfTemplate) {
      // footer cleanup
      endOfTemplate = endOfTemplate.replaceAll(FTR, "");
      endOfTemplate = endOfTemplate.replaceFirst(PAGE_SZ, CONTINUOUS + PG_SZ);
      return endOfTemplate;
   }

   //--- ProcessContent Helper Methods ---//

   /**
    * Takes a list of artifacts and loops through finding which headers should not be published. Takes an artifact and
    * loops through its' children. On each set of grandchildren, the method is called recursively.<br/>
    * <br/>
    * case1: Any MS Word Header that has only excluded children is not published<br/>
    * case2: Any MS Word Header that has only excluded children, but has included grandchildren, is published<br/>
    * case3: Non MS Word Header artifacts are still published even if all children/grandchildren are excluded<br/>
    * case4: Any MS Word Header that has no children is not published<br/>
    * case5: Any MS Word Header with only excluded header children, should not be published<br/>
    */
   protected boolean populateEmptyHeaders(List<ArtifactReadable> artifacts) {
      boolean hasIncludedChildren = false;
      boolean includeParent = false;
      List<ArtifactReadable> children = null;
      for (ArtifactReadable artifact : artifacts) {
         children = artifact.getChildren();
         if (!children.isEmpty()) {
            hasIncludedChildren = populateEmptyHeaders(children);
            if (!hasIncludedChildren) {
               if (artifact.isOfType(HeadingMsWord)) {
                  emptyFolders.add(artifact);
               }
            }
         } else if (children.isEmpty() && artifact.isOfType(HeadingMsWord)) {
            emptyFolders.add(artifact);
         }
         if (!excludeArtifactTypes.contains(artifact.getArtifactType()) && !artifact.isOfType(HeadingMsWord)) {
            includeParent = true;
         }
         if (hasIncludedChildren) {
            includeParent = true;
         }
      }
      return includeParent;
   }

   /**
    * This method sets up the DataRightsResponse for the publish. <br/>
    * <br/>
    * DataRightsClassification override comes in as a publishing option string, compare string to all
    * DataRightsClassifications, if they match, set override variable to that classification. This override makes it
    * that the entire published document uses the same data rights footer, regardless of the attribute on artifacts.
    * <br/>
    * <br/>
    * Given the list of artifacts for publish, this loops through and adds any recursive artifacts to also be published
    * (if specified through recurseChildren) and sets all of their data rights with DataRightsOperations.
    */
   protected void setUpDataRights(List<ArtifactReadable> artifacts) {
      overrideClassification = "invalid";
      if (DataRightsClassification.isValid(publishingOptions.overrideDataRights)) {
         overrideClassification = publishingOptions.overrideDataRights;
      }

      List<ArtifactId> allArtifactIds = new ArrayList<>();
      for (ArtifactReadable artifact : artifacts) {
         allArtifactIds.add(artifact);
         if (recurseChildren) {
            allArtifactIds.addAll(artifact.getDescendants());
         }
      }
      DataRightsOperationsImpl dataRightsOps = new DataRightsOperationsImpl(orcsApi);
      response = dataRightsOps.getDataRights(allArtifactIds, publishingOptions.branch, overrideClassification);
   }

   /**
    * Goes through each artifact and reassigns its' paragraph number attribute to the paragraph number calculated on it
    * during this publish.
    */
   protected void updateParagraphNumbers() {
      TransactionBuilder transaction = orcsApi.getTransactionFactory().createTransaction(publishingOptions.branch,
         "Update paragraph number on artifact");
      int count = 0;

      for (Map.Entry<ArtifactReadable, CharSequence> art : artParagraphNumbers.entrySet()) {
         if (art.getKey().isAttributeTypeValid(ParagraphNumber)) {
            transaction.setSoleAttributeValue(art.getKey(), ParagraphNumber, art.getValue());
         }
         if (count++ > 500) {
            transaction.commit();
            count = 0;
         }
      }
      transaction.commit();
      artParagraphNumbers.clear();
   }

   /**
    * Sorts the given artifact list by the OseeHierarchyComparator, logs errors gathered by the comparator.
    */
   protected void sortQueryListByHierarchy(List<ArtifactReadable> artifacts) {
      artifacts.sort(hierarchyComparator);

      for (Map.Entry<ArtifactReadable, String> entry : hierarchyComparator.errors.entrySet()) {
         ArtifactReadable art = entry.getKey();
         String description = entry.getValue();
         errorLog.add(new PublishingArtifactError(art.getId(), art.getName(), art.getArtifactType(), description));
      }
   }

   protected void sortQueryListByAttributeAlphabetical(List<ArtifactReadable> artifacts, AttributeTypeToken attributeToken) {
      try {
         artifacts.sort(new AttributeAlphabeticalComparator(activityLog, attributeToken));
      } catch (Exception ex) {
         String errorMessage = String.format("There was an error when sorting the list on %s by alphabetical order",
            attributeToken.getName());
         errorLog.add(new PublishingArtifactError(-1L, "N/A", ArtifactTypeToken.SENTINEL, errorMessage));
      }
   }

   /**
    * It is optional to set header artifacts to print under. If set, this method is able to take a list of
    * ArtifactReadables and filter out any artifacts that are not descendents/recursvively related to the set headers
    */
   protected List<ArtifactReadable> filterArtifactsNotRecursivelyRelated(List<ArtifactReadable> artifacts) {
      List<ArtifactReadable> artifactUnderHeaders = new LinkedList<>();

      for (ArtifactReadable artifact : artifacts) {
         List<ArtifactReadable> ancestors = artifact.getAncestors();
         for (ArtifactReadable headFolder : headerArtifacts) {
            if (ancestors.contains(headFolder)) {
               artifactUnderHeaders.add(artifact);
            }
         }
      }
      return artifactUnderHeaders;
   }

   /**
    * Given a list of artifacts, this method will loop through and add ancestors and sibling artifacts to the list
    */
   protected List<ArtifactReadable> addContextToArtifactList(List<ArtifactReadable> changedArtifacts) {
      List<ArtifactReadable> artifactsWithContext = new LinkedList<>();
      for (ArtifactReadable artifact : changedArtifacts) {
         if (!artifactsWithContext.contains(artifact)) {
            artifactsWithContext.add(artifact);
         }

         List<ArtifactReadable> ancestors = artifact.getAncestors();
         for (ArtifactReadable ancestor : ancestors) {
            if (!artifactsWithContext.contains(ancestor) && ancestor.notEqual(
               CoreArtifactTokens.DefaultHierarchyRoot)) {
               artifactsWithContext.add(ancestor);
            } else {
               break;
            }
         }

         List<ArtifactReadable> siblings = artifact.getParent().getChildren();
         for (ArtifactReadable sibling : siblings) {
            if (!artifactsWithContext.contains(sibling) && !sibling.isOfType(CoreArtifactTypes.HeadingMsWord)) {
               artifactsWithContext.add(sibling);
            }
         }
      }

      return artifactsWithContext;
   }

   /**
    * Takes a list of artifacts, assumed to be sorted with headers included, and creates a hashmap of each header to a
    * list of the artifacts that are a descendent of that header. Any artifacts before the first header are dropped out
    */
   protected Map<ArtifactReadable, List<ArtifactReadable>> getSortedArtifactsInHeaderMap(List<ArtifactReadable> artifacts) {
      Map<ArtifactReadable, List<ArtifactReadable>> headerMap = new HashMap<>();
      ArtifactReadable lastHeader = null;
      List<ArtifactReadable> artList = new LinkedList<>();

      for (ArtifactReadable art : artifacts) {
         if (headerArtifacts.contains(art)) {
            if (lastHeader == null) {
               lastHeader = art;
               artList = new LinkedList<>();
            } else {
               headerMap.put(lastHeader, artList);
               lastHeader = art;
               artList = new LinkedList<>();
            }
         } else {
            artList.add(art);
         }
      }
      headerMap.put(lastHeader, artList);
      return headerMap;
   }

   //--- ProcessArtifact Helper Methods ---//

   /**
    * Checks to see whether this artifact should be included in the publish or not. Default implementation checks to see
    * if the artifact has/can have valid word template content. Also checks if it's a folder and whether or not the
    * options specify to print folders or not.
    */
   protected boolean checkIncluded(ArtifactReadable artifact) {
      boolean validWordTemplateContent =
         !artifact.isAttributeTypeValid(WholeWordContent) && !artifact.isAttributeTypeValid(NativeContent);
      boolean excludeFolder = publishingOptions.excludeFolders && artifact.isOfType(Folder);

      if (!excludeFolder && checkIsArtifactApplicable(artifact)) {
         if (validWordTemplateContent) {
            return true;
         } else {
            errorLog.add(new PublishingArtifactError(artifact.getId(), artifact.getName(), artifact.getArtifactType(),
               "Only artifacts of type Word Template Content are supported in this case"));
            return false;
         }
      }
      return false;
   }

   /**
    * Method for checking the applicability of an artifact. Current method relies on a map between applicability ids and
    * a true false to the set branch. If the applicability has been processed, the map is relied upon. If not, it is
    * checked to see whether or not it contains a valid view.
    */
   protected boolean checkIsArtifactApplicable(ArtifactReadable artifact) {
      boolean isApplicable = publishingOptions.view.equals(ArtifactId.SENTINEL);
      if (isApplicable) {
         return isApplicable;
      } else {
         ApplicabilityId applicability = artifact.getApplicability();
         if (applicabilityMap.containsKey(applicability)) {
            isApplicable = applicabilityMap.get(applicability);
         } else {
            List<ArtifactId> validViews = orcsApi.getQueryFactory().applicabilityQuery().getBranchViewsForApplicability(
               publishingOptions.branch, applicability);
            if (validViews.contains(publishingOptions.view)) {
               isApplicable = true;
            }
            applicabilityMap.put(applicability, isApplicable);
         }
         return isApplicable;
      }
   }

   /**
    * This method returns the class HashMap variable applicabilityTokens to ensure that the map is loaded once needed.
    * The variable will stay null if this method is never called. This is meant to increase efficiency of applicability
    * checks
    */
   protected Map<ApplicabilityId, ApplicabilityToken> getApplicabilityTokens() {
      if (applicabilityTokens == null) {
         applicabilityTokens = new HashMap<>();
         HashMap<Long, ApplicabilityToken> tokens =
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(publishingOptions.branch);
         for (Map.Entry<Long, ApplicabilityToken> entry : tokens.entrySet()) {
            applicabilityTokens.put(ApplicabilityId.valueOf(entry.getKey()), entry.getValue());
         }
      }

      return applicabilityTokens;
   }

   /**
    * The default footer for artifacts is an empty string. If data rights/orientation are needed in the footer, this
    * method should be overridden to support that.
    */
   protected String getArtifactFooter(ArtifactReadable artifact) {
      return "";
   }

   //--- RenderArtifact Helper Methods ---//

   /**
    * If outlining is enabled, this default method inserts the heading with the paragraph number for the artifact. This
    * will also add a change tag to the heading text if this artifact is included into the populated list of changed
    * artifacts. Also puts the artifact and paragraph number into a hashmap together for potential updating of paragraph
    * numbers
    */
   protected void setArtifactOutlining(ArtifactReadable artifact, WordMLWriter wordMl) {
      AttributeTypeToken attrToken = AttributeTypeToken.valueOf(headingAttributeType.getIdString());
      String headingText = artifact.getSoleAttributeAsString(attrToken, "");
      if (changedArtifacts.contains(artifact)) {
         headingText = CHANGE_TAG.concat(headingText);
      }
      CharSequence paragraphNumber = null;

      paragraphNumber = wordMl.startOutlineSubSection(FONT, headingText, null);
      if (paragraphNumber == null) {
         paragraphNumber = wordMl.startOutlineSubSection();
      }
      if (publishingOptions.updateParagraphNumbers) {
         artParagraphNumbers.put(artifact, paragraphNumber);
      }
   }

   /**
    * Loops through and processes each metadata item that was parsed from earlier when handling the rendererOptions.
    */
   protected void renderMetadata(ArtifactReadable artifact, WordMLWriter wordMl) {
      for (MetadataElement metadataElement : metadataElements) {
         renderMetadata(artifact, wordMl, metadataElement);
      }
   }

   /**
    * Adds the metadata element to the artifact, currently the default implementation ignores applicability
    */
   protected void renderMetadata(ArtifactReadable artifact, WordMLWriter wordMl, MetadataElement element) {
      wordMl.startParagraph();
      String name = element.getType();
      String format = element.getFormat();
      String label = element.getLabel();
      String value = "";
      if (name.equals(APPLICABILITY)) {
         ApplicabilityToken applicToken = getApplicabilityTokens().get(artifact.getApplicability());
         if (applicToken.isValid()) {
            value = applicToken.getName();
         } else {
            wordMl.endParagraph();
            return;
         }
      } else if (name.equals(ARTIFACT_TYPE)) {
         value = artifact.getArtifactType().getName();
      } else if (name.equals(ARTIFACT_ID)) {
         value = artifact.getIdString();
      }
      if (!format.isEmpty() || !label.isEmpty()) {
         if (label.contains(">x<")) {
            wordMl.addWordMl(label.replace(">x<", ">" + Xml.escape(name + ": ").toString() + "<"));
         }
         if (format.contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(value).toString() + "<"));
         }
      } else {
         wordMl.addTextInsideParagraph(name + ": " + value);
      }
      wordMl.endParagraph();
   }

   /**
    * Loops through each attribute element that is to be printed, if * (all attributes), it loops through every valid
    * attribute on that artifact. Also makes sure not to print the headingAttributeType if outlining is enabled.
    * Otherwise it only runs for the specific attribute element. In this default implementation the presentation type is
    * preview.
    */
   protected void renderAttributes(ArtifactReadable artifact, WordMLWriter wordMl) {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();
         if (publishingOptions.allAttributes || attributeName.equals("*")) {
            for (AttributeTypeToken attributeType : getOrderedAttributeTypes(artifact.getValidAttributeTypes())) {
               if (!outlining || attributeType.notEqual(headingAttributeType)) {
                  renderAttribute(artifact, wordMl, attributeElement, attributeType, true, PREVIEW);
               }
            }
         } else {
            AttributeTypeToken attributeType = tokenService.getAttributeType(attributeName);
            if (artifact.isAttributeTypeValid(attributeType)) {
               renderAttribute(artifact, wordMl, attributeElement, attributeType, false, PREVIEW);
            }
         }
      }
   }

   /**
    * Orders the attribute and moves any word/plain text content to the end of the attributes.
    */
   protected List<AttributeTypeToken> getOrderedAttributeTypes(Collection<AttributeTypeToken> attributeTypes) {
      ArrayList<AttributeTypeToken> orderedAttributeTypes = new ArrayList<>(attributeTypes.size());
      AttributeTypeToken contentType = null;

      for (AttributeTypeToken attributeType : attributeTypes) {
         if (attributeType.matches(WholeWordContent, WordTemplateContent, PlainTextContent)) {
            contentType = attributeType;
         } else {
            orderedAttributeTypes.add(attributeType);
         }
      }

      Collections.sort(orderedAttributeTypes);
      if (contentType != null) {
         orderedAttributeTypes.add(contentType);
      }
      return orderedAttributeTypes;
   }

   /**
    * The default implementation does not render word ole data or relation order. This method gets the values for the
    * attributes and calls renderWordTemplateContent if of type Word Template Content, renderAttribute if any other
    * valid attribute
    */
   protected void renderAttribute(ArtifactReadable artifact, WordMLWriter wordMl, AttributeElement attributeElement, AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType) {
      //Do not publish OleData or RelationOrder
      if (attributeType.equals(WordOleData) || attributeType.equals(RelationOrder)) {
         return;
      }

      if (attributeType.equals(WordTemplateContent)) {
         renderWordTemplateContent(attributeType, artifact, presentationType, wordMl, attributeElement.getFormat(),
            attributeElement.getLabel());
      } else if (artifact.isAttributeTypeValid(attributeType)) {
         String attrValues = artifact.getAttributeValuesAsString(attributeType);
         if (!attrValues.isEmpty()) {
            renderSpecifiedAttribute(attributeType, attrValues, presentationType, wordMl, attributeElement.getFormat(),
               attributeElement.getLabel());
         }
      }
   }

   /**
    * This method is used for populated the artifact to word content map. This processes an individual artifact and
    * calls getWordTemplateContentData to have those results put in the map. This is useful for pre-processing word data
    * and storing it for later use
    */
   protected void populateArtifactWordContent(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLWriter wordMl, String format, String label) {
      String footer = getArtifactFooter(artifact);
      String data =
         getWordTemplateContentData(attributeType, artifact, presentationType, wordMl, format, label, footer);

      wordContentMap.put(artifact, data);
      if (artifact.isOfType(HeadingMsWord)) {
         headerGuids.add(artifact.getGuid());
      }
   }

   /**
    * This method derives from the WordTemplateRenderer on the client, used to render word template content attribute.
    * Uses WordTemplateContentRendererHandler to render the word ml. Also handles OSEE_Link errors if there are
    * artifacts that are linking to artifacts that aren't included in the publish.
    */
   protected void renderWordTemplateContent(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLWriter wordMl, String format, String label) {
      String footer = getArtifactFooter(artifact);

      String data =
         getWordTemplateContentData(attributeType, artifact, presentationType, wordMl, format, label, footer);

      if (data != null) {
         wordMl.addWordMl(data);
      } else if (footer != null) {
         wordMl.addWordMl(footer);
      }

      if (data != null && WordCoreUtil.containsLists(data)) {
         wordMl.resetListValue();
      }
   }

   protected String getWordTemplateContentData(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLWriter producer, String format, String label, String footer) {
      WordMLWriter wordMl = producer;
      String data = null;

      LinkType linkType = publishingOptions.linkType;

      if (label.length() > 0) {
         wordMl.addParagraph(label);
      }

      TransactionToken txId = null;
      if (artifact.isHistorical()) {
         txId = orcsApi.getTransactionFactory().getTx(artifact.getTransaction());
      } else {
         txId = TransactionToken.SENTINEL;
      }

      WordTemplateContentData wtcData = new WordTemplateContentData();
      wtcData.setArtId(artifact);
      wtcData.setBranch(artifact.getBranch());
      wtcData.setFooter(footer);
      wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
      wtcData.setLinkType(linkType != null ? linkType.toString() : null);
      wtcData.setTxId(txId);
      wtcData.setPresentationType(presentationType);
      wtcData.setViewId(publishingOptions.view);
      wtcData.setPermanentLinkUrl(new ArtifactUrlServer(orcsApi).getSelectedPermanentLinkUrl());
      wtcData.setArtIsChanged(changedArtifacts.contains(artifact));

      Pair<String, Set<String>> content = null;
      try {
         WordTemplateContentRendererHandler rendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);
         content = rendererHandler.renderWordML(wtcData);
      } catch (Exception ex) {
         errorLog.add(new PublishingArtifactError(artifact.getId(), artifact.getName(), artifact.getArtifactType(),
            ex.toString()));
      }

      if (content != null) {
         data = content.getFirst();
         processLinkErrors(artifact, data, content.getSecond());
      }

      return data;
   }

   /**
    * For non word template content attributes, this method appends the attribute to the WordMLWriter.
    */
   protected void renderSpecifiedAttribute(AttributeTypeToken attributeType, String attrValues, PresentationType presentationType, WordMLWriter producer, String format, String label) {
      WordMLWriter wordMl = producer;

      wordMl.startParagraph();

      if (publishingOptions.allAttributes) {
         if (!attributeType.matches(PlainTextContent)) {
            wordMl.addWordMl("<w:r><w:t> " + Xml.escape(attributeType.getName()) + ": </w:t></w:r>");
         } else {
            wordMl.addWordMl("<w:r><w:t> </w:t></w:r>");
         }
      } else {
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         wordMl.addWordMl(label);
      }

      if (format.contains(">x<")) {
         wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(attrValues).toString() + "<"));
      } else {
         wordMl.addTextInsideParagraph(attrValues);
      }
      wordMl.endParagraph();
   }

   /**
    * Generic method for passing in an AttributeTypeToken and getting the string value from the artifact. Meant for
    * subclasses to overwrite and make changes as needed.
    */
   protected String getAttributeValueAsString(AttributeTypeToken token, ArtifactReadable artifact) {
      return artifact.getAttributeValuesAsString(token);
   }

   //--- Error Handling Methods ---//
   /**
    * Once all of the content has been processed, any errors that have been logged are now appended to the wordml in
    * their own end section.
    */
   protected void addErrorLogToWordMl(WordMLWriter wordMl) {
      addLinkNotInPublishErrors(wordMl);

      if (!errorLog.isEmpty()) {
         wordMl.startErrorLog();
         for (PublishingArtifactError error : errorLog) {
            wordMl.addErrorRow(error.getArtId().toString(), error.getArtName(), error.getArtType().getName(),
               error.getErrorDescription());
         }
         wordMl.endErrorLog();
      }
   }

   /**
    * While processing the Word Template Content of an artifact, this method is used for keeping track of OSEE links
    * inside that content and how it relates to the other artifacts that are in this published document.<br/>
    * BookmarkedIds tracks artifacts that have been seen and book marked in the published, these artifacts are capable
    * of being linked to from other artifacts<br/>
    * HyperlinkedIds tracks the artifacts that have been linked to from another artifact
    */
   protected void processLinkErrors(ArtifactReadable artifact, String data, Set<String> unknownIds) {
      Pattern bookmarkHyperlinkPattern =
         Pattern.compile("(" + WordCoreUtil.OSEE_BOOKMARK_REGEX + ")|(" + WordCoreUtil.OSEE_HYPERLINK_REGEX + ")");
      Matcher match = bookmarkHyperlinkPattern.matcher(data);
      String id = "";

      if (!unknownIds.isEmpty()) {
         String description = String.format(
            "Artifact contains the following unknown GUIDs: %s (Delete or fix OSEE Link from Artifact)", unknownIds);
         errorLog.add(
            new PublishingArtifactError(artifact.getId(), artifact.getName(), artifact.getArtifactType(), description));
      }

      while (match.find()) {
         String bookmarkMatch = match.group(1);
         String hyperlinkMatch = match.group(3);
         if (bookmarkMatch != null) {
            id = match.group(2); // Group 2 is the OSEE id group in OSEE_BOOKMARK_REGEX
            if (!bookmarkedIds.contains(id)) {
               bookmarkedIds.add(id);
            }
         } else if (hyperlinkMatch != null) {
            id = match.group(4); // Group 4 is the OSEE id group in OSEE_HYPERLINK_REGEX
            if (!hyperlinkedIds.containsKey(id)) {
               hyperlinkedIds.put(id, artifact);
            }
         }
      }
   }

   protected void addLinkNotInPublishErrors(WordMLWriter wordMl) {
      if (!hyperlinkedIds.isEmpty()) {
         for (Map.Entry<String, ArtifactReadable> link : hyperlinkedIds.entrySet()) {
            String idString = link.getKey();
            if (!bookmarkedIds.contains(idString)) {
               String description;
               ArtifactReadable artWithLink = link.getValue();
               try {
                  ArtifactReadable linkedArt =
                     orcsApi.getQueryFactory().fromBranch(publishingOptions.branch).andGuid(idString).getArtifact();
                  description = String.format(
                     "Artifact is linking to the following Artifact Id that is not contained in this document: %s (Guid: %s)",
                     linkedArt.getId(), idString);
               } catch (Exception ex) {
                  description = String.format(
                     "Artifact contains the following unknown GUID: %s (Delete or fix OSEE Link from Artifact)",
                     idString);
               }
               errorLog.add(new PublishingArtifactError(artWithLink.getId(), artWithLink.getName(),
                  artWithLink.getArtifactType(), description));
            }
         }
      }
   }

   protected String removeUnusedBookmark(String data) {
      Pattern bookmarkHyperlinkPattern = Pattern.compile(WordCoreUtil.OSEE_BOOKMARK_REGEX);
      Matcher match = bookmarkHyperlinkPattern.matcher(data);
      String id = "";

      while (match.find()) {
         String foundMatch = match.group(0);
         if (Strings.isValid(foundMatch)) {
            id = match.group(1);
            if (!hyperlinkedIds.containsKey(id)) {
               data = data.substring(match.end(0));
            }
         }
      }

      return data;
   }

   /**
    * This method can be used to look through the OSEE hyperlinkIds that have not been found yet in the publish, and
    * populate them into a map given a set of Artifact Types that are of interest. Currently searches using guids.
    */
   protected void populateOseeLinkedArtifacts(ArtifactTypeToken... typeTokens) {
      List<ArtifactReadable> linkedArts = orcsApi.getQueryFactory().fromBranch(publishingOptions.branch).andGuids(
         hyperlinkedIds.keySet()).getResults().getList();

      for (ArtifactReadable artifact : linkedArts) {
         if (artifact.isOfType(typeTokens)) {
            ArtifactTypeToken artifactType = artifact.getArtifactType();
            if (oseeLinkedArtifactMap.containsKey(artifactType)) {
               oseeLinkedArtifactMap.get(artifactType).add(artifact);
            } else {
               List<ArtifactReadable> artList = new LinkedList<>();
               artList.add(artifact);
               oseeLinkedArtifactMap.put(artifactType, artList);
            }
            hyperlinkedIds.remove(artifact.getGuid());
         }
      }
   }

   /**
    * Uses the in place getWordMlBookmark but splits the results into a length 2 string array. The regex used to split
    * the string is a positive lookbehind on a closing tag, this technically retrieves 2 matches but due to the split
    * limit, only the first one will be used. This works assuming the bookmark only has a start and end aml:annotation
    * tag.
    */
   protected String[] getSplitWordMlBookmark(ArtifactReadable artifact) {
      String wordMlBookmark = getWordMlBookmark(artifact);
      return wordMlBookmark.split("(?<=>)", 2);
   }

   /**
    * Creates a new bookmark using a given artifact for use in the document as necessary. This method will handle the
    * bookmark/hyperlink storages to reflect that the given artifact has a bookmark.
    */
   protected String getWordMlBookmark(ArtifactReadable artifact) {
      String bookmark = linkBuilder.getWordMlBookmark(artifact);
      bookmark = WordUtilities.reassignBookMarkID(bookmark);

      String guid = artifact.getGuid();
      bookmarkedIds.add(guid);
      if (hyperlinkedIds.containsKey(guid)) {
         hyperlinkedIds.remove(guid);
      }

      return bookmark;
   }

   protected String addChapterNumToCaption(String data) {
      String[] emptySplitBookmark = {"", ""};
      return addChapterNumToCaptionAndBookmark(data, emptySplitBookmark);
   }

   protected String addChapterNumToCaptionAndBookmark(String data, String[] splitBookmark) {
      Pattern oldCaptionPattern = Pattern.compile(
         "(<w:p(( [^>]*?>)|>))(.*?)<w:fldSimple w:instr=\" SEQ (Figure|Table) \\\\\\* ARABIC \">.*?</w:fldSimple>(.*?)(</w:p[^>]*?>)");
      String newCaptionTemplate =
         "%s%s%s<w:fldSimple w:instr=\" STYLEREF 1 \\s \"><w:r><w:rPr><w:noProof/></w:rPr><w:t> #</w:t></w:r></w:fldSimple><w:r><w:noBreakHyphen/></w:r><w:fldSimple w:instr=\" SEQ %s \\* ARABIC \\s 1 \"><w:r><w:rPr><w:noProof/></w:rPr><w:t> #</w:t></w:r></w:fldSimple>%s<w:r><w:t>%s</w:t></w:r>%s";

      Matcher matcher = oldCaptionPattern.matcher(data);
      int matcherIndex = 0;
      while (matcher.find(matcherIndex)) {
         String paraStart = matcher.group(1);
         String preStyleRefTags = matcher.group(4); // Normally contains figure or table text
         String seqType = matcher.group(5); // Figure or Table
         String captionText = Strings.xmlToText(matcher.group(6));
         captionText = Xml.escape(captionText).toString(); // Re-escaping characters such as &, <, >, and "
         String paraEnd = matcher.group(7);

         String newCaption = String.format(newCaptionTemplate, paraStart, splitBookmark[0], preStyleRefTags, seqType,
            splitBookmark[1], captionText, paraEnd);

         data = data.replace(matcher.group(0), newCaption);
         matcherIndex = matcher.start() + newCaption.length();
         matcher = oldCaptionPattern.matcher(data);
      }

      return data;
   }

   protected String changeHyperlinksToReferences(String data) {
      Pattern internalDocLinkRegex = Pattern.compile(
         "<w:r><w:fldChar w:fldCharType=\"begin\"/>.*?<w:instrText>\\s+HYPERLINK[^<>]+\"OSEE\\.([^\"]*)\"\\s+</w:instrText>.*?<w:fldChar w:fldCharType=\"separate\"/>.*?<w:rStyle w:val=\"Hyperlink\"/>(.*?)<w:fldChar w:fldCharType=\"end\"/></w:r>");

      Matcher matcher = internalDocLinkRegex.matcher(data);
      int matcherIndex = 0;
      while (matcher.find(matcherIndex)) {
         String guid = matcher.group(1);
         String referenceText = Strings.xmlToText(matcher.group(2));
         referenceText = Xml.escape(referenceText).toString(); // Re-escaping characters such as &, <, >, and "
         boolean isHeader = headerGuids.contains(guid);

         String newReference = linkBuilder.getWordMlReference(guid, isHeader, referenceText);

         data = data.replace(matcher.group(0), newReference);
         matcherIndex = matcher.start() + newReference.length();
         matcher = internalDocLinkRegex.matcher(data);
      }

      return data;
   }

   protected void startOutlineSubSectionAndBookmark(WordMLWriter wordMl, ArtifactReadable artifact) {
      String[] splitBookmark = getSplitWordMlBookmark(artifact);
      wordMl.addWordMl(splitBookmark[0]);
      wordMl.startOutlineSubSection(FONT, artifact.getName(), null);
      wordMl.addWordMl(splitBookmark[1]);
   }
}
