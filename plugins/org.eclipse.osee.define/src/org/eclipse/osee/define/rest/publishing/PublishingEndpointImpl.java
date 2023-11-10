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

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.define.api.publishing.PublishingEndpoint;
import org.eclipse.osee.define.operations.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.publishing.UserNotAuthorizedForPublishingException;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;

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
   private final OrcsApi orcsApi;
   /**
    * Creates a new REST API end point implementation for Publishing.
    *
    * @param defineOperations a handle to the Define Service Publishing operations.
    * @throws NullPointerException when the parameter <code>defineOperations</code> is <code>null</code>.
    */

   public PublishingEndpointImpl(DefineOperations defineOperations, OrcsApi orcsApi) {
      this.defineOperations = Objects.requireNonNull(defineOperations,
         "PublishingEndpointImpl::new, parameter \"defineOperations\" cannot be null.");
      this.orcsApi = orcsApi;
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
   public List<ArtifactToken> getSharedPublishingArtifacts(BranchId branch, ArtifactId view, ArtifactId sharedFolder, ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String attributeValue) {

      try {
         PublishingPermissions.verify();
         return this.defineOperations.getPublishingOperations().getSharedPublishingArtifacts(branch, view, sharedFolder,
            artifactType, attributeType, attributeValue);
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
   public Attachment msWordPreview(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.defineOperations.getPublishingOperations().msWordPreview(branch, template, headArtifact, view);
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (IllegalArgumentException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }
   
   public Response applicabilityImpact(BranchId branch, String publish, List<ArtifactTypeToken> artTypes, List<AttributeTypeToken> attrTypes) {
	      boolean publishUpdates = (publish.equals("true")) ? true : false; 
	      try {
	         PublishingPermissions.verifyNonGroup();
	         Branch branchArt = orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrDefault(Branch.SENTINEL);
	         StreamingOutput streamingOutput = new FeatureImpactStreamingOutput(branchArt, orcsApi, defineOperations, publishUpdates, artTypes, attrTypes);
		      ResponseBuilder builder = Response.ok(streamingOutput);
		      builder.header("Content-Disposition", "attachment; filename=" + branchArt.getName().replaceAll("[^a-zA-Z0-9-]", "_") +".zip");
		      return builder.build();
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
   public Attachment msWordPreview(BranchId branch, ArtifactId template, List<ArtifactId> artifacts, ArtifactId view) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.defineOperations.getPublishingOperations().msWordPreview(branch, template, artifacts, view);
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
   public Attachment msWordPreview(MsWordPreviewRequestData msWordPreviewRequestData) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.defineOperations.getPublishingOperations().msWordPreview(msWordPreviewRequestData);
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
   public Attachment msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.defineOperations.getPublishingOperations().msWordTemplatePublish(branch, template, headArtifact,
            view);
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
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData wordTemplateContentData) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.defineOperations.getPublishingOperations().renderWordTemplateContent(wordTemplateContentData);
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
         return this.defineOperations.getPublishingOperations().updateWordArtifacts(wordUpdateData);
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
