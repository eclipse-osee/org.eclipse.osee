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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.osee.define.rest.api.publisher.datarights.DataRightsEndpoint;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.publishing.AttributeOptions;
import org.eclipse.osee.framework.core.publishing.DataRightContentBuilder;
import org.eclipse.osee.framework.core.publishing.ProcessedArtifactTracker;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordMLProducer;
import org.eclipse.osee.framework.core.publishing.WordRenderUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactHierarchyComparator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.MSWordTemplateClientRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
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

   private static final String LOAD_EXCLUDED_ARTIFACTIDS =
      "select art_id from osee_artifact art, osee_txs txs where art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.tx_current = 1 and not exists (select null from osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and e1 = ? and t2.gamma_id = txsP.gamma_id and txsP.branch_id = ? and txsP.tx_current = 1 and e2 = txs.app_id)";

   private static final String NESTED_TEMPLATE = "NestedTemplate";

   public static final String STYLES = "<w:lists>.*?</w:lists><w:styles>.*?</w:styles>";

   private static final Program wordApp = Program.findProgram("doc");

   private static final String staticPermanentLinkUrl =
      String.format("http://%s:%s/", ClientSessionManager.getClientName(), ClientSessionManager.getClientPort());

   private HashMap<ApplicabilityId, ApplicabilityToken> applicabilityTokens;

   private HashMap<ArtifactId, ArtifactId> artifactsToExclude;

   private List<AttributeOptions> attributeOptionsList;

   private BranchId branch;

   /**
    * Saves a handle to the {@link DataRightsEndpoint}.
    */

   private final DataRightsEndpoint dataRightsEndpoint;

   private String elementType;

   private final Set<ArtifactId> emptyFolders = new HashSet<>();

   private final List<ArtifactTypeToken> excludeArtifactTypes = new LinkedList<>();

   private boolean excludeFolders;

   private AttributeTypeToken headingAttributeTypeToken;

   private Boolean includeEmptyHeaders;

   private boolean isDiff;

   IContainer folder;

   private Integer maximumOutlineDepth;

   private int nestedCount;

   final List<Artifact> nonTemplateArtifacts = new LinkedList<>();

   private String outlineNumber = null;

   private boolean outlineOnlyHeadersFolders = false;

   private String outlineType;

   private boolean outlining;

   private String overrideClassification;

   private boolean overrideOutlineNumber = false;

   private CharSequence paragraphNumber = null;

   private final String permanentLinkUrl;

   private PresentationType presentationType;

   private PublishingTemplate primaryPublishingTemplate;

   /**
    * Tracks artifacts that have been processed by {@link ArtifactId} and GUID.
    */

   protected ProcessedArtifactTracker processedArtifactTracker;

   private PublishingTemplate publishingTemplate;

   private boolean recurseChildren;

   /**
    * Used to read publishing options from the parent renderer and to write back publishing parameters.
    */

   private MSWordTemplateClientRenderer renderer;

   private PublishingTemplate secondaryPublishingTemplate;

   private boolean templateFooter = false;

   public WordTemplateProcessorClient() {
      this.attributeOptionsList = null;
      this.artifactsToExclude = new HashMap<>();
      this.dataRightsEndpoint = ServiceUtil.getOseeClient().getDataRightsEndpoint();
      this.elementType = null;
      this.folder = null;
      this.maximumOutlineDepth = null;
      this.permanentLinkUrl = WordTemplateProcessorClient.staticPermanentLinkUrl;
      this.presentationType = null;
      this.primaryPublishingTemplate = null;
      this.processedArtifactTracker = new ProcessedArtifactTracker();
      this.publishingTemplate = null;
      this.renderer = null;
      this.secondaryPublishingTemplate = null;
   }

   public WordTemplateProcessorClient configure(MSWordTemplateClientRenderer renderer,
      PublishingTemplate publishingTemplate, PublishingTemplate secondaryPublishingTemplate, IContainer folder,
      String outlineNumber, PresentationType presentationType) {

      this.renderer = renderer;
      this.publishingTemplate = publishingTemplate;

      this.primaryPublishingTemplate =
         Objects.isNull(this.primaryPublishingTemplate) ? publishingTemplate : this.primaryPublishingTemplate;

      this.secondaryPublishingTemplate = Objects.isNull(
         this.secondaryPublishingTemplate) ? secondaryPublishingTemplate : this.secondaryPublishingTemplate;

      //this.nestedCount = 0;

      /*
       * Element Type
       */

      this.elementType = this.publishingTemplate.getRendererOptions().getElementType();

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
      this.maximumOutlineDepth =
         this.renderer.isRendererOptionSet( RendererOption.MAX_OUTLINE_DEPTH )
            ? (Integer) this.renderer.getRendererOptionValue( RendererOption.MAX_OUTLINE_DEPTH )
            : 9;
      //@formatter:on

      /*
       * Outline Number
       */

      this.outlineType = (String) this.renderer.getRendererOptionValue(RendererOption.OUTLINE_TYPE);

      /*
       * Presentation Type
       */

      this.presentationType = presentationType;

      try {
         var rendererOptions = publishingTemplate.getRendererOptions();
         var outliningOptionsArray = rendererOptions.getOutliningOptions();

         if (outliningOptionsArray.length >= 1) {
            var outliningOptions = outliningOptionsArray[0];

            this.outlining = outliningOptions.isOutlining();
            this.outlineOnlyHeadersFolders = outliningOptions.isOutlineOnlyHeaderFolders();
            this.overrideOutlineNumber = outliningOptions.isOverrideOutlineNumber();
            this.recurseChildren = outliningOptions.isRecurseChildren();
            this.includeEmptyHeaders = outliningOptions.isIncludeEmptyHeaders();
            this.outlineNumber = outliningOptions.getOutlineNumber();
            this.templateFooter = outliningOptions.isTemplateFooter();
            this.headingAttributeTypeToken = AttributeTypeManager.getType(outliningOptions.getHeadingAttributeType());
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }

      try {
         var rendererOptions = publishingTemplate.getRendererOptions();
         var attributeOptions = rendererOptions.getAttributeOptions();

         //@formatter:off
         this.attributeOptionsList =
            Arrays
               .stream( attributeOptions )
               .filter
                  (
                     ( attributeOptionsElement ) ->
                     {
                        var attributeType = attributeOptionsElement.getAttributeName();

                        return
                              "*".equals( attributeType )
                           || AttributeTypeManager.typeExists( attributeType );
                     }
                  )
               .collect( Collectors.toList() );
               ;
         //@formatter:on

      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }

      return this;
   }

   /**
    * Parse through template to find xml defining artifact sets and replace it with the result of publishing those
    * artifacts. Only used by Publish SRS
    *
    * @param artifacts null if the template defines the artifacts to be used in the publishing
    * @param templateContent the publishing template Word Ml
    * @param templateOptions the publishing template JSON rendering options
    * @param templateStyles when non-<code>null</code> the publishing template styles will be replaced
    * @param folder null when not using an extension template
    * @param outlineNumber if null will find based on first artifact
    * @param presentationType
    * @param outputStream when non-<code>null</code> generated WordMl is appended to this {@link OutputStream}. When
    * null the Word ML is written to a buffer.
    * @return when <code>outputStream</code> is non-<code>null</code>, <code>null</code>; otherwise, an
    * {@link InputStream} that reads from the buffer the WordML was written to.
    */

   public InputStream applyTemplate(List<Artifact> artifacts, OutputStream outputStream) {

      String overrideDataRights = (String) renderer.getRendererOptionValue(RendererOption.OVERRIDE_DATA_RIGHTS);
      overrideClassification = "invalid";
      if (DataRightsClassification.isValid(overrideDataRights)) {
         overrideClassification = overrideDataRights;
      }

      excludeFolders = (boolean) renderer.getRendererOptionValue(RendererOption.EXCLUDE_FOLDERS);

      if (artifacts.isEmpty()) {
         branch = BranchId.SENTINEL;
      } else {
         branch = artifacts.get(0).getBranch();
         Branch fullBranch = BranchManager.getBranch(branch);
         if (fullBranch.getBranchType().equals(BranchType.MERGE)) {
            fullBranch = fullBranch.getParentBranch();
         }
         ApplicabilityEndpoint applEndpoint = ServiceUtil.getOseeClient().getApplicabilityEndpoint(fullBranch);

         applicabilityTokens = new HashMap<>();
         Collection<ApplicabilityToken> appTokens = applEndpoint.getApplicabilityTokens();
         for (ApplicabilityToken token : appTokens) {
            applicabilityTokens.put(token, token);
         }
      }

      ArtifactId view = (ArtifactId) renderer.getRendererOptionValue(RendererOption.VIEW);
      artifactsToExclude = getNonApplicableArtifacts(artifacts, view == null ? ArtifactId.SENTINEL : view);

      WordMLProducer wordMlUnfinal = null;
      InputStream inputStream = null;
      BufferedWriter bufferedWriter = null;

      if (Objects.nonNull(outputStream)) {
         /*
          * An output stream was provided, write data to it.
          */
         var outputStreamWriter = new OutputStreamWriter(outputStream);
         bufferedWriter = new BufferedWriter(outputStreamWriter);
         wordMlUnfinal = new WordMLProducer(bufferedWriter);
      } else {
         /*
          * An output stream was not provided, write data to a buffer.
          */
         try {
            var charBak = new CharBackedInputStream();
            wordMlUnfinal = new WordMLProducer(charBak);
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

      var wordMl = wordMlUnfinal;

      /*
       * Setup Publishing Template
       */

      //@formatter:off
      var firstPublishingArtifact =
         artifacts.isEmpty()
            ? null
            : new WordRenderArtifactWrapperClientImpl( artifacts.get(0) );

      WordRenderUtil.setupPublishingTemplate
         (
            this.publishingTemplate,
            firstPublishingArtifact,
            wordMl,
            this.outlineNumber,
            this.outlineType,
            this.maximumOutlineDepth
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

                        wordMl.addWordMl( segment );

                        if(    ( presentationType == PresentationType.SPECIALIZED_EDIT )
                            && ( artifacts.size() == 1 ) ) {
                           // for single edit override outlining options
                           WordTemplateProcessorClient.this.outlining = false;
                        }

                        WordTemplateProcessorClient.this.processArtifactSet
                           (
                              artifacts,
                              wordMl
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

                        wordMl.addWordMl( segment );

                        WordTemplateProcessorClient.this.parseNestedTemplateOptions( wordMl );

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
               this.publishingTemplate.getTemplateContent().getTemplateString(),
               segmentProcessor,
               ( tail ) ->
               {
                  var cleanFooterText = WordCoreUtil.cleanupFooter( tail );
                  wordMl.addWordMl( cleanFooterText );
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

   public Set<ArtifactId> getEmptyFolders() {
      return emptyFolders;
   }

   private List<ArtifactTypeToken> getExcludeArtifactTypes() {
      excludeArtifactTypes.clear();

      Object o = renderer.getRendererOptionValue(RendererOption.EXCLUDE_ARTIFACT_TYPES);
      if (o instanceof Collection<?>) {
         Collection<?> coll = (Collection<?>) o;
         Iterator<?> iterator = coll.iterator();
         while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof ArtifactTypeToken) {
               excludeArtifactTypes.add((ArtifactTypeToken) next);
            }
         }

      }

      return excludeArtifactTypes;
   }

   private HashMap<ArtifactId, ArtifactId> getNonApplicableArtifacts(List<Artifact> artifacts, ArtifactId view) {
      HashMap<ArtifactId, ArtifactId> toReturn = new HashMap<>();

      if (artifacts != null && !artifacts.isEmpty()) {
         Object[] objs = {branch, view, branch};

         if (view.isValid()) {
            List<ArtifactId> excludedArtifacts = ArtifactLoader.selectArtifactIds(LOAD_EXCLUDED_ARTIFACTIDS, objs, 300);
            for (ArtifactId artId : excludedArtifacts) {
               toReturn.put(artId, artId);
            }
         }
      }

      return toReturn;
   }

   private boolean isArtifactIncluded(Artifact artifact) {
      boolean excludedArtifact =
         (excludeFolders && artifact.isOfType(CoreArtifactTypes.Folder)) || (artifactsToExclude.containsKey(
            ArtifactId.create(artifact)) || emptyFolders.contains(artifact));
      boolean excludedArtifactType = excludeArtifactTypes != null && isOfType(artifact, excludeArtifactTypes);

      return !excludedArtifact && !excludedArtifactType;
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
   public boolean isEmptyHeaders(List<Artifact> artifacts) {
      boolean hasIncludedChildren = false;
      boolean includeParent = false;
      List<Artifact> children = new LinkedList<>();
      for (Artifact artifact : artifacts) {
         if (artifact.getId().equals(13728L)) {
            System.out.println("Snoopy Empty");
         }
         if (!artifact.isHistorical()) {
            children = artifact.getChildren();
         }
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

   private boolean isOfType(Artifact artifact, List<ArtifactTypeToken> excludeArtifactTypes) {
      for (ArtifactTypeToken artType : excludeArtifactTypes) {
         if (artifact.isOfType(artType)) {
            return true;
         }
      }
      return false;
   }

   private boolean isWordTemplateContentValid(Artifact artifact) {
      return !artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent) && !artifact.isAttributeTypeValid(
         CoreAttributeTypes.NativeContent);
   }

   private void parseNestedTemplateOptions(WordMLProducer wordMl) {
      try {
         var rendererOptions = this.primaryPublishingTemplate.getRendererOptions();
         var nestedTemplatesArray = rendererOptions.getNestedTemplates();

         if (nestedCount < nestedTemplatesArray.length) {

            var nestedTemplates = nestedTemplatesArray[this.nestedCount++];

            var outlineType = nestedTemplates.getOutlineType();

            var sectionNumber = nestedTemplates.getSectionNumber();

            var key = nestedTemplates.getKey();
            RendererOption rendererOption = RendererOption.valueOf(key.toUpperCase());

            var value = nestedTemplates.getValue();
            this.renderer.setRendererOption(rendererOption, value);

            String artifactName = (String) renderer.getRendererOptionValue(RendererOption.NAME);
            String artifactId = (String) renderer.getRendererOptionValue(RendererOption.ID);
            BranchId branch = (BranchId) renderer.getRendererOptionValue(RendererOption.BRANCH);
            List<Artifact> artifacts = null;

            if (Strings.isValid(artifactId)) {

               List<ArtifactId> artIds = Arrays.asList(ArtifactId.valueOf(artifactId));
               artifacts = ArtifactQuery.getArtifactListFrom(artIds, branch, EXCLUDE_DELETED);

            } else if (Strings.isValid(artifactName)) {

               artifacts = ArtifactQuery.getArtifactListFromName(artifactName, branch);

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
                          subDocFileName,
                          this.presentationType
                        )
                     .applyTemplate
                        (
                          artifacts,
                          null
                        );


               IFile file = folder.getFile( new Path( subDocFileName ) );

               AIFile.writeToFile( file, inputStream );
               //@formatter:on
            }
            wordMl.createHyperLinkDoc(subDocFileName);
         }

      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void processArtifact(Artifact artifact, WordMLProducer wordMl, String outlineType,
      PresentationType presentationType, DataRightContentBuilder dataRightContentBuilder) {

      if (isWordTemplateContentValid(artifact)) {

         // If the artifact has not been processed

         if (this.processedArtifactTracker.contains(artifact)) {
            return;
         }

         boolean startedSection = false;

         if (isArtifactIncluded(artifact)) {

            startedSection |=
               this.renderArtifact(artifact, presentationType, wordMl, outlineType, dataRightContentBuilder);

         }

         // Check for option that may have been set from Publish with Diff BLAM to recurse
         boolean recurse = (boolean) renderer.getRendererOptionValue(RendererOption.RECURSE_ON_LOAD);
         boolean origDiff = (boolean) renderer.getRendererOptionValue(RendererOption.ORIG_PUBLISH_AS_DIFF);

         if (recurseChildren && !recurse || recurse && !origDiff) {
            for (Artifact childArtifact : artifact.getChildren()) {
               processArtifact(childArtifact, wordMl, outlineType, presentationType, dataRightContentBuilder);
            }
         }

         if (startedSection) {
            wordMl.endOutlineSubSection();
         }

         this.processedArtifactTracker.add(new WordRenderArtifactWrapperClientImpl(artifact));

      } else {

         nonTemplateArtifacts.add(artifact);

      }
   }

   /**
    * Generates the Word ML to be inserted into the publishing template replacing the publishing template's "insert
    * artifact here" token. The provided artifacts and optionally their hierarchical descendants are rendered to Word
    * ML.
    */

   private void processArtifactSet(List<Artifact> artifacts, WordMLProducer wordMl) {

      this.nonTemplateArtifacts.clear();

      if (Strings.isValid(this.outlineNumber)) {
         wordMl.setNextParagraphNumberTo(this.outlineNumber);
      }

      /**
       * EmptyHeaders can be set in the template RendererOptions, or via the rendererOptions (such as the Publish with
       * Specified Template Blam). Via Template's RendererOptions takes priority, if set, this will check to see if the
       * option to exclude empty headers was set to false. If true, won't run at all regardless of rendererOptions. If
       * template does not set the option, will check to see if set via rendererOptions, again, if set to false it will
       * run, if true it will not run.
       */

      if (Objects.isNull(this.includeEmptyHeaders)) {
         this.includeEmptyHeaders = !this.renderer.isRendererOptionSetAndFalse(RendererOption.PUBLISH_EMPTY_HEADERS);
      }

      if (!includeEmptyHeaders) {
         this.isEmptyHeaders(artifacts);
      }

      /*
       * Has a difference report been requested?
       */

      if ((boolean) renderer.getRendererOptionValue(RendererOption.PUBLISH_DIFF)) {

         /*
          * Diversion for difference processing..beware all ye who enter here. Diffs are only generated on the Client.
          */

         WordTemplateFileDiffer templateFileDiffer = new WordTemplateFileDiffer(renderer);

         templateFileDiffer.generateFileDifferences(artifacts, "/results/", outlineNumber, outlineType,
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

         //@formatter:off
         WordRenderUtil
            .getDataRights
               (
                  /*
                   * Publish artifacts to analyze for data rights
                   */

                  artifacts.stream().map( WordRenderArtifactWrapperClientImpl::new ).collect( Collectors.toCollection( LinkedList::new ) ),

                  /*
                   * The publishing branch
                   */

                  this.branch,

                  /*
                   * Recursion logic
                   */

                      (     this.recurseChildren
                         || (boolean) renderer.getRendererOptionValue( RendererOption.RECURSE_ON_LOAD ) )
                  && !( (boolean) renderer.getRendererOptionValue( RendererOption.ORIG_PUBLISH_AS_DIFF ) ),

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

                  ( publishingArtifact ) -> this.isArtifactIncluded( ((WordRenderArtifactWrapperClientImpl)publishingArtifact).getArtifact() ),

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
                                                    outlineType,
                                                    presentationType,
                                                    dataRightContentBuilder
                                                 )
                           )
               );
         //@formatter:on

         WordUiUtil.getStoredResultData();
      }

      // maintain a list of artifacts that have been processed so we do not
      // have duplicates.

      processedArtifactTracker.clear();
   }

   private void processAttribute(Artifact artifact, WordMLProducer wordMl, AttributeOptions attributeOptions,
      AttributeTypeToken attributeType, boolean allAttrs, PresentationType presentationType, boolean publishInLine,
      String footer) {

      renderer.setRendererOption(RendererOption.ALL_ATTRIBUTES, allAttrs);

      // This is for SRS Publishing. Do not publish unspecified attributes

      //@formatter:off
      if(
             !allAttrs
          &&  attributeType.matches( CoreAttributeTypes.Partition, CoreAttributeTypes.SeverityCategory )
          &&  artifact.isAttributeTypeValid( CoreAttributeTypes.Partition )
          &&  artifact
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
      //@formatter:on

      boolean templateOnly = (boolean) renderer.getRendererOptionValue(RendererOption.TEMPLATE_ONLY);

      if (templateOnly && attributeType.notEqual(CoreAttributeTypes.WordTemplateContent)) {
         return;
      }

      var attributes = artifact.getAttributes(attributeType);

      //@formatter:off
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

                 ||  artifact.getAttributes( attributeType ).isEmpty()
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
               wordMl,
               this.renderer.getRendererOptionsView(),
               presentationType,
               attributeOptions.getLabel(),
               footer,
               this.permanentLinkUrl,
               false,
               artifact.isHistorical()
                  ? artifact.getTransaction()
                  : TransactionToken.SENTINEL,
               unknownGuids,
               ( wordTemplateContentData ) -> PublishingRequestHandler.renderWordTemplateContent( wordTemplateContentData ),
               ( exception ) -> WordUiUtil.displayErrorMessage( artifact, exception.toString() )
            );

         if( !unknownGuids.isEmpty() ) {
            WordUiUtil.displayUnknownGuids(artifact, unknownGuids);
         }

      } else {

         if( !attributes.isEmpty() ) {

            WordRenderUtil.renderAttribute
               (
                 attributeType,
                 this.renderer::renderRelationOrder,
                 new WordRenderArtifactWrapperClientImpl( artifact ),
                 wordMl,
                 attributeOptions.getLabel(),
                 attributeOptions.getFormat()
               );
         }

      }
      //@formatter:on

   }

   private boolean processOutlining(Artifact artifact, WordMLProducer wordMl, String outlineType) {

      boolean startedSection = false;

      boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);
      boolean templateOnly = (boolean) renderer.getRendererOptionValue(RendererOption.TEMPLATE_ONLY);
      boolean headerOrFolder =
         artifact.isOfType(CoreArtifactTypes.HeadingMsWord) || artifact.isOfType(CoreArtifactTypes.Folder);
      boolean includeOutline = templateOnly ? false : (outlineOnlyHeadersFolders ? headerOrFolder : true);

      boolean includeUUIDs = (boolean) renderer.getRendererOptionValue(RendererOption.INCLUDE_UUIDS);

      if (outlining && includeOutline) {
         String headingText = artifact.getSoleAttributeValue(headingAttributeTypeToken, "");

         if (includeUUIDs) {
            String UUIDtext = String.format(" <UUID = %s>", artifact.getIdString());
            headingText = headingText.concat(UUIDtext);
         }

         Boolean mergeTag = (Boolean) renderer.getRendererOptionValue(RendererOption.ADD_MERGE_TAG);
         if (mergeTag != null && mergeTag) {
            headingText = headingText.concat(" [MERGED]");
         }

         if (!publishInline) {
            if (overrideOutlineNumber) {
               paragraphNumber = startOutlineSubSectionOverride(wordMl, artifact, headingText);
            } else {
               paragraphNumber = wordMl.startOutlineSubSection("Times New Roman", headingText, outlineType);
            }
            startedSection = true;
         }

         if (paragraphNumber == null) {
            paragraphNumber = wordMl.startOutlineSubSection();
            startedSection = true;
         }

         if ((boolean) renderer.getRendererOptionValue(RendererOption.UPDATE_PARAGRAPH_NUMBERS) && !publishInline) {
            if (artifact.isAttributeTypeValid(CoreAttributeTypes.ParagraphNumber)) {
               artifact.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, paragraphNumber.toString());

               SkynetTransaction transaction =
                  (SkynetTransaction) renderer.getRendererOptionValue(RendererOption.TRANSACTION_OPTION);
               if (transaction != null) {
                  artifact.persist(transaction);
               } else {
                  artifact.persist(getClass().getSimpleName());
               }
            }
         }
      }

      return startedSection;
   }

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

      getExcludeArtifactTypes();

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
               RendererUtil.makeRenderPath( COMMON.getShortName() ),
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
                              this.outlineNumber,
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

   private boolean renderArtifact(Artifact artifact, PresentationType presentationType, WordMLProducer wordMl,
      String outlineType, DataRightContentBuilder dataRightContentBuilder) {

      boolean publishInline = artifact.getSoleAttributeValue(CoreAttributeTypes.PublishInline, false);

      var startedSection = this.processOutlining(artifact, wordMl, outlineType);

      String footer = "";
      if (!this.templateFooter) {
         var orientation = WordRenderUtil.getPageOrientation(new WordRenderArtifactWrapperClientImpl(artifact));
         footer = dataRightContentBuilder.getContent(artifact, orientation);
      }

      final var finalFooter = footer;

      /*
       * Add metadata attributes to the Word ML output
       */

      //@formatter:off
      WordRenderUtil.processMetadataOptions
         (
            this.primaryPublishingTemplate.getRendererOptions().getMetadataOptions(),
            this.applicabilityTokens,
            new WordRenderArtifactWrapperClientImpl( artifact ),
            wordMl
         );
      //@formatter:on

      /*
       * Add attributes and the main artifact content to the Word ML output
       */

      //@formatter:off
      WordRenderUtil.processAttributes
         (
            this.attributeOptionsList,
            ( lAttributeOptions, lAttributeType, lAllAttributes ) ->
               this.processAttribute
                  (
                     artifact,
                     wordMl,
                     lAttributeOptions,
                     lAttributeType,
                     lAllAttributes,
                     presentationType,
                     publishInline,
                     finalFooter
                  ),
            ( attributeName ) -> AttributeTypeManager.getType( attributeName ),
            () -> RendererManager.getAttributeTypeOrderList( artifact ),
            new WordRenderArtifactWrapperClientImpl( artifact ),
            this.headingAttributeTypeToken,
            this.renderer.isRendererOptionSetAndTrue(RendererOption.ALL_ATTRIBUTES),
            this.outlining
         );

      //@formatter:on

      return startedSection;
   }

   public void setExcludedArtifactTypeForTest(List<ArtifactTypeToken> excludeTokens) {
      excludeArtifactTypes.clear();
      for (ArtifactTypeToken token : excludeTokens) {
         excludeArtifactTypes.add(token);
      }
   }

   private CharSequence startOutlineSubSectionOverride(WordMLProducer wordMl, Artifact artifact, String headingText) {
      String paragraphNumber = artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
      if (paragraphNumber.isEmpty()) {
         ArtifactHierarchyComparator comparator = new ArtifactHierarchyComparator();
         paragraphNumber = comparator.getHierarchyPosition(artifact);
      }
      int outlineLevel = 1;
      for (int i = 0; i < paragraphNumber.length(); i++) {
         char charAt = paragraphNumber.charAt(i);
         if (charAt == '.') {
            outlineLevel++;
         }
      }
      wordMl.startOutlineSubSection("Heading", outlineLevel, paragraphNumber, "Times New Roman", headingText);

      return paragraphNumber;
   }
}
