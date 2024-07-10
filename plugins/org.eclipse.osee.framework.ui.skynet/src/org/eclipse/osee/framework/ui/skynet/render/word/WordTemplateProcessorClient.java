/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render.word;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.define.rest.api.publisher.datarights.DataRightsEndpoint;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.publishing.AllowedOutlineTypes;
import org.eclipse.osee.framework.core.publishing.AttributeOptions;
import org.eclipse.osee.framework.core.publishing.DataRightContentBuilder;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.IncludeBookmark;
import org.eclipse.osee.framework.core.publishing.IncludeHeadings;
import org.eclipse.osee.framework.core.publishing.IncludeMainContentForHeadings;
import org.eclipse.osee.framework.core.publishing.IncludeMetadataAttributes;
import org.eclipse.osee.framework.core.publishing.OutlineNumber;
import org.eclipse.osee.framework.core.publishing.OutliningOptions;
import org.eclipse.osee.framework.core.publishing.ProcessedArtifactTracker;
import org.eclipse.osee.framework.core.publishing.PublishIoException;
import org.eclipse.osee.framework.core.publishing.PublishingAppender;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.core.publishing.SectionNumberWhenMaximumOutlineLevelExceeded;
import org.eclipse.osee.framework.core.publishing.TrailingDot;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordRenderApplicabilityChecker;
import org.eclipse.osee.framework.core.publishing.WordRenderUtil;
import org.eclipse.osee.framework.core.publishing.artifactacceptor.ArtifactAcceptor;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.word.WordCoreUtilClient;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.swt.program.Program;

/**
 * @author Robert A. Fisher
 * @author Jeff C. Phillips
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 * @link WordTemplateProcessorTest
 */
public class WordTemplateProcessorClient {

   private static final String ARTIFACT = "Artifact";

   protected static final String FONT = "Times New Roman";

   private static final String NESTED_TEMPLATE = "NestedTemplate";

   private static final String staticPermanentLinkUrl =
      String.format("http://%s:%s/", ClientSessionManager.getClientName(), ClientSessionManager.getClientPort());

   public static final String STYLES = "<w:lists>.*?</w:lists><w:styles>.*?</w:styles>";

   private static final Program wordApp = Program.findProgram("doc");

   private boolean allAttributes;

   /**
    * Saves the types of artifacts that may be used for outlining.
    */

   private AllowedOutlineTypes allowedOutlineTypes;

   private HashMap<ApplicabilityId, ApplicabilityToken> applicabilityTokens;

   private List<AttributeOptions> attributeOptionsList;

   private BranchId branchId;

   private final ArtifactTypeToken contentArtifactType;

   private AttributeTypeToken contentAttributeType;

   /**
    * Saves a handle to the {@link DataRightsEndpoint}.
    */

   private final DataRightsEndpoint dataRightsEndpoint;

   private String elementType;

   private ArtifactAcceptor emptyFoldersArtifactAcceptor;

   private ArtifactAcceptor excludedArtifactTypeArtifactAcceptor;

   IContainer folder;

   private final FormatIndicator formatIndicator;

   private ArtifactTypeToken headingArtifactTypeToken;

   private AttributeTypeToken headingAttributeTypeToken;

   private IncludeHeadings includeHeadings;

   private IncludeMainContentForHeadings includeMainContentForHeadings;

   private IncludeMetadataAttributes includeMetadataAttributes;

   private String initialOutlineNumber = null;

   private boolean isDiff;

   private Integer maximumOutlineDepth;

   private int nestedCount;

   final List<Artifact> nonTemplateArtifacts = new LinkedList<>();

   private OutlineNumber outlineNumber;

   private String outlineType;

   private String overrideClassification;

   private boolean overrideOutlineNumber = false;

   //private CharSequence paragraphNumber = null;

   private final String permanentLinkUrl;

   private PresentationType presentationType;

   private PublishingTemplate primaryPublishingTemplate;

   /**
    * Tracks artifacts that have been processed by {@link ArtifactId} and GUID.
    */

   protected ProcessedArtifactTracker processedArtifactTracker;

   private PublishingTemplate publishingTemplate;

   private Boolean recurseChildren;

   /**
    * Used to read publishing options from the parent renderer and to write back publishing parameters.
    */

   private WordTemplateRenderer renderer;

