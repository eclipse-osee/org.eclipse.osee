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

package org.eclipse.osee.define.rest.api.publisher.publishing;

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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * Interface defining the REST API endpoints for publishing.
 *
 * @author David W. Miller
 * @author Loren K. Ashley
 */

@Path("word")
@Swagger
public interface PublishingEndpoint {

   /**
    * Gets the Word ML content for an artifact and replaces the OSEE_LINK markers and legacy hyper-links with WordML
    * hyper links of the type specified by <code>linkType</code>.
    *
    * @param branchId the branch to get the artifact from.
    * @param viewId when valid if the artifact is not applicable to the view its content will not be obtained.
    * @param artifactId the identifier of the artifact to get the Word ML content from.
    * @param transactionId when specified the version of the artifact for the specified transaction will be used.
    * @param linkType the {@link LinkType} of the updated links to be put into the returned Word ML.
    * @param presentationType when the link destination artifact is historical and the <code>linkType</code> is
    * {@link LinkType#OSEE_SERVER_LINK}, the generated replacement link will contain the transaction identifier for the
    * historical destination artifact only when the <code>presentationType</code> is not {@link PresentationType#DIFF}
    * or {@link PresentationType#F5_DIFF}.
    * @return a {@link LinkHandlerResult} with:
    * <ul>
    * <li>The modified artifact content.</li>
    * <li>A set of the {@link ArtifactId}s of the linked artifacts that were found.</li>
    * <li>A set of {@link String}s of the ambiguous artifact identifier strings of the linked artifacts that were not
    * found.</li>
    * </ul>
    */

   @GET
   @Path("link/{branch}/{view}/{artifact}/{transaction}/{linkType}/{presentationType}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   //@formatter:off
   LinkHandlerResult
      link
         (
            @PathParam( "branch"           ) BranchId         branchId,
            @PathParam( "view"             ) ArtifactId       viewId,
            @PathParam( "artifact"         ) ArtifactId       artifactId,
            @PathParam( "transaction"      ) TransactionId    transactionId,
            @PathParam( "linkType"         ) LinkType         linkType,
            @PathParam( "presentationType" ) PresentationType presentationType
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

   @GET
   @Path("msWordPreview/{branch}/{template}/{artifact}/{view}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML})
   @Deprecated
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
   @Deprecated
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
            @Multipart ( value = "msWordPreviewRequestData", type = MediaType.APPLICATION_JSON ) PublishingRequestData msWordPreviewRequestData
         );
   //@formatter:on

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

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("render")
   Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   /**
    * Publishes the content of an artifact's {@link CoreAttributeTypes.WholeWordContent} attribute.
    *
    * @param branchId the branch to get the artifact from.
    * @param viewId when valid if the artifact is not applicable to the view its content will not be obtained.
    * @param artifactId the identifier of the artifact to get the Word ML content from.
    * @param transactionId when specified the version of the artifact for the specified transaction will be used.
    * @param linkType the {@link LinkType} of the updated links to be put into the returned Word ML.
    * @param presentationType the type of presentation the artifact is being rendered for.
    * @param includeErrorLog when <code>true</code> the publishing error log will be appended to the rendered document.
    * @return an {@link Attachment} containing a stream with the published document.
    */

   //@formatter:off
   @GET
   @Path( "msWordWholeWordContentPublish/{branch}/{view}/{artifact}/{transaction}/{linkType}/{presentationType}" )
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML})
   Attachment
      msWordWholeWordContentPublish
         (
                                    @PathParam( "branch"           ) BranchId         branchId,
                                    @PathParam( "view"             ) ArtifactId       viewId,
                                    @PathParam( "artifact"         ) ArtifactId       artifactId,
                                    @PathParam( "transaction"      ) TransactionId    transactionId,
                                    @PathParam( "linkType"         ) LinkType         linkType,
                                    @PathParam( "presentationType" ) PresentationType presentationType,
            @DefaultValue( "true" ) @QueryParam( "includeErrorLog" ) boolean          includeErrorLog
         );
   //@formatter:on

   /**
    * Gets the artifact's WordML with the hyperlinks replaced with OSEE_LINK markers.
    *
    * @param branchId the branch to get the artifact from.
    * @param viewId when valid if the artifact is not applicable to the view its content will not be obtained.
    * @param artifactId the identifier of the artifact to get the Word ML content from.
    * @param transactionId when specified the version of the artifact for the specified transaction will be used.
    * @param linkType the {@link LinkType} of the updated links to be put into the returned Word ML.
    * @return a {@link LinkHandlerResult} with:
    * <ul>
    * <li>The modified artifact content.</li>
    * <li>A set of the {@link ArtifactId}s of the linked artifacts that were found.</li>
    * <li>A set of {@link String}s of the ambiguous artifact identifier strings of the linked artifacts that were not
    * found.</li>
    * </ul>
    */

   @GET
   @Path("unlink/{branch}/{view}/{artifact}/{transaction}/{linkType}")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   //@formatter:off
   LinkHandlerResult
      unlink
         (
            @PathParam( "branch"      ) BranchId         branchId,
            @PathParam( "view"        ) ArtifactId       viewId,
            @PathParam( "artifact"    ) ArtifactId       artifactId,
            @PathParam( "transaction" ) TransactionId    transactionId,
            @PathParam( "linkType"    ) LinkType         linkType
         );
   //@formatter:on

   /**
    * For the specified branch, updates the {@link BranchId} in links of all artifacts with a
    * {@link CoreAttributeTypes.WholeWordContent}.
    *
    * @param branchId the {@link BranchId} of the branch to update artifacts on.
    * @return a message describing the results of the operation.
    * @implNote The endpoint is provided for the {@link FixEmbeddedLinksBlam}.
    */

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("updateLinks/{branch}")
   //@formatter:off
   String
      updateLinks
         (
            @PathParam("branch") BranchId branchId
         );
   //@formatter:on

   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   @Path("update")
   WordUpdateChange updateWordArtifacts(WordUpdateData data);
}

/* EOF */
