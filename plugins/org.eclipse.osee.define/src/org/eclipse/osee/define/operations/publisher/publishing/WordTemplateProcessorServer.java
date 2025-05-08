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

package org.eclipse.osee.define.operations.publisher.publishing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.define.rest.api.AttributeAlphabeticalComparator;
import org.eclipse.osee.define.rest.api.OseeHierarchyComparator;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.applicability.BatFile;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactSpecification;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.AllowedOutlineTypes;
import org.eclipse.osee.framework.core.publishing.AttributeOptions;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.DataRightContentBuilder;
import org.eclipse.osee.framework.core.publishing.FilterForView;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.IncludeBookmark;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.core.publishing.IncludeHeadings;
import org.eclipse.osee.framework.core.publishing.IncludeMainContentForHeadings;
import org.eclipse.osee.framework.core.publishing.IncludeMetadataAttributes;
import org.eclipse.osee.framework.core.publishing.OutlineNumber;
import org.eclipse.osee.framework.core.publishing.OutliningOptions;
import org.eclipse.osee.framework.core.publishing.ProcessedArtifactTracker;
import org.eclipse.osee.framework.core.publishing.PublishingAppender;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader.BranchIndicator;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader.WhenNotFound;
import org.eclipse.osee.framework.core.publishing.PublishingErrorLog;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.SectionNumberWhenMaximumOutlineLevelExceeded;
import org.eclipse.osee.framework.core.publishing.TrailingDot;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordRenderApplicabilityChecker;
import org.eclipse.osee.framework.core.publishing.WordRenderUtil;
import org.eclipse.osee.framework.core.publishing.artifactacceptor.ArtifactAcceptor;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

public class WordTemplateProcessorServer implements ToMessage {

   protected static final String APPLICABILITY = "Applicability";

   protected static final String ARTIFACT = "Artifact";

   protected static final Object ARTIFACT_ID = "Artifact Id";

   protected static final String ARTIFACT_TYPE = "Artifact Type";

   protected static final String FONT = "Times New Roman";

   /**
    * The initial size for the collections used to track the artifacts.
    */

   private static final int initialMapSize = 2048;

   protected final ActivityLog activityLog;

   protected boolean allAttributes;

   /**
    * Saves the types of artifacts that may be used for outlining.
    */

   protected AllowedOutlineTypes allowedOutlineTypes;

   protected final Map<ApplicabilityId, Boolean> applicabilityMap = new HashMap<>();

   protected Map<ApplicabilityId, ApplicabilityToken> applicabilityTokens;

   protected List<AttributeOptions> attributeOptionsList;

   protected final Map<ArtifactReadable, CharSequence> artParagraphNumbers = new HashMap<>();

   protected final AtsApi atsApi;

   /**
    * Saves the branch and view artifacts are to be published from.
    */

   protected BranchSpecification branchSpecification;

   protected final ArtifactTypeToken contentArtifactType;

   protected AttributeTypeToken contentAttributeType;

   protected ChangedArtifactsTracker changedArtifactsTracker;

   protected final DataAccessOperations dataAccessOperations;

   protected DataRightsOperations dataRightsOperations;

   private String elementType;

   protected ArtifactAcceptor emptyFoldersArtifactAcceptor;

   protected ArtifactAcceptor excludedArtifactTypeArtifactAcceptor;

   /**
    * Saves the output format for the publish.
    */

   protected FormatIndicator formatIndicator;

   protected final Set<PublishingArtifact> headerArtifacts;

   protected final Set<String> headerGuids = new HashSet<>();

   protected ArtifactTypeToken headingArtifactTypeToken;

   protected AttributeTypeToken headingAttributeTypeToken;

   protected final OseeHierarchyComparator hierarchyComparator;

   /**
    * A {@link Map} of {@link ArtifactReadable} objects containing references keyed with the identifier of the linked to
    * artifact.
    */

   protected HashMap<String, ArtifactReadable> hyperlinkedIds = new HashMap<>();

   protected IncludeHeadings includeHeadings;

   protected IncludeMainContentForHeadings includeMainContentForHeadings;

   /**
    * Flag used to control whether the error log is appended to the publish.
    *
    * @implNote (SERVER ONLY) This flag is defaulted to <code>true</code> and will remain so unless a derived class
    * overrides the value.
    */

   protected boolean includeErrorLog;

   protected IncludeMetadataAttributes includeMetadataAttributes;

   protected String initialOutlineNumber;

   protected final Log logger;

   private Integer maximumOutlineDepth;

   protected final OrcsApi orcsApi;

   protected final Map<ArtifactTypeToken, List<PublishingArtifact>> oseeLinkedArtifactMap = new HashMap<>();

   protected OutlineNumber outlineNumber;

   protected String overrideClassification;

   protected boolean overrideOutlineNumber;

   protected String desktopClientLoopbackUrl;

   /**
    * Tracks artifacts that have been processed by {@link ArtifactId} and GUID.
    */

   protected ProcessedArtifactTracker processedArtifactTracker;

   protected final PublishingArtifactLoader publishingArtifactLoader;

   protected final PublishingErrorLog publishingErrorLog;

   protected PublishingTemplate publishingTemplate;

   protected Boolean recurseChildren;