   private PublishingTemplate secondaryPublishingTemplate;

   private boolean templateFooter;

   protected WordRenderApplicabilityChecker wordRenderApplicabilityChecker;

   private boolean contentAttributeOnly;

   private final OrcsTokenService tokenService;

   private ArtifactId viewId;

   public WordTemplateProcessorClient() {

      final var oseeClient = ServiceUtil.getOseeClient();

      this.allAttributes = false;
      this.attributeOptionsList = null;
      this.allowedOutlineTypes = AllowedOutlineTypes.ANYTHING;
      this.branchId = null;
      this.contentArtifactType = null;
      this.contentAttributeOnly = false;
      this.contentAttributeType = null;
      this.dataRightsEndpoint = oseeClient.getDataRightsEndpoint();
      this.elementType = null;
      this.emptyFoldersArtifactAcceptor = null;
      this.excludedArtifactTypeArtifactAcceptor = null;
      this.folder = null;
      this.formatIndicator = FormatIndicator.WORD_ML;
      this.headingArtifactTypeToken = null;
      this.headingAttributeTypeToken = null;
      this.includeHeadings = null;
      this.includeMainContentForHeadings = null;
      this.includeMetadataAttributes = null;
      this.initialOutlineNumber = null;
      this.maximumOutlineDepth = null;
      this.outlineNumber = null;
      this.overrideOutlineNumber = false;
      this.permanentLinkUrl = WordTemplateProcessorClient.staticPermanentLinkUrl;
      this.presentationType = null;
      this.primaryPublishingTemplate = null;
      this.processedArtifactTracker = new ProcessedArtifactTracker();
      this.publishingTemplate = null;
      this.recurseChildren = null;
      this.renderer = null;
      this.secondaryPublishingTemplate = null;
      this.templateFooter = false;
      this.tokenService = oseeClient.tokenService();
      this.viewId = null;
      this.wordRenderApplicabilityChecker = null;
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    *
    * @param artifacts null if the template defines the artifacts to be used in the publishing
    * @param templateContent the publishing template Word Ml
    * @param templateOptions the publishing template JSON publish options
    * @param templateStyles when non-<code>null</code> the publishing template styles will be replaced
    * @param folder null when not using an extension template
    * @param initialOutlineNumber if null will find based on first artifact
    * @param presentationType
    * @param outputStream when non-<code>null</code> generated WordMl is appended to this {@link OutputStream}. When
    * null the Word ML is written to a buffer.
    * @return when <code>outputStream</code> is non-<code>null</code>, <code>null</code>; otherwise, an
    * {@link InputStream} that reads from the buffer the WordML was written to.
    */

   public InputStream applyTemplate(List<Artifact> artifacts, OutputStream outputStream) {

      if (!artifacts.isEmpty()) {

         Branch fullBranch = BranchManager.getBranch(this.branchId);

         if (fullBranch.getBranchType().equals(BranchType.MERGE)) {
            fullBranch = fullBranch.getParentBranch();
         }

         ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(fullBranch);

         this.applicabilityTokens = new HashMap<>();

         Collection<ApplicabilityToken> appTokens = applEndpoint.getApplicabilityTokens();

         for (ApplicabilityToken token : appTokens) {
            applicabilityTokens.put(token, token);
         }
      }

      /*
       * Get the initial outline number
       */

      final var firstArtifact = artifacts.isEmpty() ? null : new WordRenderArtifactWrapperClientImpl(artifacts.get(0));

      //@formatter:off
      this.initialOutlineNumber =
         this.outlineNumber.isValidOutlineNumber( this.initialOutlineNumber )
            ? this.initialOutlineNumber
            : WordRenderUtil.getStartingParagraphNumber(firstArtifact, publishingTemplate);
      //@formatter:on

      this.outlineNumber.setOutlineNumber(this.initialOutlineNumber);

      /*
       * Setup the Publishing Appender
       */

      PublishingAppender wordMlUnfinal = null;
      InputStream inputStream = null;
      BufferedWriter bufferedWriter = null;

      if (Objects.nonNull(outputStream)) {
         /*
          * An output stream was provided, write data to it.
          */
         var outputStreamWriter = new OutputStreamWriter(outputStream);
         bufferedWriter = new BufferedWriter(outputStreamWriter);
         wordMlUnfinal = this.formatIndicator.createPublishingAppender(bufferedWriter);
      } else {
         /*
          * An output stream was not provided, write data to a buffer.
          */
         try {
            var charBak = new CharBackedInputStream();
            wordMlUnfinal = this.formatIndicator.createPublishingAppender(charBak);
            inputStream = charBak;
         } catch (CharacterCodingException e) {
            //@formatter:off
            throw
               new OseeCoreException
                      (
                         new Message()
                                .title( "WordTemplateProcessorClient::applyTemplate, failed to create \"CharBackedInputStream\" for writting Word ML." )
                                .reasonFollows( e )
                                .toString(),
                         e
                      );
            //@formatter:on
         }
      }

      var publishingAppender = wordMlUnfinal;

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
            this.outlineType
         );
       //@formatter:on

      try {

         Consumer<CharSequence> segmentProcessor;

         //@formatter:off
         switch( this.elementType )
         {
            case WordTemplateProcessorClient.ARTIFACT:
            {
               segmentProcessor =
                  new Consumer<CharSequence> () {
                     @Override
                     public void accept( CharSequence segment ) {

                        publishingAppender.append( segment );

                        if(    ( presentationType == PresentationType.SPECIALIZED_EDIT )
                            && ( artifacts.size() == 1 ) ) {

                           /*
                            * Do not process headings for editing
                            */

                           WordTemplateProcessorClient.this.includeHeadings = IncludeHeadings.NEVER;

                        }

                        WordTemplateProcessorClient.this.processArtifactSet
                           (
                              artifacts,
                              publishingAppender
                           );
                     }
                  };
            }
            break;

            case WordTemplateProcessorClient.NESTED_TEMPLATE:
            {
               segmentProcessor =
                  new Consumer<CharSequence> () {
                     @Override
                     public void accept( CharSequence segment ) {

                        publishingAppender.append( segment );

                        WordTemplateProcessorClient.this.parseNestedTemplateOptions( publishingAppender );

                     }
                  };
            }
            break;

            default:
            {
               throw new OseeArgumentException("Invalid ElementType [%s]", this.elementType );
            }
         }

         WordCoreUtil.processPublishingTemplate
            (
               this.publishingTemplate.getTemplateContent(),
               segmentProcessor,
               ( tail ) ->
               {
                  var cleanFooterText = WordCoreUtil.cleanupFooter( tail );
                  publishingAppender.append( cleanFooterText );
               }
           );
         //@formatter:on

         displayNonTemplateArtifacts(nonTemplateArtifacts,
            "Only artifacts of type Word Template Content are supported in this case.");

      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         if (Objects.nonNull(outputStream) && Objects.nonNull(bufferedWriter)) {

            try {
               bufferedWriter.close();
               outputStream.close();
            } catch (IOException ex) {
               throw new RuntimeException(ex);
            }
         }
      }

      return inputStream;
   }

