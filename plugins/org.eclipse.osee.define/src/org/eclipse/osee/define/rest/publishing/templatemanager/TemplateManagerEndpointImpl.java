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

package org.eclipse.osee.define.rest.publishing.templatemanager;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.api.publisher.templatemanager.TemplateManagerOperations;
import org.eclipse.osee.define.operations.publisher.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.publisher.publishing.UserNotAuthorizedForPublishingException;
import org.eclipse.osee.define.rest.DefineApplication;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.TemplateManagerEndpoint;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Implementation of the {@link TemplateManagerEndpoint} interface contains the methods that are invoked when a REST API
 * call has been made for a publishing template.
 *
 * @author Loren K. Ashley
 */

public class TemplateManagerEndpointImpl implements TemplateManagerEndpoint {

   /**
    * Saves a handle to the {@link TemplateManagerOperations} service.
    */

   private final @NonNull TemplateManagerOperations templateManagerOperations;

   /**
    * Creates an instance of the {@link TemplateManagerEndpointImpl} class.
    *
    * @implNote Only one instance of this class should be instantiated and only by the {@link DefineApplication} class.
    * @param defineOperations a handle to the {@link DefineOperations} service.
    */

   public TemplateManagerEndpointImpl(@NonNull DefineOperations defineOperations) {
      //@formatter:off
      try {
         this.templateManagerOperations =
            defineOperations
               .getPublisherOperations()
               .getTemplateManagerOperations();
      } catch( NullPointerException e ) {
         var npe =
            new NullPointerException
                   (
                      "TemplateManagerEndpointImpl::new, unable to obtian the \"TemplateManagerOperations\"."
                   );

         npe.initCause(e);

         throw npe;
      } catch( Exception e ) {
         throw
            new OseeCoreException
                   (
                      "TemplateManagerEndpointImpl::new, unable to obtian the \"TemplateManagerOperations\".",
                      e
                   );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public void deleteCache() {

      try {
         PublishingPermissions.verify();
         this.templateManagerOperations.deleteCache();
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
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
   public PublishingTemplate getPublishingTemplate(PublishingTemplateRequest publishingTemplateRequest) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.templateManagerOperations.getPublishingTemplate(publishingTemplateRequest);
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
   public String getPublishingTemplateStatus(PublishingTemplateRequest publishingTemplateRequest) {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.templateManagerOperations.getPublishingTemplateStatus(publishingTemplateRequest);
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
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public PublishingTemplateKeyGroups getPublishingTemplateKeyGroups() {

      try {
         PublishingPermissions.verifyNonGroup();
         return this.templateManagerOperations.getPublishingTemplateKeyGroups();
      } catch (UserNotAuthorizedForPublishingException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build(), e);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }
}

/* EOF */
