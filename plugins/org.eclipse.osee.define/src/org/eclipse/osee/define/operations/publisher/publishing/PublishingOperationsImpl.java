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

package org.eclipse.osee.define.operations.publisher.publishing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.define.operations.api.publisher.publishing.PublishingOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.operations.api.utils.AttachmentFactory;
import org.eclipse.osee.define.operations.markdown.MarkdownCleaner;
import org.eclipse.osee.define.operations.markdown.MarkdownConverter;
import org.eclipse.osee.define.rest.api.ArtifactUrlServer;
import org.eclipse.osee.define.rest.api.publisher.publishing.LinkHandlerResult;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateChange;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateData;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMlLinkHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.publishing.Cause;
import org.eclipse.osee.framework.core.publishing.DataAccessException;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.ProcessRecursively;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.core.publishing.PublishingErrorLog;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.osgi.service.event.EventAdmin;

/**
 * An implementation of the {@link PublishingOperations} interface.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class PublishingOperationsImpl implements PublishingOperations {

   /**
    * The estimated number of artifacts to be loaded for a small publish.
    */

   private static final int SMALL_PUBLISH_SIZE = 256;

   /**
    * Internal class encapsulating various publishing utility classes.
    */

   private class PublishingPack {

      /**
       * A lookup for artifacts in a publish that have been modified on the publishing branch.
       */

      @NonNull
      ChangedArtifactsTracker changedArtifactsTracker;

      /**
       * Loads and caches artifacts for the publish.
       */

      @NonNull
      PublishingArtifactLoader publishingArtifactLoader;

      /**
       * Records non-fatal publishing errors.
       */

      @NonNull
      PublishingErrorLog publishingErrorLog;

      /**
       * A handler to process feature tags in an artifact's content.
       */

      @Nullable
      WordMLApplicabilityHandler wordMlApplicabilityHandler;

      /**
       * Creates the publishing utility objects for the specified branch and view.
       *
       * @param branchSpecification The branch and optional view that is being published.
       * @throws NullPointerException when <code>branchSpecification</code> is <code>null</code>.
       */

      PublishingPack(@NonNull BranchSpecification branchSpecification) {

         final var safeBranchSpecification = Conditions.requireNonNull(branchSpecification, "branchSpecification");

         //@formatter:off
         this.publishingErrorLog =
            new PublishingErrorLog();

         this.changedArtifactsTracker =
            new ChangedArtifactsTracker
                   (
                      PublishingOperationsImpl.this.atsApi,
                      PublishingOperationsImpl.this.dataAccessOperations,
                      publishingErrorLog
                   );

         this.publishingArtifactLoader =
            new PublishingArtifactLoader
                   (
                      PublishingOperationsImpl.this.dataAccessOperations,
                      publishingErrorLog,
                      WordRenderArtifactWrapperServerImpl::new,
                      WordRenderArtifactWrapperServerImpl::new,
                      changedArtifactsTracker::loadByAtsTeamWorkflow
                   )
                .configure
                   (
                      safeBranchSpecification,
                      PublishingOperationsImpl.SMALL_PUBLISH_SIZE
                   );

         try {
            this.wordMlApplicabilityHandler =
               new WordMLApplicabilityHandler
                      (
                         PublishingOperationsImpl.this.orcsApi,
                         PublishingOperationsImpl.this.logger,
                         safeBranchSpecification.getBranchIdWithOutViewId(),
                         safeBranchSpecification.getViewId()
                      );
         } catch( Exception e ) {
            this.wordMlApplicabilityHandler = null;
         }

      }
   }

   /**
    * Saves the single instance of the {@link PublishingOperationsImpl}.
    */

   private static PublishingOperationsImpl publishingOperationsImpl = null;

   /**
    * Gets or creates the single instance of the {@link PublishingOperationsImpl} class.
    *
    * @param defineOperations the {@link DefineOperations} handle.
    * @param orcsApi the {@link OrcsApi} handle.
    * @param atsApi the {@link AtsApi} handle.
    * @param logger the {@link Log} handle.
    * @param eventAdmin the {@link EventAdmin} handle.
    * @param dataAccessOperations a handle to the {@link DataAccessOperationsImplArtifactReadableImpl} for database
    * access.
    * @return the single {@link PublishingOperationsImpl} object.
    */

   public synchronized static PublishingOperationsImpl create(OrcsApi orcsApi, AtsApi atsApi, Log logger,
      EventAdmin eventAdmin, DataAccessOperations dataAccessOperations, DataRightsOperations dataRightsOperations,
      TemplateManagerOperations templateManagerOperations) {

      //@formatter:off
      return
         Objects.isNull( PublishingOperationsImpl.publishingOperationsImpl )
            ? PublishingOperationsImpl.publishingOperationsImpl =
                 new PublishingOperationsImpl
                        (
                           Objects.requireNonNull( orcsApi ),
                           Objects.requireNonNull( atsApi ),
                           Objects.requireNonNull( logger ),
                           Objects.requireNonNull( eventAdmin ),
                           Objects.requireNonNull( dataAccessOperations ),
                           Objects.requireNonNull( dataRightsOperations ),
                           Objects.requireNonNull( templateManagerOperations )
                        )
            : PublishingOperationsImpl.publishingOperationsImpl;
      //@formatter:on
   }

   /**
    * Sets the statically saved instance of the {@link PublishingOperationsImpl} class to <code>null</code>.
    */

   public synchronized static void free() {
      PublishingOperationsImpl.publishingOperationsImpl = null;
   }

   private final AtsApi atsApi;
   private final AttachmentFactory attachmentFactory;
   private final DataAccessOperations dataAccessOperations;
   private final DataRightsOperations dataRightsOperations;
   private final EventAdmin eventAdmin;
   private final Log logger;
   private final OrcsApi orcsApi;
   private final String permanentLinkUrl;
   private final TemplateManagerOperations templateManagerOperations;

   private PublishingOperationsImpl(OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin, DataAccessOperations dataAccessOperations, DataRightsOperations dataRightsOperations, TemplateManagerOperations templateManagerOperations) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;
      this.dataAccessOperations = dataAccessOperations;
      this.dataRightsOperations = dataRightsOperations;
      this.templateManagerOperations = templateManagerOperations;
      this.permanentLinkUrl = new ArtifactUrlServer(this.orcsApi).getSelectedPermanentLinkUrl();

      //@formatter:off
      this.attachmentFactory =
         new AttachmentFactory
                (
                   "MsWordPreview",
                   "xml",
                   this.dataAccessOperations
                );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when:
    * <dl>
    * <dt><code>branch</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>view</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than -1.</dd>
    * <dt><code>sharedFolder</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>artifactTypeToken</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than -1.</dd>
    * <dt><code>attributeType</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than -1.</dd>
    * <dt><code>attributeValue</code>:</dt>
    * <dd>when <code>null</code> or an empty {@link String}.</dd>
    * </dl>
    * @throws OseeNotFoundException when shared artifacts are not found for the specified parameters.
    */

   @Override
   public List<PublishingArtifact> getSharedPublishingArtifacts(BranchId branch, ArtifactId view,
      ArtifactId sharedFolder, ArtifactTypeToken artifactType, AttributeTypeToken attributeType,
      String attributeValue) {

      Message message = null;

      //@formatter:off
      message =
         Conditions.require
            (
               message,
               branch,
               ValueType.PARAMETER,
               "branch",
               "cannot be null or negative",
               Conditions.or
                  (
                     Objects::isNull,
                     (p) -> p.getId() <  0l
                  )
            );

      message =
         Conditions.require
            (
               message,
               view,
               ValueType.PARAMETER,
               "view",
               "cannot be null or less than minus one",
               Conditions.or
                  (
                    Objects::isNull,
                    (p) -> p.getId() < -1l
                  )
            );

      message =
         Conditions.require
            (
               message,
               sharedFolder,
               ValueType.PARAMETER,
               "sharedFolder",
               "cannot be null or negative",
               Conditions.or
                  (
                     Objects::isNull,
                     (p) -> p.getId() <  0l
                  )
            );

      message =
         Conditions.require
            (
               message,
               artifactType,
               ValueType.PARAMETER,
               "artifactType",
               "cannot be null or less than minus one",
               Conditions.or
                  (
                     Objects::isNull,
                     (p) -> p.getId() < -1l
                  )
            );

      message =
         Conditions.require
            (
               message,
               attributeType,
               ValueType.PARAMETER,
               "attributeType",
               "cannot be null or less than minus one",
               Conditions.or
                  (
                     Objects::isNull,
                     (p) -> p.getId() < -1l
                  )
            );

      message =
         Conditions.require
            (
               message,
               attributeValue,
               ValueType.PARAMETER,
               "attributeValue",
               "cannot be null or an empty string",
               Conditions.or
                  (
                     Objects::isNull,
                     String::isEmpty
                  )
            );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Conditions.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "getSharedPublishingArtifacts",
                            message
                         )
                   );
      }
      //@formatter:on

      PublishingPack publishingPack;

      try {
         final var branchSpecification = new BranchSpecification(branch, view);
         publishingPack = new PublishingPack(branchSpecification);
      } catch (DataAccessException dataAccessException) {
         if (dataAccessException.getPublishingUtilCause().equals(Cause.NOT_FOUND)) {
            //@formatter:off
            throw
               new OseeNotFoundException
                      (
                         new Message()
                                .title( "PublishingOperationsImpl::getSharedPublishingArtifacts, Unable to locate the shared folder." )
                                .indentInc()
                                .segment( "Branch Identifier", branch         )
                                .segment( "View Identifier",   view           )
                                .segment( "Shared Folder",     sharedFolder   )
                                .segment( "Artifact Type",     artifactType   )
                                .segment( "Attribute Type",    attributeType  )
                                .segment( "Attribute Value",   attributeValue )
                                .indentDec()
                                .reasonFollows( dataAccessException )
                                .toString(),
                         dataAccessException
                      );
            //@formatter:on
         }

         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "PublishingOperationsImpl::getSharedPublishingArtifacts, Failed to load the shared folder." )
                             .indentInc()
                             .segment( "Branch Identifier", branch         )
                             .segment( "View Identifier",   view           )
                             .segment( "Shared Folder",     sharedFolder   )
                             .segment( "Artifact Type",     artifactType   )
                             .segment( "Attribute Type",    attributeType  )
                             .segment( "Attribute Value",   attributeValue )
                             .indentDec()
                             .reasonFollows( dataAccessException )
                             .toString(),
                      dataAccessException
                   );
         //@formatter:on
      }

      //@formatter:off
      var publishingSharedArtifactsFolder =
         ArtifactTypeToken.SENTINEL.equals( artifactType )
            ? PublishingSharedArtifactsFolder.create
                 (
                   publishingPack.publishingArtifactLoader,
                   publishingPack.publishingErrorLog,
                   new BranchSpecification( branch, view ),
                   "Shared Artifacts Folder",
                   ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                   attributeType,
                   ProcessRecursively.YES
                 )
            : PublishingSharedArtifactsFolder.create
                    (
                      publishingPack.publishingArtifactLoader,
                      publishingPack.publishingErrorLog,
                      new BranchSpecification( branch, view ),
                      "Shared Artifacts Folder",
                      ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                      artifactType,
                      attributeType,
                      ProcessRecursively.YES
                    );
      //@formatter:on

      var sharedArtifacts = publishingSharedArtifactsFolder.getSharedArtifacts(attributeValue);

      if (publishingPack.publishingErrorLog.size() > 0) {
         var errorLogMessage = new StringBuilder(1024);
         publishingPack.publishingErrorLog.publishErrorLog(errorLogMessage);
         throw new OseeNotFoundException(errorLogMessage.toString());
      }

      return sharedArtifacts;
   }

   @Override
   public LinkHandlerResult link(BranchId branchId, ArtifactId viewId, ArtifactId artifactId,
      TransactionId transactionId, LinkType linkType, PresentationType presentationType) {
      return null;
   }

   /**
    * {@inheritDoc}
    *
    * @param msWordPreviewRequestData the {@link PublishingRequestData} structure containing the publishing parameters.
    * @return an {@link InputStream} containing the Word ML XML containing the published artifacts.
    * @throws IllegalArgumentException when the parameter <code>msWordPreviewRequestData</code> is <code>null</code> or
    * invalid according to {@link PublishingRequestData#isValid}.
    */

   @Override
   public Attachment msWordPreview(PublishingRequestData msWordPreviewRequestData) {

      //@formatter:off
      var publishingRendererOptions = msWordPreviewRequestData.getPublishingRendererOptions();
      var firstArtifactId = msWordPreviewRequestData.getArtifactIds().get(0);

      var inputStream = processPublishingRequest(msWordPreviewRequestData, publishingRendererOptions);

      var attachment =
         this.attachmentFactory.create
            (
               inputStream,
               publishingRendererOptions.getRendererOptionValue( RendererOption.PUBLISH_IDENTIFIER),
               publishingRendererOptions.getRendererOptionValue( RendererOption.BRANCH ),
               firstArtifactId
            );

      return attachment;
      //@formatter:on
   }

   private ByteArrayInputStream processPublishingRequest(PublishingRequestData publishingRequestData,
      RendererMap publishingRendererOptions) {

      //@formatter:off
      Conditions.require
         (
            publishingRequestData,
            ValueType.PARAMETER,
            "publishingRequestData",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "cannot be invalid",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );

      var publishingTemplateRequest = publishingRequestData.getPublishingTemplateRequest();
      var publishArtifacts = publishingRequestData.getArtifactIds();

      var publishingTemplate =
         this.templateManagerOperations
            .getPublishingTemplate( publishingTemplateRequest );

      if( publishingTemplate.isSentinel() ) {

         var message =
            new Message()
                   .title( "PublishingOperationsImpl::msWordPreviewInternal: Failed to find a publishing template." )
                   .indentInc()
                   .toMessage( publishingRequestData )
                   .toString()
                   ;

         this.logger.error( message );

         throw new OseeCoreException( message );
      }


      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try ( var writer = new OutputStreamWriter(outputStream) ) {

         new GeneralPublishingWordTemplateProcessorServer
                (
                  this.orcsApi,
                  this.atsApi,
                  this.dataAccessOperations,
                  this.dataRightsOperations
                )
             .configure
                (
                   publishingTemplate,
                   publishingRendererOptions
                )
             .applyTemplate
                (
                   publishArtifacts,
                   writer
                );

      } catch (Exception e) {

         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "PublishingOperationsImpl::msWordPreviewIntenal, Failed to publish document." )
                             .indentInc()
                             .toMessage( publishingRequestData )
                             .reasonFollows( e )
                             .toString(),
                             e
                   );
      }

      return new ByteArrayInputStream( outputStream.getBuffer(), 0, outputStream.size() );
      //@formatter:on
   }

   @Override
   public Attachment msWordWholeWordContentPublish(BranchId branchId, ArtifactId viewId, ArtifactId artifactId,
      TransactionId transactionId, LinkType linkType, PresentationType presentationType, boolean includeErrorLog) {
      return null;
   }

   @Override
   public String renderPlainText(BranchId branchId, String data) {
      String PL_STYLE_WITH_RETURN =
         "<w:rPr><w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>)).*?</w:rPr>";
      String PL_STYLE = "<w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>))";
      String PL_HIGHLIGHT =
         "<w:highlight w:val=\"light-gray\"></w:highlight><w:shd w:color=\"auto\" w:fill=\"BFBFBF\" w:val=\"clear\"></w:shd>";
      String EMPTY_PARAGRAPHS = "<w:r wsp:rsidRPr=\"\\d+\"><w:t></w:t></w:r>";

      var dataCharSequence = WordCoreUtil.replaceBinaryDataIdentifiers(data);
      data = WordMlLinkHandler.renderPlainTextWithoutLinks(orcsApi.getQueryFactory(), branchId,
         dataCharSequence.toString());
      data = WordCoreUtil.reassignBookMarkID(data).toString();
      //data = WordCoreUtilServer.removeNewLines(data);

      // if no extra paragraphs have been added this will replace the normal footer
      var charSequenceData = WordCoreUtil.removeFootersAndNoDataRightsStatements(data);
      data = WordCoreUtil.replaceEmptySectionBreaksWithPageBreaks(charSequenceData).toString();

      if (!data.contains("<w:tbl>")) {
         int lastIndex = data.lastIndexOf("<w:p wsp:rsidR=");

         if (lastIndex != -1) {
            // temp should equal <w:p wsp:rsidR ..</w:p> ...
            String temp = data.substring(lastIndex);
            temp = temp.replaceAll("<w:p\\s[^>]*>(<w:pPr><w:spacing[^>]*></w:spacing></w:pPr>)?</w:p>", "");
            data = data.substring(0, lastIndex) + temp;
         }
      }

      data = data.replaceAll(PL_STYLE_WITH_RETURN, "");
      data = data.replaceAll(PL_STYLE, "");
      data = data.replaceAll(PL_HIGHLIGHT, "");
      data = data.replaceAll(EMPTY_PARAGRAPHS, "");
      data = WordCoreUtil.removeReviewComments(data).toString();

      return data;
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when the parameter <code>wordTemplateContentData</code> is <code>null</code> or
    * invalid according to {@link WordTemplateContentData#isValid}.
    */

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData wordTemplateContentData) {

      //@formatter:off
      Conditions.require
         (
            wordTemplateContentData,
            ValueType.PARAMETER,
            "wordTemplateContentData",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "cannot be invalid",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );
      //@formatter:on

      WordTemplateContentRendererHandler wordRendererHandler =
         new WordTemplateContentRendererHandler(this.orcsApi, this.dataAccessOperations, this.logger);

      return wordRendererHandler.renderWordML(wordTemplateContentData);
   }

   @Override
   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data) {

      WordMLApplicabilityHandler wordHandler = new WordMLApplicabilityHandler(orcsApi, logger, branchId, viewId);
      return wordHandler.previewValidApplicabilityContent(data);
   }

   @Override
   public LinkHandlerResult unlink(BranchId branchId, ArtifactId viewId, ArtifactId artifactId,
      TransactionId transactionId, LinkType linkType) {
      return null;
   }

   @Override
   public String updateLinks(BranchId branchId) {
      return null;
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when the parameter <code>wordUpdateData</code> is <code>null</code> or invalid
    * according to {@link WordUpdateData#isValid}.
    */

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData wordUpdateData) {

      //@formatter:off
      Conditions.require
         (
            wordUpdateData,
            ValueType.PARAMETER,
            "wordUpdateData",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "is invalid",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );
      //@formatter:on

      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi, eventAdmin);

      return updateArt.updateArtifacts(wordUpdateData);
   }

   /**
    * {@inheritDoc}
    *
    * @param publishMarkdownAsHtmlRequestData the {@link PublishingRequestData} structure containing the publishing
    * parameters.
    * @return an {@link InputStream} containing the Word ML XML containing the published artifacts.
    * @throws IllegalArgumentException when the parameter <code>msWordPreviewRequestData</code> is <code>null</code> or
    * invalid according to {@link PublishingRequestData#isValid}.
    */

   @Override
   public Attachment publishMarkdownAsHtml(PublishingRequestData publishMarkdownAsHtmlRequestData) {

      //@formatter:off
      var publishingRendererOptions = publishMarkdownAsHtmlRequestData.getPublishingRendererOptions();
      var firstArtifactId = publishMarkdownAsHtmlRequestData.getArtifactIds().get(0);

      var inputStream = processPublishingRequest(publishMarkdownAsHtmlRequestData, publishingRendererOptions);

      // Convert Markdown to HTML

      MarkdownConverter mdConverter = new MarkdownConverter();
      ByteArrayInputStream htmlInputStream = mdConverter.convertToHtmlStream(inputStream);

      // Create attachment

      Attachment attachment = new AttachmentFactory
         (
            "MsWordPreview",
            "html",
            this.dataAccessOperations
         )
         .create
            (
               htmlInputStream,
               publishingRendererOptions.getRendererOptionValue( RendererOption.PUBLISH_IDENTIFIER),
               publishingRendererOptions.getRendererOptionValue( RendererOption.BRANCH ),
               firstArtifactId
            );

      return attachment;
      //@formatter:on
   }

   @Override
   public String cleanAllMarkdownArtifactsForBranch(BranchId branchId) {
      orcsApi.userService().requireRole(CoreUserGroups.OseeAdmin);
      StringBuilder outputLog = new StringBuilder();

      List<ArtifactReadable> markdownArtifacts = this.orcsApi.getQueryFactory().fromBranch(branchId).andExists(
         CoreAttributeTypes.MarkdownContent).asArtifacts();
      TransactionBuilder tx = this.orcsApi.getTransactionFactory().createTransaction(branchId,
         "Cleaning and updating Markdown artifacts on branch that have special (non-Markdown) characters.");

      int artifactsProcessed = 0;
      int artifactsCleaned = 0;

      for (ArtifactReadable art : markdownArtifacts) {
         artifactsProcessed++;
         boolean isArtifactCleaned = false;

         if (art.getExistingAttributeTypes().contains(CoreAttributeTypes.MarkdownContent)) {
            String mdContent = art.getSoleAttributeValue(CoreAttributeTypes.MarkdownContent, Strings.EMPTY_STRING);
            String name = art.getName();

            if (!mdContent.equals(Strings.EMPTY_STRING)) {
               boolean hasSpecialCharsInContent = MarkdownCleaner.containsSpecialCharacters(mdContent);

               if (hasSpecialCharsInContent) {
                  outputLog.append("Issues detected in the Markdown content: ").append(mdContent).append("\n");
                  String cleanedMarkdownContent = MarkdownCleaner.removeSpecialCharacters(mdContent);
                  outputLog.append("Cleaned Markdown content: ").append(cleanedMarkdownContent).append("\n");
                  tx.setAttributeById(art.getArtifactId(), art.getSoleAttributeId(CoreAttributeTypes.MarkdownContent),
                     cleanedMarkdownContent);
                  isArtifactCleaned = true;
               } else {
                  outputLog.append("No special characters detected in the Markdown content.\n");
               }
            }

            if (!name.equals(Strings.EMPTY_STRING)) {
               boolean hasSpecialCharsInName = MarkdownCleaner.containsSpecialCharacters(name);

               if (hasSpecialCharsInName) {
                  outputLog.append("Issues detected in the name: ").append(name).append("\n");
                  String cleanedName = MarkdownCleaner.removeSpecialCharacters(name);
                  outputLog.append("Cleaned name: ").append(cleanedName).append("\n");
                  tx.setAttributeById(art.getArtifactId(), art.getSoleAttributeId(CoreAttributeTypes.Name),
                     cleanedName);
                  isArtifactCleaned = true;
               } else {
                  outputLog.append("No special characters detected in the name.\n");
               }
            }
         }

         if (isArtifactCleaned) {
            artifactsCleaned++;
         }
      }
      tx.commit();

      outputLog.append("Finished processing artifacts.\nTotal artifacts processed: ").append(artifactsProcessed).append(
         "\nTotal artifacts cleaned: ").append(artifactsCleaned);

      return outputLog.toString();
   }

   @Override
   public String removeMarkdownBoldSymbolsFromAllMarkdownArtifactsForBranch(BranchId branchId) {
      orcsApi.userService().requireRole(CoreUserGroups.OseeAdmin);
      StringBuilder outputLog = new StringBuilder();

      List<ArtifactReadable> markdownArtifacts = this.orcsApi.getQueryFactory().fromBranch(branchId).andExists(
         CoreAttributeTypes.MarkdownContent).asArtifacts();
      TransactionBuilder tx = this.orcsApi.getTransactionFactory().createTransaction(branchId,
         "Removing bold symbols (**) from all Markdown artifacts on branch.");

      int artifactsProcessed = 0;
      int artifactsCleaned = 0;

      for (ArtifactReadable art : markdownArtifacts) {
         artifactsProcessed++;
         boolean isArtifactCleaned = false;

         if (art.getExistingAttributeTypes().contains(CoreAttributeTypes.MarkdownContent)) {
            String mdContent = art.getSoleAttributeValue(CoreAttributeTypes.MarkdownContent, Strings.EMPTY_STRING);
            String name = art.getName();

            if (!mdContent.equals(Strings.EMPTY_STRING)) {
               boolean hasMarkdownBoldsInContent = MarkdownCleaner.containsMarkdownBolds(mdContent);

               if (hasMarkdownBoldsInContent) {
                  outputLog.append("Markdown bold symbols detected in the Markdown content: ").append(mdContent).append(
                     "\n");
                  String cleanedMarkdownContent = MarkdownCleaner.removeMarkdownBolds(mdContent);
                  outputLog.append("Cleaned Markdown content: ").append(cleanedMarkdownContent).append("\n");
                  tx.setAttributeById(art.getArtifactId(), art.getSoleAttributeId(CoreAttributeTypes.MarkdownContent),
                     cleanedMarkdownContent);
                  isArtifactCleaned = true;
               } else {
                  outputLog.append("No Markdown bold symbols detected in the Markdown content.\n");
               }
            }

            if (!name.equals(Strings.EMPTY_STRING)) {
               boolean hasMarkdownBoldsInName = MarkdownCleaner.containsMarkdownBolds(name);

               if (hasMarkdownBoldsInName) {
                  outputLog.append("Markdown bold symbols detected in the name: ").append(name).append("\n");
                  String cleanedName = MarkdownCleaner.removeMarkdownBolds(name);
                  outputLog.append("Cleaned name: ").append(cleanedName).append("\n");
                  tx.setAttributeById(art.getArtifactId(), art.getSoleAttributeId(CoreAttributeTypes.Name),
                     cleanedName);
                  isArtifactCleaned = true;
               } else {
                  outputLog.append("No Markdown bold symbols detected in the name.\n");
               }
            }
         }

         if (isArtifactCleaned) {
            artifactsCleaned++;
         }
      }
      tx.commit();

      outputLog.append("Finished processing artifacts.\nTotal artifacts processed: ").append(artifactsProcessed).append(
         "\nTotal artifacts cleaned: ").append(artifactsCleaned);

      return outputLog.toString();
   }

}

/* EOF */
