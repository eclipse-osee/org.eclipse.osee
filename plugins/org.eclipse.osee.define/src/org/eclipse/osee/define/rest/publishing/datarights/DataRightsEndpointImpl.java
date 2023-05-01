/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.define.rest.publishing.datarights;

import java.util.List;
import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.publishing.datarights.DataRightsEndpoint;
import org.eclipse.osee.define.operations.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.publishing.UserNotAuthorizedForPublishingException;
import org.eclipse.osee.define.rest.DefineApplication;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.publishing.DataRightResult;

/**
 * Implementation of the {@link DataRightsEndpoint} interface contains the methods that are invoked when a REST API call
 * has been made for artifact data rights.
 *
 * @author Angel Avila
 * @author Loren K. Ashley
 */

public class DataRightsEndpointImpl implements DataRightsEndpoint {

   /**
    * Saves a handle to the {@link DefineOperations} service.
    */

   private final DefineOperations defineOperations;

   /**
    * Creates an instance of the {@link DataRightsEndpointImpl} class.
    *
    * @implNote Only one instance of this class should be instantiated and only by the {@link DefineApplication} class.
    * @param defineOperations a handle to the {@link DefineOperations} service.
    */

   public DataRightsEndpointImpl(DefineOperations defineOperations) {
      this.defineOperations = Objects.requireNonNull(defineOperations,
         "DataRightsEndpointImpl::new, parameter \"defineOperaions\" cannot be null.");
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
         this.defineOperations.getDataRightsOperations().deleteCache();
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
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public DataRightResult getDataRights(BranchId branchIdentifier, List<ArtifactId> artifactIdentifiers) {

      try {
         PublishingPermissions.verifyNonGroup();
         return defineOperations.getDataRightsOperations().getDataRights(branchIdentifier, artifactIdentifiers);
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
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public DataRightResult getDataRights(BranchId branchIdentifier, String overrideClassification, List<ArtifactId> artifactIdentifiers) {

      try {
         PublishingPermissions.verifyNonGroup();
         return defineOperations.getDataRightsOperations().getDataRights(branchIdentifier, overrideClassification,
            artifactIdentifiers);
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
