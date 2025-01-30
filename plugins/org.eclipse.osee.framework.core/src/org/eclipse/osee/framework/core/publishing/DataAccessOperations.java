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

package org.eclipse.osee.framework.core.publishing;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactSpecification;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.Result;

/**
 * This interface contains various methods for server side publishing (such as finding artifacts).
 * <p>
 * The methods in this interface return a {@link Result} with the search result, return a empty {@link Result} when the
 * did not find a result, and a {@link Result} with a {@link DataAccessException} when an error occurred.
 *
 * @author Loren K. Ashley
 * @implNote This interface is intended to only be used by the Define bundle and specialized publishing bundles that
 * inherit from publishing classes in the Define bundle.
 */

public interface DataAccessOperations {

   /**
    * Gets the applicability name view map for the branch.
    */

   Result<Map<String, List<String>>, DataAccessException> getApplicabilityNamedViewMap(
      BranchSpecification branchSpecification);

   /**
    * Gets the applicability tokens for a branch.
    *
    * @param branchSpecification the branch to get the applicability tokens from.
    * @return on success a {@link Result} with a {@link Map} of the branch's {@link ApplicabilityToken}s by
    * {@link ApplicabilityId}; otherwise a {@link Result} with a {@link DataAccessException}/
    */

   Result<Map<ApplicabilityId, ApplicabilityToken>, DataAccessException> getApplicabilityTokenMap(
      BranchSpecification branchSpecification);

   /**
    * Gets the identifiers of the specified artifacts from a branch.
    *
    * @param branchSpecification the branch and optional view to get the artifact identifiers from.
    * @param attributeTypeId when non-<code>null</code> and not {@link AttributeTypeId#SENTINEL}, only the identifiers
    * of artifacts with an attribute of the specified attribute type will be included in the result.
    * @param attributeValue when non-<code>null</code> and a valid <code>attributeTypeId</code> is also specified, only
    * the identifiers of artifacts with an attribute of the specified attribute type which contains the specified
    * attribute value will be included.
    * @param transactionId when non-<code>null</code> and not {@link TransactionId#SENTINEL}, only the identifiers from
    * the artifacts in the specified transaction will be included.
    * @param includeDeleted when {@link IncludeDeleted#YES} deleted artifacts and deleted attributes will be included.
    * @return on success a {@link Result} with a list of the {@link ArtifactId}s of the matching artifacts; otherwise, a
    * {@link Result} with a {@link DataAccessException}.
    */

   Result<List<ArtifactId>, DataAccessException> getArtifactIdentifiers(BranchSpecification branchSpecification,
      AttributeTypeId attributeTypeId, String attributeValue, TransactionId transactionId,
      IncludeDeleted includeDeleted);

   /**
    * Finds an artifact by identifier.
    *
    * @param artifactSpecification the branch identifier, optional view identifier, and artifact identifier of the
    * artifact to load.
    * @return when the artifact is found, a {@link Result} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, a {@link Result} containing a {@link DataAccessException}.
    */

   Result<ArtifactReadable, DataAccessException> getArtifactReadableByIdentifier(
      ArtifactSpecification artifactSpecification);

   /**
    * Loads artifacts from a branch.
    *
    * @param branchSpecification the branch and optional view to load the artifacts from.
    * @param artifactIdentifiers only include artifacts with the identifiers in this collection.
    * @param guids only include artifacts with the GUIDs in this collection.
    * @param artifactName only include artifacts with this name.
    * @param artifactTypeToken only include artifacts of this type.
    * @param transactionId when not {@link TransactionId#SENTINEL} load artifacts with the specified
    * {@link TransactionId}.
    * @param includeDeleted when {@link IncludeDeleted#YES} deleted artifacts will also be loaded.
    * @return on success a {@link Result} with an unordered list of the loaded artifacts; otherwise, a {@link Result}
    * with a {@link DataAccessException}.
    */

   //@formatter:off
   Result<List<ArtifactReadable>, DataAccessException>
      getArtifactReadables
         (
            BranchSpecification    branchSpecification,
            Collection<ArtifactId> artifactIdentifiers,
            Collection<String>     guids,
            String                 artifactName,
            ArtifactTypeToken      artifactTypeToken,
            TransactionId          transactionId,
            IncludeDeleted         includeDeleted
         );
   //@formatter:on

   /**
    * Finds the artifacts that are parents of the child artifact specified by <code>childArtifactId</code>.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param childArtifactId the identifier of the child artifact.
    * @return on success a {@link Result} with a possibly empty {@link List} of the {@link ArtifactReadable} objects
    * that are the parents of the child artifact; otherwise, a {@link Result} with a {@link DataAccessException}.
    */

