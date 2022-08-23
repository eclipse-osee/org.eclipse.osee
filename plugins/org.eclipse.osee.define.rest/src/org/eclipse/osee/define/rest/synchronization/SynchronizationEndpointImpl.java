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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.synchronization.ExportRequest;
import org.eclipse.osee.define.api.synchronization.ImportRequest;
import org.eclipse.osee.define.api.synchronization.SynchronizationEndpoint;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Implementation of the {@link SynchronizationEndpoint} interface contains the methods that are invoked when a REST API
 * call has been made for a synchronization artifact.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationEndpointImpl implements SynchronizationEndpoint {

   /**
    * Saves the single instance of the {@SynchronizationEndpointImpl}.
    */

   private static SynchronizationEndpointImpl synchronizationEndpointImpl = null;

   /**
    * Saves the orcsApi handle.
    */

   private final OrcsApi orcsApi;

   /**
    * Creates an object to process synchronization REST calls.
    *
    * @param orcsApi the {@link OrcsApi} handle.
    */

   private SynchronizationEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * Gets or creates the single instance of the {@link SynchronizationEndpointImpl} class.
    *
    * @param orcsApi A reference to the {@link OrcsApi}.
    * @return the single {@link SynchronizationEndpointImpl} object.
    * @throws NullPointerException when the parameter <code>orcsApi</code> is <code>null</code> and the single instance
    * of the {@link SynchronizationEndpointImpl} has not yet been created.
    */

   public synchronized static SynchronizationEndpointImpl create(OrcsApi orcsApi) {
      //@formatter:off
      return
         Objects.isNull( SynchronizationEndpointImpl.synchronizationEndpointImpl )
            ? ( SynchronizationEndpointImpl.synchronizationEndpointImpl = new SynchronizationEndpointImpl( Objects.requireNonNull( orcsApi ) ) )
            : SynchronizationEndpointImpl.synchronizationEndpointImpl;
      //@formatter:on
   }

   /**
    * Builds an exception message containing the message and stack trace from the provided exception. This is done so
    * that the full server stack trace is sent to the client in the exception message.
    *
    * @param e the {@link Throwable} to create a message for.
    * @return the exception message with full stack trace.
    */

   private String buildExceptionMessage(Throwable e) {
      var stringWriter = new StringWriter();
      var printWriter = new PrintWriter(stringWriter);
      e.printStackTrace(printWriter);

      //@formatter:off
      return
         new StringBuilder( 2 * 1024 )
                .append( "\n" )
                .append( "<---S-E-R-V-E-R---E-X-C-E-P-T-I-O-N--->" ).append( "\n" )
                .append( e.getMessage() ).append( "\n" )
                .append( stringWriter.toString() ).append( "\n" )
                .toString();
      //@formatter:on
   }

   /**
    * Builds an exception message containing the message and stack trace from the provided exception. This is done so
    * that the full server stack trace is sent to the client in the exception message.
    *
    * @param e the {@link Throwable} to create a message for.
    * @return the exception message with full stack trace.
    */

   private String buildBadInputMessage(String parameterName, Object badObject) {

      var indent0 = IndentedString.indentString(0);
      var indent1 = IndentedString.indentString(1);

      //@formatter:off
      var message =
         new StringBuilder( 2 * 1024 )
                .append( "\n" )
                .append( indent0 ).append( "<---S-E-R-V-E-R---E-X-C-E-P-T-I-O-N--->" ).append( "\n" )
                .append( indent0 ).append( "Bad input parameter." ).append( "\n" )
                .append( indent1 ).append( "Parameter:                " ).append( parameterName ).append( "\n" )
                .append( indent1 ).append( "Received Parameter Value: " );
      //@formatter:on

      if (Objects.isNull(badObject)) {
         message.append("(null)").append("\n");
      } else if (badObject instanceof ToMessage) {
         ((ToMessage) badObject).toMessage(2, message);
      } else {
         message.append(badObject.toString()).append("\n");
      }

      return message.toString();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public InputStream export(ExportRequest exportRequest) {

      try {
         orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);
      } catch (OseeAccessDeniedException e) {
         throw new NotAuthorizedException(this.buildExceptionMessage(e),
            Response.status(Response.Status.UNAUTHORIZED).build());
      }

      if (Objects.isNull(exportRequest) || !exportRequest.isValid()) {
         throw new BadRequestException(this.buildBadInputMessage("exportRequest", exportRequest),
            Response.status(Response.Status.BAD_REQUEST).build());
      }

      try {
         var synchronizationArtifactType = exportRequest.getSynchronizationArtifactType();
         var rootsArray = exportRequest.getRoots();
         var rootList = RootList.create(this.orcsApi, synchronizationArtifactType);

         for (var root : rootsArray) {
            rootList.add(root);
         }

         rootList.validate();

         var synchronizationArtifact = SynchronizationArtifact.create(rootList);
         synchronizationArtifact.build();
         var inputStream = synchronizationArtifact.serialize();

         return inputStream;

      } catch (UnknownSynchronizationArtifactTypeException | BadDocumentRootException e) {

         throw new BadRequestException(this.buildExceptionMessage(e),
            Response.status(Response.Status.BAD_REQUEST).build());

      } catch (Exception e) {

         throw new ServerErrorException(this.buildExceptionMessage(e),
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void importSynchronizationArtifact(ImportRequest importRequest, InputStream inputStream) {

      try (var reqIfInputStream = inputStream;) {

         try {
            orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);
         } catch (OseeAccessDeniedException e) {
            throw new NotAuthorizedException(this.buildExceptionMessage(e),
               Response.status(Response.Status.UNAUTHORIZED).build());
         }

         if (Objects.isNull(importRequest) || !importRequest.isValid()) {
            throw new BadRequestException(this.buildBadInputMessage("importRequest", importRequest),
               Response.status(Response.Status.BAD_REQUEST).build());
         }

         if (Objects.isNull(inputStream)) {
            throw new BadRequestException(this.buildBadInputMessage("inputStream", inputStream),
               Response.status(Response.Status.BAD_REQUEST).build());
         }

         var synchronizationArtifactType = importRequest.getSynchronizationArtifactType();
         var importMappingsArray = importRequest.getImportMappings();
         var rootList = RootList.create(this.orcsApi, synchronizationArtifactType);

         for (var importMapping : importMappingsArray) {
            rootList.add(importMapping.getRoot());
         }

         var synchronizationArtifact = SynchronizationArtifact.create(rootList);

         synchronizationArtifact.deserialize(inputStream);

      } catch (UnknownSynchronizationArtifactTypeException | BadDocumentRootException e) {

         throw new BadRequestException(this.buildExceptionMessage(e),
            Response.status(Response.Status.BAD_REQUEST).build());

      } catch (Exception e) {

         throw new ServerErrorException(this.buildExceptionMessage(e),
            Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
      }
   }
}

/* EOF */