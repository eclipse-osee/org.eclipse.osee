/*
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

package org.eclipse.osee.synchronization.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * This interface defines the REST API end points for generating synchronization artifacts. These methods allow the
 * selection of one or more OSEE artifact trees to be included in the synchronization artifact. Artifact trees are
 * identified with the branch identifier and artifact identifier of the root item of the tree. For each artifact tree
 * the root item and all of its children items will be included in the synchronization artifact.<br>
 * <br>
 * A collection of artifact trees maybe specified in a string form as defined by the following EBNF:<br>
 * <br>
 * <ul style="list-style-type:none">
 * <li>roots ::= &lt;branch-id-artifact-id-list&gt; { "," &lt;branch-id-artifact-id-list&gt; }</li>
 * <li>branch-id-artifact-id-list ::= &lt;branch-id&gt; ":" &lt;artifact-id-list&gt;</li>
 * <li>artifact-id-list ::= &lt;artifact-id&gt; { "," &lt;artifact-id&gt; }</li>
 * <li>branch-id ::= &lt;integer&gt;</li>
 * <li>artifact-id ::= &lt;integer&gt;</li>
 * <li>integer ::= &lt;digit&gt; { &lt;digit&gt; }</li>
 * <li>digit ::= "0" "1" "2" "3" "4" "5" "6" "7" "8" "9"</li>
 * </ul>
 * <br>
 * For example the string "10:123,124;22:450,342" specifies the artifact trees with the following branch identifier and
 * artifact identifier pairs:
 * <ul>
 * <li>(10, 123)</li>
 * <li>(10, 124)</li>
 * <li>(22, 450)</li>
 * <li>(22, 342)</li>
 * </ul>
 *
 * @author Loren K. Ashley
 */

@Path("/")
public interface SynchronizationEndpoint {

   /**
    * Get a synchronization artifact of the specified type for the artifact tree specified with the
    * <code>branchId</code> and <code>artifactId</code>.
    *
    * @param branchId the identifier of the root artifact.
    * @param artifactId identifier of the root artifact.
    * @param synchronizationArtifactType the type of synchronization artifact to be produced.
    * @return the synchronization artifact.
    */

   @GET
   @Path("getSynchronizationArtifact/branch/{branch}/artifact/{artifact}")
   @Produces({MediaType.APPLICATION_OCTET_STREAM})
   Response getSynchronizationArtifact(@PathParam("branch") BranchId branchId, @PathParam("artifact") ArtifactId artifactId, @QueryParam("synchronizationArtifactType") String synchronizationArtifactType);

   /**
    * Get a synchronization artifact of the specified type for the artifact trees specified by the <code>roots</code>
    * parameter.
    *
    * @param roots the artifact trees to be included in the synchronization artifact.
    * @param synchronizationArtifactType the type of synchronization artifact to be produced.
    * @return the synchronization artifact.
    */

   @GET
   @Path("getSynchronizationArtifact")
   @Produces({MediaType.APPLICATION_OCTET_STREAM})
   Response getSynchronizationArtifact(@QueryParam("roots") String roots, @QueryParam("synchronizationArtifactType") String synchronizationArtifactType);

}

/* EOF */