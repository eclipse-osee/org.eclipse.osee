/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.define.rest.operations;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.SeverityCategory;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.define.api.ArtifactUrlServer;
import org.eclipse.osee.define.api.AttributeElement;
import org.eclipse.osee.define.api.MetadataElement;
import org.eclipse.osee.define.api.OseeLinkBuilder;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.rest.DataRightsOperationsImpl;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Branden W. Phillips
 * @link WordTemplateProcessorTest
 */
public class WordTemplateProcessor {

   private static final String LOAD_EXCLUDED_ARTIFACTIDS =
      "select art_id from osee_artifact art, osee_txs txs where art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = 1 and not exists (select null from osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and e1 = ? and t2.gamma_id = txsP.gamma_id and txsP.branch_id = ? and txsP.tx_current = 1 and e2 = txs.app_id)";
   protected static final String ARTIFACT = "Artifact";
   private static final String ARTIFACT_TYPE = "Artifact Type";
   private static final Object ARTIFACT_ID = "Artifact Id";
   private static final String APPLICABILITY = "Applicability";
   private static final String INSERT_LINK = "INSERT_LINK_HERE";
   private static final String INSERT_ARTIFACT_HERE = "INSERT_ARTIFACT_HERE";
   protected static final String NESTED_TEMPLATE = "NestedTemplate";
   public static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";
   public static final String STYLES = "<w:lists>.*?</w:lists><w:styles>.*?</w:styles>";
   private final String newLineChar = System.getProperty("line.separator");

