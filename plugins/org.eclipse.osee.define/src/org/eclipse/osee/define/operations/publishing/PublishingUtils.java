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

package org.eclipse.osee.define.operations.publishing;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactSpecification;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
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
    * Gets a branch query for both the branch and view when the {@link BranchSpecification} contains a
    * non-{@link ArtifactId#SENTINEL} view; otherwise, a branch query for the branch without view.
    *
    * @param branchSpecification the branch to get a branch query for.
    * @return the {@link QueryBuilder} for the branch query.
    */

   private QueryBuilder getBranchQuery(BranchSpecification branchSpecification) {
      //@formatter:off
      return
         ( branchSpecification.hasView() )
            ? this.queryFactory.fromBranch( branchSpecification.getBranchId(), branchSpecification.getViewId() )
            : this.queryFactory.fromBranch( branchSpecification.getBranchId() );
      //@formatter:on
   }

   /**
    * Gets a branch query for both the branch and view when the {@link ArtifactSpecification} contains a
    * non-{@link ArtifactId#SENTINEL} view; otherwise, a branch query for the branch without view.
    *
    * @param branchSpecification the branch to get a branch query for.
    * @return the {@link QueryBuilder} for the branch query.
    */

   private QueryBuilder getBranchQuery(ArtifactSpecification artifactSpecification) {
      //@formatter:off
      return
         ( artifactSpecification.hasView() )
            ? this.queryFactory.fromBranch( artifactSpecification.getBranchId(), artifactSpecification.getViewId() )
            : this.queryFactory.fromBranch( artifactSpecification.getBranchId() );
      //@formatter:on
   }

   /**
    * Finds an artifact by its GUID.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param guid the GUID of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact GUID; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByGuid(BranchSpecification branchSpecification, String guid) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                  this
                     .getBranchQuery( branchSpecification )
                     .andGuid( guid )
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
    * Finds an artifact by its artifact identifier.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByIdentifier(ArtifactSpecification artifactSpecification) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( artifactSpecification )
                    .andId( artifactSpecification.getArtifactId() )
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
    * Finds an artifact by its artifact name.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param name the name of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByName(BranchSpecification branchSpecification,
      String artifactName) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( branchSpecification )
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

   public Optional<ArtifactReadable> getArtifactReadableByTypeAndName(BranchSpecification branchSpecification,
      ArtifactTypeToken artifactTypeToken, String artifactName) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( branchSpecification )
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
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactTypeName the name of the artifact type of the artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the specified branch view, an
    * {@link Optional} containing an {@link ArtifactReadable} representing that artifact; otherwise an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadableByTypeNameAndName(BranchSpecification branchSpecification,
      String artifactTypeName, String artifactName) {
      //@formatter:off
      return
         this.getArtifactTypeTokenByName(artifactTypeName)
            .flatMap
               (
                 (artifactTypeToken) -> this.getArtifactReadableByTypeAndName(branchSpecification, artifactTypeToken, artifactName)
               );
      //@formatter:on
   }

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

   public Optional<ArtifactReadable> getArtifactReadablePossiblyDeletedByIdentifierAndTransactionIdWithDeleteAttributes(
      ArtifactSpecification artifactSpecification, TransactionId transactionId) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( artifactSpecification )
                    .fromTransaction( transactionId )
                    .andId( artifactSpecification.getArtifactId() )
                    .includeDeletedArtifacts()
                    .includeDeletedAttributes()
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
    * Finds an artifact possibly deleted on a branch by artifact identifier with deleted attributes.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactReadable> getArtifactReadablePossiblyDeletedByIdentifierWithDeletedAttributes(
      ArtifactSpecification artifactSpecification) {
      this.startOperation();
      try {
        //@formatter:off
        return
           Optional.of
              (
                this
                   .getBranchQuery( artifactSpecification )
                   .andId( artifactSpecification.getArtifactId() )
                   .includeDeletedArtifacts()
                   .includeDeletedAttributes()
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
    * Gets all artifacts on a branch with the specified artifact identifiers.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactIds the {@link ArtifactId} identifiers of the artifacts to get.
    * @return when artifacts with the specified identifiers are present for the branch and view, an {@link Optional}
    * containing a {@link List} of the {@link ArtifactReadable}s representing the artifacts found; otherwise an empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getArtifactReadablesByIdentifiers(BranchSpecification branchSpecification,
      Collection<ArtifactId> artifactIds) {

      this.startOperation();

      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( branchSpecification )
                    .andIds(artifactIds)
                    .getResults()
                    .getList()
               );
         //@formatter:on
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
    * Gets all artifacts on a branch with the artifact type.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param artifactTypeToken the type of artifacts to be found.
    * @return when artifacts with the given type and specified branch are present, an {@link Optional} containing a
    * {@link List} of the {@link ArtifactReadable}s representing the artifacts found; otherwise an empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getArtifactReadablesByType(BranchSpecification branchSpecification,
      ArtifactTypeToken artifactTypeToken) {

      this.startOperation();

      try {
         //@formatter:off
         return
            Optional.of
               (
                  this
                     .getBranchQuery( branchSpecification )
                     .andIsOfType( artifactTypeToken )
                     .getResults()
                     .getList()
               );
         //@formatter:on
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
    * Gets all artifacts on a branch with a transaction comment that indicates the artifact has been changed.
    *
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @return when {@link ArtifactReadable}s with a transaction comment that indicate the artifact has been changed are
    * present, an {@link Optional} containing the changed artifacts from the branch; otherwise, a empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getArtifactReadablesFilterByTxCommentForChange(
      BranchSpecification branchSpecification) {

      this.startOperation();

      try {
         //@formatter:off
         return
            Optional.of
               (
                  this
                     .getBranchQuery( branchSpecification )
                     .andTxComment( "(E|P)R\\s?\\d{5}", CoreAttributeTypes.NameWord )
                     .getResults()
                     .getList()
               );
         //@formatter:on
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
    * Finds an artifact token by its artifact identifier.
    *
    * @param artifactSpecification the branch identifier, optional view artifact identifier, and the artifact identifier
    * of the artifact to get.
    * @return when the artifact is found, an {@link Optional} containing the {@link ArtifactReadable} for the specified
    * artifact; otherwise, an empty {@link Optional}.
    */

   public Optional<ArtifactToken> getArtifactTokenByIdentifier(ArtifactSpecification artifactSpecification) {

      this.startOperation();

      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( artifactSpecification )
                    .andId( artifactSpecification.getArtifactId() )
                    .asArtifactToken()
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
    * Gets an {@link ArtifactTypeToken} by the artifact type name.
    *
    * @param artifactTypeName the name of the artifact type to get.
    * @return when an artifact type exists with the specified name, an {@link Optional} with the
    * {@link ArtifactTypeToken} for the artifact type; otherwise, and empty {@link Optional}.
    */

   public Optional<ArtifactTypeToken> getArtifactTypeTokenByName(String artifactTypeName) {
      this.startOperation();
      //@formatter:off
      try {
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
      //@formatter:on
   }

   /**
    * Gets the {@link Branch} by {@link BranchId}.
    *
    * @param branchId the branch to load.
    * @return when a branch with the specified {@link BranchId} exists, an {@link Optional} containing the
    * {@link Branch}; otherwise, an empty {@link Optional}.
    */

   public Optional<Branch> getBranchByIdentifier(BranchId branchId) {
      this.startOperation();
      //@formatter:off
      try {
         return
            Optional.of
               (
                 this.queryFactory
                    .branchQuery()
                    .andId(branchId)
                    .getResults()
                    .getExactlyOne()
               );
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

   public Optional<ArtifactReadable> getChildArtifactReadableByTypeAndName(BranchSpecification branchSpecification, ArtifactId parent, ArtifactTypeToken artifactTypeToken, String artifactName) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                  this
                     .getBranchQuery( branchSpecification )
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
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeName the name of the artifact type of artifact to be found.
    * @param artifactName the name of the artifact to be found.
    * @return when only one artifact with the given type and name exists on the branch view as an immediate child of the
    * parent, an {@link Optional} containing the {@link ArtifactReadable} for the found artifact; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<ArtifactReadable> getChildArtifactReadableByTypeNameAndName(BranchSpecification branchSpecification,
      ArtifactId parent, String artifactTypeName, String artifactName) {
      //@formatter:off
      return
         this.getArtifactTypeTokenByName( artifactTypeName )
            .flatMap
               (
                 ( artifactTypeToken ) -> this.getChildArtifactReadableByTypeAndName( branchSpecification, parent, artifactTypeToken, artifactName )
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
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param attributeTypeId the attribute type identifier of the attribute to be checked for a matching value.
    * @param value the attribute value to check for.
    * @return when the search was successful, an {@link Optional} containing a possibly empty list of the artifacts
    * matching the search criteria; otherwise, an empty {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getRecursiveChildenArtifactReadablesByAttributeTypeAndAttributeValue(
      BranchSpecification branchSpecification, ArtifactId parent, AttributeTypeId attributeTypeId, String value) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( branchSpecification )
                    .andAttributeIs( AttributeTypeToken.valueOf( attributeTypeId.getId() ), value)
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
    * @param branchSpecification the branch and optionally view to get artifacts from.
    * @param parent the identifier of the parent artifact.
    * @param artifactTypeToken the type of children artifact to get.
    * @param attributeTypeId the attribute type identifier of the attribute to be checked for a matching value.
    * @param value the attribute value to check for.
    * @return when the search was successful, an {@link Optional} containing a possibly empty list of the artifacts
    * matching the search criteria; otherwise, an empty {@link Optional}.
    */

   public Optional<List<ArtifactReadable>> getRecursiveChildenArtifactReadablesOfTypeByAttributeTypeAndAttributeValue(
      BranchSpecification branchSpecification, ArtifactId parent, ArtifactTypeToken artifactTypeToken,
      AttributeTypeId attributeTypeId, String value) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                 this
                    .getBranchQuery( branchSpecification )
                    .andAttributeIs( AttributeTypeToken.valueOf( attributeTypeId.getId() ), value)
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
    * Gets the {@link ArtifactId}s of the ATS Workflow artifacts related to the artifact specified by
    * <code>artifactId</code>.
    *
    * @param artifactId the artifact to get related ATS Workflow artifacts for.
    * @return when there are ATS Workflow artifacts related to the artifact specified by <code>artifactId</code>, an
    * {@link Optional} containing the {@link ArtifactId}s of the related ATS Workflow artifacts; otherwise, an empty
    * {@link Optional}.
    */

   public Optional<List<ArtifactId>> getWorkFlowArtifactIdentifiers(ArtifactId artifactId) {
      this.startOperation();
      try {
         //@formatter:off
         return
            Optional.of
               (
                  this.queryFactory
                     .fromBranch( CoreBranches.COMMON )
                     .andRelatedTo( AtsRelationTypes.Goal_Goal, artifactId )
                     .asArtifactIds()
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
