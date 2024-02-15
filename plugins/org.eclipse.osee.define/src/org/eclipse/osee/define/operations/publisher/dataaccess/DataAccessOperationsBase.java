/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.define.operations.publisher.dataaccess;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.publishing.Cause;
import org.eclipse.osee.framework.core.publishing.DataAccessException;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.core.publishing.ProcessRecursively;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * Implements methods of the {@link DataAccessOperations} interface that are common to both the server and client.
 * 
 * @author Loren K. Ashley
 */

public abstract class DataAccessOperationsBase implements DataAccessOperations {

   /**
    * Saves a reference to the ORCS Token Service.
    */

   protected final OrcsTokenService orcsTokenService;

   /**
    * Saves a reference to the ORCS Query Factory.
    */

   protected final QueryFactory queryFactory;

   /**
    * Creates a new {@link DataAccessOperationsBase} object associated with the provided {@link OrcsApi}.
    *
    * @param orcsApi a non-<code>null</code> reference to the ORCS API.
    * @throws NullPointerException when the provided {@link OrcsApi} is <code>null</code>; or the {@link QueryFactory}
    * or the {@link OrcsTokenService} obtained from the {@link OrcsApi} is <code>null</code>.
    */

   protected DataAccessOperationsBase(OrcsApi orcsApi) {

      Objects.requireNonNull(orcsApi);
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

   protected QueryBuilder getBranchQuery(BranchSpecification branchSpecification) {
      //@formatter:off
      return
         ( branchSpecification.hasView() )
            ? this.queryFactory.fromBranch( branchSpecification.getBranchId(), branchSpecification.getViewId() )
            : this.queryFactory.fromBranch( branchSpecification.getBranchId() );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<Map<String, List<String>>, DataAccessException> getApplicabilityNamedViewMap(
      BranchSpecification branchSpecification) {

      //@formatter:off
      try {
         var result =
            Result.<Map<String,List<String>>, DataAccessException>ofValue
               (
                  this.queryFactory
                     .applicabilityQuery()
                     .getNamedViewApplicabilityMap
                        (
                           BranchId.valueOf( branchSpecification.getBranchId().getId() ),
                           branchSpecification.getViewId()
                        )
                );
         return result;
      } catch( Exception e ) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<Map<ApplicabilityId, ApplicabilityToken>, DataAccessException> getApplicabilityTokenMap(
      BranchSpecification branchSpecification) {

      //@formatter:off
      try {
         var result =
            Result.<Map<ApplicabilityId, ApplicabilityToken>, DataAccessException>ofValue
               (
                  this.queryFactory
                     .applicabilityQuery()
                     .getApplicabilityTokens( branchSpecification.getBranchId() )
                     .entrySet()
                     .stream()
                     .collect
                        (
                           Collectors.toMap
                              (
                                 ( entrySet ) -> ApplicabilityId.valueOf( entrySet.getKey() ),
                                 Entry::getValue
                              )
                        )
                );
         return result;
      } catch( Exception e ) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getArtifactIdentifiers(BranchSpecification branchSpecification,
      AttributeTypeId attributeTypeId, String attributeValue, TransactionId transactionId,
      IncludeDeleted includeDeleted) {

      //@formatter:off
      try {

         var query = this.getBranchQuery( branchSpecification );

         switch(   ( Objects.nonNull( attributeTypeId ) && attributeTypeId.isValid() ? 1 : 0 )
                 + ( Objects.nonNull( attributeValue  )                              ? 2 : 0 ) ) {
            case 1:
               query = query.andExists( AttributeTypeToken.valueOf( attributeTypeId.getId() ) );
               break;
            case 3:
               query = query.andAttributeIs( AttributeTypeToken.valueOf( attributeTypeId.getId() ), attributeValue );
               break;
         }

         if( includeDeleted.yes() ) {
            query = query
                       .includeDeletedArtifacts()
                       .includeDeletedAttributes();
         }

         if( transactionId.isValid() ) {
            query = query.fromTransaction(transactionId);
         }

         var result = Result.<List<ArtifactId>, DataAccessException>ofValue( query.asArtifactIds() );

         return result;

      } catch (ItemDoesNotExist e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.NOT_FOUND,
                            e
                         )
               );
      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getArtifactIdentifiersFilterByTxCommentForChange(
      BranchSpecification branchSpecification) {

      //@formatter:off
      try {
         var result =
            Result.<List<ArtifactId>, DataAccessException>ofValue
               (
                  this
                     .getBranchQuery( branchSpecification )
                     .andTxComment( "(E|P)R\\s?\\d{5}", CoreAttributeTypes.NameWord )
                     .asArtifactIds()
               );
         return result;
      } catch (ItemDoesNotExist e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.NOT_FOUND,
                            e
                         )
               );
      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<ArtifactReadable, DataAccessException> getArtifactReadableByIdentifier(
      ArtifactSpecification artifactSpecification) {
      //@formatter:off
      return
         this
            .getArtifactReadables
               (
                  artifactSpecification,
                  List.of( artifactSpecification.getArtifactId() ),
                  List.of(),
                  Strings.EMPTY_STRING,
                  ArtifactTypeToken.SENTINEL,
                  TransactionId.SENTINEL,
                  IncludeDeleted.NO
               )
            .filterValue( Predicate.not( List::isEmpty ) )
            .mapValue
               (
                  ( artifacts ) -> artifacts.get(0)
               )
            .orWhenEmpty
               (
                  () -> Result.ofError
                           (
                              new DataAccessException
                                     (
                                        new Object() {}.getClass().getEnclosingMethod().getName(),
                                        Cause.NOT_FOUND
                                     )
                           )
               );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactToken>, DataAccessException> getArtifactTokens(BranchSpecification branchSpecification,
      Collection<ArtifactId> artifactIdentifiers, ArtifactTypeToken artifactTypeToken,
      RelationTypeSide relationTypeSide) {

      //@formatter:off
      try {
         var query = this.queryFactory.fromBranch( BranchId.valueOf( branchSpecification.getBranchId().getId() ) );

         if( Objects.nonNull( artifactIdentifiers ) && !artifactIdentifiers.isEmpty() ) {
            query = query.andIds( artifactIdentifiers );
         }
         if( artifactTypeToken.isValid() ) {
            query = query.andTypeEquals( artifactTypeToken );
         }

         if( relationTypeSide.isValid() ) {
            query = query.andRelationExists( relationTypeSide );
         }

         var result = Result.<List<ArtifactToken>, DataAccessException>ofValue( query.asArtifactTokens() );

         return result;

      } catch( Exception e ) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<Branch, DataAccessException> getBranchByIdentifier(BranchId branchId) {

      //@formatter:off
      try {
         var result =
            Result.<Branch, DataAccessException>ofValue
               (
                 this.queryFactory
                    .branchQuery()
                    .andId(branchId)
                    .getResults()
                    .getExactlyOne()
               );
         return result;
      } catch (MultipleItemsExist e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.MORE_THAN_ONE,
                            e
                         )
               );
      } catch (ItemDoesNotExist e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.NOT_FOUND,
                            e
                         )
               );
      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getBranchViewsForApplicability(
      BranchSpecification branchSpecification, ApplicabilityId applicabilityId) {

      //@formatter:off
      try {
         return
            Result.ofValue
               (
                  this.queryFactory
                     .applicabilityQuery()
                     .getBranchViewsForApplicability( branchSpecification.getBranchId(), applicabilityId )
               );
      } catch ( Exception e )  {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getParentArtifactIdentifiers(
      BranchSpecification branchSpecification, ArtifactId artifactId, ProcessRecursively processRecursively) {

      //@formatter:off
      try {

         var query = this.getBranchQuery( branchSpecification );

         query = processRecursively.yes()
                    ? query.andRelatedRecursive( CoreRelationTypes.DefaultHierarchical_Parent, artifactId )
                    : query.andRelatedTo( CoreRelationTypes.DefaultHierarchical_Parent, artifactId );

         var result = Result.<List<ArtifactId>, DataAccessException>ofValue( query.asArtifactIds() );

         return result;

      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getChildrenArtifactIdentifiers(
      BranchSpecification branchSpecification, ArtifactId parent, ArtifactTypeToken artifactTypeToken,
      AttributeTypeId attributeTypeId, String value, ProcessRecursively processRecursively) {

      //@formatter:off
      try {

         var query = this.getBranchQuery( branchSpecification );

         if( artifactTypeToken.isValid() ) {
            query = query.andIsOfType( artifactTypeToken );
         }

         if( attributeTypeId.isValid() && Strings.isValidAndNonBlank( value ) ) {
            query = query.andAttributeIs( AttributeTypeToken.valueOf( attributeTypeId.getId() ), value );
         }

         query = processRecursively.yes()
                    ? query.andRelatedRecursive( CoreRelationTypes.DefaultHierarchical_Child, parent )
                    : query.andRelatedTo( CoreRelationTypes.DefaultHierarchical_Parent, parent );


         var result = Result.<List<ArtifactId>, DataAccessException>ofValue( query.asArtifactIds() );

         return result;

      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getWorkFlowArtifactIdentifiers(ArtifactId artifactId) {

      //@formatter:off
      try {
         var result =
            Result.<List<ArtifactId>, DataAccessException>ofValue
               (
                  this.queryFactory
                     .fromBranch( CoreBranches.COMMON )
                     .andRelatedTo( AtsRelationTypes.Goal_Goal, artifactId )
                     .asArtifactIds()
               );
         return result;
      } catch (Exception e) {
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Object() {}.getClass().getEnclosingMethod().getName(),
                            Cause.ERROR,
                            e
                         )
               );
      }
      //@formatter:on
   }

}
