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

package org.eclipse.osee.define.operations.api.publisher.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactSpecification;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.jdk.core.type.Result;

/**
 * This class contains various methods for server side publishing (such as finding artifacts).
 * <p>
 * The methods in this class return an empty {@link Optional} when the search did not find a result. When a method
 * returns an empty {@link Optional} the method {@link getLastCause} can be used to determine if an error occurred or no
 * result was found. In the case of an error the method {@link getLastError} can be used to obtain the {@link Exception}
 * that caused the error.
 *
 * @author Loren K. Ashley
 */

public interface DataAccessOperations {

   /**
    * Enumeration used to categorize the results of an operation.
    */

   public static enum Cause {

      /**
       * An error occurred, use the method {@link DataAccessOperations#getLastError} to get the exception.
       */

      ERROR,

      /**
       * The operation resulted in more that one result when only one result was expected. The
       * {@link DataAccessOperations#getLastError} may or may not contain further information.
       */

      MORE_THAN_ONE,

      /**
       * The operation did not find a result when one was found. The {@link DataAccessOperations#getLastError} may or
       * may not contain further information.
       */

      NOT_FOUND,

      /**
       * The operation completed successfully. The {@link DataAccessOperations#getLastError} will not provide any
       * further information.
       */

      OK;
   }

   public Result<List<ArtifactReadable>, DataAccessException> getArtifactReadables(BranchSpecification branchSpecification, Collection<ArtifactId> artifactIdentifiers, Collection<String> guids, String artifactName, ArtifactTypeToken artifactTypeToken, TransactionId transactionId, IncludeDeleted includeDeleted);

   /**
    * Finds an artifact by its GUID.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param guid the GUID of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact GUID; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByGuid(BranchSpecification branchSpecification, String guid);

   /**
    * Finds an artifact by its artifact identifier.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByIdentifier(ArtifactSpecification artifactSpecification);

   /**
    * Finds an artifact by its artifact name.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param name the name of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByName(BranchSpecification branchSpecification, String artifactName);

   /**
    * Finds an artifact by the artifact type and artifact name on a branch view. There can only be one artifact with
    * specified type and name for the search to be successful.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactTypeToken the type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the specified branch view, an
    * {@link Optional} containing an {@link ArtifactReadable} representing that artifact; otherwise an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByTypeAndName(BranchSpecification branchSpecification, ArtifactTypeToken artifactTypeToken, String artifactName);

   /**
    * Finds an artifact by the artifact type name and artifact name on a branch view. There can only be one artifact
    * with specified type and name for the search to be successful.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactTypeName the name of the artifact type of the artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the specified branch view, an
    * {@link Optional} containing an {@link ArtifactReadable} representing that artifact; otherwise an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByTypeNameAndName(BranchSpecification branchSpecification, String artifactTypeName, String artifactName);

   /**
    * Finds an artifact possibly deleted on a branch by artifact identifier and transaction identifier with delete
    * attributes.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @param transactionId the transaction identifier to get the artifact from.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadablePossiblyDeletedByIdentifierAndTransactionIdWithDeleteAttributes(ArtifactSpecification artifactSpecification, TransactionId transactionId);

   /**
    * Finds an artifact possibly deleted on a branch by artifact identifier with deleted attributes.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadablePossiblyDeletedByIdentifierWithDeletedAttributes(ArtifactSpecification artifactSpecification);

   /**
    * Gets all artifacts on a branch with the specified artifact identifiers.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactIds the {@link ArtifactId} identifiers of the artifacts to get.
    * @return when artifacts with the specified identifiers are present for the branch and view, an {@link Optional}
    * containing a {@link List} of the {@link ArtifactReadable}s representing the artifacts found; otherwise an empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getArtifactReadablesByIdentifiers(BranchSpecification branchSpecification, Collection<ArtifactId> artifactIds);

   /**
    * Gets all artifacts on a branch with the artifact type.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactTypeToken the type of artifacts to be found.
    * @return when artifacts with the given type and specified branch are present, an {@link Optional} containing a
    * {@link List} of the {@link ArtifactReadable}s representing the artifacts found; otherwise an empty
    * {@link Optional}.
    */

   public Result<List<ArtifactReadable>, DataAccessException> getArtifactReadablesByType(BranchSpecification branchSpecification, ArtifactTypeToken artifactTypeToken);

