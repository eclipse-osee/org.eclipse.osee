/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.publishing;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HeadingMsWord;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParagraphNumber;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PlainTextContent;
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
import org.eclipse.osee.define.api.ArtifactUrlServer;
import org.eclipse.osee.define.api.AttributeElement;
import org.eclipse.osee.define.api.MetadataElement;
import org.eclipse.osee.define.api.OseeHierarchyComparator;
import org.eclipse.osee.define.api.PublishingErrorElement;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.rest.DataRightsOperationsImpl;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Branden W. Phillips
 */
public abstract class AbstractMSWordTemplatePublisher {

   //Constants
   protected static final String ARTIFACT = "Artifact";
   protected static final Object ARTIFACT_ID = "Artifact Id";
   protected static final String ARTIFACT_TYPE = "Artifact Type";
   protected static final String APPLICABILITY = "Applicability";
   protected static final String INSERT_ARTIFACT_HERE = "INSERT_ARTIFACT_HERE";
   protected static final String INSERT_LINK = "INSERT_LINK_HERE";
   protected static final String PGNUMTYPE_START_1 = "<w:pgNumType [^>]*w:start=\"1\"/>";

   //Patterns
   protected static final Pattern headElementsPattern =
      Pattern.compile("(" + INSERT_ARTIFACT_HERE + ")" + "|" + INSERT_LINK,
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   //Template
   protected String elementType;

   //Outlining Options
   protected AttributeTypeId headingAttributeType;
   protected boolean outlining;
   protected boolean recurseChildren;
   protected String outlineNumber = "";
   protected boolean includeEmptyHeaders = false;

   //DataRights
   protected DataRightResult response = null;
   protected DataRightsClassification overrideClassification;

   //Data Structures
   protected final PublishingOptions publishingOptions;
   protected final List<AttributeElement> attributeElements = new LinkedList<>();
   protected final List<MetadataElement> metadataElements = new LinkedList<>();
   protected final List<ArtifactReadable> nonTemplateArtifacts = new LinkedList<>();
   protected final Set<ArtifactReadable> processedArtifacts = new HashSet<>();
   protected final Set<ArtifactId> emptyFolders = new HashSet<>();
   protected final Map<ArtifactReadable, CharSequence> artParagraphNumbers = new HashMap<>();
   protected final List<ArtifactTypeToken> excludeArtifactTypes = new LinkedList<>();

   //Error Variables
   protected final List<PublishingErrorElement> errorElements = new LinkedList<>();
   protected Set<String> bookmarkedIds = new HashSet<>();
   protected HashMap<String, ArtifactReadable> hyperlinkedIds = new HashMap<>();

   protected final OrcsApi orcsApi;
   protected final Log logger;

   protected AbstractMSWordTemplatePublisher(PublishingOptions publishingOptions, Log logger, OrcsApi orcsApi) {
      this.publishingOptions = publishingOptions;
      this.logger = logger;
      this.orcsApi = orcsApi;
   }

   /**
    * Beginning method of the publishing process. Default version takes in the artifact id of the head that the publish
    * is based off of, and then the artifact id for the template. This method is where the artifact readable is
    * gathered, and the template options are set up. Other artifact gathering/template set up can be done here. If
    * everything is valid, move onto the next step for publishing.
    */
   public abstract String publish(ArtifactId templateArtId, ArtifactId headArtId);

   /**
    * Second step of the publishing process. This method is where the WordMLProducer is set up and the word xml starts
    * to be written. The default version changes some elements of the template first. Then is the start of the template
    * up until the marking where the artifact content should be. The artifacts/content is then inserted in the middle
    * via processContent. Finally the rest of the template's word content is placed at the end to finish off the
    * published document.
    */
   protected abstract StringBuilder applyContentToTemplate(ArtifactReadable headArtifact, String templateContent);

   /**
    * Third step of the publishing process, this is where the processed content of the publish is handled in between the
    * beginning and end of the render template. In the default implementation, the artifact hierarchy is processed
    * starting from our head artifact, then any errors are added in their own final section.
    */
   protected abstract void processContent(ArtifactReadable headArtifact, WordMLProducer wordMl);

   /**
    * This method processes each artifact on an individually. The default implementation only handles word template
    * content, not whole word content or native content. A running list of processed artifacts is kept so no artifact is
    * processed multiple times. Folder artifacts are not processed either. In the default implementation, artifacts are
    * processed in hierarchy order so it traces through each artifacts' child recursively if the option is enabled.
    * Within each artifact, the metadata and attributes are published.
    */
   protected abstract void processArtifact(ArtifactReadable artifact, WordMLProducer wordMl);

   //--- Publish Helper Methods ---//

   /**
    * Grabs artifact readable for the artifact id passed in as the head artifact, default uses the view option that was
    * set.
    */
   protected ArtifactReadable getArtifactHead(ArtifactId artifactId) {
      ArtifactReadable artifact =
         orcsApi.getQueryFactory().fromBranch(publishingOptions.branch, publishingOptions.view).andId(
            artifactId).getArtifact();
      return artifact;
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
         JSONObject jsonObject = new JSONObject(templateOptions);
         elementType = jsonObject.getString("ElementType");
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
         try {
            includeEmptyHeaders = options.getBoolean("IncludeEmptyHeaders");
         } catch (JSONException ex) {
            // The template file json may not have this defined, default is false
            includeEmptyHeaders = false;
         }
         outlineNumber = options.getString("OutlineNumber");
         String headingAttrType = options.getString("HeadingAttributeType");
         headingAttributeType = orcsApi.getOrcsTypes().getAttributeTypes().getByName(headingAttrType);
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   protected void parseAttributeOptions(String templateOptions) {
      try {
         attributeElements.clear();

         JSONObject jsonObject = new JSONObject(templateOptions);
         JSONArray attributeOptions = jsonObject.getJSONArray("AttributeOptions");
         JSONObject options = null;

         for (int i = 0; i < attributeOptions.length(); i++) {
            options = attributeOptions.getJSONObject(i);
            String attributeType = options.getString("AttrType");
            String attributeLabel = options.getString("Label");
            String formatPre = options.getString("FormatPre");
            String formatPost = options.getString("FormatPost");

            AttributeElement attrElement = new AttributeElement();
            boolean typeExists = orcsApi.getOrcsTypes().getAttributeTypes().typeExists(attributeType);
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

            String metadataType = options.getString("Type");
            String metadataFormat = options.getString("Format");
            String metadataLabel = options.getString("Label");

            MetadataElement metadataElement = new MetadataElement();

            metadataElement.setElements(metadataType, metadataFormat, metadataLabel);
            metadataElements.add(metadataElement);
         }
      } catch (JSONException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
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

   //--- ApplyContentToTemplate Helper Methods ---//

   /**
    * This default version of the method cleans up some pieces of the render template's whole word content, and grabs
    * the paragraph number if needed.
    */
   protected String setUpTemplateContent(WordMLProducer wordMl, ArtifactReadable artifact, String templateContent, int maxOutline) {
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

   protected String setUpTemplateContent(WordMLProducer wordMl, ArtifactReadable artifact, String templateContent) {
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
   protected int handleStartOfTemplate(WordMLProducer wordMl, String templateContent, Matcher matcher) {
      wordMl.addWordMl(templateContent.substring(0, matcher.start()));
      return matcher.end();
   }

   /**
    * For the default version, now that the content has been inserted the rest of the render template's whole word
    * content is appended to the end after the footer is updated
    */
   protected void handleEndOfTemplate(WordMLProducer wordMl, String templateContent, int lastEndIndex) {
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

   /**
    * DataRightsClassification override comes in as a publishing option string, compare string to all
    * DataRightsClassifications, if they match, set override variable to that classification. This override makes it
    * that the entire published document uses the same data rights footer, regardless of the attribute on artifacts.
    */
   protected void getDataRightsOverride() {
      overrideClassification = DataRightsClassification.noOverride;
      for (DataRightsClassification classification : DataRightsClassification.values()) {
         if (classification.getDataRightsClassification().equals(publishingOptions.overrideDataRights)) {
            overrideClassification = classification;
            break;
         }
      }
   }

   //--- ProcessContent Helper Methods ---//

   /**
    * Starting from the head artifact, goes through all artifacts in the hierarchy to be published (if specified through
    * recurseChildren) and sets their data rights.
    */
   protected void setDataRightResponse(ArtifactReadable artifact) {
      List<ArtifactId> artifacts = new ArrayList<>();
      artifacts.add(artifact);
      if (recurseChildren) {
         artifacts.addAll(artifact.getDescendants());
      }
      DataRightsOperationsImpl dataRightsOps = new DataRightsOperationsImpl(orcsApi);
      response = dataRightsOps.getDataRights(artifacts, publishingOptions.branch, overrideClassification);
   }

   /**
    * Goes through each artifact and reassigns its' paragraph number attribute to the paragraph number calculated on it
    * during this publish.
    */
   protected void updateParagraphNumbers() {
      TransactionBuilder transaction = orcsApi.getTransactionFactory().createTransaction(publishingOptions.branch,
         SystemUser.OseeSystem, "Update paragraph number on artifact");
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
      OseeHierarchyComparator comparator = new OseeHierarchyComparator(null);
      artifacts.sort(comparator);

      for (Map.Entry<ArtifactReadable, String> entry : comparator.errors.entrySet()) {
         ArtifactReadable art = entry.getKey();
         String description = entry.getValue();
         errorElements.add(new PublishingErrorElement(art.getId(), art.getName(), art.getArtifactType(), description));
      }
   }

   //--- ProcessArtifact Helper Methods ---//

   /**
    * If outlining is enabled, this default method inserts the heading with the paragraph number for the artifact. Also
    * puts the artifact and paragraph number into a hashmap together for potential updating of paragraph numbers
    */
   protected void setArtifactOutlining(ArtifactReadable artifact, WordMLProducer wordMl) {
      AttributeTypeToken attrToken = AttributeTypeToken.valueOf(headingAttributeType.getIdString());
      String headingText = artifact.getSoleAttributeAsString(attrToken, "");
      CharSequence paragraphNumber = null;

      paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, null);
      if (paragraphNumber == null) {
         paragraphNumber = wordMl.startOutlineSubSection();
      }
      if (publishingOptions.updateParagraphNumbers) {
         artParagraphNumbers.put(artifact, paragraphNumber);
      }
   }

   /**
    * The default footer for artifacts is an empty string. If data rights/orientation are needed in the footer, this
    * method should be overridden to support that.
    */
   protected String getArtifactFooter(ArtifactReadable artifact) {
      return "";
   }

   /**
    * Loops through and processes each metadata item that was parsed from earlier when handling the rendererOptions.
    */
   protected void processMetadata(ArtifactReadable artifact, WordMLProducer wordMl) {
      for (MetadataElement metadataElement : metadataElements) {
         processMetadata(artifact, wordMl, metadataElement);
      }
   }

   /**
    * Adds the metadata element to the artifact, currently the default implementation ignores applicability
    */
   protected void processMetadata(ArtifactReadable artifact, WordMLProducer wordMl, MetadataElement element) {
      wordMl.startParagraph();
      String name = element.getType();
      String format = element.getFormat();
      String label = element.getLabel();
      String value = "";
      if (name.equals(APPLICABILITY)) {
         //TODO Handle for when the meta data option is for applicability.
         return;
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
   protected void processAttributes(ArtifactReadable artifact, WordMLProducer wordMl) {
      for (AttributeElement attributeElement : attributeElements) {
         String attributeName = attributeElement.getAttributeName();
         if (publishingOptions.allAttributes || attributeName.equals("*")) {
            for (AttributeTypeToken attributeType : getOrderedAttributeTypes(artifact.getValidAttributeTypes())) {
               if (!outlining || attributeType.notEqual(headingAttributeType)) {
                  processAttribute(artifact, wordMl, attributeElement, attributeType, true, PREVIEW);
               }
            }
         } else {
            AttributeTypeToken attributeType = orcsApi.getOrcsTypes().getAttributeTypes().getByName(attributeName);
            if (artifact.isAttributeTypeValid(attributeType)) {
               processAttribute(artifact, wordMl, attributeElement, attributeType, false, PREVIEW);
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
   protected void processAttribute(ArtifactReadable artifact, WordMLProducer wordMl, AttributeElement attributeElement, AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType) {
      //Do not publish OleData or RelationOrder
      if (attributeType.equals(WordOleData) || attributeType.equals(RelationOrder)) {
         return;
      }

      List<Object> attributes = artifact.getAttributeValues(attributeType);
      if (attributeType.equals(WordTemplateContent)) {
         renderWordTemplateContent(attributeType, artifact, presentationType, wordMl, attributeElement.getFormat(),
            attributeElement.getLabel());
      } else if (!attributes.isEmpty()) {
         renderAttribute(attributeType, artifact, presentationType, wordMl, attributeElement.getFormat(),
            attributeElement.getLabel());
      }
   }

   /**
    * This method derives from the WordTemplateRenderer on the client, used to render word template content attribute.
    * Uses WordTemplateContentRendererHandler to render the word ml. Also handles OSEE_Link errors if there are
    * artifacts that are linking to artifacts that aren't included in the publish.
    */
   protected void renderWordTemplateContent(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLProducer producer, String format, String label) {
      WordMLProducer wordMl = producer;
      String data = null;

      LinkType linkType = publishingOptions.linkType;
      String footer = getArtifactFooter(artifact);

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
      wtcData.setPresentationType(presentationType);
      wtcData.setViewId(publishingOptions.view);
      wtcData.setPermanentLinkUrl(new ArtifactUrlServer(orcsApi).getSelectedPermanentLinkUrl());

      Pair<String, Set<String>> content = null;
      try {
         WordTemplateContentRendererHandler rendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);
         content = rendererHandler.renderWordML(wtcData);
      } catch (Exception ex) {
         errorElements.add(new PublishingErrorElement(artifact.getId(), artifact.getName(), artifact.getArtifactType(),
            ex.toString()));
      }

      if (content != null) {
         data = content.getFirst();
         processLinkErrors(artifact, data, content.getSecond());
      }

      if (data != null) {
         wordMl.addWordMl(data);
      } else if (footer != null) {
         wordMl.addWordMl(footer);
      }
      wordMl.resetListValue();

   }

   /**
    * For non word template content attributes, this method appends the attribute to the WordMLProducer.
    */
   protected void renderAttribute(AttributeTypeToken attributeType, ArtifactReadable artifact, PresentationType presentationType, WordMLProducer producer, String format, String label) {
      WordMLProducer wordMl = producer;

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

      String valueList = artifact.getAttributeValuesAsString(attributeType);
      if (format.contains(">x<")) {
         wordMl.addWordMl(format.replace(">x<", ">" + Xml.escape(valueList).toString() + "<"));
      } else {
         wordMl.addTextInsideParagraph(valueList);
      }
      wordMl.endParagraph();

   }

   //--- Error Handling Methods ---//
   /**
    * Once all of the content has been processed, any errors that have been logged are now appended to the wordml in
    * their own end section.
    */
   protected void addErrorLogToWordMl(WordMLProducer wordMl) {
      addLinkNotInPublishErrors(wordMl);

      if (!errorElements.isEmpty()) {
         wordMl.startErrorLog();
         for (PublishingErrorElement error : errorElements) {
            wordMl.addErrorRow(error.getArtId().toString(), error.getArtName(), error.getArtType().getName(),
               error.getErrorDescription());
         }
         wordMl.endErrorLog();
      }
   }

   /**
    * When rendering word template content, this method keeps track of OSEE links in artifacts and the artifacts that
    * are linked to. After processing all artifacts, hyperlinkedIds will contain any artifact that has an OSEE link to
    * an artifact that was not published in the document.
    */
   protected void processLinkErrors(ArtifactReadable artifact, String data, Set<String> unknownIds) {
      Pattern bookmarkHyperlinkPattern = Pattern.compile(
         "(^<aml:annotation[^<>]+w:name=\"OSEE\\.[^\"]*\"[^<>]+w:type=\"Word\\.Bookmark\\.Start\\\"/>)|" + "(<w:instrText>\\s+HYPERLINK[^<>]+\"OSEE\\.[^\"]*\"\\s+</w:instrText>)");
      Pattern oseeLinkPattern = Pattern.compile("\"OSEE\\.[^\"]*\"");
      Matcher match = bookmarkHyperlinkPattern.matcher(data);
      Matcher linkMatch = null;
      String id = "";

      if (!unknownIds.isEmpty()) {
         String description = "Contains the following unknown GUIDs: " + unknownIds;
         errorElements.add(
            new PublishingErrorElement(artifact.getId(), artifact.getName(), artifact.getArtifactType(), description));
      }

      while (match.find()) {
         String foundMatch = match.group(0);
         if (Strings.isValid(foundMatch)) {
            linkMatch = oseeLinkPattern.matcher(foundMatch);
            if (linkMatch.find()) {
               id = foundMatch.substring(linkMatch.start() + 6, linkMatch.end() - 1);
               if (foundMatch.contains("Bookmark")) {
                  bookmarkedIds.add(id);
                  if (hyperlinkedIds.containsKey(id)) {
                     hyperlinkedIds.remove(id);
                  }
               } else if (foundMatch.contains("HYPERLINK")) {
                  if (!bookmarkedIds.contains(id) && !hyperlinkedIds.containsKey(id)) {
                     hyperlinkedIds.put(id, artifact);
                  }
               }
            }
         }
      }
   }

   protected void addLinkNotInPublishErrors(WordMLProducer wordMl) {
      if (!hyperlinkedIds.isEmpty()) {
         for (Map.Entry<String, ArtifactReadable> link : hyperlinkedIds.entrySet()) {
            ArtifactReadable artifact = link.getValue();
            String description =
               "Contains the following GUIDs that are not found in this published document: " + link.getKey();
            errorElements.add(new PublishingErrorElement(artifact.getId(), artifact.getName(),
               artifact.getArtifactType(), description));
         }
      }
   }
}
