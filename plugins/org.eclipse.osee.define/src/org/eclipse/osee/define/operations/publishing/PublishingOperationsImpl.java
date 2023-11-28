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
import java.util.stream.Collectors;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.api.publishing.PublishingOperations;
import org.eclipse.osee.define.api.publishing.PublishingOptions;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMLApplicabilityHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordMlLinkHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordTemplateContentRendererHandler;
import org.eclipse.osee.define.rest.internal.wordupdate.WordUpdateArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Validation;
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
    * Enumeration with configuration data for the publishable document types.
    */

   //@formatter:off
   private enum DocumentType {

      PREVIEW_WITH_FOLDERS
         (
           "Publish Preview With Folders",                     /* Thread Name */
           RendererMap.of                                      /* Publishing Options */
              (
                RendererOption.EXCLUDE_FOLDERS,    false,
                RendererOption.LINK_TYPE,          LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
                RendererOption.MAX_OUTLINE_DEPTH,  9
              )
         ),

      PREVIEW_WITHOUT_FOLDERS
         (
           "Publish Preview Without Folders",                  /* Thread Name        */
           RendererMap.of                                     /* Publishing Options */
              (
                RendererOption.EXCLUDE_FOLDERS,    true,
                RendererOption.LINK_TYPE,          LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
                RendererOption.MAX_OUTLINE_DEPTH,  9
              )
         );
      //@formatter:on

      /**
       * Saves the {@link RendererMap} for the {@link DocumentType}.
       */

      private RendererMap publishingOptions;

      /**
       * Saves the basename for the thread used to publish a document of the {@link DocumentType}.
       */

      private String threadName;

      /**
       * Creates a {@link DocumentType} member and saves the configuration data for the document type.
       *
       * @param threadName the basename for publishing documents of the {@link DocumentType}.
       * @param publishingOptions the {@link RendererMap} for publishing document of the {@link DocumentType}.
       */

      DocumentType(String threadName, RendererMap publishingOptions) {

         this.threadName = threadName;
         this.publishingOptions = publishingOptions;

      }

      /**
       * Gets the {@link PublishingOptions} for the {@link DocumentType}.
       *
       * @return the {@link PublishingOptions} for the {@link DocumentType}.
       */

      RendererMap getPublishingOptions() {
         return this.publishingOptions;
      }

      /**
       * Gets the base thread name for publishing document of the {@link DocumentType}.
       *
       * @return the base thread name for the {@link DocumentType}.
       */

      String getThreadName() {
         return this.threadName;
      }
   }

   private static final boolean excludeFolders = true;
   private static final boolean includeFolders = false;

   /**
    * Saves the single instance of the {@link PublishingOperationsImpl}.
    */

   private static PublishingOperationsImpl publishingOperationsImpl = null;

   /**
    * Saves a {@link PublishingOptionsFactory} that is used to create new {@link PublishingOptions} objects preset with
    * defaults for the {@link DocumentType}.
    */

   //@formatter:off
   private static final PublishingOptionsFactory<PublishingOperationsImpl.DocumentType> publishingOptionsFactory =
      PublishingOptionsFactory.ofEntries
         (
            PublishingOperationsImpl.DocumentType.class,
            PublishingOperationsImpl.DocumentType::getPublishingOptions
         );
   //@formatter:on

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
   private final AttachmentFactory attachmentFactory;

   private PublishingOperationsImpl(DefineOperations defineOperations, OrcsApi orcsApi, AtsApi atsApi, Log logger, EventAdmin eventAdmin) {
      this.defineOperations = defineOperations;
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.logger = logger;
      this.eventAdmin = eventAdmin;

      //@formatter:off
      this.attachmentFactory =
         new AttachmentFactory
                (
                   "MsWordPreview",
                   "xml",
                   this.orcsApi
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
         Validation.require
            (
               message,
               branch,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "branch",
               "cannot be null",
               Objects::isNull,
               "branch identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               view,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "view",
               "cannot be null",
               Objects::isNull,
               "view artifact identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

      message =
         Validation.require
            (
               message,
               sharedFolder,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "sharedFolder",
               "cannot be null",
               Objects::isNull,
               "shared folder artifact identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               artifactType,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "artifactType",
               "cannot be null",
               Objects::isNull,
               "artifcat type identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

      message =
         Validation.require
            (
               message,
               attributeType,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "getSharedPublishingArtifacts",
               "attributeType",
               "cannot be null",
               Objects::isNull,
               "attribute type identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

      message =
         Validation.require
            (
               message,
               attributeValue,
               Validation.ValueType.PARAMETER,
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
   public Attachment msWordPreview(BranchId branch, ArtifactId templateArtifactId, ArtifactId headArtifact, ArtifactId view) {

      Message message = null;

      //@formatter:off
      message =
         Validation.require
            (
               message,
               branch,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordPreview",
               "branch",
               "cannot be null",
               Objects::isNull,
               "branch identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               templateArtifactId,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordPreview",
               "templateArtifactId",
               "cannot be null",
               Objects::isNull,
               "publishing template identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               headArtifact,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordPreview",
               "headArtifact",
               "cannot be null",
               Objects::isNull,
               "head artifact identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               view,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordPreview",
               "view",
               "cannot be null",
               Objects::isNull,
               "view artifact identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
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
      var attachment =
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

      return attachment;
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
   public Attachment msWordPreview(BranchId branch, ArtifactId templateArtifactId, List<ArtifactId> artifacts, ArtifactId view) {

      Message message = null;

      //@formatter:off
      message =
         Validation.require
            (
               message,
               branch,
               Validation.ValueType.PARAMETER,
               "PublishingOperationImpl",
               "msWordPreview",
               "branch",
               "cannot be null",
               Objects::isNull,
               "branch identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               templateArtifactId,
               Validation.ValueType.PARAMETER,
               "PublishingOperationImpl",
               "msWordPreview",
               "templateArtifactId",
               "cannot be null",
               Objects::isNull,
               "publishing template identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               artifacts,
               Validation.ValueType.PARAMETER,
               "PublishingOperationImpl",
               "msWordPreview",
               "artifacts",
               "cannot be null",
               Objects::isNull,
               "artifact identifier list is non-empty, all elements are non-null, and all elements are non-negative",
               Validation.<List<ArtifactId>>predicate( List::isEmpty )
                  .or( Validation.collectionContainsNull )
                  .or( Validation.collectionElementPredicate( ( p ) -> p.getId() < 0 ) )
            );

      message =
         Validation.require
            (
               message,
               view,
               Validation.ValueType.PARAMETER,
               "PublishingOperationImpl",
               "msWordPreview",
               "view",
               "cannot be null",
               Objects::isNull,
               "view artifact identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
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
      var attachment =
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

      return attachment;
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
   public Attachment msWordPreview(MsWordPreviewRequestData msWordPreviewRequestData) {

      //@formatter:off
      Validation.require
         (
            msWordPreviewRequestData,
            Validation.ValueType.PARAMETER,
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

      return this.msWordPreviewInternal
                (
                   msWordPreviewRequestData,
                   PublishingOperationsImpl.includeFolders
                );
      //@formatter:on
   }

   private Attachment msWordPreviewInternal(MsWordPreviewRequestData msWordPreviewRequestData, boolean folderInclusion) {

      //@formatter:off
      var branchId = msWordPreviewRequestData.getBranchId();
      var viewId = branchId.getViewId();
      var firstArtifactId = msWordPreviewRequestData.getArtifactIds().get(0);

      var publishingTemplate =
         this.defineOperations
            .getTemplateManagerOperations()
            .getPublishingTemplate( msWordPreviewRequestData.getPublishingTemplateRequest() );

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

      var publishingOptions =
         PublishingOperationsImpl.publishingOptionsFactory.create
            (
               PublishingOperationsImpl.DocumentType.PREVIEW_WITH_FOLDERS,
               branchId,
               viewId
            );

      var publishArtifacts = msWordPreviewRequestData.getArtifactIds();

      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try ( var writer = new OutputStreamWriter(outputStream) ) {

         new GeneralPublishingWordTemplateProcessorServer
                (
                  orcsApi,
                  atsApi
                )
             .configure
                (
                   publishingTemplate,
                   publishingOptions
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
                             .segment( "Publishing Branch Id", branchId )
                             .segment( "Publishing View Id",   viewId   )
                             .segment( "Publish Artifacts",    Objects.nonNull(publishArtifacts)
                                                                  ? (publishArtifacts.size() > 0)
                                                                       ? publishArtifacts.stream().map( ArtifactId::toString).collect( Collectors.joining(", ", "[ ", " ]"))
                                                                       : "(no artifacts to publish specified)"
                                                                  : "(no artifacts to publish specified)" )
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
               PublishingOperationsImpl.DocumentType.PREVIEW_WITH_FOLDERS.name(),
               branchId,
               firstArtifactId
            );

      return attachment;
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
   public Attachment msWordTemplatePublish(BranchId branch, ArtifactId templateArtifactId, ArtifactId headArtifact, ArtifactId view) {

      Message message = null;

      //@formatter:off
      message =
         Validation.require
            (
               message,
               branch,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordTemplatePublish",
               "branch",
               "cannot be null",
               Objects::isNull,
               "branch identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               templateArtifactId,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordTemplatePublish",
               "templateArtifactId",
               "cannot be null",
               Objects::isNull,
               "publishing template identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               headArtifact,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordTemplatePublish",
               "headArtifact",
               "cannot be null",
               Objects::isNull,
               "head artifact identifier is non-negative",
               (p) -> p.getId() <  0l
            );

      message =
         Validation.require
            (
               message,
               view,
               Validation.ValueType.PARAMETER,
               "PublishingOperationsImpl",
               "msWordTemplatePublish",
               "view",
               "cannot be null",
               Objects::isNull,
               "view artifact identifier is greater than or equal to minus one",
               (p) -> p.getId() < -1l
            );

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

      var publishingTemplateRequest = new PublishingTemplateRequest("AT-" + templateArtifactId.getIdString());
      var publishingTemplate = this.defineOperations.getTemplateManagerOperations().getPublishingTemplate(publishingTemplateRequest);

      var publishingOptions =
         PublishingOperationsImpl.publishingOptionsFactory.create
            (
               PublishingOperationsImpl.DocumentType.PREVIEW_WITHOUT_FOLDERS,
               branch,
               view
            );

      var publishArtifacts = List.of( headArtifact );

      var outputStream = new ByteArrayOutputStream() {
         byte[] getBuffer() {
            return this.buf;
         }
      };

      try ( var writer = new OutputStreamWriter( outputStream ) ) {

         new WordTemplateProcessorServer
                (
                  orcsApi,
                  atsApi
                )
             .configure
                (
                   publishingTemplate,
                   publishingOptions
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
                            .title( "PublishingOperationsImpl::msWordTemplatePublish, Failed to publish document." )
                            .indentInc()
                            .segment( "Publishing Branch Id", branch.getIdString() )
                            .segment( "Publishing View Id",   view.getIdString()   )
                            .segment( "Publish Artifacts",    Objects.nonNull( publishArtifacts )
                                                                 ? (publishArtifacts.size() > 0)
                                                                      ? publishArtifacts.stream().map( ArtifactId::toString).collect( Collectors.joining( ", ", "[ ", " ]" ) )
                                                                      : "(no artifacts to publish specified)"
                                                                 : "(no artifacts to publish specified)" )
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
               PublishingOperationsImpl.DocumentType.PREVIEW_WITHOUT_FOLDERS.name(),
               branch,
               headArtifact
            );

      return attachment;
      //@formatter:on
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
      Validation.require
         (
            wordTemplateContentData,
            Validation.ValueType.PARAMETER,
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

   /**
    * {@inheritDoc}
    *
    * @throws IllegalArgumentException when the parameter <code>wordUpdateData</code> is <code>null</code> or invalid
    * according to {@link WordUpdateData#isValid}.
    */

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData wordUpdateData) {

      //@formatter:off
      Validation.require
         (
            wordUpdateData,
            Validation.ValueType.PARAMETER,
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
