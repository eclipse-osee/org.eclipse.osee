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

package org.eclipse.osee.define.api.publishing;

import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.eclipse.osee.define.api.MsWordPreviewRequestData;
import org.eclipse.osee.define.api.WordTemplateContentData;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author David W. Miller
 */

@Path("word")
@Swagger
public interface PublishingEndpoint {
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("update")
   WordUpdateChange updateWordArtifacts(WordUpdateData data);

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("render")
   Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   @GET
   @Path("msWordTemplatePublish/{branch}/{template}/{artifact}/{view}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML})
   //@formatter:off
   Attachment
      msWordTemplatePublish
         (
            @PathParam("branch")   BranchId branch,
            @PathParam("template") ArtifactId template,
            @PathParam("artifact") ArtifactId headArtifact,
            @PathParam("view")     ArtifactId view
         );
   //@formatter:on

   @GET
   @Path("msWordPreview/{branch}/{template}/{artifact}/{view}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML})
   //@formatter:off
   Attachment
      msWordPreview
         (
            @PathParam("branch")   BranchId   branch,
            @PathParam("template") ArtifactId template,
            @PathParam("artifact") ArtifactId headArtifact,
            @PathParam("view")     ArtifactId view
         );
   //@formatter:on

   @GET
   @Path("msWordPreview/{branch}/{template}/{view}/artifacts")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML})
   //@formatter:off
   Attachment
      msWordPreview
         (
            @PathParam("branch")     BranchId         branch,
            @PathParam("template")   ArtifactId       template,
            @QueryParam("artifacts") List<ArtifactId> artifacts,
            @PathParam("view")       ArtifactId       view
         );
   //@formatter:on

   //@formatter:off
   @POST
   @Path("msWordPreview")
   @Consumes({MediaType.MULTIPART_FORM_DATA})
   @Produces({MediaType.APPLICATION_XML})
   Attachment
      msWordPreview
         (
            @Multipart ( value = "msWordPreviewRequestData", type = MediaType.APPLICATION_JSON ) MsWordPreviewRequestData msWordPreviewRequestData
         );
   //@formatter:on

   /**
    * Gets all artifacts under a shared publishing folder (artifact) of a specific type with a specific attribute value.
    * When the type is specified as {@link ArtifactId#SENTINEL} all artifacts regardless of type with the specified
    * value in the specified attribute will be included.
    *
    * @param branch the branch to search for the shared publishing folder.
    * @param view the branch view to search for the shared publishing folder. This parameter maybe
    * {@link ArtifactId#SENTINEL}.
    * @param sharedFolder the artifact identifier of the shared publishing folder.
    * @param artifactType the type of child artifacts to get. This parameter maybe {@link ArtifactTypeToken#SENTINEL}.
    * @param attributeType the child artifact attribute to be checked.
    * @param attributeValue the required child artifact value.
    * @return a list of the artifacts of the specified type with the specified attribute value.
    * @throws NotAuthorizedException when the user is not a member of the {@link CoreUserGroups#OseeAccessAdmin}.
    */

   @GET
   @Path("sharedPublishingArtifacts/{branch}/{view}/{artifact}")
   @Produces({MediaType.APPLICATION_JSON})
   //@formatter:off
   List<ArtifactToken>
      getSharedPublishingArtifacts
         (
            @PathParam( "branch"          )                       BranchId           branch,
            @PathParam( "view"            )                       ArtifactId         view,
            @PathParam( "artifact"        )                       ArtifactId         sharedFolder,
            @QueryParam( "artifactType"   ) @DefaultValue( "-1" ) ArtifactTypeToken  artifactType,
            @QueryParam( "attributeType"  )                       AttributeTypeToken attributeType,
            @QueryParam( "attriubteValue" )                       String             attributeValue
         );
}
