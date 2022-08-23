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

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

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

public class PublishingUtils {

   /**
    * Enumeration used to categorize the results of an operation.
    */

   public static enum Cause {

      /**
       * An error occurred, use the method {@link PublishingUtils#getLastError} to get the exception.
       */

      ERROR,

      /**
       * The operation resulted in more that one result when only one result was expected. The
       * {@link PublishingUtils#getLastError} may or may not contain further information.
       */

      MORE_THAN_ONE,

      /**
       * The operation did not find a result when one was found. The {@link PublishingUtils#getLastError} may or may not
       * contain further information.
       */

      NOT_FOUND,

      /**
       * The operation completed successfully. The {@link PublishingUtils#getLastError} will not provide any further
       * information.
       */

      OK;
   }

   /**
    * If the prior operation threw an exception, it will be categorized and saved in the member. This member's initial
    * value is {@link Cause#OK} and is set at the start of each operation.
    */

   private final ThreadLocal<Cause> lastCause;

   /**
    * If the prior operation threw an exception, this member saves the exception for later retrieval. This member's
    * value is cleared at the start of each operation.
    */

   private final ThreadLocal<Exception> lastError;

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
      this.lastCause = new ThreadLocal<>() {
         @Override
         protected Cause initialValue() {
            return Cause.OK;
         }
      };
      this.lastError = new ThreadLocal<>();
      this.queryFactory = Objects.requireNonNull(orcsApi.getQueryFactory());
      this.orcsTokenService = Objects.requireNonNull(orcsApi.tokenService());
   }

   /**
    * Finds an artifact by its artifact identifier.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param artifactId the identifier of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByIdentifier(BranchId branchId, ArtifactId viewId, ArtifactId artifactId) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this.queryFactory
                    .fromBranch( branchId, viewId )
                    .andId( artifactId )
                    .getArtifact()
               );
         //@formatter:on
      } catch (MultipleItemsExist e) {
         this.lastCause.set(Cause.MORE_THAN_ONE);
         this.lastError.set(e);
      } catch (ItemDoesNotExist e) {
         this.lastCause.set(Cause.NOT_FOUND);
         this.lastError.set(e);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
      }

      return Optional.empty();
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
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this.queryFactory
                    .fromBranch(branchId, viewId)
                    .andIsOfType(artifactTypeToken)
                    .andNameEquals(artifactName)
                    .getArtifact()
               );
         //@formatter:on
      } catch (MultipleItemsExist e) {
         this.lastCause.set(Cause.MORE_THAN_ONE);
         this.lastError.set(e);
      } catch (ItemDoesNotExist e) {
         this.lastCause.set(Cause.NOT_FOUND);
         this.lastError.set(e);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
      }

      return Optional.empty();
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
      //@formatter:off
      return
         this.getArtifactTypeTokenByName(artifactTypeName)
            .flatMap
               (
                 (artifactTypeToken) -> this.getArtifactReadableByTypeAndName(branchId, viewId, artifactTypeToken, artifactName)
               );
      //@formatter:on
   }

   /**
    * Gets an {@link ArtifactTypeToken} by the artifact type name.
    *
    * @param artifactTypeName the name of the artifact type to get.
    * @return when an artifact type exists with the specified name, an {@link Optional} with the
    * {@link ArtifactTypeToken} for the artifact type; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactTypeToken> getArtifactTypeTokenByName(String artifactTypeName) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                  this.orcsTokenService.getArtifactType(artifactTypeName)
               );
      } catch( OseeTypeDoesNotExist e ) {
         this.lastCause.set(Cause.NOT_FOUND);
         this.lastError.set(e);
      }
      catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
      }

      return Optional.empty();
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
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                  this.queryFactory
                     .fromBranch(branchId, viewId)
                     .andIsOfType(artifactTypeToken)
                     .andNameEquals(artifactName)
                     .andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent, parent)
                     .getArtifact()
               );
         //@formatter:on
      } catch (MultipleItemsExist e) {
         this.lastCause.set(Cause.MORE_THAN_ONE);
         this.lastError.set(e);
      } catch (ItemDoesNotExist e) {
         this.lastCause.set(Cause.NOT_FOUND);
         this.lastError.set(e);
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
      }

      return Optional.empty();
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
      //@formatter:off
      return
         this.getArtifactTypeTokenByName( artifactTypeName )
            .flatMap
               (
                 ( artifactTypeToken ) -> this.getChildArtifactReadableByTypeAndName( branchId, viewId, parent, artifactTypeToken, artifactName )
               );
      //@formatter:on
   }

   /**
    * Gets the status ({@link Cause}) of the last completed operation.
    *
    * @return the status of the last operation categorized as a {@link Cause}.
    */

   public Cause getLastCause() {
      return this.lastCause.get();
   }

   /**
    * If the last operation threw an {@link Exception}, gets the {@link Exception}. This method may return an
    * {@link Exception} even when the {@link Cause} is other than {@link Cause#ERROR}.
    *
    * @return when the last operation threw an {@link Exception}, an {@link Optional} containing the {@link Exception};
    * otherwise, an empty {@link Optional}.
    */

   public Optional<Exception> getLastError() {
      return Optional.ofNullable(this.lastError.get());
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
    * @return when the search was successful, an {@link Optional} containing a possibly empty list of the artifacts
    * matching the search criteria; otherwise, an empty {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getRecursiveChildenArtifactReadablesByAttributeTypeAndAttributeValue(BranchId branchId, ArtifactId viewId, ArtifactId parent, AttributeTypeId attributeTypeId, String value) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this.queryFactory
                    .fromBranch(branchId, viewId)
                    .andAttributeIs(attributeTypeId, value)
                    .andRelatedRecursive(DefaultHierarchical_Child,parent)
                    .getResults()
                    .getList()
               );
         //@formatter:on
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
      }

      return Optional.empty();
   }

   /**
    * Finds the artifacts of the specified type with an attribute that contains a specific value that are children of a
    * specified artifact on a branch view. This method may throw an exception when the search fails. A successful search
    * with no results will return an empty list.
    *
    * @param branchId the branch identifier.
    * @param viewId the branch view identifier.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeToken the type of children artifact to get.
    * @param attributeTypeId the attribute type identifier of the attribute to be checked for a matching value.
    * @param value the attribute value to check for.
    * @return when the search was successful, an {@link Optional} containing a possibly empty list of the artifacts
    * matching the search criteria; otherwise, an empty {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getRecursiveChildenArtifactReadablesOfTypeByAttributeTypeAndAttributeValue(BranchId branchId, ArtifactId viewId, ArtifactId parent, ArtifactTypeToken artifactTypeToken, AttributeTypeId attributeTypeId, String value) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this.queryFactory
                    .fromBranch(branchId, viewId)
                    .andAttributeIs(attributeTypeId, value)
                    .andRelatedRecursive(DefaultHierarchical_Child,parent)
                    .andIsOfType(artifactTypeToken)
                    .getResults()
                    .getList()
               );
         //@formatter:on
      } catch (Exception e) {
         this.lastCause.set(Cause.ERROR);
         this.lastError.set(e);
      }

      return Optional.empty();
   }

   /**
    * Sets the thread local error tracking members to initial values.
    */

   private void startOperation() {
      this.lastCause.remove();
      this.lastError.remove();
   }
}

/* EOF */
