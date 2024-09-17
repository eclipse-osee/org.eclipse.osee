/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.rest.publishing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.markdown.MarkdownConverter;
import org.eclipse.osee.define.operations.publisher.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.publisher.publishing.UserNotAuthorizedForPublishingException;
import org.eclipse.osee.define.rest.api.publisher.publishing.LinkHandlerResult;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateChange;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * Provides the wrapper methods that expose the Publishing operations methods as REST API end points.
 *
 * @author Loren K. Ashley
 */

public class PublishingEndpointImpl implements PublishingEndpoint {

   /**
    * Saves a handle to the Define Service Publishing operations implementation.
    */

   private final DefineOperations defineOperations;

   /**
    * Creates a new REST API end point implementation for Publishing.
    *
    * @param defineOperations a handle to the Define Service Publishing operations.
    * @throws NullPointerException when the parameter <code>defineOperations</code> is <code>null</code>.
    */

   public PublishingEndpointImpl(DefineOperations defineOperations) {
      this.defineOperations = Objects.requireNonNull(defineOperations,
         "PublishingEndpointImpl::new, parameter \"defineOperations\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws NotFoundException when shared publishing artifacts were not found for the specified parameters.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public List<ArtifactToken> getSharedPublishingArtifacts(BranchId branch, ArtifactId view, ArtifactId sharedFolder,
      ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String attributeValue) {

      try {
         PublishingPermissions.verify();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .getSharedPublishingArtifacts
                  (
                     branch,
                     view,
                     sharedFolder,
                     artifactType,
                     attributeType,
                     attributeValue
                  )
               .stream()
               .map( ArtifactToken::valueOf )
               .collect( Collectors.toList() );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (OseeNotFoundException onfe) {
         throw new NotFoundException(onfe.getMessage(), Response.status(Response.Status.NOT_FOUND).build(), onfe);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws NotFoundException when shared publishing artifacts were not found for the specified parameters.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public LinkHandlerResult link(BranchId branchId, ArtifactId viewId, ArtifactId artifactId,
      TransactionId transactionId, LinkType linkType, PresentationType presentationType) {

      try {

         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .link
                  (
                     branchId,
                     viewId,
                     artifactId,
                     transactionId,
                     linkType,
                     presentationType
                  );
            //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (OseeNotFoundException onfe) {
         throw new NotFoundException(onfe.getMessage(), Response.status(Response.Status.NOT_FOUND).build(), onfe);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Deprecated
   @Override
   public Attachment msWordPreview(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view) {

      try {
         PublishingPermissions.verifyNonGroup();

         var branchSpecification = new BranchSpecification(branch, view);

         //@formatter:off
         var publishingTemplateRequest =
            new PublishingTemplateRequest
                   (
                      "AT-" + template.getIdString(),
                      FormatIndicator.WORD_ML
                   );

         var publishingRendererOptions =
            RendererMap.of
               (
                  RendererOption.PUBLISH_IDENTIFIER, "Publish Preview With Folders",
                  RendererOption.LINK_TYPE,          LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
                  RendererOption.MAX_OUTLINE_DEPTH,  9,
                  RendererOption.PUBLISHING_FORMAT,  FormatIndicator.WORD_ML,
                  RendererOption.BRANCH,             branchSpecification.getBranchIdWithOutViewId(),
                  RendererOption.VIEW,               branchSpecification.getViewId()
               );

         var msWordPreviewRequestData =
            new PublishingRequestData
                   (
                      publishingTemplateRequest,
                      publishingRendererOptions,
                      List.of( headArtifact )
                   );
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .msWordPreview
                  (
                     msWordPreviewRequestData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }

   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Deprecated
   @Override
   public Attachment msWordPreview(BranchId branch, ArtifactId template, List<ArtifactId> artifacts, ArtifactId view) {

      try {
         PublishingPermissions.verifyNonGroup();

         var branchSpecification = new BranchSpecification(branch, view);

         //@formatter:off
         var publishingTemplateRequest =
            new PublishingTemplateRequest
                   (
                      "AT-" + template.getIdString(),
                      FormatIndicator.WORD_ML
                   );

         var publishingRendererOptions =
            RendererMap.of
               (
                  RendererOption.PUBLISH_IDENTIFIER, "Publish Preview With Folders",
                  RendererOption.LINK_TYPE,          LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
                  RendererOption.MAX_OUTLINE_DEPTH,  9,
                  RendererOption.PUBLISHING_FORMAT,  FormatIndicator.WORD_ML,
                  RendererOption.BRANCH,             branchSpecification.getBranchIdWithOutViewId(),
                  RendererOption.VIEW,               branchSpecification.getViewId()
               );

         var msWordPreviewRequestData =
            new PublishingRequestData
                   (
                      publishingTemplateRequest,
                      publishingRendererOptions,
                      artifacts
                   );
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .msWordPreview
                  (
                     msWordPreviewRequestData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }

   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public Attachment msWordPreview(PublishingRequestData msWordPreviewRequestData) {

      var thread = Thread.currentThread();
      var origThreadName = thread.getName();
      thread.setName("PublishingEndpointImpl::msWordPreviewRequestData");

      try {
         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .msWordPreview
                  (
                     msWordPreviewRequestData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      } finally {
         thread.setName(origThreadName);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   @Deprecated
   public Attachment msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact,
      ArtifactId view) {
      try {
         PublishingPermissions.verifyNonGroup();

         var branchSpecification = new BranchSpecification(branch, view);

         //@formatter:off
         var publishingTemplateRequest =
            new PublishingTemplateRequest
                   (
                      "AT-" + template.getIdString(),
                      FormatIndicator.WORD_ML
                   );

         var publishingRendererOptions =
            RendererMap.of
               (
                  RendererOption.PUBLISH_IDENTIFIER,                               "Publish Preview Without Folders",
                  RendererOption.OUTLINING_OPTION_OVERRIDE_EXCLUDE_ARTIFACT_TYPES, List.of( CoreArtifactTypes.Folder ),
                  RendererOption.LINK_TYPE,                                        LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
                  RendererOption.MAX_OUTLINE_DEPTH,                                9,
                  RendererOption.PUBLISHING_FORMAT,                                FormatIndicator.WORD_ML,
                  RendererOption.BRANCH,                                           branchSpecification.getBranchIdWithOutViewId(),
                  RendererOption.VIEW,                                             branchSpecification.getViewId()
               );

         var msWordPreviewRequestData =
            new PublishingRequestData
                   (
                      publishingTemplateRequest,
                      publishingRendererOptions,
                      List.of( headArtifact )
                   );

         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .msWordPreview
                  (
                     msWordPreviewRequestData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }

   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws NotFoundException when shared publishing artifacts were not found for the specified parameters.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public Attachment msWordWholeWordContentPublish(BranchId branchId, ArtifactId viewId, ArtifactId artifactId,
      TransactionId transactionId, LinkType linkType, PresentationType presentationType, boolean includeErrorLog) {

      try {
         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
            .getPublisherOperations()
            .getPublishingOperations()
            .msWordWholeWordContentPublish( branchId, viewId, artifactId, transactionId, linkType, presentationType, includeErrorLog );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (OseeNotFoundException onfe) {
         throw new NotFoundException(onfe.getMessage(), Response.status(Response.Status.NOT_FOUND).build(), onfe);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData wordTemplateContentData) {

      try {
         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .renderWordTemplateContent
                  (
                     wordTemplateContentData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws NotFoundException when shared publishing artifacts were not found for the specified parameters.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public LinkHandlerResult unlink(BranchId branchId, ArtifactId viewId, ArtifactId artifactId,
      TransactionId transactionId, LinkType linkType) {

      try {

         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .unlink
                  (
                     branchId,
                     viewId,
                     artifactId,
                     transactionId,
                     linkType
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (OseeNotFoundException onfe) {
         throw new NotFoundException(onfe.getMessage(), Response.status(Response.Status.NOT_FOUND).build(), onfe);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }

   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public String updateLinks(BranchId branchId) {

      try {

         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .updateLinks(branchId);
         //@formatter:on

      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData wordUpdateData) {

      try {
         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .updateWordArtifacts
                  (
                     wordUpdateData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public Response convertMarkdownToHtml(String markdownContent) {
      try {
         PublishingPermissions.verifyNonGroup();
         if (markdownContent == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Markdown content is null").build();
         }
         MarkdownConverter mdConverter = new MarkdownConverter();
         String html = mdConverter.convertToHtmlString(markdownContent);
         InputStream stream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
         return Response.ok(stream).header("Content-Disposition", "attachment; filename=markdownToHtml.html").build();
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   @Override
   public Attachment publishMarkdownAsHtml(PublishingRequestData publishMarkdownAsHtmlRequestData) {

      var thread = Thread.currentThread();
      var origThreadName = thread.getName();
      thread.setName("PublishingEndpointImpl::msWordPreviewRequestData");

      try {
         PublishingPermissions.verifyNonGroup();
         //@formatter:off
         return
            this.defineOperations
               .getPublisherOperations()
               .getPublishingOperations()
               .publishMarkdownAsHtml
                  (
                     publishMarkdownAsHtmlRequestData
                  );
         //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      } finally {
         thread.setName(origThreadName);
      }
   }

   @Override
   public String cleanAllMarkdownArtifactsForBranch(BranchId branchId) {

      try {
         PublishingPermissions.verify();
         //@formatter:off
	      return
	         this.defineOperations
	            .getPublisherOperations()
	            .getPublishingOperations()
	            .cleanAllMarkdownArtifactsForBranch(branchId);
	      //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

   @Override
   public String removeMarkdownBoldSymbolsFromAllMarkdownArtifactsForBranch(BranchId branchId) {

      try {
         PublishingPermissions.verify();
         //@formatter:off
	      return
	         this.defineOperations
	            .getPublisherOperations()
	            .getPublishingOperations()
	            .removeMarkdownBoldSymbolsFromAllMarkdownArtifactsForBranch(branchId);
	      //@formatter:on
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

}

/* EOF */