   public WordTemplateProcessorClient configure(WordTemplateRenderer renderer, PublishingTemplate publishingTemplate,
      PublishingTemplate secondaryPublishingTemplate, IContainer folder, PresentationType presentationType) {

      /*
       * Publishing Template
       */

      this.publishingTemplate = publishingTemplate;

      this.primaryPublishingTemplate =
         Objects.isNull(this.primaryPublishingTemplate) ? publishingTemplate : this.primaryPublishingTemplate;

      this.secondaryPublishingTemplate = Objects.isNull(
         this.secondaryPublishingTemplate) ? secondaryPublishingTemplate : this.secondaryPublishingTemplate;

      /*
       * Publishing Options
       */

      this.renderer = renderer;

      /*
       * All Attributes
       */

      this.allAttributes = this.renderer.isRendererOptionSetAndTrue(RendererOption.ALL_ATTRIBUTES);

      /**
       * Publishing Format
       * <p>
       * Client side publishing only support Word Markup Language.
       */

      /*
       * Content Attribute Only
       */

      this.contentAttributeOnly = renderer.getRendererOptionValue(RendererOption.CONTENT_ATTRIBUTE_ONLY);

      /*
       * Element Type
       */

      this.elementType = this.publishingTemplate.getPublishOptions().getElementType();

      //@formatter:off
      if (    !WordTemplateProcessorClient.ARTIFACT.equals( this.elementType )
           && !WordTemplateProcessorClient.NESTED_TEMPLATE.equals(this.elementType ) ) {

         throw
            new OseeCoreException
                   (
                      new Message()
                            .title( "WordTemplateProcessor::applyTemplate, publishing is only implemented for ARTIFACT and NESTED_TEMPLATE element types.")
                            .indentInc()
                            .segment( "Publishing Template", this.publishingTemplate.getName() )
                            .segment( "Element Type",        this.elementType                  )
                            .toString()
                   );
      }

      /*
       * Folder
       */

      this.folder = folder;

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

      /*
       * Outline Number
       */

      this.outlineNumber = new OutlineNumber(this.maximumOutlineDepth, 1, TrailingDot.NO,
         SectionNumberWhenMaximumOutlineLevelExceeded.INCREMENT_CURRENT_LEVEL);

      /*
       * Outline Type
       */

      this.outlineType = (String) this.renderer.getRendererOptionValue(RendererOption.OUTLINE_TYPE);

      /*
       * Override Classification
       */

      String overrideDataRights = (String) renderer.getRendererOptionValue(RendererOption.OVERRIDE_DATA_RIGHTS);

      this.overrideClassification =
         DataRightsClassification.isValid(overrideDataRights) ? overrideDataRights : "invalid";

      /*
       * Presentation Type
       */

      this.presentationType = presentationType;

      /*
       * Branch Identifier
       */

      this.branchId = this.renderer.getRendererOptionValue(RendererOption.BRANCH);

      /*
       * View Identifier
       */

      this.viewId = this.renderer.getRendererOptionValue(RendererOption.VIEW);

      /*
       * Applicability Checker
       */

      //@formatter:off
      this.wordRenderApplicabilityChecker =
         new WordRenderApplicabilityChecker
                (
                   (branchId, viewId) -> WordCoreUtilClient.getNonApplicableArtifacts(this.branchId, this.viewId)
                );
      //@formatter:on

      this.wordRenderApplicabilityChecker.load(this.branchId, this.viewId);

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
            ( allowedOutlineTypes                 ) -> this.allowedOutlineTypes                  = allowedOutlineTypes,
            ( contentAttributeType                ) -> this.contentAttributeType                 = contentAttributeType,
            ( excludeArtifactTypes                ) -> this.excludedArtifactTypeArtifactAcceptor = WordRenderUtil.getExcludedArtifactTypeArtifactAcceptor( excludeArtifactTypes ),
            ( headingArtifactType                 ) -> this.headingArtifactTypeToken             = headingArtifactType,
            ( headingAttributeType                ) -> this.headingAttributeTypeToken            = headingAttributeType,
            ( includeHeadings                     ) -> this.includeHeadings                      = includeHeadings,
            ( includeMainContentForHeadings       ) -> this.includeMainContentForHeadings        = includeMainContentForHeadings,
            ( includeMetadataAttributes           ) -> this.includeMetadataAttributes            = includeMetadataAttributes,
            ( initialOutlineNumber                ) -> this.initialOutlineNumber                 = initialOutlineNumber,
            ( overrideOutlineNumber               ) -> this.overrideOutlineNumber                = overrideOutlineNumber,
            ( recurseChildren                     ) -> this.recurseChildren                      = recurseChildren,
            ( templateFooter                      ) -> this.templateFooter                       = templateFooter
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

   private void displayNonTemplateArtifacts(final Collection<Artifact> artifacts, final String warningString) {
      if (!artifacts.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               ArrayList<Artifact> nonTempArtifacts = new ArrayList<>(artifacts.size());
               nonTempArtifacts.addAll(artifacts);
               WordUiUtil.displayUnhandledArtifacts(artifacts, warningString);
            }
         });
      }
   }

   private void parseNestedTemplateOptions(PublishingAppender wordMl) {
      try {
         var rendererOptions = this.primaryPublishingTemplate.getPublishOptions();
         var nestedTemplatesArray = rendererOptions.getNestedTemplates();

         if (nestedCount < nestedTemplatesArray.length) {

            var nestedTemplates = nestedTemplatesArray[this.nestedCount++];

            var outlineType = nestedTemplates.getOutlineType();

            var sectionNumber = nestedTemplates.getSectionNumber();

            final var key = nestedTemplates.getKey();
            final var value = nestedTemplates.getValue();
            final var branch = (BranchId) renderer.getRendererOptionValue(RendererOption.BRANCH);

            List<Artifact> artifacts = null;

            switch (key) {
               case "Id":
                  List<ArtifactId> artIds = Arrays.asList(ArtifactId.valueOf(value));
                  artifacts = ArtifactQuery.getArtifactListFrom(artIds, branch, DeletionFlag.EXCLUDE_DELETED);
                  break;
               case "Name":
                  artifacts = ArtifactQuery.getArtifactListFromName(value, branch);
                  break;
               default:
                  Conditions.invalidCase(key, "Key from a Nested Publishing Template", OseeCoreException::new);
            }

            var subDocName = nestedTemplates.getSubDocName();
            var subDocFileName = subDocName + ".xml";

            if (isDiff) {
               WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);
               if (artifacts != null) {
                  templateFileDiffer.generateFileDifferences(artifacts, "/results/" + subDocFileName, sectionNumber,
                     outlineType, recurseChildren);
               }

            } else {

            //@formatter:off
               var inputStream =
                  this
                     .configure
                        (
                          this.renderer,
                          secondaryPublishingTemplate,
                          null,
                          this.folder,
                          this.presentationType
                        )
                     .applyTemplate
                        (
                          artifacts,
                          null
                        );


               IFile file = folder.getFile( new Path( subDocFileName ) );

               try {

                  AIFile.writeToFile( file, inputStream );

               } catch( Exception e ) {

                  final var cause = e.getCause();

                  if( !( cause instanceof CoreException ) ) {
                     throw e;
                  }

                  final var coreException = (CoreException) cause;
                  final var status = coreException.getStatus();
                  final var code = status.getCode();

                  if( code != IResourceStatus.FAILED_WRITE_LOCAL ) {
                     throw e;
                  }

                  final var osStringPath = file.getLocation().toOSString();

                  final var publishIoException =
                     new PublishIoException( "Could not write file.", osStringPath, e );

                  throw publishIoException;

               }
               //@formatter:on
            }
            wordMl.createHyperLinkDoc(subDocFileName);
         }

      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   //@formatter:off
   private void
      processArtifact
         (
            PublishingArtifact      artifact,
            PublishingAppender      publishingAppender,
            ArtifactAcceptor        artifactAcceptor,
            DataRightContentBuilder dataRightContentBuilder,
            String                  outlineType,
            PresentationType        presentationType
         ) {

      if (this.processedArtifactTracker.contains(artifact)) {
         return;
      }

      this.processedArtifactTracker.add(artifact);

      boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);

      /*
       * Render and append the artifact's content. startedSection is true when the artifact content contains a
       * sub-section open tag.
       */

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
               null,
               this.contentAttributeOnly,
               this.contentAttributeType,
               dataRightContentBuilder,
               this.emptyFoldersArtifactAcceptor,
               this.excludedArtifactTypeArtifactAcceptor,
               formatIndicator,
               this.headingArtifactTypeToken,
               this.headingAttributeTypeToken,
               ( lambdaHeadingText ) -> this.headingTextProcessor( lambdaHeadingText, artifact ),
               IncludeBookmark.NO.getArtifactAcceptor(),
               this.includeHeadings,
               this.includeMainContentForHeadings,
               this.includeMetadataAttributes,
               this::nonTemplateArtifactHandler,
               this.publishingTemplate.getPublishOptions().getMetadataOptions(),
               this.tokenService,
               presentationType,
               publishingAppender,
               publishInline,
               () -> RendererManager.getAttributeTypeOrderList( ((WordRenderArtifactWrapperClientImpl) artifact).getArtifact() ),
               this.outlineNumber,
               this.overrideOutlineNumber,
               ( lambdaArtifact, lambdaParagraphNumber ) -> this.paragraphNumberUpdater(lambdaParagraphNumber.toString(), lambdaArtifact),
               this.wordRenderApplicabilityChecker
            );

      var children = List.<PublishingArtifact> of();

      if (this.recurseChildren) {

         try {

            children = artifact.getChildrenAsPublishingArtifacts();

         } catch (Exception e) {

            //Eat the error, client processing does not have a publishing error log yet
         }

         if (!children.isEmpty()) {

            if (startedSection) {
               this.outlineNumber.startLevel();
            }

            for (final var childArtifact : children) {

               if (childArtifact == null) {

                  //Eat the null artifact, client processing does not have a publishing error log yet
                  continue;
               }

               this.processArtifact
                  (
                     childArtifact,
                     publishingAppender,
                     artifactAcceptor,
                     dataRightContentBuilder,
                     outlineType,
                     presentationType
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

   }
   //@formatter:on

   /**
    * Generates the Word ML to be inserted into the publishing template replacing the publishing template's "insert
    * artifact here" token. The provided artifacts and optionally their hierarchical descendants are rendered to Word
    * ML.
    */

   //@formatter:off
   private void
      processArtifactSet
         (
            List<Artifact>     nativeArtifacts,
            PublishingAppender wordMl
         ) {

      this.nonTemplateArtifacts.clear();

      if (Strings.isValid(this.initialOutlineNumber)) {
         this.outlineNumber.setOutlineNumber(this.initialOutlineNumber);
      }

      final List<PublishingArtifact> artifacts =
         nativeArtifacts
            .stream()
            .map( WordRenderArtifactWrapperClientImpl::new )
            .collect( Collectors.toCollection( LinkedList::new ) );

      this.emptyFoldersArtifactAcceptor =
         WordRenderUtil.populateEmptyHeaders
            (
               artifacts,
               this.includeHeadings,
               this.allowedOutlineTypes,
               this.headingArtifactTypeToken,
               this.contentAttributeType,
               this.excludedArtifactTypeArtifactAcceptor,
               ArtifactAcceptor.not( ArtifactReadable::isHistorical )
            );

      /*
       * Has a difference report been requested?
       */

      if ((boolean) renderer.getRendererOptionValue(RendererOption.PUBLISH_DIFF)) {

         /*
          * Diversion for difference processing..beware all ye who enter here. Diffs are only generated on the Client.
          */

         WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);

         templateFileDiffer.generateFileDifferences(nativeArtifacts, "/results/", initialOutlineNumber, outlineType,
            recurseChildren);

      } else {

         /**
          * Setup Data Rights for the publish.
          * <p>
          * DataRightsClassification override comes in as a publishing option string, compare string to all
          * DataRightsClassifications, if they match, set override variable to that classification. This override makes
          * it that the entire published document uses the same data rights footer, regardless of the attribute on
          * artifacts.
          * <p>
          * Given the list of artifacts for the publish, this loops through and adds any recursive artifacts to also be
          * published (if specified through recurseChildren) and determines all of their data rights.
          */

         WordRenderUtil
            .getDataRights
               (
                  /*
                   * Publish artifacts to analyze for data rights
                   */

                  artifacts,

                  /*
                   * The publishing branch
                   */

                  this.branchId,

                  /*
                   * Recursion logic
                   */

                  this.recurseChildren,

                  /*
                   * Not Historical, true -> reject descendants of historical artifacts and historical descendants
                   */

                  true,

                  /*
                   * Data rights classification override
                   */

                  this.overrideClassification,

                  /*
                   * When recursing, this tester accepts or rejects descendant artifacts
                   */

                  ArtifactAcceptor.and
                     (
                        this.wordRenderApplicabilityChecker,
                        this.emptyFoldersArtifactAcceptor
                     ),

                  /*
                   * Client/Server calling of the Data Rights Manager is different.
                   */

                  this.dataRightsEndpoint::getDataRights

               )
            .ifPresent
               (
                  /**
                   * If any artifacts survived the selection process, the artifacts on the original artifact
                   * list without any descendants will be processed.
                   * <p>
                   * The artifact section always copies the artifact on the original artifact list to the artifactIds
                   * list for data rights processing. The only way for the list to be empty is if the original artifact
                   * list is also empty.
                   */

                  ( dataRightContentBuilder ) ->
                     artifacts
                        .forEach
                           (
                              ( artifact ) -> this.processArtifact
                                                 (
                                                    artifact,
                                                    wordMl,
                                                    ArtifactAcceptor.ok(),
                                                    dataRightContentBuilder,
                                                    //client-only
                                                    outlineType,
                                                    presentationType
                                                 )
                           )
               );

         WordUiUtil.getStoredResultData();
      }

      // maintain a list of artifacts that have been processed so we do not
      // have duplicates.

      processedArtifactTracker.clear();
   }
   //@formatter:on

   //@formatter:off
   private void
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

      final var nativeArtifact = ((WordRenderArtifactWrapperClientImpl) artifact).getArtifact();

      renderer.setRendererOption(RendererOption.ALL_ATTRIBUTES, allAttrs);

      // This is for SRS Publishing. Do not publish unspecified attributes

      if(
             !allAttrs
          &&  attributeType.matches( CoreAttributeTypes.Partition, CoreAttributeTypes.SeverityCategory )
          &&  artifact.isAttributeTypeValid( CoreAttributeTypes.Partition )
          &&  nativeArtifact
                 .getAttributes( CoreAttributeTypes.Partition )
                 .stream()
                 .anyMatch
                    (
                       ( partition ) ->    Objects.isNull( partition )
                                        || "Unspecified".equals( partition.getValue() )
                    )
        ) {
         return;
      }

      if (this.contentAttributeOnly && attributeType.notEqual(CoreAttributeTypes.WordTemplateContent)) {
         return;
      }

      var attributes = nativeArtifact.getAttributes(attributeType);

      if (
                  !attributes.isEmpty()

            && (
                     /*
                      * If WordOleData, the attribute is skipped
                      */

                     CoreAttributeTypes.WordOleData.equals( attributeType )

                     /*
                      * Do not publish relation order during publishing
                      */

                 ||  (
                           this.renderer.isRendererOptionSetAndTrue( RendererOption.IN_PUBLISH_MODE )
                        && CoreAttributeTypes.RelationOrder.equals( attributeType )
                     )

                     /*
                      * When publishing in line, artifact has word template content, and the attribute type
                      * is something else; skip it.
                      */

                 || (
                          publishInLine
                       && artifact.isAttributeTypeValid( CoreAttributeTypes.WordTemplateContent )
                       && attributeType.notEqual( CoreAttributeTypes.WordTemplateContent )
                    )

                    /*
                     * If there are no attribute values of the attribute type for the artifact, skip it
                     */

                 ||  nativeArtifact.getAttributes( attributeType ).isEmpty()
               )
         )
      {
         return;
      }

      if( attributeType.equals( CoreAttributeTypes.WordTemplateContent ) )
      {

         var unknownGuids = new HashSet<String>();

         WordRenderUtil.renderWordAttribute
            (
               new WordRenderArtifactWrapperClientImpl( artifact ),
               this.renderer.getRendererOptionValue(RendererOption.VIEW),
               publishingAppender,
               this.renderer.getRendererOptionsView(),
               presentationType,
               attributeOptions.getLabel(),
               footer,
               this.permanentLinkUrl,
               false,
               includeBookmark,
               artifact.isHistorical()
                  ? nativeArtifact.getTransaction()
                  : TransactionToken.SENTINEL,
               unknownGuids,
               ( wordTemplateContentData ) -> PublishingRequestHandler.renderWordTemplateContent( wordTemplateContentData ),
               ( exception ) -> WordUiUtil.displayErrorMessage( nativeArtifact, exception.toString() )
            );

         if( !unknownGuids.isEmpty() ) {
            WordUiUtil.displayUnknownGuids( nativeArtifact, unknownGuids);
         }

      } else {

         if( !attributes.isEmpty() ) {

            WordRenderUtil.renderAttribute
               (
                 this.formatIndicator,
                 attributeType,
                 this.renderer::renderRelationOrder,
                 new WordRenderArtifactWrapperClientImpl( artifact ),
                 publishingAppender,
                 attributeOptions.getLabel(),
                 attributeOptions.getFormat()
               );
         }

      }

   }
   //@formatter:on

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts Only used by Publish SRS
    */

   public void publishWithNestedTemplates(List<Artifact> artifacts) {

      if (Objects.isNull(this.secondaryPublishingTemplate)) {
         this.renderer.setRendererOption(RendererOption.PUBLISHING_TEMPLATE_IDENTIFIER,
            this.primaryPublishingTemplate.getIdentifier());
      } else {
         this.renderer.setRendererOption(RendererOption.PUBLISHING_TEMPLATE_IDENTIFIER,
            this.secondaryPublishingTemplate.getIdentifier());
      }

      nestedCount = 0;

      isDiff = (boolean) this.renderer.getRendererOptionValue(RendererOption.PUBLISH_DIFF);

      // Need to check if all attributes will be published.  If so set the AllAttributes option.
      // Assumes that all (*) will not be used when other attributes are specified
      renderer.setRendererOption(RendererOption.ALL_ATTRIBUTES, false);
      if (attributeOptionsList.size() == 1) {
         String attributeName = attributeOptionsList.get(0).getAttributeName();
         if (attributeName.equals("*")) {
            renderer.setRendererOption(RendererOption.ALL_ATTRIBUTES, true);
         }
      }

      //@formatter:off
      var artifactFilenameSegment =
         Objects.nonNull( artifacts ) && ( artifacts.size() > 0 )
            ? artifacts.get(0).getSafeName()
            : "NO-ARTIFACTS";
      //@formatter:on

      //@formatter:off
      RenderingUtil
         .getRenderFile
            (
               renderer,
               PresentationType.PREVIEW,
               RendererUtil.makeRenderPath( CoreBranches.COMMON.getShortName() ),
               "xml",
               artifactFilenameSegment
            )
         .map
            (
               ( iFile ) ->
               {
                  var inputStream =
                     this
                        .configure
                           (
                              this.renderer,
                              this.publishingTemplate,
                              null,
                              iFile.getParent(),
                              PresentationType.PREVIEW
                           )
                        .applyTemplate
                           (
                             artifacts,
                             null
                           );

                  AIFile.writeToFile( iFile, inputStream );

                  return iFile.getLocation();
               }
            )
         .flatMap( RenderingUtil::getOsString )
         .ifPresentOrElse
            (
               ( renderingFileAbsoultePath ) ->
               {
                  if( !( (boolean) renderer.getRendererOptionValue( RendererOption.NO_DISPLAY ) ) && !isDiff ) {

                     RenderingUtil.ensureFilenameLimit( renderingFileAbsoultePath );

                     wordApp.execute( renderingFileAbsoultePath );
                  }
               },
               () -> new OseeCoreException
                            (
                               new Message()
                                      .title( "WordTemplateProcessor::publishWithNestedTemplates, failed to write content file.")
                                      .indentInc()
                                      .segment( "Publishing Template", this.publishingTemplate.getName() )
                                      .toString()
                            )
            );
         ;
      //@formatter:on

   }

   protected CharSequence headingTextProcessor(CharSequence headingText, PublishingArtifact artifact) {

      var includeUUIDs = renderer.isRendererOptionSetAndTrue(RendererOption.INCLUDE_UUIDS);
      var mergeTag = renderer.isRendererOptionSetAndTrue(RendererOption.ADD_MERGE_TAG);

      if (!includeUUIDs && !mergeTag) {
         return headingText;
      }

      var stringBuilder = new StringBuilder(headingText.length() + 256).append(headingText);

      if (includeUUIDs) {

         /*
          * Add UUID
          */

         stringBuilder.append(" <UUID = ").append(artifact.getIdString()).append(">");
      }

      if (mergeTag) {

         /*
          * Add Merge Tag
          */

         stringBuilder.append(" [MERGED]");
      }

      return stringBuilder;

   }

   void nonTemplateArtifactHandler(PublishingArtifact publishingArtifact) {
      var artifact = ((WordRenderArtifactWrapperClientImpl) publishingArtifact).getArtifact();
      this.nonTemplateArtifacts.add(artifact);
   }

   protected void paragraphNumberUpdater(String paragraphNumber, PublishingArtifact artifact) {
      if (paragraphNumber == null) {
         return;
      }
      var localArtifact = ((WordRenderArtifactWrapperClientImpl) artifact).getArtifact();
      if ((boolean) renderer.getRendererOptionValue(RendererOption.UPDATE_PARAGRAPH_NUMBERS)) {
         if (localArtifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
            localArtifact.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, paragraphNumber.toString());

            SkynetTransaction transaction =
               (SkynetTransaction) renderer.getRendererOptionValue(RendererOption.TRANSACTION_OPTION);
            if (transaction != null) {
               localArtifact.persist(transaction);
            } else {
               localArtifact.persist(getClass().getSimpleName());
            }
         }
      }
   }

}
