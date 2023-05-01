/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.api.publishing.datarights;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.publishing.DataRightResult;

/**
 * The interface defines the REST API end points for obtaining the data rights for a sequence of artifacts.
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

@Path("datarights")
public interface DataRightsEndpoint {

   /**
    * Empties any caches of Data Right footers that maybe held by the Data Rights Manager.
    *
    * @implSpec This entry point requires the user has Publishing Group permissions.
    */

   @DELETE
   void deleteCache();

   /**
    * Creates a map supplier for maps of {@link DataRightAnchor} objects by {@link ArtifactId}. The map will contain one
    * {@link DataRightAnchor} for each artifact that was retrieved from the OSEE database for the artifacts listed on
    * the <code>artifactIdentifiers</code> list. Each {@link DataRightAnchor} contains the following data for it's
    * associated artifact:
    * <ul>
    * <li>A flag to indicated if this artifact is the first in a subsequence of artifacts with the same data rights
    * classification.</li>
    * <li>A flag to indicate if this artifact is the last in a subsequence of artifacts with the same data rights Word
    * ML footer.</li>
    * <li>The artifact's data rights classification.</li>
    * <li>The Word ML for the artifact's data right footer.</li>
    * </ul>
    *
    * @param branchIdentifier the branch to obtain the artifacts from.
    * @param artifactIdentifiers an ordered list of the artifacts by identifier to analyze.
    * @return a {@link DataRightAnchor} map supplier.
    */

   //@formatter:off
   @POST
   @Path("artifacts/branch/{branch}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public DataRightResult
      getDataRights
         (
            @PathParam("branch") BranchId         branchIdentifier,
                                 List<ArtifactId> artifactIdentifiers
         );
   //@formatter:on

   /**
    * Creates a map supplier for maps of {@link DataRightAnchor} objects by {@link ArtifactId}. The map will contain one
    * {@link DataRightAnchor} for each artifact that was retrieved from the OSEE database for the artifacts listed on
    * the <code>artifactIdentifiers</code> list. Each {@link DataRightAnchor} contains the following data for it's
    * associated artifact:
    * <ul>
    * <li>A flag to indicated if this artifact is the first in a subsequence of artifacts with the same data rights
    * classification.</li>
    * <li>A flag to indicate if this artifact is the last in a subsequence of artifacts with the same data rights Word
    * ML footer.</li>
    * <li>The artifact's data rights classification.</li>
    * <li>The Word ML for the artifact's data right footer.</li>
    * </ul>
    *
    * @param branchIdentifier the branch to obtain the artifacts from.
    * @param overrideClassification when specified, this will be used as each artifact's data right classification
    * instead of the classification specified by the artifacts data rights attributes.
    * @param artifactIdentifiers an ordered list of the artifacts by identifier to analyze.
    * @return a {@link DataRightAnchor} map supplier.
    */

   //@formatter:off
   @POST
   @Path("artifacts/branch/{branch}/classification/{classification}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public DataRightResult
      getDataRights
         (
            @PathParam("branch")         BranchId         branchIdentifier,
            @PathParam("classification") String           overrideClassification,
                                         List<ArtifactId> artifactIdentifiers
         );
   //@formatter:on

}

/* EOF */
