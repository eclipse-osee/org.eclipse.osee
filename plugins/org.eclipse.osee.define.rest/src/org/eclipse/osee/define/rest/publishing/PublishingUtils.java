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

package org.eclipse.osee.define.rest.publishing;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * This class contains various methods for server side publishing (such as finding artifacts).
 * <p>
 * The methods in this class that return an {@link Optional} do not throw exceptions. Methods returning a value not
 * contained within an {@link Optional} may throw an exception.
 *
 * @author Loren K. Ashley
 */

public class PublishingUtils {

   /**
    * Saves a reference to the ORCS Token Service.
    */

   private final OrcsTokenService orcsTokenService;

   /**
    * Saves a reference to the ORCS Query Factory.
    */

   private final QueryFactory queryFactory;

   /**
    * Creates a new {@link PublishingUtils} object associated with the provided {@link OrcsApi}.
    *
    * @param orcsApi a non-<code>null</code> reference to the ORCS API.
    * @throws NullPointerException when the provided {@link OrcsApi} is <code>null</code>; or the {@link QueryFactory}
    * or the {@link OrcsTokenService} obtained from the {@link OrcsApi} is <code>null</code>.
    */

   public PublishingUtils(OrcsApi orcsApi) {
      this.queryFactory = Objects.requireNonNull(orcsApi.getQueryFactory());
      this.orcsTokenService = Objects.requireNonNull(orcsApi.tokenService());
   }

   /**
    * Finds an artifact by the artifact type and artifact name on a branch view. There can only be one artifact with
    * specified type and name for the search to be successful.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param artifactTypeToken the type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the specified branch view, an
    * {@link Optional} containing an {@link ArtifactReadable} representing that artifact; otherwise an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByTypeAndName(BranchId branchId, ArtifactId viewId, ArtifactTypeToken artifactTypeToken, String artifactName) {
      try {
         //@formatter:off
         return
            Optional.ofNullable
               (
                 this.queryFactory
                    .fromBranch(branchId, viewId)
                    .andIsOfType(artifactTypeToken)
                    .andNameEquals(artifactName)
                    .getArtifact()
               );
         //@formatter:on
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Finds an artifact by the artifact type name and artifact name on a branch view. There can only be one artifact
    * with specified type and name for the search to be successful.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param artifactTypeName the name of the artifact type of the artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the specified branch view, an
    * {@link Optional} containing an {@link ArtifactReadable} representing that artifact; otherwise an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByTypeNameAndName(BranchId branchId, ArtifactId viewId, String artifactTypeName, String artifactName) {
      try {
         return this.getArtifactTypeTokenByName(artifactTypeName).flatMap(
            (artifactTypeToken) -> this.getArtifactReadableByTypeAndName(branchId, viewId, artifactTypeToken,
               artifactName));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Gets an {@link ArtifactTypeToken} by the artifact type name.
    *
    * @param artifactTypeName the name of the artifact type to get.
    * @return when an artifact type exists with the specified name, an {@link Optional} with the
    * {@link ArtifactTypeToken} for the artifact type; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactTypeToken> getArtifactTypeTokenByName(String artifactTypeName) {
      try {
         return Optional.of(this.orcsTokenService.getArtifactType(artifactTypeName));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Finds an artifact by type and name that is an immediate child of a specified artifact on a branch view.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeToken the type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the branch view as an immediate child of the
    * parent, an {@link Optional} containing the {@link ArtifactReadable} for the found artifact; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getChildArtifactReadableByTypeAndName(BranchId branchId, ArtifactId viewId, ArtifactId parent, ArtifactTypeToken artifactTypeToken, String artifactName) {
      try {
         //@formatter:off
         return
            Optional.ofNullable
               (
                  this.queryFactory
                     .fromBranch(branchId, viewId)
                     .andIsOfType(artifactTypeToken)
                     .andNameEquals(artifactName)
                     .andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent, parent)
                     .getArtifact()
               );
         //@formatter:on
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Finds an artifact by type name and name that is an immediate child of a specified artifact on a branch view.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeName the name of the artifact type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the branch view as an immediate child of the
    * parent, an {@link Optional} containing the {@link ArtifactReadable} for the found artifact; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getChildArtifactReadableByTypeNameAndName(BranchId branchId, ArtifactId viewId, ArtifactId parent, String artifactTypeName, String artifactName) {
      try {
         return this.getArtifactTypeTokenByName(artifactTypeName).flatMap(
            (artifactTypeToken) -> this.getChildArtifactReadableByTypeAndName(branchId, viewId, parent,
               artifactTypeToken, artifactName));
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Finds the artifacts with an attribute that contains a specific value that are children of a specified artifact on
    * a branch view. This method may throw an exception when the search fails. A successful search with no results will
    * return an empty list.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param parent the identifier of the parent artifact.
    * @param attributeTypeId the attribute type identifier of the attribute to be checked for a matching value.
    * @param value the attribute value to check for.
    * @return a list, possibly empty, of the artifacts matching the search criteria.
    */

   public List<ArtifactReadable> getRecursiveChildenArtifactReadablesByAttributeTypeAndAttributeValue(BranchId branchId, ArtifactId viewId, ArtifactReadable parent, AttributeTypeId attributeTypeId, String value) {
      //@formatter:off
      return
         this.queryFactory
            .fromBranch(branchId, viewId)
            .andAttributeIs(attributeTypeId, value)
            .andRelatedRecursive(DefaultHierarchical_Child,parent)
            .getResults()
            .getList();
      //@formatter:on
   }

}

/* EOF */
