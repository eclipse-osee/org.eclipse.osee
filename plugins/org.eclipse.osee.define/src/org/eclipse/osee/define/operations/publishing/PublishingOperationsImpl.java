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

package org.eclipse.osee.define.operations.publishing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.api.publishing.PublishingOperations;
import org.eclipse.osee.define.api.publishing.PublishingOptions;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMlLinkHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUtilities;
import org.eclipse.osee.define.util.Validation;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.util.ReportConstants;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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

   @SuppressWarnings("unused")
   private static boolean excludeFolders = false;

   private static boolean includeFolders = true;

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
    * @return the single {@link PublishingOperationsImpl} object.
    */

   public synchronized static PublishingOperationsImpl create(DefineOperations defineOperations, OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin) {

      //@formatter:off
      return
         Objects.isNull( PublishingOperationsImpl.publishingOperationsImpl )
            ? ( PublishingOperationsImpl.publishingOperationsImpl = new PublishingOperationsImpl(defineOperations, orcsApi, atsApi, logger, eventAdmin) )
            : PublishingOperationsImpl.publishingOperationsImpl;
      //@formatter:on
   }

   private final AtsApi atsApi;
   private final DefineOperations defineOperations;
   private final EventAdmin eventAdmin;
   private final Log logger;
   private final OrcsApi orcsApi;

   private PublishingOperationsImpl(DefineOperations defineOperations, OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin) {
      this.defineOperations = defineOperations;
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;
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
      message = Validation.verifyParameter(branch,         "branch",         message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter(view,           "view",           message, "with an Id less than minus one", (p) -> p.getId() < -1l );
      message = Validation.verifyParameter(sharedFolder,   "sharedFolder",   message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter(artifactType,   "artifactType",   message, "with an Id less than minus one", (p) -> p.getId() < -1l );
      message = Validation.verifyParameter(attributeType,  "attributeType",  message, "with an Id less than minus one", (p) -> p.getId() < -1l );
      message = Validation.verifyParameter(attributeValue, "attributeValue", message, "empty string",                   (p) -> p.isEmpty()     );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "getSharedPublishingArtifacts",
                            message
                         )
                   );
      }
      //@formatter:on

      var publishingUtils = new PublishingUtils(this.orcsApi);
      var publishingErrorLog = new PublishingErrorLog();

      //@formatter:off
      var publishingSharedArtifactsFolder =
         ArtifactTypeToken.SENTINEL.equals( artifactType )
            ? PublishingSharedArtifactsFolder.create
                 (
                   publishingUtils,
                   publishingErrorLog,
                   BranchId.create( branch.getId(), view ),
                   "Shared Artifacts Folder",
                   ArtifactToken.valueOf( sharedFolder.getId(), branch ),
                   attributeType
                 )
            : PublishingSharedArtifactsFolder.create
                    (
                      publishingUtils,
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

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when:
    * <dl>
    * <dt><code>branch</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>templateArtifactId</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>headArtifact</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>view</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than -1.</dd>
    * </dl>
    */

   @Override
   public InputStream msWordPreview(BranchId branch, ArtifactId templateArtifactId, ArtifactId headArtifact, ArtifactId view) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( branch,             "branch",             message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( templateArtifactId, "templateArtifactId", message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( headArtifact,       "headArtifact",       message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( view,               "view",               message, "with an Id less than minus one", (p) -> p.getId() < -1l );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "msWordPreview",
                            message
                         )
                   );
      }
      //@formatter:on

      //@formatter:off
      return
         this.msWordPreviewInternal
            (
               new MsWordPreviewRequestData
                      (
                         new PublishingTemplateRequest( "AT-" + templateArtifactId.getIdString() ),
                         BranchId.create( branch.getId(), view ),
                         List.of( headArtifact )
                      ),
               PublishingOperationsImpl.includeFolders
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
    * <dt><code>templateArtifactId</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>artifact</code>:</dt>
    * <dd>when <code>null</code>, empty, or has an entry with an Id less than 0.</dd>
    * <dt><code>view</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than -1.</dd>
    * </dl>
    */

   @Override
   public InputStream msWordPreview(BranchId branch, ArtifactId templateArtifactId, List<ArtifactId> artifacts, ArtifactId view) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( branch,             "branch",             message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( templateArtifactId, "templateArtifactId", message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( view,               "view",               message, "with an Id less than minus one", (p) -> p.getId() < -1l );

      message =
         Validation.verifyParameter
            (
               artifacts,
               "artifact",
               message,
               "an empty list, or with an Id entry less than zero",
               (p) -> p.isEmpty() || p.stream().map( Id::getId ).min( Long::compare ).get() < 0
            );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "msWordPreview",
                            message
                         )
                   );
      }
      //@formatter:on

      //@formatter:off
      return
         this.msWordPreviewInternal
            (
               new MsWordPreviewRequestData
                      (
                        new PublishingTemplateRequest("AT-" + templateArtifactId.getIdString()),
                        BranchId.create( branch.getId(), view),
                        artifacts
                      ),
               PublishingOperationsImpl.includeFolders
            );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @param msWordPreviewRequestData the {@link MsWordPreviewRequestData} structure containing the publishing
    * parameters.
    * @return an {@link InputStream} containing the Word ML XML containing the published artifacts.
    * @throws IllegalArgumentException when the parameter <code>msWordPreviewRequestData</code> is <code>null</code> or
    * invalid according to {@link MsWordPreviewRequestData#isValid}.
    */

   @Override
   public InputStream msWordPreview(MsWordPreviewRequestData msWordPreviewRequestData) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( msWordPreviewRequestData, "msWordPreviewRequestData", message, "is invalid", (p) -> !p.isValid() );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "msWordPreview",
                            message
                         )
                   );
      }
      //@formatter:on

      return this.msWordPreviewInternal(msWordPreviewRequestData, PublishingOperationsImpl.includeFolders);
   }

   private InputStream msWordPreviewInternal(MsWordPreviewRequestData msWordPreviewRequestData, boolean folderInclusion) {

      var publishingTemplate = this.defineOperations.getTemplateManagerOperations().getPublishingTemplate(
         msWordPreviewRequestData.getPublishingTemplateRequest());

      if (publishingTemplate.isSentinel()) {

         //@formatter:off
         var message =
            new Message()
                   .title( "Failed to find a publishing template." )
                   .toMessage( msWordPreviewRequestData )
                   .toString()
                   ;
         //@formatter:on

         this.logger.error(message);

         throw new OseeCoreException(message);
      }

      var publishingOptions = new PublishingOptions();

      publishingOptions.branch = msWordPreviewRequestData.getBranchId();
      publishingOptions.view = msWordPreviewRequestData.getBranchId().getViewId();
      publishingOptions.linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      publishingOptions.excludeFolders = !folderInclusion;

      var publishArtifacts = msWordPreviewRequestData.getArtifactIds();

      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try (var writer = new OutputStreamWriter(outputStream);) {
         var publisher = new MSWordPreviewPublisher(publishingOptions, publishingTemplate, writer, orcsApi, atsApi);

         publisher.publish(publishArtifacts);
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new StringBuilder(1024)
                             .append( "MsWord Renderer for \"msWordPreview\" failed.").append("\n")
                             .append("   Publishing Branch Id: ").append( publishingOptions.branch).append("\n")
                             .append("   Publishing View Id:   ").append( publishingOptions.view).append("\n")
                             .append("   Publish Artifacts:    ").append( Objects.nonNull(publishArtifacts) ? (publishArtifacts.size() > 0) ? publishArtifacts.stream().map( ArtifactId::toString).collect( Collectors.joining(", ", "[ ", " ]")) : "(no artifacts to publish specified)" : "(no artifacts to publish specified)").append( "\n")
                             .toString()
                   );
      }

      return new ByteArrayInputStream(outputStream.getBuffer(), 0, outputStream.size());
   }
   //@formatter:on

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when:
    * <dl>
    * <dt><code>branch</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>templateArtifactId</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>headArtifact</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than 0.</dd>
    * <dt><code>view</code>:</dt>
    * <dd>when <code>null</code> or with an Id less than -1.</dd>
    * </dl>
    */

   @Override
   public InputStream msWordTemplatePublish(BranchId branch, ArtifactId templateArtifactId, ArtifactId headArtifact, ArtifactId view) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( branch,             "branch",             message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( templateArtifactId, "templateArtifactId", message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( headArtifact,       "headArtifact",       message, "with an Id less than zero",      (p) -> p.getId() <  0l );
      message = Validation.verifyParameter( view,               "view",               message, "with an Id less than minus one", (p) -> p.getId() < -1l );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "msWordTemplatePublish",
                            message
                         )
                   );
      }
      //@formatter:on

      //Generate filename with the headArtifact name and current time
      //String name = orcsApi.getQueryFactory().fromBranch(branch).andId(headArtifact).asArtifactToken().getName();
      //SimpleDateFormat format = new SimpleDateFormat("MM-dd_HH-mm-ss");
      //Date date = new Date(System.currentTimeMillis());
      //String time = format.format(date);
      //String fileName = name + "_" + time + ".xml";

      var publishingTemplateRequest = new PublishingTemplateRequest("AT-" + templateArtifactId.getIdString());
      var publishingTemplate =
         this.defineOperations.getTemplateManagerOperations().getPublishingTemplate(publishingTemplateRequest);

      var publishingOptions = new PublishingOptions();

      publishingOptions.branch = branch;
      publishingOptions.view = view;
      publishingOptions.linkType = LinkType.INTERNAL_DOC_REFERENCE_USE_NAME;
      publishingOptions.excludeFolders = true;

      var publishArtifacts = List.of(headArtifact);

      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try (var writer = new OutputStreamWriter(outputStream)) {
         var publisher = new MSWordTemplatePublisher(publishingOptions, publishingTemplate, writer, orcsApi, atsApi);

         publisher.publish(publishArtifacts);
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                     new StringBuilder(1024)
                            .append( "MsWord Renderer for \"msWordTemplatePublish\" failed." ).append( "\n" )
                            .append( "   Publishing Branch Id: " ).append( publishingOptions.branch ).append( "\n" )
                            .append( "   Publishing View Id:   " ).append( publishingOptions.view   ).append( "\n" )
                            .append( "   Publish Artifacts:    " ).append
                                                                      (
                                                                         Objects.nonNull( publishArtifacts )
                                                                            ? (publishArtifacts.size() > 0)
                                                                               ? publishArtifacts.stream().map( ArtifactId::toString).collect( Collectors.joining( ", ", "[ ", " ]" ) )
                                                                               : "(no artifacts to publish specified)"
                                                                            : "(no artifacts to publish specified)"
                                                                      ).append( "\n" )
                            .toString()
                   );
         //@formatter:on
      }

      return new ByteArrayInputStream(outputStream.getBuffer(), 0, outputStream.size());
   }

   @Override
   public String renderPlainText(BranchId branchId, String data) {
      String PL_STYLE_WITH_RETURN =
         "<w:rPr><w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>)).*?</w:rPr>";
      String PL_STYLE = "<w:rStyle w:val=\"ProductLineApplicability\"((?=/>)(/>)|(.*?</w:rStyle>))";
      String PL_HIGHLIGHT =
         "<w:highlight w:val=\"light-gray\"></w:highlight><w:shd w:color=\"auto\" w:fill=\"BFBFBF\" w:val=\"clear\"></w:shd>";
      String EMPTY_PARAGRAPHS = "<w:r wsp:rsidRPr=\"\\d+\"><w:t></w:t></w:r>";
      Pattern REVIEW_COMMENT = Pattern.compile(
         "<aml:annotation[^>]*w:type=\"Word.Comment.Start\"/?>(</aml:annotation>)?[\\s\\S]+?<aml:annotation[^>]*w:type=\"Word.Comment.End\"/?>(</aml:annotation>)?[\\s\\S]+?</aml:annotation></w:r>");

      data = WordUtilities.reassignBinDataID(data);
      data = WordMlLinkHandler.renderPlainTextWithoutLinks(orcsApi.getQueryFactory(), branchId, data);
      data = WordUtilities.reassignBookMarkID(data);
      data = WordUtilities.removeNewLines(data);

      // if no extra paragraphs have been added this will replace the normal footer
      data = data.replaceAll(ReportConstants.ENTIRE_FTR_EXTRA_PARA, "");
      data = data.replaceAll(ReportConstants.ENTIRE_FTR, "");
      data = data.replaceAll(ReportConstants.EMPTY_SECTION_BREAK, ReportConstants.PAGE_BREAK);
      data = data.replaceAll(ReportConstants.NO_DATA_RIGHTS, "");

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
      Matcher commentMatch = REVIEW_COMMENT.matcher(data);
      if (commentMatch.find()) {
         data = data.replaceAll(ReportConstants.WORD_COMMENT_START, "");
         data = data.replaceAll(ReportConstants.WORD_COMMENT_END, "");
         data = data.replaceAll(ReportConstants.WORD_COMMENT, "");
      }
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

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( wordTemplateContentData, "wordTemplateContentData", message, "is invalid", (p) -> !p.isValid() );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "renderWordTemplateContent",
                            message
                         )
                   );
      }
      //@formatter:on

      WordTemplateContentRendererHandler wordRendererHandler = new WordTemplateContentRendererHandler(orcsApi, logger);

      return wordRendererHandler.renderWordML(wordTemplateContentData);
   }

   @Override
   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data) {

      WordMLApplicabilityHandler wordHandler = new WordMLApplicabilityHandler(orcsApi, logger, branchId, viewId);
      return wordHandler.previewValidApplicabilityContent(data);
   }

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when the parameter <code>wordUpdateData</code> is <code>null</code> or invalid
    * according to {@link WordUpdateData#isValid}.
    */

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData wordUpdateData) {

      Message message = null;

      //@formatter:off
      message = Validation.verifyParameter( wordUpdateData, "wordUpdateData", message, "is invalid", (p) -> !p.isValid() );

      if (Objects.nonNull(message)) {
         throw
            new IllegalArgumentException
                   (
                      Validation.buildIllegalArgumentExceptionMessage
                         (
                            this.getClass().getSimpleName(),
                            "updateWordArtifacts",
                            message
                         )
                   );
      }
      //@formatter:on

      WordUpdateArtifact updateArt = new WordUpdateArtifact(logger, orcsApi, eventAdmin);

      return updateArt.updateArtifacts(wordUpdateData);
   }

}
