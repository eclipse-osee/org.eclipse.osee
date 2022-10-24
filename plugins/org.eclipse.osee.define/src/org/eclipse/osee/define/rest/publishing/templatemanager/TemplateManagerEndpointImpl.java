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

import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplate;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateSafeNames;
import org.eclipse.osee.define.api.publishing.templatemanager.TemplateManagerEndpoint;
import org.eclipse.osee.define.operations.publishing.PublishingPermissions;
import org.eclipse.osee.define.rest.DefineApplication;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;

/**
 * Implementation of the {@link TemplateManagerEndpoint} interface contains the methods that are invoked when a REST API
 * call has been made for a publishing template.
 *
 * @author Loren K. Ashley
 */

public class TemplateManagerEndpointImpl implements TemplateManagerEndpoint {

   /**
    * Verifies the threads current user is authorized to access the REST API for end points requiring Publishing Group
    * membership.
    *
    * @implNote Catches the OSEE exception and wraps it into a {@link javax.ws.rs} exception.
    * @throws NotAuthorizedException when the user is not a member of the {@link CoreUserGroups#OseeAccessAdmin}.
    */

   private static void verifyAccess() {
      try {
         PublishingPermissions.verify();
      } catch (OseeAccessDeniedException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build());
      }
   }

   /**
    * Verifies the threads current user is authorized to access the REST API for end points that do not require
    * membership in the Publishing Group.
    *
    * @implNote Catches the OSEE exception and wraps it into a {@link javax.ws.rs} exception.
    * @throws NotAuthorizedException when the user is not a member of the {@link CoreUserGroups#OseeAccessAdmin}.
    */

   private static void verifyAccessNonGroup() {
      try {
         PublishingPermissions.verify();
      } catch (OseeAccessDeniedException e) {
         throw new NotAuthorizedException(e.getMessage(), Response.status(Response.Status.UNAUTHORIZED).build());
      }
   }

   /**
    * Saves a handle to the {@link DefineOperations} service.
    */

   private final DefineOperations defineOperations;

   /**
    * Creates an instance of the {@link TemplateManagerEndpointImpl} class.
    *
    * @implNote Only on instance of this class should be instantiated an only by the {@link DefineApplication} class.
    * @param defineOperations a handle to the {@link DefineOperations} service.
    */

   public TemplateManagerEndpointImpl(DefineOperations defineOperations) {
      this.defineOperations = Objects.requireNonNull(defineOperations,
         "TemplateManagerEndpointImpl::new, parameter \"defineOperations\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public void deleteCache() {

      TemplateManagerEndpointImpl.verifyAccess();

      try {
         this.defineOperations.getTemplateManagerOperations().deleteCache();
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
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public PublishingTemplate getPublishingTemplate(PublishingTemplateRequest publishingTemplateRequest) {

      TemplateManagerEndpointImpl.verifyAccessNonGroup();

      try {
         return this.defineOperations.getTemplateManagerOperations().getPublishingTemplate(publishingTemplateRequest);
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
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public PublishingTemplate getPublishingTemplate(String primaryKey, String secondaryKey) {

      TemplateManagerEndpointImpl.verifyAccessNonGroup();

      try {
         return this.defineOperations.getTemplateManagerOperations().getPublishingTemplate(primaryKey, secondaryKey);
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
   public PublishingTemplateSafeNames getPublishingTemplateSafeNames() {

      TemplateManagerEndpointImpl.verifyAccessNonGroup();

      try {
         return this.defineOperations.getTemplateManagerOperations().getPublishingTemplateSafeNames();
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }
}

/* EOF */