   protected RendererMap renderer;

   /**
    * Stores image artifacts linked in Markdown. Key: ArtID String, Value: Name
    */
   Map<String, String> linkedMdImages = new HashMap<>();

   /**
    * Used to track the time required for the publish.
    *
    * @implNote (SERVER ONLY)
    */

   long startTime;

   boolean templateFooter;

   protected OrcsApplicability applicOps;

   protected WordRenderApplicabilityChecker wordRenderApplicabilityChecker;

   protected WordTemplateContentRendererHandler wordTemplateContentRendererHandler;

   protected boolean contentAttributeOnly;

   protected final OrcsTokenService tokenService;

   public WordTemplateProcessorServer(OrcsApi orcsApi, AtsApi atsApi, DataAccessOperations dataAccessOperations, DataRightsOperations dataRightsOperations) {

      this.startTime = System.currentTimeMillis();

      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
      this.applicOps = orcsApi.getApplicabilityOps();
      this.publishingErrorLog = new PublishingErrorLog();
      this.dataAccessOperations = dataAccessOperations;
      this.activityLog = orcsApi.getActivityLog();

      this.allAttributes = false;
      this.attributeOptionsList = null;
      this.allowedOutlineTypes = AllowedOutlineTypes.ANYTHING;
      this.branchSpecification = null;
      this.changedArtifactsTracker =
         new ChangedArtifactsTracker(this.atsApi, this.dataAccessOperations, this.publishingErrorLog);
      this.contentArtifactType = null;
      this.contentAttributeType = null;
      this.dataRightsOperations = dataRightsOperations;
      this.elementType = null;
      this.emptyFoldersArtifactAcceptor = null;
      this.excludedArtifactTypeArtifactAcceptor = null;
      this.formatIndicator = null;
      this.headerArtifacts = new HashSet<>();
      this.headingArtifactTypeToken = null;
      this.headingAttributeTypeToken = null;
      this.hierarchyComparator = new OseeHierarchyComparator(this.activityLog);
      this.includeHeadings = null;
      this.includeErrorLog = true;
      this.initialOutlineNumber = null;
      this.includeMetadataAttributes = null;
      this.logger = atsApi.getLogger();
      this.maximumOutlineDepth = null;
      this.outlineNumber = null;
      this.overrideClassification = null;
      this.overrideOutlineNumber = false;
      this.desktopClientLoopbackUrl = null;
      this.processedArtifactTracker = new ProcessedArtifactTracker();
      //@formatter:off
      this.publishingArtifactLoader =
         new PublishingArtifactLoader
                (
                   this.dataAccessOperations,
                   this.publishingErrorLog,
                   WordRenderArtifactWrapperServerImpl::new,
                   WordRenderArtifactWrapperServerImpl::new,
                   this.changedArtifactsTracker::loadByAtsTeamWorkflow
                );
      //@formatter:on
      this.publishingTemplate = null;
      this.recurseChildren = null;
      this.renderer = null;
      this.contentAttributeOnly = false;
      this.templateFooter = false;
      this.tokenService = orcsApi.tokenService();
      this.wordRenderApplicabilityChecker = null;

   }

   /**
    * Given a list of artifacts, this method will loop through and add ancestors and sibling artifacts to the list
    */

