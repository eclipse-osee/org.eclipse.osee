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
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * This interface defines the methods for obtaining the data rights for a sequence of artifacts.
 * <p>
 * The {@link #getDataRights} methods all return a {@link DataRightResult} object which is a supplier for a {@link Map}
 * of {@link DataRightAnchor} objects by {@link ArtifactId}. The map will contain one {@link DataRightAnchor} for each
 * artifact that was retrieved from the OSEE database for the artifacts listed on the <code>artifactIdentifiers</code>
 * list. Each {@link DataRightAnchor} contains the following data for it's associated artifact:
 * <ul>
 * <li>A flag to indicated if this artifact is the first in a subsequence of artifacts with the same data rights
 * classification.</li>
 * <li>A flag to indicate if this artifact is the last in a subsequence of artifacts with the same data rights Word ML
 * footer.</li>
 * <li>The artifact's data rights classification.</li>
 * <li>The Word ML for the artifact's data right footer.</li>
 * </ul>
 *
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public interface DataRightsOperations {

   /**
    * Deletes the {@link DataRightClassificationMap}. The map will be reloaded during the next API get operation.
    */

   void deleteCache();

   /**
    * Calling this method is functionally the same as calling:
    *
    * <pre>
    *    dataRightAnchorsResult = {@link #getDataRights(List, BranchId, String) getDataRights}( artifactIdentifiers, branchIdentifier, "" );
    * </pre>
    *
    * @param artifactIdentifiers an ordered list of the artifacts by identifier to analyze.
    * @param branchIdentifier the branch to obtain the artifacts from.
    * @return a {@link DataRightAnchor} map supplier.
    * @see {@link DataRightsOperations}.
    * @implNote This method is for REST API calls and Operations calls.
    */

   DataRightResult getDataRights(List<ArtifactId> artifactIdentifiers, BranchId branchIdentifier);

   /**
    * Gets the data rights, page orientation, and sequence flags for the specified publish artifacts.
    *
    * @param artifactIdentifiers an ordered list of the artifacts by identifier to analyze.
    * @param branchIdentifier the branch to obtain the artifacts from.
    * @param overrideClassification when specified, this will be used as each artifact's data right classification
    * instead of the classification specified by the artifacts data rights attributes.
    * @return a {@link DataRightAnchor} map supplier.
    * @see {@link DataRightsOperations}.
    * @implNote This method is for REST API calls and Operations calls.
    */

   DataRightResult getDataRights(List<ArtifactId> artifactIdentifiers, BranchId branchIdentifier, String overrideClassification);

   /**
    * Gets the data rights, page orientation, and sequence flags for the specified publish artifacts.
    *
    * @param artifactIdentifiers a {@link List} of the {@link ArtifactId}s of the artifacts to be published in
    * publishing order.
    * @param artifactMap a {@link Map} of the loaded {@link ArtifactReadable} objects for the publish.
    * @param overrideClassification when specified, this will be used as each artifact's data right classification
    * instead of the classification specified by the artifacts data rights attributes.
    * @return a {@link DataRightAnchor} map supplier.
    * @see {@link DataRightsOperations}.
    * @implNote This method is for Operations calls only.
    */

   DataRightResult getDataRights(List<ArtifactId> artifactIdentifiers, Map<ArtifactId, ArtifactReadable> artifactMap, String overrideClassification);

}

/* EOF */