   /**
    * Gets all artifacts on a branch with a transaction comment that indicates the artifact has been changed.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @return when {@link ArtifactReadable}s with a transaction comment that indicate the artifact has been changed are
    * present, an {@link Optional} containing the changed artifacts from the branch; otherwise, a empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getArtifactReadablesFilterByTxCommentForChange(BranchSpecification branchSpecification);

   /**
    * Finds an artifact token by its artifact identifier.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactToken> getArtifactTokenByIdentifier(ArtifactSpecification artifactSpecification);

   /**
    * Gets an {@link ArtifactTypeToken} by the artifact type name.
    *
    * @param artifactTypeName the name of the artifact type to get.
    * @return when an artifact type exists with the specified name, an {@link Optional} with the
    * {@link ArtifactTypeToken} for the artifact type; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactTypeToken> getArtifactTypeTokenByName(String artifactTypeName);

   /**
    * Gets the {@link Branch} by {@link BranchId}.
    *
    * @param branchId the branch to load.
    * @return when a branch with the specified {@link BranchId} exists, an {@link Optional} containing the
    * {@link Branch}; otherwise, an empty {@link Optional}.
    */

   public Optional<Branch> getBranchByIdentifier(BranchId branchId);

   /**
    * Finds an artifact by type and name that is an immediate child of a specified artifact on a branch view.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeToken the type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the branch view as an immediate child of the
    * parent, an {@link Optional} containing the {@link ArtifactReadable} for the found artifact; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getChildArtifactReadableByTypeAndName(BranchSpecification branchSpecification, ArtifactId parent, ArtifactTypeToken artifactTypeToken, String artifactName);

   /**
    * Finds an artifact by type name and name that is an immediate child of a specified artifact on a branch view.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeName the name of the artifact type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the branch view as an immediate child of the
    * parent, an {@link Optional} containing the {@link ArtifactReadable} for the found artifact; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getChildArtifactReadableByTypeNameAndName(BranchSpecification branchSpecification, ArtifactId parent, String artifactTypeName, String artifactName);

   /**
    * Gets the status ({@link Cause}) of the last completed operation.
    *
    * @return the status of the last operation categorized as a {@link Cause}.
    */

   public Cause getLastCause();

   /**
    * If the last operation threw an {@link Exception}, gets the {@link Exception}. This method may return an
    * {@link Exception} even when the {@link Cause} is other than {@link Cause#ERROR}.
    *
    * @return when the last operation threw an {@link Exception}, an {@link Optional} containing the {@link Exception};
    * otherwise, an empty {@link Optional}.
    */

   public Optional<Exception> getLastError();

   /**
    * Finds the artifacts with an attribute that contains a specific value that are children of a specified artifact on
    * a branch view. This method may throw an exception when the search fails. A successful search with no results will
    * return an empty list.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param attributeTypeId the attribute type identifier of the attribute to be checked for a matching value.
    * @param value the attribute value to check for.
    * @return when the search was successful, an {@link Optional} containing a possibly empty list of the artifacts
    * matching the search criteria; otherwise, an empty {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getRecursiveChildenArtifactReadablesByAttributeTypeAndAttributeValue(BranchSpecification branchSpecification, ArtifactId parent, AttributeTypeId attributeTypeId, String value);

   /**
    * Finds the artifacts of the specified type with an attribute that contains a specific value that are children of a
    * specified artifact on a branch view. This method may throw an exception when the search fails. A successful search
    * with no results will return an empty list.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeToken the type of children artifact to get.
    * @param attributeTypeId the attribute type identifier of the attribute to be checked for a matching value.
    * @param value the attribute value to check for.
    * @return when the search was successful, an {@link Optional} containing a possibly empty list of the artifacts
    * matching the search criteria; otherwise, an empty {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getRecursiveChildenArtifactReadablesOfTypeByAttributeTypeAndAttributeValue(BranchSpecification branchSpecification, ArtifactId parent, ArtifactTypeToken artifactTypeToken, AttributeTypeId attributeTypeId, String value);

   /**
    * Gets the {@link ArtifactId}s of the ATS Workflow artifacts related to the artifact specified by
    * <code>artifactId</code>.
    *
    * @param artifactId the artifact to get related ATS Workflow artifacts for.
    * @return when there are ATS Workflow artifacts related to the artifact specified by <code>artifactId</code>, an
    * {@link Optional} containing the {@link ArtifactId}s of the related ATS Workflow artifacts; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactId>> getWorkFlowArtifactIdentifiers(ArtifactId artifactId);

}

/* EOF */
