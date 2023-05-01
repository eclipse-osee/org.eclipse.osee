/*********************************************************************
 * Copyright (c) 2020, 2022 Boeing
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

package org.eclipse.osee.define.operations.publishing;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.HeadingMsWord;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.NativeContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParagraphNumber;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PlainTextContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PublishInline;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.ArtifactUrlServer;
import org.eclipse.osee.define.api.AttributeAlphabeticalComparator;
import org.eclipse.osee.define.api.OseeHierarchyComparator;
import org.eclipse.osee.define.api.publishing.datarights.DataRightsOperations;
import org.eclipse.osee.define.operations.publishing.datarights.DataRightsOperationsImpl;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactSpecification;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.AttributeOptions;
import org.eclipse.osee.framework.core.publishing.DataRightContentBuilder;
import org.eclipse.osee.framework.core.publishing.MetadataOptions;
import org.eclipse.osee.framework.core.publishing.PublishingTemplateInsertTokenType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.ProcessedArtifactTracker;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.core.publishing.WordRenderUtil;
import org.eclipse.osee.framework.core.server.publishing.WordRenderArtifactWrapperServerImpl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

public class WordTemplateProcessorServer {

   protected static final String APPLICABILITY = "Applicability";

   protected static final String ARTIFACT = "Artifact";

   protected static final Object ARTIFACT_ID = "Artifact Id";

   protected static final String ARTIFACT_TYPE = "Artifact Type";

   protected static final String FONT = "Times New Roman";

   protected final ActivityLog activityLog;

   protected final Map<ApplicabilityId, Boolean> applicabilityMap = new HashMap<>();

   protected Map<ApplicabilityId, ApplicabilityToken> applicabilityTokens;

   protected final Map<ArtifactReadable, CharSequence> artParagraphNumbers = new HashMap<>();

   protected final AtsApi atsApi;

   protected BranchId branchId;

   protected DataRightsOperations dataRightsOperations;

   private String elementType;

   protected final Set<ArtifactId> emptyFolders = new HashSet<>();

   protected final List<ArtifactTypeToken> excludeArtifactTypes = new LinkedList<>();

   private Boolean excludeFolders;

   protected final Set<ArtifactReadable> headerArtifacts = new HashSet<>();

   protected final Set<String> headerGuids = new HashSet<>();

   protected AttributeTypeToken headingAttributeType;

   protected final OseeHierarchyComparator hierarchyComparator;

   /**
    * A {@link Map} of {@link ArtifactReadable} objects containing references keyed with the identifier of the linked to
    * artifact.
    */

   protected HashMap<String, ArtifactReadable> hyperlinkedIds = new HashMap<>();

   protected final Log logger;

   private Integer maximumOutlineDepth;

   protected final OrcsApi orcsApi;

   protected final Map<ArtifactTypeToken, List<ArtifactReadable>> oseeLinkedArtifactMap = new HashMap<>();

   protected String outlineNumber;

   protected String overrideClassification;

   protected final String permanentLinkUrl;

   /**
    * Tracks artifacts that have been processed by {@link ArtifactId} and GUID.
    */

   protected ProcessedArtifactTracker processedArtifactTracker;

   protected final PublishingErrorLog publishingErrorLog;

   protected PublishingTemplate publishingTemplate;

   protected final PublishingUtils publishingUtils;

   protected EnumRendererMap renderer;

   protected final OrcsTokenService tokenService;

   protected ArtifactId viewId;

   protected ChangedArtifactsTracker changedArtifactsTracker;

   public WordTemplateProcessorServer(OrcsApi orcsApi, AtsApi atsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.publishingErrorLog = new PublishingErrorLog();
      this.publishingUtils = new PublishingUtils(this.orcsApi);

      this.activityLog = orcsApi.getActivityLog();
      this.branchId = null;
      this.changedArtifactsTracker =
         new ChangedArtifactsTracker(this.atsApi, this.publishingUtils, this.publishingErrorLog);
      this.dataRightsOperations = DataRightsOperationsImpl.create(this.orcsApi);
      this.elementType = null;
      this.excludeFolders = null;
      this.headingAttributeType = null;
      this.hierarchyComparator = new OseeHierarchyComparator(this.activityLog);
      this.logger = atsApi.getLogger();
      this.maximumOutlineDepth = null;
      this.outlineNumber = null;
      this.overrideClassification = null;
      this.permanentLinkUrl = new ArtifactUrlServer(this.orcsApi).getSelectedPermanentLinkUrl();
      this.processedArtifactTracker = new ProcessedArtifactTracker();
      this.publishingTemplate = null;
      this.renderer = null;
      this.tokenService = orcsApi.tokenService();
      this.viewId = null;
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
    * Generates an error for each identifier in {@link #hyperlinkedIds} that is not also in {@link #bookmarkedIds}.
    */

   protected void addLinkNotInPublishErrors() {

      //@formatter:off
      this.hyperlinkedIds
         .entrySet()
         .stream()
         .filter( this.processedArtifactTracker::isNotBookmarked )
         .forEach
            (
               ( hyperlinkEntry ) ->
               {
                  var linkReference = hyperlinkEntry.getKey();
                  var artifactWithLink = hyperlinkEntry.getValue();

                  var title =
                     this.processedArtifactTracker.containsByLinkReference( linkReference )
                        ?  this.processedArtifactTracker.isPublished( linkReference )
                           ? "Artifact contains a link to an unbookmarked artifact."
                           : "Artifact contains a link to a processed artifact that was excluded from the publish."
                        : ( WordCoreUtil.isLinkReferenceAnArtifactId( linkReference )
                               ? this.publishingUtils.getArtifactReadableByIdentifier( new ArtifactSpecification( this.branchId, ArtifactId.SENTINEL, ArtifactId.valueOf( linkReference ) ) )
                               : this.publishingUtils.getArtifactReadableByGuid( new BranchSpecification( this.branchId ), linkReference )
                          ).map( ( artifactReadable ) -> "Artifact contains a link to an artifact that is not contained in the document." )
                           .orElse( "Artifact contains a link to an unknown artifact. ");

                  this.publishingErrorLog.error
                     (
                        artifactWithLink,
                        new Message()
                               .title( title )
                               .segment( "Artifact With Link", artifactWithLink.getIdString() )
                               .segment( "Link Reference",     linkReference                  )
                               .toString()
                     );
               }
        );
      //@formatter:on
   }

   /**
    * Second step of the publishing process. This method is where the WordMLProducer is set up and the word xml starts
    * to be written. The default version changes some elements of the template first. Then is the start of the template
    * up until the marking where the artifact content should be. The artifacts/content is then inserted in the middle
    * via processContent. Finally the rest of the template's word content is placed at the end to finish off the
    * published document.
    */

   protected void applyContentToTemplate(List<ArtifactReadable> artifacts, CharSequence templateContent,
      WordMLProducer wordMl) {

      //@formatter:off
      WordRenderUtil.setupPublishingTemplate
         (
            this.publishingTemplate,
            artifacts.get(0),
            wordMl,
            this.outlineNumber,
            null,
            this.maximumOutlineDepth
         );

      WordCoreUtil.processPublishingTemplate
         (
            templateContent,
            ( segment ) ->
            {
               wordMl.addWordMl( segment );
               this.processArtifactSet(artifacts,wordMl);
               this.addLinkNotInPublishErrors();
               this.publishingErrorLog.publishErrorLog(wordMl);
            },
            ( tail ) ->
            {
               var cleanFooterText = WordCoreUtil.cleanupFooter( tail );
               wordMl.addWordMl( cleanFooterText );
            }
         );
      //@formatter:on
   }

   /**
    * Beginning method of the publishing process. Default version takes in the list of artifact ids to be published, and
    * then the artifact id for the template. This method is where the artifact readable's are gathered, and the template
    * options are set up. If everything is valid, move onto the next step for publishing.
    */

   public void applyTemplate(List<ArtifactId> publishArtifactIds, Writer writer) {

      if (Objects.isNull(publishArtifactIds) || publishArtifactIds.isEmpty()) {
         /*
          * Nothing to do
          */
         return;
      }

      //@formatter:off
      var templateContent =
         Objects.nonNull( this.publishingTemplate )
            ? this.publishingTemplate.getTemplateContent().getTemplateString()
            : "";
      //@formatter:on

      /*
       * Load the artifacts and sort according to hierarchy
       */

      var publishArtifacts = this.getSelectedArtifacts(publishArtifactIds);

      /*
       * Setup the Output Stream
       */

      WordMLProducer wordMl = new WordMLProducer(writer);

      applyContentToTemplate(publishArtifacts, templateContent, wordMl);

   }

   /**
    * Checks to see whether this artifact should be included in the publish or not. Default implementation checks to see
    * if the artifact has/can have valid word template content. Also checks if it's a folder and whether or not the
    * options specify to print folders or not.
    */

   protected boolean checkIncluded(ArtifactReadable artifact) {
      boolean validWordTemplateContent =
         !artifact.isAttributeTypeValid(WholeWordContent) && !artifact.isAttributeTypeValid(NativeContent);
      boolean excludeFolder = this.excludeFolders && artifact.isOfType(Folder);

      if (!excludeFolder && checkIsArtifactApplicable(artifact)) {
         if (validWordTemplateContent) {
            return true;
         } else {
            this.publishingErrorLog.error(artifact,
               "Only artifacts of type Word Template Content are supported in this case");
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
      boolean isApplicable = this.viewId.equals(ArtifactId.SENTINEL);
      if (isApplicable) {
         return isApplicable;
      } else {
         ApplicabilityId applicability = artifact.getApplicability();
         if (applicabilityMap.containsKey(applicability)) {
            isApplicable = applicabilityMap.get(applicability);
         } else {
            List<ArtifactId> validViews = orcsApi.getQueryFactory().applicabilityQuery().getBranchViewsForApplicability(
               this.branchId, applicability);
            if (validViews.contains(this.viewId)) {
               isApplicable = true;
            }
            applicabilityMap.put(applicability, isApplicable);
         }
         return isApplicable;
      }
   }

   public WordTemplateProcessorServer configure(PublishingTemplate publishingTemplate,
      EnumRendererMap publishingOptions) {

      this.publishingTemplate = publishingTemplate;
      this.renderer = publishingOptions;

      /*
       * Element Type
       */

      this.elementType = this.publishingTemplate.getRendererOptions().getElementType();

      if (!WordTemplateProcessorServer.ARTIFACT.equals(this.elementType)) {

         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                      .title( "MSWordTemplatePublisher::applyTemplate, publishing is only implement for ARTIFACT element types.")
                      .indentInc()
                      .segment( "Publishing Template", this.publishingTemplate.getName() )
                      .segment( "Element Type",        this.elementType                       )
                      .toString()
                   );
      }

      /*
       * Exclude Folders
       */

      this.excludeFolders = this.renderer.isRendererOptionSetAndTrue(RendererOption.EXCLUDE_FOLDERS);

      /*
       * Heading Attribute Type
       */

      var outliningOptionsArray = this.publishingTemplate.getRendererOptions().getOutliningOptions();

      //@formatter:off
      var outliningOptions =
         Objects.nonNull( outliningOptionsArray ) && ( outliningOptionsArray.length >= 1 )
            ? outliningOptionsArray[0]
            : null;
      //@formatter:on

      if (Objects.isNull(outliningOptions)) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "WordTemplateProcessorServer::applyTemplate, publishing outlining options must be provided in the template publishing options.")
                             .indentInc()
                             .segment( "Publishing Template", this.publishingTemplate.getName() )
                             .toString()
                   );
         //@formatter:on
      }

      this.headingAttributeType = this.tokenService.getAttributeType(outliningOptions.getHeadingAttributeType());

      /*
       * Maximum outline depth
       */

      //@formatter:off
      this.maximumOutlineDepth =
         this.renderer.isRendererOptionSet( RendererOption.MAX_OUTLINE_DEPTH )
            ? this.renderer.getRendererOptionValue( RendererOption.MAX_OUTLINE_DEPTH )
            : 9;
      //@formatter:on

      /*
       * Outline Number
       */

      this.outlineNumber = outliningOptions.getOutlineNumber();

      /*
       * Override Classification
       */

      var overrideDataRights = (String) this.renderer.getRendererOptionValue(RendererOption.OVERRIDE_DATA_RIGHTS);

      this.overrideClassification =
         DataRightsClassification.isValid(overrideDataRights) ? overrideDataRights : "invalid";

      /*
       * Branch Identifier
       */

      this.branchId = this.renderer.getRendererOptionValue(RendererOption.BRANCH);

      /*
       * View Identifier
       */

      this.viewId = this.renderer.getRendererOptionValue(RendererOption.VIEW);

      return this;
   }

   //--- Publish Helper Methods ---//

   /**
    * This method returns the class HashMap variable applicabilityTokens to ensure that the map is loaded once needed.
    * The variable will stay null if this method is never called. This is meant to increase efficiency of applicability
    * checks
    */

   protected Map<ApplicabilityId, ApplicabilityToken> getApplicabilityTokens() {
      if (applicabilityTokens == null) {
         applicabilityTokens = new HashMap<>();
         HashMap<Long, ApplicabilityToken> tokens =
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(this.branchId);
         for (Map.Entry<Long, ApplicabilityToken> entry : tokens.entrySet()) {
            applicabilityTokens.put(ApplicabilityId.valueOf(entry.getKey()), entry.getValue());
         }
      }

      return applicabilityTokens;
   }

   /**
    * The default footer for artifacts is an empty string. If data rights/orientation are needed in the footer, this
    * method should be overridden to support that.
    *
    * @param artifact the {@link ArtifactReadable} to get the footer for.
    * @return a {@link String} possibly empty containing the artifact's footer.
    * @implSpec Overrides of this method should never return <code>null</code>. Overrides must return an empty
    * {@link String} when the artifact does not have a footer.
    */

   protected String getArtifactFooter(ArtifactReadable artifact) {
      return "";
   }

   /**
    * Generic method for passing in an AttributeTypeToken and getting the string value from the artifact. Meant for
    * subclasses to overwrite and make changes as needed.
    */

   protected CharSequence getAttributeValueAsString(AttributeTypeToken token, ArtifactReadable artifact) {
      return artifact.getAttributeValuesAsString(token);
   }

   //--- ProcessContent Helper Methods ---//

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
    * Performs a database query to obtain the {@link ArtifactReadable} for each {@link ArtifactId}.
    *
    * @param artifactIds the identifiers of the artifacts to be retrieved.
    * @return a {@link List} of {@link ArtifactReadble} objects.
    */

   protected List<ArtifactReadable> getSelectedArtifacts(List<ArtifactId> artifactIds) {
      List<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory().fromBranch(this.branchId).andIds(artifactIds).getResults().getList();
      artifacts.sort(hierarchyComparator);
      return artifacts;
   }

   /**
    * Takes a list of artifacts, assumed to be sorted with headers included, and creates a hashmap of each header to a
    * list of the artifacts that are a descendant of that header. Any artifacts before the first header are dropped out
    */

   protected Map<ArtifactReadable, List<ArtifactReadable>> getSortedArtifactsInHeaderMap(
      List<ArtifactReadable> artifacts) {
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

   /**
    * Uses the in place getWordMlBookmark but splits the results into a length 2 string array. The regex used to split
    * the string is a positive lookbehind on a closing tag, this technically retrieves 2 matches but due to the split
    * limit, only the first one will be used. This works assuming the bookmark only has a start and end aml:annotation
    * tag. TODO: use precompiled regex
    */

   protected String[] getSplitWordMlBookmark(ArtifactReadable artifact) {
      String wordMlBookmark = getWordMlBookmark(artifact).toString();
      return wordMlBookmark.split("(?<=>)", 2);
   }

   /**
    * Creates a new bookmark using a given artifact for use in the document as necessary. This method will handle the
    * bookmark/hyperlink storages to reflect that the given artifact has a bookmark.
    */
   protected CharSequence getWordMlBookmark(ArtifactReadable artifact) {

      CharSequence bookmark = WordCoreUtil.getWordMlBookmark(artifact.getId());
      bookmark = WordCoreUtilServer.reassignBookMarkID(bookmark);

      String guid = artifact.getGuid();
      this.processedArtifactTracker.setBookmarked(guid);
      if (hyperlinkedIds.containsKey(guid)) {
         hyperlinkedIds.remove(guid);
      }

      return bookmark;
   }

   //--- ProcessArtifact Helper Methods ---//

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
    * This method can be used to look through the OSEE hyperlinkIds that have not been found yet in the publish, and
    * populate them into a map given a set of Artifact Types that are of interest. Currently searches using guids.
    */

   protected void populateOseeLinkedArtifacts(ArtifactTypeToken... typeTokens) {
      List<ArtifactReadable> linkedArts =
         orcsApi.getQueryFactory().fromBranch(this.branchId).andGuids(hyperlinkedIds.keySet()).getResults().getList();

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
    * This method processes each artifact individually. A running list of processed artifacts is kept so no artifact is
    * processed multiple times. In the default implementation, artifacts are processed in hierarchy order so it traces
    * through each artifacts' child recursively if the option is enabled. Within each artifact, the metadata and
    * attributes are published.
    */

   protected void processArtifact(ArtifactReadable artifact, WordMLProducer wordMl,
      DataRightContentBuilder dataRightContentBuilder) {

      if (this.processedArtifactTracker.isOk(artifact)) {
         return;
      }

      this.processedArtifactTracker.add(artifact);

      boolean startedSection = false;

      if (this.checkIncluded(artifact)) {

         startedSection |= renderArtifact(artifact, wordMl, dataRightContentBuilder);

      }

      var recurse = this.publishingTemplate.getRendererOptions().getOutliningOptions()[0].isRecurseChildren();

      if (recurse) {

         List<ArtifactReadable> children = new LinkedList<>();

         try {

            children = artifact.getChildren();

         } catch (OseeCoreException ex) {

            this.publishingErrorLog.error(artifact,
               "There is an error when finding children for this artifact. Possible Cause: Empty Relation Order Attribute");

         }

         for (ArtifactReadable childArtifact : children) {

            if (childArtifact != null) {

               this.processArtifact(childArtifact, wordMl, dataRightContentBuilder);

            } else {

               this.publishingErrorLog.error(artifact, "Artifact has an empty child relation");

            }
         }
      }

      if (startedSection) {
         wordMl.endOutlineSubSection();
      }

      this.processedArtifactTracker.setOk(artifact);

   }

   //--- RenderArtifact Helper Methods ---//

   /**
    * Third step of the publishing process, this is where the processed content of the publish is handled in between the
    * beginning and end of the render template. In the default implementation, the artifact hierarchy is processed
    * starting from our head artifact, then any errors are added in their own final section. This section is likely to
    * be overridden by subclasses.
    */

   protected void processArtifactSet(List<ArtifactReadable> artifacts, WordMLProducer wordMl) {
      artifacts.forEach((artifact) -> processArtifact(artifact, wordMl, null));
   }

   /**
    * The default implementation does not render word ole data or relation order. This method gets the values for the
    * attributes and calls renderWordTemplateContent if of type Word Template Content, renderAttribute if any other
    * valid attribute
    */

   protected void processAttribute(ArtifactReadable artifact, WordMLProducer wordMl, AttributeOptions attributeOptions,
      AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType, String footer) {

      /*
       * Do not publish empty, invalid attribute types, OleData, or RelationOrder
       */

      //@formatter:off
      if(
            /*
             * If WordOleData, the attribute is skipped
             */

             CoreAttributeTypes.WordOleData.equals( attributeType )

             /*
              * Publishing relation order is not implemented on the server
              */

          || CoreAttributeTypes.RelationOrder.equals( attributeType )

             /*
              * Skip invalid attribute types
              */

          || !artifact.isAttributeTypeValid( attributeType )

             /*
              * If there are not attribute values of the attribute type for the artifact, skip it.
              */

          || artifact.getAttributeValues(attributeType).isEmpty()) {

         return;
      }

      this.processedArtifactTracker.incrementAttributeCount(artifact);

      if (attributeType.equals(WordTemplateContent)) {

         this.renderWordTemplateContent
            (
               artifact,
               presentationType,
               wordMl,
               attributeOptions.getFormat(),
               attributeOptions.getLabel(),
               footer
           );

         return;
      }

      WordRenderUtil.renderAttribute
         (
            attributeType,
            null,
            artifact,
            wordMl,
            attributeOptions.getLabel(),
            attributeOptions.getFormat()
         );
      //@formatter:on
   }

   /**
    * While processing the Word Template Content of an artifact, this method is used for keeping track of OSEE links
    * inside that content and how it relates to the other artifacts that are in this published document.
    * <p>
    * {@link #bookmarkedIds} tracks artifacts that have been seen and book marked in the published, these artifacts are
    * capable of being linked to from other artifacts.
    * <p>
    * {@link #hyperlinkedIds} tracks the artifacts that have been linked to from another artifact.
    */

   protected void trackDocumentLinks(ArtifactReadable artifact, String data, Set<String> unknownIds) {

      if (!unknownIds.isEmpty()) {
         //@formatter:off
         this.publishingErrorLog.error
            (
               artifact,
               String.format
                  (
                     "Artifact contains the following unknown GUIDs: %s (Delete or fix OSEE Link from Artifact)",
                     unknownIds
                  )
            );
         //@formatter:on
      }

      Pattern bookmarkHyperlinkPattern =
         Pattern.compile("(" + WordCoreUtil.OSEE_BOOKMARK_REGEX + ")|(" + WordCoreUtil.OSEE_HYPERLINK_REGEX + ")");

      Matcher match = bookmarkHyperlinkPattern.matcher(data);

      while (match.find()) {

         var bookmarkMatch = Objects.nonNull(match.group(1));
         var hyperlinkMatch = Objects.nonNull(match.group(3));

         if (bookmarkMatch) {

            /*
             * Group 2 is the OSEE id group in OSEE_BOOKMARK_REGEX
             */

            var bookmarkIdentifier = match.group(2);
            this.processedArtifactTracker.setBookmarked(bookmarkIdentifier);

         }

         if (hyperlinkMatch) {

            /*
             * Group 4 is the OSEE id group in OSEE_HYPERLINK_REGEX
             */

            var hyperlinkIdentifier = match.group(4);

            if (!hyperlinkedIds.containsKey(hyperlinkIdentifier)) {
               hyperlinkedIds.put(hyperlinkIdentifier, artifact);
            }

         }

      }
   }

   private boolean processOutlining(ArtifactReadable artifact, WordMLProducer wordMl) {

      var outlining = this.publishingTemplate.getRendererOptions().getOutliningOptions()[0].isOutlining();
      var publishInline = artifact.getSoleAttributeValue(PublishInline, false);

      if (outlining && !publishInline) {

         this.setArtifactOutlining(artifact, wordMl);
         return true;
      }

      return false;

   }

   protected String removeUnusedBookmark(CharSequence input) {
      String data = input.toString();
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
    * This is the fifth level in the main process of publishing. This is where any processing needed happens once it is
    * determined that the artifact will be included in the publish. In the default implementation, we just check
    * outlining and publishInLine to see whether or not to print the header and start the outlining. Then metadata and
    * attributes are processed. The reason this method returns a boolean is to say whether or not the MS Word section
    * was begun with a header.
    */
   protected boolean renderArtifact(ArtifactReadable artifact, WordMLProducer wordMl,
      DataRightContentBuilder dataRightContentBuilder) {

      var startedSection = this.processOutlining(artifact, wordMl);

      String footer = "";

      if (true /* this.templatePublishingData.getAttributeElements() does not support template footer */ ) {
         var orientation = WordRenderUtil.getPageOrientation(new WordRenderArtifactWrapperServerImpl(artifact));
         //@formatter:off
         footer =
            Objects.nonNull( dataRightContentBuilder )
               ? dataRightContentBuilder.getContent( artifact, orientation )
               : "";
      }

      final var finalFooter = footer;

      /*
       * Get Applicability Tokens
       */

      if (Objects.isNull(this.applicabilityTokens)) {
         this.applicabilityTokens = WordCoreUtilServer.getApplicabilityTokens(this.orcsApi, this.branchId);
      }

      /*
       * Add metadata attributes to the Word ML output
       */

      //@formatter:off
      WordRenderUtil.processMetadataOptions
         (
            this.publishingTemplate.getRendererOptions().getMetadataOptions(),
            this.applicabilityTokens,
            new WordRenderArtifactWrapperServerImpl( artifact ),
            wordMl
         );
      //@formatter:on

      /*
       * Add attributes and the main artifact content to the Word ML output
       */

      //@formatter:off
      WordRenderUtil.processAttributes
         (
            Arrays.asList(this.publishingTemplate.getRendererOptions().getAttributeOptions()),
            ( lAttributeOptions, lAttributeType, lAllAttributes ) ->
               this.processAttribute
                  (
                     artifact,
                     wordMl,
                     lAttributeOptions,
                     lAttributeType,
                     lAllAttributes,
                     PresentationType.PREVIEW,
                     finalFooter
                  ),
            ( attributeName ) -> this.tokenService.getAttributeType(attributeName),
            () -> this.getOrderedAttributeTypes(artifact.getValidAttributeTypes()),
            artifact,
            this.headingAttributeType,
            this.renderer.isRendererOptionSetAndTrue(RendererOption.ALL_ATTRIBUTES),
            this.publishingTemplate.getRendererOptions().getOutliningOptions()[0].isOutlining()
         );
      //@formatter:on

      return startedSection;
   }

   /**
    * This method derives from the WordTemplateRenderer on the client, used to render word template content attribute.
    * Uses WordTemplateContentRendererHandler to render the word ml. Also handles OSEE_Link errors if there are
    * artifacts that are linking to artifacts that aren't included in the publish.
    */

   protected void renderWordTemplateContent(ArtifactReadable artifact, PresentationType presentationType,
      WordMLProducer wordMl, String format, String label, String footer) {

      //@formatter:off
      assert
           Objects.nonNull( footer )
         : "MSWordTemplatePublisher::renderWordTemplateContent, an artifact's footer must never be null.";
      //@formatter:on

      var unknownGuids = new HashSet<String>();

      //@formatter:off
      var wordMlContentDataAndFooter =
         WordRenderUtil.renderWordAttribute
            (
              artifact,
              this.viewId,
              wordMl,
              this.renderer,
              presentationType,
              label,
              footer,
              this.permanentLinkUrl,
              this.changedArtifactsTracker.isChangedArtifact(artifact),
              artifact.isHistorical()
                 ? this.orcsApi.getTransactionFactory().getTx( artifact.getTransaction() )
                 : TransactionToken.SENTINEL,
              unknownGuids,
              ( wordTemplateContentData ) -> new WordTemplateContentRendererHandler( this.orcsApi, this.logger )
                                                    .renderWordML( wordTemplateContentData ),
              ( exception ) -> this.publishingErrorLog.error( artifact, exception.getMessage() )
            );

         this.trackDocumentLinks(artifact, wordMlContentDataAndFooter, unknownGuids);
      //@formatter:on
   }

   /**
    * If outlining is enabled, this default method inserts the heading with the paragraph number for the artifact. This
    * will also add a change tag to the heading text if this artifact is included into the populated list of changed
    * artifacts. Also puts the artifact and paragraph number into a hashmap together for potential updating of paragraph
    * numbers
    */

   protected void setArtifactOutlining(ArtifactReadable artifact, WordMLProducer wordMl) {

      CharSequence headingText = artifact.getSoleAttributeAsString(this.headingAttributeType, "");

      if (this.changedArtifactsTracker.isChangedArtifact(artifact)) {
         headingText = WordCoreUtil.appendInlineChangeTagToHeadingText(headingText);
      }

      CharSequence paragraphNumber = null;

      paragraphNumber = wordMl.startOutlineSubSection(FONT, headingText, null);

      if (paragraphNumber == null) {
         paragraphNumber = wordMl.startOutlineSubSection();
      }

      if (this.renderer.isRendererOptionSetAndTrue(RendererOption.UPDATE_PARAGRAPH_NUMBERS)) {
         this.artParagraphNumbers.put(artifact, paragraphNumber);
      }
   }

   protected void sortQueryListByAttributeAlphabetical(List<ArtifactReadable> artifacts,
      AttributeTypeToken attributeToken) {
      try {
         artifacts.sort(new AttributeAlphabeticalComparator(activityLog, attributeToken));
      } catch (Exception ex) {
         String errorMessage = String.format("There was an error when sorting the list on %s by alphabetical order",
            attributeToken.getName());
         this.publishingErrorLog.error(errorMessage);
      }
   }

   /**
    * Sorts the given artifact list by the OseeHierarchyComparator, logs errors gathered by the comparator.
    */

   protected void sortQueryListByHierarchy(List<ArtifactReadable> artifacts) {
      artifacts.sort(hierarchyComparator);

      for (Map.Entry<ArtifactReadable, String> entry : hierarchyComparator.errors.entrySet()) {
         ArtifactReadable art = entry.getKey();
         String description = entry.getValue();
         this.publishingErrorLog.error(art, description);
      }
   }

   protected void startOutlineSubSectionAndBookmark(WordMLProducer wordMl, ArtifactReadable artifact) {
      this.processedArtifactTracker.add(artifact);
      String[] splitBookmark = getSplitWordMlBookmark(artifact);
      wordMl.addWordMl(splitBookmark[0]);
      wordMl.startOutlineSubSection(FONT, artifact.getName(), null);
      wordMl.addWordMl(splitBookmark[1]);
      this.processedArtifactTracker.setOk(artifact);
   }

   /**
    * Goes through each artifact and reassigns its' paragraph number attribute to the paragraph number calculated on it
    * during this publish.
    */

   protected void updateParagraphNumbers() {
      TransactionBuilder transaction =
         orcsApi.getTransactionFactory().createTransaction(this.branchId, "Update paragraph number on artifact");
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
}

/* EOF */
