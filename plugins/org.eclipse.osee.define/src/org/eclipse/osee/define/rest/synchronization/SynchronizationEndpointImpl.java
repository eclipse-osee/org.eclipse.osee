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

package org.eclipse.osee.define.rest.synchronization;

import java.io.InputStream;
import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineOperations;
import org.eclipse.osee.define.api.synchronization.ExportRequest;
import org.eclipse.osee.define.api.synchronization.ImportRequest;
import org.eclipse.osee.define.api.synchronization.SynchronizationEndpoint;
import org.eclipse.osee.define.operations.publishing.PublishingPermissions;
import org.eclipse.osee.define.operations.synchronization.BadDocumentRootException;
import org.eclipse.osee.define.operations.synchronization.UnknownSynchronizationArtifactTypeException;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;

/**
 * Provides the wrapper methods that expose the Synchronization operations methods as REST API end points.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationEndpointImpl implements SynchronizationEndpoint {

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
    * Saves a handle to the Define Service Publishing operations implementation.
    */

   private final DefineOperations defineOperations;

   /**
    * Creates a new REST API end point implementation for Synchronization.
    *
    * @param defineOperations a handle to the Define Service Synchronization operations.
    * @throws NullPointerException when the parameter <code>defineOperations</code> is <code>null</code>.
    */

   public SynchronizationEndpointImpl(DefineOperations defineOperations) {
      this.defineOperations = Objects.requireNonNull(defineOperations,
         "SynchronizationEndpointImpl::new, parameter \"defineOperations\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException when the user is not an active login user that is a member of the publishing group.
    * @throws BadRequestException when the operation's method indicates any arguments were illegal.
    * @throws ServerErrorException when an unaccounted for exception is thrown by the operations method.
    */

   @Override
   public InputStream exporter(ExportRequest exportRequest) {

      SynchronizationEndpointImpl.verifyAccess();

      try {
         return this.defineOperations.getSynchronizationOperations().exporter(exportRequest);
      } catch (IllegalArgumentException | BadDocumentRootException | UnknownSynchronizationArtifactTypeException iae) {
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
   public void importer(ImportRequest importRequest, InputStream inputStream) {

      SynchronizationEndpointImpl.verifyAccess();

      try {
         this.defineOperations.getSynchronizationOperations().importer(importRequest, inputStream);
      } catch (IllegalArgumentException | BadDocumentRootException | UnknownSynchronizationArtifactTypeException iae) {
         throw new BadRequestException(iae.getMessage(), Response.status(Response.Status.BAD_REQUEST).build(), iae);
      } catch (Exception e) {
         throw new ServerErrorException(e.getMessage(), Response.status(Response.Status.INTERNAL_SERVER_ERROR).build(),
            e);
      }
   }

}

/* EOF */
