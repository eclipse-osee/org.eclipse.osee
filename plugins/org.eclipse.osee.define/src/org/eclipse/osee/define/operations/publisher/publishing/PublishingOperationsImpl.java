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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.api.publisher.dataaccess.DataAccessOperations;
import org.eclipse.osee.define.operations.api.publisher.datarights.DataRightsOperations;
import org.eclipse.osee.define.operations.api.publisher.publishing.PublishingOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.operations.api.utils.AttachmentFactory;
import org.eclipse.osee.define.rest.api.ArtifactUrlServer;
import org.eclipse.osee.define.rest.api.publisher.publishing.LinkHandlerResult;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateChange;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateData;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMlLinkHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Conditions.ValueType;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.osgi.service.event.EventAdmin;

/**
 * An implementation of the {@link PublishingOperations} interface.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class PublishingOperationsImpl implements PublishingOperations {

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

   public synchronized static PublishingOperationsImpl create(OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin, DataAccessOperations dataAccessOperations, DataRightsOperations dataRightsOperations, TemplateManagerOperations templateManagerOperations) {

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
   public List<ArtifactToken> getSharedPublishingArtifacts(BranchId branch, ArtifactId view, ArtifactId sharedFolder, ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String attributeValue) {

      Message message = null;

      //@formatter:off
      message =
         Conditions.require
            (
               message,
               branch,
               ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "branch",
               "cannot be null",
               Objects::isNull,
               "branch identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Conditions.require
            (
               message,
               view,
               ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "view",
               "cannot be null",
               Objects::isNull,
               "view artifact identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

      message =
         Conditions.require
            (
               message,
               sharedFolder,
               ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "sharedFolder",
               "cannot be null",
               Objects::isNull,
               "shared folder artifact identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Conditions.require
            (
               message,
               artifactType,
               ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "artifactType",
               "cannot be null",
               Objects::isNull,
               "artifcat type identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

      message =
         Conditions.require
            (
               message,
               attributeType,
               ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "attributeType",
               "cannot be null",
               Objects::isNull,
               "attribute type identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

      message =
         Conditions.require
            (
               message,
               attributeValue,
               ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "attributeValue",
               "cannot be null",
               Objects::isNull,
               "cannot be an empty string",
               String::isEmpty
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

      var publishingErrorLog = new PublishingErrorLog();

      //@formatter:off
      var publishingSharedArtifactsFolder =
         ArtifactTypeToken.SENTINEL.equals( artifactType )
            ? PublishingSharedArtifactsFolder.create
                 (
                   this.dataAccessOperations,
                   publishingErrorLog,
                   BranchId.create( branch.getId(), view ),
                   "Shared Artifacts Folder",
                   ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                   attributeType
                 )
            : PublishingSharedArtifactsFolder.create
                    (
                      this.dataAccessOperations,
                      publishingErrorLog,
                      BranchId.create( branch.getId(), view ),
                      "Shared Artifacts Folder",
                      ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                      artifactType,
                      attributeType
                    );
      //@formatter:on

      var sharedArtifacts = publishingSharedArtifactsFolder.getSharedArtifacts(attributeValue);

      if (publishingErrorLog.size() > 0) {
         var errorLogMessage = new StringBuilder(1024);
         publishingErrorLog.publishErrorLog(errorLogMessage);
         throw new OseeNotFoundException(errorLogMessage.toString());
      }

      @SuppressWarnings("unchecked")
      var sharedArtifactTokens = (List<ArtifactToken>) (Object) sharedArtifacts;

      return sharedArtifactTokens;
   }

   @Override
   public LinkHandlerResult link(BranchId branchId, ArtifactId viewId, ArtifactId artifactId, TransactionId transactionId, LinkType linkType, PresentationType presentationType) {
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
      Conditions.require
         (
            msWordPreviewRequestData,
            ValueType.PARAMETER,
            "PublishingOperationsImpl",
            "msWordPreview",
            "msWordPreviewRequestData",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "cannot be invalid",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );

      var publishingTemplateRequest = msWordPreviewRequestData.getPublishingTemplateRequest();
      var publishingRendererOptions = msWordPreviewRequestData.getPublishingRendererOptions();
      var publishArtifacts = msWordPreviewRequestData.getArtifactIds();

      var firstArtifactId = msWordPreviewRequestData.getArtifactIds().get(0);

      var publishingTemplate =
         this.templateManagerOperations
            .getPublishingTemplate( publishingTemplateRequest );

      if( publishingTemplate.isSentinel() ) {

         var message =
            new Message()
                   .title( "PublishingOperationsImpl::msWordPreviewInternal: Failed to find a publishing template." )
                   .indentInc()
                   .toMessage( msWordPreviewRequestData )
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
                             .toMessage( msWordPreviewRequestData )
                             .reasonFollows( e )
                             .toString(),
                             e
                   );
      }

      var inputStream = new ByteArrayInputStream( outputStream.getBuffer(), 0, outputStream.size() );

      var attachment =
         this.attachmentFactory.create
            (
               inputStream,
               publishingRendererOptions.getRendererOptionValue( RendererOption.PUBLISH_IDENTIFIER),
               publishingRendererOptions.getRendererOptionValue( RendererOption.BRANCH ),
               firstArtifactId
            );

      return attachment;
   }
   //@formatter:on

   @Override
   public Attachment msWordWholeWordContentPublish(BranchId branchId, ArtifactId viewId, ArtifactId artifactId, TransactionId transactionId, LinkType linkType, PresentationType presentationType, boolean includeErrorLog) {
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
      data = WordCoreUtilServer.reassignBookMarkID(data).toString();
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
            "PublishingOperationsImpl",
            "renderWordTemplateContent",
            "wordTemplateContentData",
            "cannot be null",
            Objects::isNull,
            NullPointerException::new,
            "cannot be invalid",
            (p) -> !p.isValid(),
            IllegalArgumentException::new
         );
      //@formatter:on

      WordTemplateContentRendererHandler wordRendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);

      return wordRendererHandler.renderWordML(wordTemplateContentData);
   }

   @Override
   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data) {

      WordMLApplicabilityHandler wordHandler = new WordMLApplicabilityHandler(orcsApi, logger, branchId, viewId);
      return wordHandler.previewValidApplicabilityContent(data);
   }

   @Override
   public LinkHandlerResult unlink(BranchId branchId, ArtifactId viewId, ArtifactId artifactId, TransactionId transactionId, LinkType linkType) {
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
            "PublishingOperationsImpl",
            "updateWordArtifacts",
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

}

/* EOF */