   protected List<PublishingArtifact> addContextToArtifactList(List<PublishingArtifact> changedArtifacts) {
      final var artifactsWithContext = new LinkedList<PublishingArtifact>();
      for (final var artifact : changedArtifacts) {
         if (!artifactsWithContext.contains(artifact)) {
            artifactsWithContext.add(artifact);
         }

         //@formatter:off
         var ancestors =
            this.publishingArtifactLoader
               .loadAncestors( artifact, FilterForView.YES )
               .orElseThrow();
         //@formatter:on

         for (final var ancestor : ancestors) {
            if (!artifactsWithContext.contains(ancestor) && ancestor.notEqual(
               CoreArtifactTokens.DefaultHierarchyRoot)) {
               artifactsWithContext.add(ancestor);
            } else {
               break;
            }
         }

         //@formatter:off
         var siblings =
            this.publishingArtifactLoader
               .getSiblings( artifact, FilterForView.YES )
               .orElseThrow();
         //@formatter:on

         for (final var sibling : siblings) {
            if (!artifactsWithContext.contains(sibling) && !sibling.isOfType(this.headingArtifactTypeToken)) {
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
                               ? this.dataAccessOperations.getArtifactReadableByIdentifier
                                    (
                                       new ArtifactSpecification
                                              (
                                                 this.branchSpecification,
                                                 ArtifactId.valueOf( linkReference )
                                              )
                                     )
                               : this.dataAccessOperations.getArtifactReadables
                                    (
                                       this.branchSpecification,
                                       null,
                                       List.of( linkReference ),
                                       null,
                                       ArtifactTypeToken.SENTINEL,
                                       TransactionId.SENTINEL,
                                       IncludeDeleted.NO
                                    )
                          ).mapValue( ( artifactReadable ) -> "Artifact contains a link to an artifact that is not contained in the document." )
                           .orElseGet( "Artifact contains a link to an unknown artifact. ");

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
    * Beginning method of the publishing process. Default version takes in the list of artifact ids to be published, and
    * then the artifact id for the template. This method is where the artifact readable's are gathered, and the template
    * options are set up. If everything is valid, move onto the next step for publishing. Second step of the publishing
    * process. This method is where the WordMLProducer is set up and the word xml starts to be written. The default
    * version changes some elements of the template first. Then is the start of the template up until the marking where
    * the artifact content should be. The artifacts/content is then inserted in the middle via processContent. Finally
    * the rest of the template's word content is placed at the end to finish off the published document.
    */

   public void applyTemplate(List<ArtifactId> publishArtifactIds, @NonNull Writer writer,
      @NonNull OutputStream outputStream) {

      if (Objects.isNull(publishArtifactIds) || publishArtifactIds.isEmpty()) {
         /*
          * Nothing to do
          */
         return;
      }

      /*
       * Setup the Publishing Appender
       */

      var publishingAppender = this.formatIndicator.createPublishingAppender(writer, this.maximumOutlineDepth);

      /*
       * Get Applicability Tokens
       */

      //@formatter:off
      this.applicabilityTokens =
         WordCoreUtilServer.getApplicabilityTokens
            (
               this.orcsApi,
               this.branchSpecification.getBranchIdWithOutViewId()
            );
      //@formatter:on

      /*
       * Load the artifacts and sort according to hierarchy
       */

      var publishArtifacts = this.getSelectedArtifacts(publishArtifactIds);

      /*
       * Get the initial outline number
       */

      final var firstArtifact = publishArtifacts.isEmpty() ? ArtifactReadable.SENTINEL : publishArtifacts.get(0);

      //@formatter:off
      this.initialOutlineNumber =
         this.outlineNumber.isValidOutlineNumber( this.initialOutlineNumber )
            ? this.initialOutlineNumber
            : WordRenderUtil.getStartingParagraphNumber(firstArtifact, publishingTemplate);
      //@formatter:on

      this.outlineNumber.setOutlineNumber(this.initialOutlineNumber);

      /*
       * Setup Publishing Template
       */

      //@formatter:off
      WordRenderUtil.setupPublishingTemplate
         (
            this.formatIndicator,
            this.publishingTemplate,
            publishingAppender,
            this.initialOutlineNumber,
            null
         );

      WordCoreUtil.processPublishingTemplate
         (
            this.publishingTemplate.getTemplateContent(),
            ( segment ) ->
            {
               publishingAppender.append( segment );
               this.processArtifactSet(publishArtifacts,publishingAppender);
               if( this.formatIndicator.isWordMl() && this.includeErrorLog ) {
                  this.addLinkNotInPublishErrors();
                  this.publishingErrorLog.publishErrorLog(publishingAppender);
               }
            },
            ( tail ) ->
            {
               var cleanFooterText =
                  this.formatIndicator.isWordMl()
                     ? WordCoreUtil.cleanupFooter( tail )
                     : tail;
               publishingAppender.append( cleanFooterText );
            }
         );

      if (this.formatIndicator.isMarkdown()) {
         if (outputStream instanceof ByteArrayOutputStream) {
             packageMarkdown(writer, (ByteArrayOutputStream) outputStream, publishArtifacts);
         } else {
             throw new IllegalArgumentException("Unsupported OutputStream type. NOTE: You cannot use \"PIPED\" email "
                + "for markdown publishing. Post manipulation of the stream is required for markdown publishing.");
         }
     }

    //@formatter:on
   }

   private void packageMarkdown(Writer writer, ByteArrayOutputStream outputStream,
      List<PublishingArtifact> publishArtifacts) {
      try {
         // 1. Flush the writer to ensure all data is written to the outputStream
         writer.flush();

         // 2. Get markdown content as byte array
         byte[] markdownBytes = outputStream.toByteArray();

         // 3. Prepare in-memory ZIP
         ByteArrayOutputStream zipBytesStream = new ByteArrayOutputStream();
         try (ZipOutputStream zipOut = new ZipOutputStream(zipBytesStream)) {

            // 4. Add the markdown file
            zipOut.putNextEntry(new ZipEntry("document.md"));
            zipOut.write(markdownBytes);
            zipOut.closeEntry();

            // 5. Add image artifacts under resources/
            for (Map.Entry<String, String> entry : linkedMdImages.entrySet()) {
               byte[] content = loadImageArtifactContent(entry.getKey());
               // Only .png supported. Update if expanded to support addition image types.
               zipOut.putNextEntry(new ZipEntry("resources/" + entry.getValue() + ".png"));
               zipOut.write(content);
               zipOut.closeEntry();
            }
         }

         // 6. Overwrite the original outputStream with zip content
         outputStream.reset();
         outputStream.write(zipBytesStream.toByteArray());
         outputStream.flush();

      } catch (IOException e) {
         throw new RuntimeException("Failed to create in-memory zip from markdown content", e);
      }
   }

   private String sanitizeFileName(String name) {
      return name.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
   }

   private byte[] loadImageArtifactContent(String idStr) {
      ArtifactReadable imgArtifact =
         orcsApi.getQueryFactory().fromBranch(this.branchSpecification.getBranchIdWithOutViewId()).andId(
            ArtifactId.valueOf(idStr)).getArtifact();

      byte[] pngBytes = null;
      try (InputStream inputStream = imgArtifact.getSoleAttributeValue(CoreAttributeTypes.NativeContent)) {
         pngBytes = Lib.inputStreamToBytes(inputStream);
      } catch (IOException ex) {
         this.publishingErrorLog.error(
            "Unable to transform native content input stream of artifact " + idStr + " to bytes.");
      }
      return pngBytes;
   }

   /**
    * Method for checking the applicability of an artifact. Current method relies on a map between applicability ids and
    * a true false to the set branch. If the applicability has been processed, the map is relied upon. If not, it is
    * checked to see whether or not it contains a valid view.
    */

   protected boolean checkIsArtifactApplicable(ArtifactReadable artifact) {

      if (!this.branchSpecification.hasView()) {
         return true;
      }

      ApplicabilityId applicability = artifact.getApplicability();

      if (applicabilityMap.containsKey(applicability)) {

         return applicabilityMap.get(applicability);

      }

      var isApplicable = false;

      final var validViews = orcsApi.getQueryFactory().applicabilityQuery().getBranchViewsForApplicability(
         this.branchSpecification.getBranchIdWithOutViewId(), applicability);

      if (validViews.contains(this.branchSpecification.getViewId())) {
         isApplicable = true;
      }

      applicabilityMap.put(applicability, isApplicable);

      return isApplicable;

   }

   public WordTemplateProcessorServer configure(PublishingTemplate publishingTemplate, RendererMap publishingOptions,
      String desktopClientLoopbackUrl) {

      /*
       * Publishing Template
       */

      this.publishingTemplate = publishingTemplate;

      /*
       * Publishing Options
       */

      this.renderer = publishingOptions;

      /**
       * The base URL of the user's machine used by {@link WordMlLinkWordMlLinkHandler} to replace 'OSEE_LINK' (artifact
       * links) in the Word Template Content with loopback links to the user's desktop client.
       */

      this.desktopClientLoopbackUrl = desktopClientLoopbackUrl;

      /*
       * All Attributes
       */

      this.allAttributes = this.renderer.isRendererOptionSetAndTrue(RendererOption.ALL_ATTRIBUTES);

      /*
       * Publishing Format
       */

      this.formatIndicator = this.renderer.getRendererOptionValue(RendererOption.PUBLISHING_FORMAT);

      /*
       * Content Attribute Only
       */

      this.contentAttributeOnly = renderer.getRendererOptionValue(RendererOption.CONTENT_ATTRIBUTE_ONLY);

      /*
       * Element Type
       */

      this.elementType = this.publishingTemplate.getPublishOptions().getElementType();

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

      /**
       * Folder
       * <p>
       * Client Only
       */

      /*
       * Maximum outline depth
       */

      //@formatter:off
      int rendererOptionMaximumOutlineDepth =
         this.renderer.isRendererOptionSet( RendererOption.MAX_OUTLINE_DEPTH )
            ? this.renderer.getRendererOptionValue( RendererOption.MAX_OUTLINE_DEPTH )
            : -1;

      int formatMaximumOutlineDepth = this.formatIndicator.getMaximumOutlineDepth();

      switch(    ( rendererOptionMaximumOutlineDepth == -1 ? 1 : 0 )
               + ( formatMaximumOutlineDepth         == -1 ? 2 : 0 ) ) {
         case 0: this.maximumOutlineDepth = Math.min( rendererOptionMaximumOutlineDepth, formatMaximumOutlineDepth );
                 break;
         case 1: this.maximumOutlineDepth = formatMaximumOutlineDepth;
                 break;
         case 2: this.maximumOutlineDepth = rendererOptionMaximumOutlineDepth;
                 break;
         case 3: this.maximumOutlineDepth = 100;
                 break;
      }
      //@formatter:on

      /**
       * Outline Number
       */

      this.outlineNumber = new OutlineNumber(this.maximumOutlineDepth, 1, TrailingDot.NO,
         SectionNumberWhenMaximumOutlineLevelExceeded.INCREMENT_CURRENT_LEVEL);

      /**
       * Outline Type
       * <p>
       * Client Only
       */

      /*
       * Override Classification
       */

      var overrideDataRights = (String) this.renderer.getRendererOptionValue(RendererOption.OVERRIDE_DATA_RIGHTS);

      this.overrideClassification =
         DataRightsClassification.isValid(overrideDataRights) ? overrideDataRights : "invalid";

      /**
       * Presentation Type
       * <p>
       * Client Only
       */

      /*
       * Branch Specification
       */

      //@formatter:off
      this.branchSpecification =
         new BranchSpecification
                (
                   this.renderer.getRendererOptionValue(RendererOption.BRANCH),
                   this.renderer.getRendererOptionValue(RendererOption.VIEW)
                );
      //@formatter:on

      /*
       * Configure Publishing Artifact Loader
       */

      //@formatter:off
      this.publishingArtifactLoader.configure
         (
            this.branchSpecification,
            WordTemplateProcessorServer.initialMapSize
         );
      //@formatter:on

      /*
       * Applicability Checker
       */

      //@formatter:off
      this.wordRenderApplicabilityChecker =
         new WordRenderApplicabilityChecker
                (
                   (branchId, viewId) -> WordCoreUtilServer.getNonApplicableArtifacts
                                            (
                                               this.orcsApi,
                                               this.branchSpecification.getBranchIdWithOutViewId(),
                                               this.branchSpecification.getViewId()
                                            )
                );
      //@formatter:on
      this.wordRenderApplicabilityChecker.load(this.branchSpecification.getBranchId(),
         this.branchSpecification.getViewId());

      /*
       * Word Template Content Renderer Handler
       */

      //@formatter:off
      this.wordTemplateContentRendererHandler =
         new WordTemplateContentRendererHandler
                (
                   this.orcsApi,
                   this.dataAccessOperations,
                   this.logger
                );
      //@formatter:on

      /*
       * Content Options
       */

      final var publishOptions = publishingTemplate.getPublishOptions();

      /*
       * Content Artifact Type
       */

      /*
       * Content Attribute Type
       */

      /*
       * Outlining Options
       */

      //@formatter:off
      OutliningOptions.setValues
         (
            publishOptions.getOutliningOptions(),
            this.formatIndicator,
            this.renderer,
            this.tokenService,
            ( allowedOutlineTypes                  ) -> this.allowedOutlineTypes                  = allowedOutlineTypes,
            ( contentAttributeType                 ) -> this.contentAttributeType                 = contentAttributeType,
            ( excludedArtifactTypes                ) -> this.excludedArtifactTypeArtifactAcceptor = WordRenderUtil.getExcludedArtifactTypeArtifactAcceptor( excludedArtifactTypes ),
            ( headingArtifactType                  ) -> this.headingArtifactTypeToken             = headingArtifactType,
            ( headingAttributeType                 ) -> this.headingAttributeTypeToken            = headingAttributeType,
            ( includeHeadings                      ) -> this.includeHeadings                      = includeHeadings,
            ( includeMainContentForHeadings        ) -> this.includeMainContentForHeadings        = includeMainContentForHeadings,
            ( includeMetadataAttributes            ) -> this.includeMetadataAttributes            = includeMetadataAttributes,
            ( initialOutlineNumber                 ) -> this.initialOutlineNumber                 = initialOutlineNumber,
            ( overrideOutlineNumber                ) -> this.overrideOutlineNumber                = overrideOutlineNumber,
            ( recurseChildren                      ) -> this.recurseChildren                      = recurseChildren,
            ( templateFooter                       ) -> this.templateFooter                       = templateFooter
         );
      //@formatter:on

      /*
       * Attribute Options
       */

      //@formatter:off
      this.attributeOptionsList =
         AttributeOptions.setValues
            (
               publishOptions.getAttributeOptions(),
               this.tokenService
            );
      //@formatter:on

      return this;

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
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(
               this.branchSpecification.getBranchIdWithOutViewId());
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

   /**
    * Orders the attribute and moves any word/plain text content to the end of the attributes.
    */

   protected List<AttributeTypeToken> getOrderedAttributeTypes(Collection<AttributeTypeToken> attributeTypes) {

      var orderedAttributeTypes = new LinkedList<AttributeTypeToken>();
      var contentAttributeType = this.formatIndicator.getContentAttributeTypeToken();

      AttributeTypeToken contentType = null;

      for (var attributeType : attributeTypes) {

         if (attributeType.matches(contentAttributeType)) {
            contentType = attributeType;
            continue;
         }

         orderedAttributeTypes.add(attributeType);
      }

      Collections.sort(orderedAttributeTypes);

      if (Objects.nonNull(contentType)) {

         switch (this.formatIndicator.getContentPosition()) {
            case START:
               orderedAttributeTypes.add(0, contentType);
               break;
            case END:
               orderedAttributeTypes.add(contentType);
               break;
         }

      }

      return orderedAttributeTypes;
   }

   /**
    * Performs a database query to obtain the {@link ArtifactReadable} for each {@link ArtifactId}.
    *
    * @param artifactIds the identifiers of the artifacts to be retrieved.
    * @return a {@link List} of {@link ArtifactReadble} objects.
    */

   protected List<PublishingArtifact> getSelectedArtifacts(List<? extends ArtifactId> artifactIdentifiers) {

      //@formatter:off
      var artifacts =
         this.publishingArtifactLoader
            .getPublishingArtifactsByArtifactIdentifiers
               (
                  BranchIndicator.PUBLISHING_BRANCH,
                  artifactIdentifiers,
                  FilterForView.YES,
                  WhenNotFound.EMPTY,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .orElseThrow
               (
                  ( dataAccessException ) -> new OseeCoreException
                                                    (
                                                       new Message()
                                                              .title( "WordTemplateProcessorServer::getSelectedArtifacts, failed to load artifacts." )
                                                              .indentInc()
                                                              .segment( "Branch Identifier", this.branchSpecification.getBranchId().getIdString() )
                                                              .segment( "View Identifier",   this.branchSpecification.getViewId().getIdString()   )
                                                              .segmentIndexed( "Artifact Identifiers", artifactIdentifiers, ( v ) -> v, 20 )
                                                              .reasonFollows( dataAccessException )
                                                              .toString(),
                                                       dataAccessException
                                                    )
               );
      //@formatter:on
      this.publishingArtifactLoader.sort(artifacts);

      return artifacts;

   }

   /**
    * Takes a list of artifacts, assumed to be sorted with headers included, and creates a hashmap of each header to a
    * list of the artifacts that are a descendant of that header. Any artifacts before the first header are dropped out
    */

   protected Map<PublishingArtifact, List<PublishingArtifact>> getSortedArtifactsInHeaderMap(
      List<PublishingArtifact> artifacts) {
      final var headerMap = new HashMap<PublishingArtifact, List<PublishingArtifact>>();
      PublishingArtifact lastHeader = null;
      var artList = new LinkedList<PublishingArtifact>();

      for (final var art : artifacts) {
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
    * This method can be used to look through the OSEE hyperlinkIds that have not been found yet in the publish, and
    * populate them into a map given a set of Artifact Types that are of interest. Currently searches using guids.
    */

   protected void populateOseeLinkedArtifacts(ArtifactTypeToken... typeTokens) {

      //@formatter:off
         this.publishingArtifactLoader
            .getPublishingArtifactsByGuids
               (
                  BranchIndicator.PUBLISHING_BRANCH,
                  this.hyperlinkedIds.keySet(),
                  FilterForView.NO,
                  WhenNotFound.ERROR,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .orElseThrow
               (
                  ( dataAccessException ) ->
                     new OseeCoreException
                            (
                               new Message()
                                      .title( "WordTemplateProcessorServer::populateOseeLinkedArtifacts, failed to load hyperlinked artifacts" )
                                      .indentInc()
                                      .segmentIndexed( "Hyperlinked Artifacts", this.hyperlinkedIds.keySet() )
                                      .toString(),
                               dataAccessException
                            )
               )
            .forEach
               (
                  ( artifact ) ->
                  {
                     if (artifact.isOfType(typeTokens)) {
                        ArtifactTypeToken artifactType = artifact.getArtifactType();
                        if (oseeLinkedArtifactMap.containsKey(artifactType)) {
                           oseeLinkedArtifactMap.get(artifactType).add(artifact);
                        } else {
                           final var artList = new LinkedList<PublishingArtifact>();
                           artList.add(artifact);
                           oseeLinkedArtifactMap.put(artifactType, artList);
                        }
                        hyperlinkedIds.remove(artifact.getGuid());
                     }

                  }
               );
      //@formatter:on
   }

   /**
    * This method processes each artifact individually. A running list of processed artifacts is kept so no artifact is
    * processed multiple times. In the default implementation, artifacts are processed in hierarchy order so it traces
    * through each artifacts' child recursively if the option is enabled. Within each artifact, the metadata and
    * attributes are published.
    */

   //@formatter:off
   protected void
      processArtifact
         (
            PublishingArtifact                     artifact,
            PublishingAppender                     publishingAppender,
            ArtifactAcceptor                       artifactAcceptor,
            DataRightContentBuilder                dataRightContentBuilder,
            //server-only
            PublishingArtifactLoader.CacheReadMode cacheReadMode,
            ArtifactAcceptor                       includeBookmarkArtifactAcceptor,
            Consumer<PublishingArtifact>           artifactPostProcess
         ) {

      if (this.processedArtifactTracker.isOk(artifact)) {
         return;
      }

      this.processedArtifactTracker.add(artifact);

      boolean startedSection =
         WordRenderUtil.renderArtifact
            (
               this.allAttributes,
               this.allowedOutlineTypes,
               this.applicabilityTokens,
               this.attributeOptionsList,
               this::processAttribute,
               artifact,
               artifactAcceptor,
               artifactPostProcess,
               this.contentAttributeOnly,
               this.contentAttributeType,
               dataRightContentBuilder,
               this.emptyFoldersArtifactAcceptor,
               this.excludedArtifactTypeArtifactAcceptor,
               this.formatIndicator,
               this.headingArtifactTypeToken,
               this.headingAttributeTypeToken,
               ( lambdaHeadingText ) -> this.headingTextProcessor( lambdaHeadingText, artifact ),
               includeBookmarkArtifactAcceptor,
               this.includeHeadings,
               this.includeMainContentForHeadings,
               this.includeMetadataAttributes,
               this::nonTemplateArtifactHandler,
               this.publishingTemplate.getPublishOptions().getMetadataOptions(),
               this.publishingTemplate.getRelationTableOptions(),
               this.tokenService,
               PresentationType.PREVIEW,
               publishingAppender,
               false, /*publishInline*/
               () -> this.getOrderedAttributeTypes( artifact.getValidAttributeTypes() ),
               this.outlineNumber,
               this.overrideOutlineNumber,
               ( lambdaArtifact, lambdaParagraphNumber ) -> this.paragraphNumberUpdater(lambdaParagraphNumber.toString(), lambdaArtifact),
               this.wordRenderApplicabilityChecker
            );

      var children = List.<PublishingArtifact> of();

      if (this.recurseChildren) {

         //@formatter:off
         children =
            this.publishingArtifactLoader
               .getChildren( artifact, FilterForView.YES, cacheReadMode )
               .orElseThrow
                  (
                     ( dataAccessException ) -> new OseeCoreException
                                                       (
                                                          new Message()
                                                                 .title( "WordTemplateProcessorServer::processArtifact, failed to load children of artifact." )
                                                                 .indentInc()
                                                                 .segment( "Artifact", artifact.getIdString() )
                                                                 .reasonFollows( dataAccessException )
                                                                 .toString(),
                                                          dataAccessException
                                                       )
                  );

         if (!children.isEmpty()) {

            if (startedSection) {
               this.outlineNumber.startLevel();
            }

            for (final var childArtifact : children) {

               if (childArtifact == null) {

                  this.publishingErrorLog.error(artifact, "Artifact has an empty child relation");
                  continue;
               }

               this.processArtifact
                  (
                     childArtifact,
                     publishingAppender,
                     artifactAcceptor,
                     dataRightContentBuilder,
                     //server-only
                     cacheReadMode,
                     includeBookmarkArtifactAcceptor,
                     artifactPostProcess
                  );

            }

            if (startedSection) {
               this.outlineNumber.endLevel();
            }

         }

      }

      if (startedSection) {

         this.outlineNumber.nextSection();

         if (!this.outlineNumber.isAboveMaximumOutlingLevel()) {

            publishingAppender.endSubSection();

         }

      }

      this.processedArtifactTracker.setOk(artifact);

   }
   //@formatter:on

   /**
    * Third step of the publishing process, this is where the processed content of the publish is handled in between the
    * beginning and end of the render template. In the default implementation, the artifact hierarchy is processed
    * starting from our head artifact, then any errors are added in their own final section. This section is likely to
    * be overridden by subclasses.
    */

   //@formatter:off
   protected void
      processArtifactSet
         (
            List<PublishingArtifact> artifacts,
            PublishingAppender       publishingAppender
         ) {

      this.emptyFoldersArtifactAcceptor = ArtifactAcceptor.ok();

      artifacts.forEach
         (
            (artifact) -> processArtifact
                             (
                                artifact,
                                publishingAppender,
                                ArtifactAcceptor.ok(),
                                (DataRightContentBuilder) null,
                                //server-only
                                PublishingArtifactLoader.CacheReadMode.LOAD_FROM_DATABASE,
                                IncludeBookmark.NO.getArtifactAcceptor(),
                                null
                             )
         );

   }
   //@formatter:on

   /**
    * The default implementation does not render word ole data or relation order. This method gets the values for the
    * attributes and calls renderWordTemplateContent if of type Word Template Content, renderAttribute if any other
    * valid attribute
    */

   //@formatter:off
   protected void
      processAttribute
         (
            PublishingArtifact artifact,
            PublishingAppender publishingAppender,
            AttributeOptions   attributeOptions,
            AttributeTypeToken attributeType,
            boolean            allAttrs,
            PresentationType   presentationType,
            boolean            publishInLine,
            String             footer,
            IncludeBookmark    includeBookmark
         ) {

      /*
       * Do not publish empty, invalid attribute types, OleData, or RelationOrder
       */

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

      if( attributeType.equals(this.formatIndicator.getContentAttributeTypeToken())) {

         this.renderMainContent
            (
               artifact,
               presentationType,
               publishingAppender,
               attributeOptions.getFormat(),
               attributeOptions.getLabel(),
               footer,
               includeBookmark
           );

         return;
      }

      WordRenderUtil.renderAttribute
         (
            this.formatIndicator,
            attributeType,
            null,
            artifact,
            publishingAppender,
            attributeOptions.getLabel(),
            attributeOptions.getFormat()
         );

   }
   //@formatter:on

   protected CharSequence headingTextProcessor(CharSequence headingText, PublishingArtifact artifact) {

      if (this.publishingArtifactLoader.isChangedArtifact(artifact)) {
         headingText = WordCoreUtil.appendInlineChangeTagToHeadingText(headingText);
      }

      return headingText;

   }

   protected void nonTemplateArtifactHandler(PublishingArtifact publishingArtifact) {
      this.publishingErrorLog.error(publishingArtifact, "WholeWordContent and NativeContent are not supported.");
   }

   protected void paragraphNumberUpdater(String paragraphNumber, PublishingArtifact artifact) {
      if (this.renderer.isRendererOptionSetAndTrue(RendererOption.UPDATE_PARAGRAPH_NUMBERS)) {
         this.artParagraphNumbers.put(artifact, paragraphNumber);
      }
   }

   /**
    * This method derives from the WordTemplateRenderer on the client, used to render word template content attribute.
    * Uses WordTemplateContentRendererHandler to render the word ml. Also handles OSEE_Link errors if there are
    * artifacts that are linking to artifacts that aren't included in the publish.
    */

   protected void renderMainContent(PublishingArtifact artifact, PresentationType presentationType,
      PublishingAppender publishingAppender, String format, String label, String footer,
      IncludeBookmark includeBookmark) {

      if (this.formatIndicator.isMarkdown()) {

         var markdownContent = artifact.getSoleAttributeAsString(CoreAttributeTypes.MarkdownContent);
         ArtifactId viewId = branchSpecification.getViewId();

         List<BatFile> configurationList;

         if (viewId.isValid()) {
            configurationList = (List<BatFile>) applicOps.getBlockApplicabilityConfigurationFromView(
               branchSpecification.getBranchIdWithOutViewId(), viewId);
         } else {
            configurationList = (List<BatFile>) applicOps.getBlockApplicabilityToolConfiguration(
               branchSpecification.getBranchIdWithOutViewId(), "");
         }

         // Perhaps reassignment isn't necessary?
         markdownContent = applicOps.processApplicability(markdownContent, "", "md", configurationList.get(0));

         //@formatter:off

         Pattern oseeImageLinkPattern =
            Pattern.compile("<oseeimagelink>\\[(.*?)\\]-\\[(.*?)\\]</oseeimagelink>");
         Matcher imageLinkMatcher = oseeImageLinkPattern.matcher(markdownContent);
         while (imageLinkMatcher.find()) {
            String idStr = imageLinkMatcher.group(1);
            String name = sanitizeFileName(imageLinkMatcher.group(2));

            // Only .png supported. Update if expanded to support addition image types.
            String mdLink = "![" + name + "](resources/" + name + ".png \"" + name + "\")";
            markdownContent = markdownContent.replaceFirst("<oseeimagelink>(.*?)</oseeimagelink>", mdLink);


            linkedMdImages.put(idStr, name);
         }

         publishingAppender
            .append( "\n\n" )
            .append( markdownContent )
            .append( "\n\n" );
         //@formatter:on

         return;
      }

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
              this.branchSpecification.getViewId(),
              publishingAppender,
              this.renderer,
              presentationType,
              label,
              footer,
              this.desktopClientLoopbackUrl,
              this.publishingArtifactLoader.isChangedArtifact(artifact),
              includeBookmark,
              artifact.isHistorical()
                 ? this.orcsApi.getTransactionFactory().getTx( artifact.getTransaction() )
                 : TransactionToken.SENTINEL,
              unknownGuids,
              ( wordTemplateContentData ) -> this.wordTemplateContentRendererHandler
                                                .renderWordMLForArtifact
                                                   (
                                                      artifact,
                                                      wordTemplateContentData
                                                   ),
              ( exception ) -> this.publishingErrorLog.error( artifact, exception.getMessage() )
            );

         this.trackDocumentLinks(artifact, wordMlContentDataAndFooter, unknownGuids);
      //@formatter:on
   }

   protected void sortQueryListByAttributeAlphabetical(List<PublishingArtifact> artifacts,
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

   protected void sortQueryListByHierarchy(List<PublishingArtifact> artifacts) {

      artifacts.sort(hierarchyComparator);

      for (final var entry : hierarchyComparator.errors.entrySet()) {

         final var artifact = entry.getKey();
         final var description = entry.getValue();

         this.publishingErrorLog.error(artifact, description);
      }
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

      Pattern bookmarkHyperlinkPattern = Pattern.compile(
         "(" + WordCoreUtil.OSEE_BOOKMARK_START_REGEX + ")|(" + WordCoreUtil.OSEE_HYPERLINK_REGEX + ")");

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

   /**
    * While processing the Word Template Content of an artifact, this method is used for keeping track of OSEE links
    * inside that content and how it relates to the other artifacts that are in this published document.
    * <p>
    * {@link #hyperlinkedIds} tracks the artifacts that have been linked to from another artifact.
    */

   protected void trackHyperLinks(ArtifactReadable artifact, String data, Set<String> unknownIds) {

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

      Pattern bookmarkHyperlinkPattern = Pattern.compile(WordCoreUtil.OSEE_HYPERLINK_REGEX);

      Matcher match = bookmarkHyperlinkPattern.matcher(data);

      while (match.find()) {

         /*
          * Artifact Identifier or GUID of the artifact being linked to
          */

         var hyperlinkIdentifier = match.group(1);

         if (!hyperlinkedIds.containsKey(hyperlinkIdentifier)) {
            hyperlinkedIds.put(hyperlinkIdentifier, artifact);
         }

      }
   }

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      long currentTime = System.currentTimeMillis();
      long elapsedTime = currentTime - this.startTime;
      long hours = elapsedTime / 3600000;
      elapsedTime %= 3600000;
      long minutes = elapsedTime / 60000;
      elapsedTime %= 60000;
      long seconds = elapsedTime / 1000;

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "WordTemplateProcessorServer, publish summary." )
         .indentInc()
         .segment( "Time", String.format( "%s:%s:%s", hours, minutes, seconds ) )
         .toMessage( this.publishingArtifactLoader )
         ;

      return outMessage;
   }

   @Override
   public String toString() {

      return this.toMessage( 0,  null ).toString();
   }

   /**
    * Goes through each artifact and reassigns its' paragraph number attribute to the paragraph number calculated on it
    * during this publish.
    */

   protected void updateParagraphNumbers() {
      TransactionBuilder transaction = orcsApi.getTransactionFactory().createTransaction(
         this.branchSpecification.getBranchIdWithOutViewId(), "Update paragraph number on artifact");
      int count = 0;

      for (Map.Entry<ArtifactReadable, CharSequence> art : artParagraphNumbers.entrySet()) {
         if (art.getKey().isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
            transaction.setSoleAttributeValue(art.getKey(), CoreAttributeTypes.ParagraphNumber, art.getValue());
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