   protected static final Pattern headElementsPattern =
      Pattern.compile("(" + INSERT_ARTIFACT_HERE + ")" + "|" + INSERT_LINK,
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private String slaveTemplate;
   private String slaveTemplateOptions;
   private String slaveTemplateStyles;

   protected String elementType;
   private DataRightsClassification overrideClassification;
   private BranchId branch;

   //Outlining Options
   protected AttributeTypeId headingAttributeType;
   protected boolean outlining;
   protected boolean recurseChildren;
   protected String outlineNumber;

   //Attribute Options
   private String attributeLabel;
   private String attributeType;
   private String formatPre;
   private String formatPost;

   //Metadata Options
   private String metadataType;
   private String metadataLabel;
   private String metadataFormat;

   //Nested Template Options
   private String outlineType;
   private String sectionNumber;
   private String subDocName;
   private String key;
   private String value;
   private int nestedCount;

   protected final List<AttributeElement> attributeElements = new LinkedList<>();
   protected final List<MetadataElement> metadataElements = new LinkedList<>();
   protected final List<ArtifactReadable> nonTemplateArtifacts = new LinkedList<>();
   protected final Set<ArtifactReadable> processedArtifacts = new HashSet<>();
   protected PublishingOptions publishingOptions = new PublishingOptions();
   private boolean isDiff;
   protected boolean excludeFolders;
   protected CharSequence paragraphNumber = null;
   protected final List<ArtifactTypeToken> excludeArtifactTypes = new LinkedList<>();
   private HashMap<ApplicabilityId, ApplicabilityToken> applicabilityTokens;
   private final HashMap<ArtifactId, ArtifactId> artifactsToExclude;
   private final Set<ArtifactId> emptyFolders = new HashSet<>();

   private final Log logger;
   private final OrcsApi orcsApi;

   public WordTemplateProcessor(PublishingOptions publishingOptions, Log logger, OrcsApi orcsApi) {
      this.publishingOptions = publishingOptions;
      this.logger = logger;
      this.orcsApi = orcsApi;
      artifactsToExclude = new HashMap<>();
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    */
   public String publishWithNestedTemplates(ArtifactId masterTemplateArtId, ArtifactId slaveTemplateArtId, ArtifactId headArtifact) {
      ArtifactReadable masterTemplateArtifact = null, slaveTemplateArtifact = null;
      List<ArtifactReadable> artifacts = new LinkedList<>();

      if (masterTemplateArtId != ArtifactId.SENTINEL) {
         masterTemplateArtifact = orcsApi.getQueryFactory().fromBranch(COMMON).andId(masterTemplateArtId).getArtifact();
      } else {
         masterTemplateArtifact = ArtifactReadable.SENTINEL;
      }
      if (slaveTemplateArtId != ArtifactId.SENTINEL) {
         slaveTemplateArtifact = orcsApi.getQueryFactory().fromBranch(COMMON).andId(slaveTemplateArtId).getArtifact();
      }
      if (headArtifact != null) {
         ArtifactReadable art =
            orcsApi.getQueryFactory().fromBranch(publishingOptions.branch).andId(headArtifact).getArtifact();
         artifacts.add(art);
      }

      nestedCount = 0;
      String masterTemplate = masterTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
      String masterTemplateOptions =
         masterTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.RendererOptions, "");
      slaveTemplate = "";
      slaveTemplateOptions = "";
      isDiff = publishingOptions.publishDiff;
      publishingOptions.templateArtifact = masterTemplateArtifact;

      slaveTemplateStyles = "";
      if (slaveTemplateArtifact != null) {
         publishingOptions.templateArtifact = slaveTemplateArtifact;
         slaveTemplate = slaveTemplateArtifact.getSoleAttributeValue(CoreAttributeTypes.WholeWordContent, "");
         slaveTemplateOptions = slaveTemplateArtifact.getSoleAttributeAsString(CoreAttributeTypes.RendererOptions, "");

         ResultSet<ArtifactReadable> slaveTemplateRelatedArtifacts =
            slaveTemplateArtifact.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo);

         if (slaveTemplateRelatedArtifacts.size() == 1) {
            slaveTemplateStyles += slaveTemplateRelatedArtifacts.getExactlyOne().getSoleAttributeAsString(
               CoreAttributeTypes.WholeWordContent, "");
         } else {
            logger.info(
               "More than one style relation currently not supported. Defaulting to styles defined in the template.");
         }
      }

      try {
         attributeElements.clear();
         metadataElements.clear();
         JSONObject jsonObject = new JSONObject(masterTemplateOptions);
         elementType = jsonObject.getString("ElementType");
         if (elementType.equals(ARTIFACT)) {
            parseAttributeOptions(masterTemplateOptions);
            parseMetadataOptions(masterTemplateOptions);
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
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
      List<ArtifactReadable> masterTemplateRelatedArtifacts =
         masterTemplateArtifact.getRelated(CoreRelationTypes.SupportingInfo_SupportingInfo).getList();
      String masterTemplateStyles = "";

      if (masterTemplateRelatedArtifacts.size() == 1) {
         masterTemplateStyles +=
            masterTemplateRelatedArtifacts.get(0).getSoleAttributeAsString(CoreAttributeTypes.WholeWordContent, "");
      } else {
         logger.info(
            "More than one style relation currently not supported. Defaulting to styles defined in the template.");
      }

      getExcludeArtifactTypes();

      if (!publishingOptions.publishEmptyHeaders) {
         isEmptyHeaders(artifacts);
      }

      //Using applyTemplate, get the xml string, create and write to the file.
      StringBuilder wordMlOutput =
         applyTemplate(artifacts, masterTemplate, masterTemplateOptions, masterTemplateStyles, null, null, PREVIEW);

      return wordMlOutput.toString();
   }

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
   public boolean isEmptyHeaders(List<ArtifactReadable> artifacts) {
      boolean hasIncludedChildren = false;
      boolean includeParent = false;
      List<ArtifactReadable> children = null;
      for (ArtifactReadable artifact : artifacts) {
         children = artifact.getChildren();
         if (!children.isEmpty()) {
            hasIncludedChildren = isEmptyHeaders(children);
            if (!hasIncludedChildren) {
               if (artifact.isOfType(CoreArtifactTypes.HeadingMsWord)) {
                  emptyFolders.add(artifact);
               }
            }
         } else if (children.isEmpty() && artifact.isOfType(CoreArtifactTypes.HeadingMsWord)) {
            emptyFolders.add(artifact);
         }
         if (!isOfType(artifact, excludeArtifactTypes) && !artifact.isOfType(CoreArtifactTypes.HeadingMsWord)) {
            includeParent = true;
         }
         if (hasIncludedChildren) {
            includeParent = true;
         }
      }
      return includeParent;
   }

   protected List<ArtifactTypeToken> getExcludeArtifactTypes() {
      excludeArtifactTypes.clear();

      if (publishingOptions.excludeArtifactTypes != null) {
         for (ArtifactTypeToken artToken : publishingOptions.excludeArtifactTypes) {
            excludeArtifactTypes.add(artToken);
         }
      }

      return excludeArtifactTypes;
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    *
    * @param artifacts = null if the template defines the artifacts to be used in the publishing
    * @param folder = null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    */
   public StringBuilder applyTemplate(List<ArtifactReadable> artifacts, String templateContent, String templateOptions, String templateStyles, String outlineNumber, String outlineType, PresentationType presentationType) {

      String overrideDataRights = publishingOptions.overrideDataRights;
      overrideClassification = DataRightsClassification.noOverride;
      for (DataRightsClassification classification : DataRightsClassification.values()) {
         if (classification.getDataRightsClassification().equals(overrideDataRights)) {
            overrideClassification = classification;
         }
      }

      excludeFolders = publishingOptions.excludeFolders;

      if (artifacts.isEmpty()) {
         branch = BranchId.SENTINEL;
      } else {
         branch = artifacts.get(0).getBranch();
         BranchId fullBranch = branch;
         BranchQuery branchQuery = orcsApi.getQueryFactory().branchQuery().andId(fullBranch);
         BranchType branchType = branchQuery.getResults().getExactlyOne().getBranchType();
         if (branchType.equals(BranchType.MERGE)) {
            fullBranch = branchQuery.getResults().getExactlyOne().getParentBranch();
         }

         applicabilityTokens = new HashMap<>();
         Collection<ApplicabilityToken> appTokens =
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(fullBranch).values();
         for (ApplicabilityToken token : appTokens) {
            applicabilityTokens.put(token, token);
         }
      }

      //TODO Create server publishing way of handling applicability
      //ArtifactId view = publishingOptions.getView();
      //artifactsToExclude = getNonApplicableArtifacts(artifacts, view == null ? ArtifactId.SENTINEL : view);

      WordMLProducer wordMl = null;
      StringBuilder strBuilder = null;

      try {
         strBuilder = new StringBuilder();
         wordMl = new WordMLProducer(strBuilder);

         templateContent = templateContent.replaceAll(PGNUMTYPE_START_1, "");

         if (!templateStyles.isEmpty()) {
            templateContent = templateContent.replaceAll(STYLES, templateStyles);
         }

         this.outlineNumber = outlineNumber == null ? peekAtFirstArtifactToGetParagraphNumber(templateContent, null,
            artifacts) : outlineNumber;
         templateContent = wordMl.setHeadingNumbers(this.outlineNumber, templateContent, outlineType);

         Matcher matcher = headElementsPattern.matcher(templateContent);

         int lastEndIndex = 0;
         while (matcher.find()) {
            wordMl.addWordMl(templateContent.substring(lastEndIndex, matcher.start()));
            lastEndIndex = matcher.end();

            JSONObject jsonObject = new JSONObject(templateOptions);
            elementType = jsonObject.getString("ElementType");

            if (elementType.equals(ARTIFACT)) {
               parseOutliningOptions(templateOptions);

               if (presentationType == PresentationType.SPECIALIZED_EDIT && artifacts.size() == 1) {
                  // for single edit override outlining options
                  outlining = false;
               }
               processArtifactSet(templateOptions, artifacts, wordMl, outlineType, presentationType,
                  publishingOptions.view);
            } else if (elementType.equals(NESTED_TEMPLATE)) {
               //TODO Handle nested templates on server publishing
            } else {
               throw new OseeArgumentException("Invalid input [%s]", "");
            }
         }

         String endOfTemplate = templateContent.substring(lastEndIndex);
         // Write out the last of the template
         wordMl.addWordMl(updateFooter(endOfTemplate));

         displayNonTemplateArtifacts(nonTemplateArtifacts,
            "Only artifacts of type Word Template Content are supported in this case.");

      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      return strBuilder;
   }

   //TODO Handle nested templates on server publishing
   //   private void parseNestedTemplateOptions(String templateOptions, WordMLProducer wordMl, PresentationType presentationType) {
   //      try {
   //         JSONObject jsonObject = new JSONObject(templateOptions);
   //         JSONArray nestedTemplateOptions = jsonObject.getJSONArray("NestedTemplates");
   //         JSONObject options = null;
   //
   //         if (nestedCount < nestedTemplateOptions.length()) {
   //            options = nestedTemplateOptions.getJSONObject(nestedCount);
   //            nestedCount++;
   //            outlineType = options.getString("OutlineType");
   //            if (outlineType.isEmpty()) {
   //               outlineType = null;
   //            }
   //            sectionNumber = options.getString("SectionNumber");
   //            subDocName = options.getString("SubDocName");
   //            key = options.getString("Key");
   //            // rendererOption is either ID or NAME
   //            RendererOption rendererOption = RendererOption.valueOf(key.toUpperCase());
   //            value = options.getString("Value");
   //
   //            updateOption(rendererOption, value);
   //
   //            String artifactName = (String) getRendererOptionValue(RendererOption.NAME);
   //            String artifactId = (String) getRendererOptionValue(RendererOption.ID);
   //            String orcsQuery = (String) getRendererOptionValue(RendererOption.ORCS_QUERY);
   //            BranchId branch = (BranchId) getRendererOptionValue(RendererOption.BRANCH);
   //            List<ArtifactReadable> artifacts = null;
   //
   //            if (Strings.isValid(artifactId)) {
   //               List<ArtifactId> artIds = Arrays.asList(ArtifactId.valueOf(artifactId));
   //               artifacts = orcsApi.getQueryFactory().fromBranch(branch).andIds(artIds).asArtifacts();
   //            } else if (Strings.isValid(artifactName)) {
   //               artifacts = orcsApi.getQueryFactory().fromBranch(branch).andNameEquals(artifactName).asArtifacts();
   //            } else if (Strings.isValid(orcsQuery)) {
   //               artifacts = getScriptResult(orcsQuery);
   //
   //            }
   //
   //            String subDocFileName = subDocName + ".xml";
   //
   //            if (isDiff) {
   //               WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
   //               templateFileDiffer.generateFileDifferences(artifacts, "/results/" + subDocFileName, sectionNumber,
   //                  outlineType, recurseChildren);
   //            } else {
   //               //implement server method for creating/saving a file
   //            }
   //            wordMl.createHyperLinkDoc(subDocFileName);
   //         }
   //
   //      } catch (JSONException ex) {
   //         OseeCoreException.wrapAndThrow(ex);
   //      }
   //   }

   protected void parseAttributeOptions(String templateOptions) {
      try {
         attributeElements.clear();

         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray attributeOptions = jsonObject.getJSONArray("AttributeOptions");
         JSONObject options = null;

         for (int i = 0; i < attributeOptions.length(); i++) {
            options = attributeOptions.getJSONObject(i);
            attributeType = options.getString("AttrType");
            attributeLabel = options.getString("Label");
            formatPre = options.getString("FormatPre");
            formatPost = options.getString("FormatPost");

            AttributeElement attrElement = new AttributeElement();
            boolean typeExists = orcsApi.getOrcsTypes().getAttributeTypes().typeExists(attributeType);
            if (attributeType.equals("*") || typeExists) { //TODO talk with Ryan or David on how to replace this
               attrElement.setElements(attributeType, attributeLabel, formatPre, formatPost);
               attributeElements.add(attrElement);
            }

         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected void parseOutliningOptions(String templateOptions) {
      try {
         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray optionsArray = jsonObject.getJSONArray("OutliningOptions");
         JSONObject options = optionsArray.getJSONObject(0);

         outlining = options.getBoolean("Outlining");
         recurseChildren = options.getBoolean("RecurseChildren");
         String headingAttrType = options.getString("HeadingAttributeType");
         headingAttributeType = orcsApi.getOrcsTypes().getAttributeTypes().getByName(headingAttrType);
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected void parseMetadataOptions(String metadataOptions) {
      try {
         JSONObject jsonObject = new JSONObject(metadataOptions);
         JSONObject options = null;

         if (!jsonObject.has("MetadataOptions")) {
            return;
         }

         JSONArray optionsArray = jsonObject.getJSONArray("MetadataOptions");
         for (int i = 0; i < optionsArray.length(); i++) {
            options = optionsArray.getJSONObject(i);

            metadataType = options.getString("Type");
            metadataFormat = options.getString("Format");
            metadataLabel = options.getString("Label");

            MetadataElement metadataElement = new MetadataElement();

            metadataElement.setElements(metadataType, metadataFormat, metadataLabel);
            metadataElements.add(metadataElement);
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected String updateFooter(String endOfTemplate) {
      // footer cleanup
      endOfTemplate = endOfTemplate.replaceAll(ReportConstants.FTR, "");
      endOfTemplate =
         endOfTemplate.replaceFirst(ReportConstants.PAGE_SZ, ReportConstants.CONTINUOUS + ReportConstants.PG_SZ);
      return endOfTemplate;
   }

   protected String peekAtFirstArtifactToGetParagraphNumber(String template, String nextParagraphNumber, List<ArtifactReadable> artifacts) {
      String startParagraphNumber = "1";
      if (artifacts != null) {
         Matcher matcher = headElementsPattern.matcher(template);

         if (matcher.find()) {
            String elementType = matcher.group(0);

            if (elementType != null && elementType.equals(INSERT_ARTIFACT_HERE) && !artifacts.isEmpty()) {
               ArtifactReadable artifact = artifacts.iterator().next();
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

   protected void processArtifactSet(String templateOptions, List<ArtifactReadable> artifacts, WordMLProducer wordMl, String outlineType, PresentationType presentationType, ArtifactId viewId) {
      nonTemplateArtifacts.clear();
      publishingOptions.view = (viewId == null ? ArtifactId.SENTINEL : viewId);

      if (Strings.isValid(outlineNumber)) {
         wordMl.setNextParagraphNumberTo(outlineNumber);
      }

      // Don't extract the settings from the template if already done.
      if (attributeElements.isEmpty()) {
         parseAttributeOptions(templateOptions);
      }

      if (metadataElements.isEmpty()) {
         parseMetadataOptions(templateOptions);
      }

      if (publishingOptions.publishDiff) {
         //TODO Handle diff publishing on server
         //         WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
         //         templateFileDiffer.generateFileDifferences(artifacts, "/results/", outlineNumber, outlineType,
         //            recurseChildren);
      } else {

         List<ArtifactId> allArtifacts = new ArrayList<>();
         if (recurseChildren || publishingOptions.recurseOnLoad && !(publishingOptions.originPublishAsDiff)) {
            for (ArtifactReadable art : artifacts) {
               allArtifacts.add(art);
               if (!art.isHistorical()) {
                  allArtifacts.addAll(art.getDescendants());
               }
            }
         } else {
            allArtifacts.addAll(artifacts);
         }

         DataRightsOperationsImpl dataRightsOps = new DataRightsOperationsImpl(orcsApi);
         DataRightResult response = dataRightsOps.getDataRights(allArtifacts, branch, overrideClassification);

         for (ArtifactReadable artifact : artifacts) {
            processObjectArtifact(artifact, wordMl, outlineType, presentationType, response);
         }
      }
      // maintain a list of artifacts that have been processed so we do not
      // have duplicates.
      processedArtifacts.clear();
   }

   private void processObjectArtifact(ArtifactReadable artifact, WordMLProducer wordMl, String outlineType, PresentationType presentationType, DataRightResult data) {
      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent) && !artifact.isAttributeTypeValid(
         CoreAttributeTypes.NativeContent)) {
         // If the artifact has not been processed
         if (!processedArtifacts.contains(artifact)) {

            boolean ignoreArtifact =
               (excludeFolders && artifact.isOfType(CoreArtifactTypes.Folder)) || (artifactsToExclude.containsKey(
                  ArtifactId.valueOf(artifact.getId())) || emptyFolders.contains(artifact));

            boolean ignoreArtType = excludeArtifactTypes != null && isOfType(artifact, excludeArtifactTypes);
            boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);
            boolean startedSection = false;
            boolean templateOnly = publishingOptions.templateOnly;
            boolean includeUUIDs = publishingOptions.includeUuids;

            if (!ignoreArtifact && !ignoreArtType) {
               if (outlining && !templateOnly) {
                  AttributeTypeToken attrToken = AttributeTypeToken.valueOf(headingAttributeType.getIdString());
                  String headingText = artifact.getSoleAttributeAsString(attrToken, "");

                  if (includeUUIDs) {
                     String UUIDtext = String.format(" <UUID = %s>", artifact.getId());
                     headingText = headingText.concat(UUIDtext);
                  }

                  Boolean mergeTag = publishingOptions.addMergeTag;
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

                  if (publishingOptions.updateParagraphNumbers && !publishInline) {
                     if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
                        //TODO UpdateParagraphNumbers not currently a server publishing capability, fix below method to limit amount of transaction commits
                        TransactionBuilder transaction = orcsApi.getTransactionFactory().createTransaction(branch,
                           SystemUser.OseeSystem, "Update paragraph number on artifact");

                        transaction.setSoleAttributeValue(artifact, CoreAttributeTypes.ParagraphNumber,
                           paragraphNumber.toString());

                        transaction.commit();
                     }
                  }
               }
               String orientationStr = null;
               if (artifact.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
                  orientationStr = artifact.getSoleAttributeValue(CoreAttributeTypes.PageOrientation, "Portrait");
               }
               PageOrientation orientation = PageOrientation.fromString(orientationStr);
               String footer = data.getContent(artifact, orientation);

               processMetadata(artifact, wordMl);

               processAttributes(artifact, wordMl, presentationType, publishInline, footer);
            }

            // Check for option that may have been set from Publish with Diff BLAM to recurse
            boolean recurse = publishingOptions.recurseOnLoad;
            boolean origDiff = publishingOptions.originPublishAsDiff;

            if (recurseChildren && !recurse || recurse && !origDiff) {
               for (ArtifactReadable childArtifact : artifact.getChildren()) {
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

   protected boolean isOfType(ArtifactReadable artifact, List<ArtifactTypeToken> excludeArtifactTypes) {
      for (ArtifactTypeToken artType : excludeArtifactTypes) {
         if (artifact.isOfType(artType)) {
            return true;
         }
      }
      return false;
   }

   protected void processMetadata(ArtifactReadable artifact, WordMLProducer wordMl) {
      for (MetadataElement metadataElement : metadataElements) {
         processMetadata(artifact, wordMl, metadataElement);
      }
   }

   protected void processAttributes(ArtifactReadable artifact, WordMLProducer wordMl, PresentationType presentationType, boolean publishInLine, String footer) {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();

         if (publishingOptions.allAttributes || attributeName.equals("*")) {
            for (AttributeTypeToken attributeType : getOrderedAttributeTypes(artifact,
               artifact.getValidAttributeTypes())) {
               if (!outlining || attributeType.notEqual(headingAttributeType)) {
                  processAttribute(artifact, wordMl, attributeElement, attributeType, true, presentationType,
                     publishInLine, footer);
               }
            }
         } else {
            AttributeTypeToken attributeType = orcsApi.getOrcsTypes().getAttributeTypes().getByName(attributeName);
            if (artifact.isAttributeTypeValid(attributeType)) {
               processAttribute(artifact, wordMl, attributeElement, attributeType, false, presentationType,
                  publishInLine, footer);
            }
         }
      }
   }

   protected void processMetadata(ArtifactReadable artifact, WordMLProducer wordMl, MetadataElement element) {
      wordMl.startParagraph();
      String name = element.getType();
      String format = element.getFormat();
      String label = element.getLabel();
      String value = "";
      if (name.equals(APPLICABILITY)) {
         value = "unknown";
         if (artifact.getApplicability().isValid()) {
            ApplicabilityToken applicabilityToken = applicabilityTokens.get(artifact.getApplicability());
            if (applicabilityToken != null && applicabilityToken.isValid()) {
               value = applicabilityToken.getName();
            } else {
               value = artifact.getApplicability().getIdString();
            }
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

   private void processAttribute(ArtifactReadable artifact, WordMLProducer wordMl, AttributeElement attributeElement, AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType, boolean publishInLine, String footer) {
      publishingOptions.allAttributes = allAttrs;
      // This is for SRS Publishing. Do not publish unspecified attributes
      if (!allAttrs && attributeType.matches(Partition, SeverityCategory)) {
         if (artifact.isAttributeTypeValid(Partition)) {
            for (AttributeReadable<?> partition : artifact.getAttributes(Partition)) {
               if (partition == null || partition.getValue() == null || partition.getValue().equals("Unspecified")) {
                  return;
               }
            }
         }
      }
      boolean templateOnly = publishingOptions.templateOnly;
      if (templateOnly && attributeType.notEqual(WordTemplateContent)) {
         return;
      }

      /**
       * In some cases this returns no attributes at all, including no wordTemplateContent, even though it exists This
       * happens when wordTemplateContent is blank, so the else if condition takes this into account.
       */
      ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes(attributeType);

      if (!attributes.isEmpty()) {
         if (attributeType.equals(CoreAttributeTypes.WordOleData)) {
            return;
         }

         // Do not publish relation order during publishing
         if (publishingOptions.inPublishMode && CoreAttributeTypes.RelationOrder.equals(attributeType)) {
            return;
         }

         if (!(publishInLine && artifact.isAttributeTypeValid(WordTemplateContent)) || attributeType.equals(
            WordTemplateContent)) {
            renderAttribute(attributeType, artifact, presentationType, wordMl, attributeElement.getFormat(),
               attributeElement.getLabel(), footer);
         }
      } else if (attributeType.equals(WordTemplateContent)) {
         renderAttribute(attributeType, artifact, presentationType, wordMl, attributeElement.getFormat(),
            attributeElement.getLabel(), footer);
      }
   }

   protected void displayNonTemplateArtifacts(final Collection<ArtifactReadable> artifacts, final String warningString) {
      //TODO Add page at end of published document that contains all errors
   }

   public Set<ArtifactId> getEmptyFolders() {
      return emptyFolders;
   }

   public void setExcludedArtifactTypeForTest(List<ArtifactTypeToken> excludeTokens) {
      excludeArtifactTypes.clear();
      for (ArtifactTypeToken token : excludeTokens) {
         excludeArtifactTypes.add(token);
      }
   }

   public List<ArtifactReadable> getScriptResult(String script) {
      //TODO use new orcs rest call from orcs api
      return null;
   }

   //Copied from DefaultArtifactRenderer for server publishing
   public List<AttributeTypeToken> getOrderedAttributeTypes(ArtifactReadable artifact, Collection<AttributeTypeToken> attributeTypes) {
      ArrayList<AttributeTypeToken> orderedAttributeTypes = new ArrayList<>(attributeTypes.size());
      AttributeTypeToken contentType = null;

      for (AttributeTypeToken attributeType : attributeTypes) {
         if (attributeType.matches(CoreAttributeTypes.WholeWordContent, CoreAttributeTypes.WordTemplateContent,
            CoreAttributeTypes.PlainTextContent)) {
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

   //Copied from WordTemplateRenderer for server publishing
   protected void renderAttribute(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer) {
      WordMLProducer wordMl = producer;

      if (attributeType.equals(CoreAttributeTypes.WordTemplateContent)) {
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
         wtcData.setArtId(artifact.getUuid());
         wtcData.setBranch(artifact.getBranch());
         wtcData.setFooter(footer);
         wtcData.setIsEdit(presentationType == PresentationType.SPECIALIZED_EDIT);
         wtcData.setLinkType(linkType != null ? linkType.toString() : null);
         wtcData.setTxId(txId);
         OseeSessionGrant session = new OseeSessionGrant();
         wtcData.setPresentationType(presentationType);
         ArtifactId view = publishingOptions.view;
         wtcData.setViewId(view == null ? ArtifactId.SENTINEL : view);
         ArtifactUrlServer artUrl = new ArtifactUrlServer(orcsApi);
         wtcData.setPermanentLinkUrl(artUrl.getSelectedPermanentLinkUrl());

         Pair<String, Set<String>> content = null;
         try {
            WordTemplateContentRendererHandler rendererHandler =
               new WordTemplateContentRendererHandler(orcsApi, logger);
            content = rendererHandler.renderWordML(wtcData);
         } catch (Exception ex) {
            logger.error(ex.toString());
         }

         if (content != null) {
            data = content.getFirst();
            data = data.replaceAll(newLineChar, "");
            //TODO Display unknown guids by printing them on final page of published document
            //WordUiUtil.displayUnknownGuids(artifact, content.getSecond());
         }

         if (presentationType == PresentationType.SPECIALIZED_EDIT) {
            OseeLinkBuilder linkBuilder = new OseeLinkBuilder();
            wordMl.addEditParagraphNoEscape(linkBuilder.getStartEditImage(artifact.getGuid()));
            wordMl.addWordMl(data);
            wordMl.addEditParagraphNoEscape(linkBuilder.getEndEditImage(artifact.getGuid()));

         } else if (data != null) {
            wordMl.addWordMl(data);
         } else if (footer != null) {
            wordMl.addWordMl(footer);
         }
         wordMl.resetListValue();
      } else {
         if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
            //TODO implement relation order for server publishing
            return;
         }
         defaultRenderAttribute(attributeType, artifact, presentationType, producer, format, label, footer);

      }
   }

   //Copied from DefaultArtifactRenderer for server publishing
   protected void defaultRenderAttribute(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLProducer producer, String format, String label, String footer) {
      WordMLProducer wordMl = producer;
      boolean allAttrs = publishingOptions.allAttributes;

      wordMl.startParagraph();

      if (allAttrs) {
         if (!attributeType.matches(CoreAttributeTypes.PlainTextContent)) {
            wordMl.addWordMl("<w:r><w:t> " + Xml.escape(attributeType.getName()) + ": </w:t></w:r>");
         } else {
            wordMl.addWordMl("<w:r><w:t> </w:t></w:r>");
         }
      } else {
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         wordMl.addWordMl(label);
      }

      if (attributeType.equals(CoreAttributeTypes.RelationOrder)) {
         //TODO Implement relation order for server publishing
         wordMl.endParagraph();
         //         String data = renderRelationOrder(artifact);
         //         wordMl.addWordMl(data);
      } else {
         //String valueList = artifact.getAttributesToString(attributeType); TODO old method remove
         String valueList = artifact.getAttributeValuesAsString(attributeType);
         if (format.contains(">x<")) {
            wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
         } else {
            wordMl.addTextInsideParagraph(valueList);
         }
         wordMl.endParagraph();
      }
   }
}