   Result<List<ArtifactReadable>, DataAccessException> getArtifactReadablesListOfParents(
      BranchSpecification branchSpecification, ArtifactId childArtifactId);

   /**
    * Gets all artifact tokens from a branch with filters for artifact identifier, artifact type and relationships.
    *
    * @param branchSpecification the branch and optionally view to get {@link ArtifactToken}s from.
    * @param artifactIdentifiers when not <code>null</code> or empty, only the {@link ArtifactToken}s for artifacts with
    * identifiers in the <code>artifactIdentifiers</code> {@link Collection} will be loaded.
    * @param artifactTypeToken when not {@link ArtifactTypeToken#SENTINEL}, only the {@link ArtifactToken}s for
    * artifacts of the specified <code>artifactTypeToken</code> will be returned.
    * @param relationTypeSide when not {@link RelationTypeSide#SENTINEL}, only the {@link ArtifactToken}s for artifacts
    * with a relationship of the specified <code>relationTypeSide</code> will be returned.
    * @return when {@link ArtifactToken}s exist that match the selection criteria a {@link Result} with the matching
    * {@link ArtifactToken}s, when not {@link ArtifactToken}s match the selection criteria a {@link Result} with an
    * empty {@link List}; otherwise, a {@link Result} with a {@link DataAccessException}.
    */

   Result<List<ArtifactToken>, DataAccessException> getArtifactTokens(BranchSpecification branchSpecification,
      Collection<ArtifactId> artifactIdentifiers, ArtifactTypeToken artifactTypeToken,
      RelationTypeSide relationTypeSide);

   /**
    * Gets a {@link Branch} by identifier.
    *
    * @param branchId the identifier of the branch to be loaded.
    * @return when found a {@link Result} with the {@link Branch}; otherwise, a {@link Result} with a
    * {@link DataAccessException}.
    */

   Result<Branch, DataAccessException> getBranchByIdentifier(BranchId branchId);

   /**
    * Gets the list of valid branch views for an applicability.
    *
    * @param branchSpecification the branch to test.
    * @param applicabilityId the applicability to get the valid view for.
    * @return on success a {@link Result} with a {@link List} of the {@link ArtifactId}s for the branch views that are
    * valid for the <code>applicabilityId</code>; otherwise, a {@link Result} with a {@link DataAccessException}.
    */

   Result<List<ArtifactId>, DataAccessException> getBranchViewsForApplicability(BranchSpecification branchSpecification,
      ApplicabilityId applicabilityId);

   /**
    * Get the identifiers of the hierarchical children of the artifact specified by <code>branchSpecification</code> and
    * <code>artifactId</code>.
    *
    * @param branchSpecification the branch and optional view to find the artifacts on.
    * @param artifactId the identifier of the artifact to get the children identifiers of.
    * @param processRecursively when {@link ProcessRecursively#YES} the identifiers of the children artifacts all the
    * way down the hierarchy will be found; otherwise, only the immediate children artifact identifier will be found.
    * @return an unordered {@link List} of the hierarchical children identifiers.
    */

   Result<List<ArtifactId>, DataAccessException> getChildrenArtifactIdentifiers(BranchSpecification branchSpecification,
      ArtifactId parent, ArtifactTypeToken artifactTypeToken, AttributeTypeId attributeTypeId, String value,
      ProcessRecursively processRecursively);

   /**
    * Get the identifiers of the hierarchical parents of the artifact specified by <code>branchSpecification</code> and
    * <code>artifactId</code>.
    *
    * @param branchSpecification the branch and optional view to find the artifacts on.
    * @param artifactId the identifier of the artifact to get the parent identifiers of.
    * @param processRecursively when {@link ProcessRecursively#YES} the identifiers of the parent artifacts all the way
    * up the hierarchy will be found; otherwise, only the immediate parent artifact identifier will be found.
    * @return an unordered {@link List} of the hierarchical parent identifiers.
    */

   Result<List<ArtifactId>, DataAccessException> getParentArtifactIdentifiers(BranchSpecification branchSpecification,
      ArtifactId artifactId, ProcessRecursively processRecursively);

   /**
    * Gets the {@link ArtifactId}s of the ATS Workflow artifacts related to the artifact specified by
    * <code>artifactId</code>.
    *
    * @param artifactId the artifact to get related ATS Workflow artifacts for.
    * @return when there are ATS Workflow artifacts related to the artifact specified by <code>artifactId</code>, a
    * {@link Result} containing the {@link ArtifactId}s of the related ATS Workflow artifacts; otherwise, a
    * {@link Result} with a {@link DataAccessException}.
    */

   Result<List<ArtifactId>, DataAccessException> getWorkFlowArtifactIdentifiers(ArtifactId artifactId);

}

/* EOF */
