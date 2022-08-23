/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.define.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.define.api.DefineApi;
import org.eclipse.osee.define.api.RenderEndpoint;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.exception.OseeAccessDeniedException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public final class RenderEndpointImpl implements RenderEndpoint {

   private final DefineApi defineApi;
   private final OrcsApi orcsApi;

   public RenderEndpointImpl(DefineApi defineApi, OrcsApi orcsApi) {
      this.defineApi = defineApi;
      this.orcsApi = orcsApi;
   }

   /**
    * Verifies the threads current user is authorized to access the REST API.
    *
    * @throws NotAuthorizedException when the user is not a member of the {@link CoreUserGroups#OseeAccessAdmin}.
    */

   private void verifyAccess() {
      try {
         this.orcsApi.userService().requireRole(CoreUserGroups.OseeAccessAdmin);
      } catch (OseeAccessDeniedException e) {
         throw new NotAuthorizedException("User must have OSEE Admin role.",
            Response.status(Response.Status.UNAUTHORIZED).build());
      }
   }

   @Override
   public WordUpdateChange updateWordArtifacts(WordUpdateData data) {
      return defineApi.getMSWordOperations().updateWordArtifacts(data);
   }

   @Override
   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data) {
      return defineApi.getMSWordOperations().renderWordTemplateContent(data);
   }

   @Override
   public Response msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view) {
      return defineApi.getMSWordOperations().msWordTemplatePublish(branch, template, headArtifact, view);
   }

   @Override
   public Response msWordPreview(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view) {
      return defineApi.getMSWordOperations().msWordPreview(branch, template, Arrays.asList(headArtifact), view);
   }

   @Override
   public Response msWordPreview(BranchId branch, ArtifactId template, List<ArtifactId> artifacts, ArtifactId view) {
      return defineApi.getMSWordOperations().msWordPreview(branch, template, artifacts, view);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NotAuthorizedException {@inheritDoc}
    */

   @Override
   public List<ArtifactToken> getSharedPublishingArtifacts(BranchId branch, ArtifactId view, ArtifactId sharedFolder, ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String attributeValue) {
      this.verifyAccess();

      StringBuilder message = null;

      if (Objects.isNull(branch) || (branch.getId() < 0)) {
         message = Objects.isNull(message) ? new StringBuilder(1024) : message;
         //@formatter:off
         message
            .append( "Path parameter \"branch\" cannont be null or less than zero." ).append( "\n" )
            .append( "   " ).append( "branch: " ).append( branch ).append( "\n" )
            .append( "\n" );
         //@formatter:on
      }

      if (Objects.isNull(view) || view.getId() < -1) {
         message = Objects.isNull(message) ? new StringBuilder(1024) : message;
         //@formatter:off
         message
            .append( "Path parameter \"view\" cannont be null or less than minus 1." ).append( "\n" )
            .append( "   " ).append( "view: " ).append( view ).append( "\n" )
            .append( "\n" );
         //@formatter:on
      }

      if (Objects.isNull(sharedFolder) || sharedFolder.getId() < 0) {
         message = Objects.isNull(message) ? new StringBuilder(1024) : message;
         //@formatter:off
         message
            .append( "Path parameter \"sharedFolder\" cannont be null or less than zero." ).append( "\n" )
            .append( "   " ).append( "sharedFolder: " ).append( sharedFolder ).append( "\n" )
            .append( "\n" );
         //@formatter:on
      }

      if (Objects.isNull(artifactType) || artifactType.getId() < -1) {
         message = Objects.isNull(message) ? new StringBuilder(1024) : message;
         //@formatter:off
         message
            .append( "Path parameter \"artifactType\" cannont be null or less than minus 1." ).append( "\n" )
            .append( "   " ).append( "artifactType: " ).append( artifactType ).append( "\n" )
            .append( "\n" );
         //@formatter:on
      }

      if (Objects.isNull(attributeType) || attributeType.getId() < -1) {
         message = Objects.isNull(message) ? new StringBuilder(1024) : message;
         //@formatter:off
         message
            .append( "Path parameter \"attributeType\" cannont be null or less than minus 1." ).append( "\n" )
            .append( "   " ).append( "attributeType: " ).append( attributeType ).append( "\n" )
            .append( "\n" );
         //@formatter:on
      }

      if (Objects.isNull(attributeValue) || attributeValue.isEmpty()) {
         message = Objects.isNull(message) ? new StringBuilder(1024) : message;
         //@formatter:off
         message
            .append( "Path parameter \"attributeValue\" cannont be null or empty." ).append( "\n" )
            .append( "   " ).append( "attributeValue: " ).append( "\"" ).append( attributeType ).append( "\"" ).append( "\n" )
            .append( "\n" );
         //@formatter:on
      }

      if (Objects.nonNull(message)) {
         throw new BadRequestException(message.toString(), Response.status(Response.Status.BAD_REQUEST).build());
      }

      return defineApi.getMSWordOperations().getSharedPublishingArtifacts(branch, view, sharedFolder, artifactType,
         attributeType, attributeValue);
   }
}
