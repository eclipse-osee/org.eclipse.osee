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

package org.eclipse.osee.synchronization.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;
import org.eclipse.osee.synchronization.util.CharSequenceStreamingOutput;
import org.eclipse.osee.synchronization.util.InputStreamStreamingOutput;

/**
 * Implementation of the {@link SynchronizationEndpoint} interface contains the methods that are invoked when a REST API
 * call has been made for a synchronization artifact.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationEndpointImpl implements SynchronizationEndpoint {

   /**
    * Saves the orcsApi handle.
    */

   private final OrcsApi orcsApi;

   /**
    * Creates an object to process synchronization REST calls.
    *
    * @param orcsApi the {@link OrcsApi} handle.
    */

   public SynchronizationEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Response getSynchronizationArtifact(BranchId branchId, ArtifactId artifactId, String artifactType) {

      try {
         RootList rootList = RootList.create(this.orcsApi, artifactType, branchId, artifactId);
         return this.processRootList(rootList);
      } catch (Exception e) {
         return Response.ok(new CharSequenceStreamingOutput(e.getMessage())).status(
            Response.Status.BAD_REQUEST).build();
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Response getSynchronizationArtifact(String roots, String artifactType) {

      try {
         RootList rootList = RootList.create(this.orcsApi, artifactType, roots);
         return this.processRootList(rootList);
      } catch (Exception e) {
         return Response.ok(new CharSequenceStreamingOutput(e.getMessage())).status(
            Response.Status.BAD_REQUEST).build();
      }

   }

   /**
    * Generates a response for the synchronization artifact.
    *
    * @param rootsList a list of the artifact trees to be included in the synchronization artifact.
    * @return one of the following {@link Response}s:
    * <ul>
    * <li>an HTTP 200 response with the generated synchronization artifact,</li>
    * <li>an HTTP 400 response with an unknown artifact type message, or</li>
    * <li>an HTTP 500 response with an exception report.</li>
    * </ul>
    */

   private Response processRootList(RootList rootList) {

      try {

         SynchronizationArtifact synchronizationArtifact = SynchronizationArtifact.create(rootList);
         synchronizationArtifact.build();
         var inputStream = synchronizationArtifact.serialize();
         return Response.ok(new InputStreamStreamingOutput(inputStream)).build();

      } catch (UnknownSynchronizationArtifactTypeException e) {
         return Response.ok(new CharSequenceStreamingOutput(e.getMessage())).status(
            Response.Status.BAD_REQUEST).build();
      } catch (Exception e) {

         StringBuilder message = new StringBuilder(2 * 1024);

         StringWriter stringWriter = new StringWriter();
         PrintWriter printWriter = new PrintWriter(stringWriter);
         e.printStackTrace(printWriter);

         //@formatter:off
         message
            .append( "<---E-X-C-E-P-T-I-O-N--->" ).append( "\n" )
            .append( e.getMessage() ).append( "\n" )
            .append( stringWriter.toString() ).append( "\n" );
         //@formatter:on

         return Response.ok(new CharSequenceStreamingOutput(message)).status(
            Response.Status.INTERNAL_SERVER_ERROR).build();
      }
   }
}

/* EOF */