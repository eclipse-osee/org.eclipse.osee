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

package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.publishing.Cause;
import org.eclipse.osee.framework.core.publishing.DataAccessException;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.core.publishing.ProcessRecursively;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

/**
 * An implementation of the {@link DataAccessOperations} interface for client side publishing.
 * <p>
 * The {@link ArtifactReadable} implementations returned by this class are implemented with {@link Artifact}.
 *
 * @author Loren K. Ashley
 */

public class DataAccessOperationsImplArtifact implements DataAccessOperations {

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public Result<Map<String, List<String>>, DataAccessException> getApplicabilityNamedViewMap(
      BranchSpecification branchSpecification) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public Result<Map<ApplicabilityId, ApplicabilityToken>, DataAccessException> getApplicabilityTokenMap(
      BranchSpecification branchSpecification) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getArtifactIdentifiers(BranchSpecification branchSpecification,
      AttributeTypeId attributeTypeId, String attributeValue, TransactionId transactionId,
      IncludeDeleted includeDeleted) {

      try {
         //@formatter:off
         var queryBuilder = ServiceUtil.getOseeClient().createQueryBuilder( branchSpecification.getBranchId() );

         switch(   ( Objects.nonNull( attributeTypeId ) && attributeTypeId.isValid() ? 1 : 0 )
                 + ( Objects.nonNull( attributeValue  )                              ? 2 : 0 ) ) {
            case 1:
               queryBuilder = queryBuilder.andExists( attributeTypeId );
               break;
            case 3:
               queryBuilder = queryBuilder.and( attributeTypeId, attributeValue );
               break;
         }

         if( transactionId.isValid() ) {
            queryBuilder = queryBuilder.fromTransaction( transactionId );
         }

         queryBuilder =
            includeDeleted.yes()
               ? queryBuilder.includeDeleted()
               : queryBuilder.excludeDeleted();

         var foundArtifactIdentifiers = queryBuilder.getIds();

         return Result.<List<ArtifactId>,DataAccessException>ofValue( foundArtifactIdentifiers );

      } catch( Exception e ) {

         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getArtifactIdentifiers, failed to load artifact identifiers." )
                                   .indentInc()
                                   .segment( "Branch Specification", branchSpecification )
                                   .segmentIfNot( "Attribute Type Id", attributeTypeId, AttributeTypeId.SENTINEL )
                                   .segmentIfNot( "Attribute Value", attributeValue, Strings.isInvalidOrBlank( attributeValue ) )
                                   .segmentIfNot( "Transaction Identifier", transactionId, TransactionId.SENTINEL )
                                   .segment( "Include Deleted", includeDeleted )
                                   .reasonFollows( e )
                                   .toString(),
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

      try {

         //@formatter:off
         var artifacts =
            ArtifactLoader
               .loadArtifacts
                  (
                     List.of( artifactSpecification.getArtifactId() ),
                     artifactSpecification.getBranchId(),
                     LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA,
                     LoadType.RELOAD_CACHE,
                     DeletionFlag.EXCLUDE_DELETED,
                     TransactionId.SENTINEL
                  );

         if( artifacts.isEmpty() ) {

            return
               Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getArtifactReadableByIdentifier, artifact not found." )
                                   .indentInc()
                                   .segment( "ArtifactSpecification", artifactSpecification )
                                   .toString(),
                            Cause.NOT_FOUND
                         )
               );

         }

         if( artifacts.size() > 1 ) {

            return
               Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getArtifactReadableByIdentifier, more than one artifact found when only one is expected." )
                                   .indentInc()
                                   .segment( "ArtifactSpecification", artifactSpecification )
                                   .toString(),
                            Cause.MORE_THAN_ONE
                         )
               );

         }

         var artifact = artifacts.get(0);

         var publishingArtifact =
            artifactSpecification.hasView()
               ? new WordRenderArtifactWrapperClientImpl( artifact, artifactSpecification )
               : new WordRenderArtifactWrapperClientImpl( artifact );
         //@formatter:off

         return Result.ofValue( publishingArtifact );

      } catch( Exception e ) {
         //@formatter:off
         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getArtifactReadableByIdentifier, failed to load artifact." )
                                   .indentInc()
                                   .segment( "ArtifactSpecification", artifactSpecification )
                                   .reasonFollows( e )
                                   .toString(),
                            Cause.ERROR,
                            e
                         )
               );
         //@formatter:on
      }
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactReadable>, DataAccessException> getArtifactReadables(
      BranchSpecification branchSpecification, Collection<ArtifactId> artifactIdentifiers, Collection<String> guids,
      String artifactName, ArtifactTypeToken artifactTypeToken, TransactionId transactionId,
      IncludeDeleted includeDeleted) {

      try {

         //@formatter:off
         var queryBuilder = ServiceUtil.getOseeClient().createQueryBuilder( branchSpecification.getBranchId() );

         if( Objects.nonNull( artifactIdentifiers ) && !artifactIdentifiers.isEmpty() ) {
            queryBuilder = queryBuilder.andIds( artifactIdentifiers );
         }

         if( Objects.nonNull( guids ) && !guids.isEmpty() ) {
            queryBuilder = queryBuilder.andGuids
                              (
                                 ( guids instanceof List )
                                    ? (List<String>) guids
                                    : new ArrayList<String>( guids )
                              );
         }

         if( Strings.isValidAndNonBlank( artifactName ) ) {
            queryBuilder = queryBuilder.andNameEquals( artifactName );
         }

         if( artifactTypeToken.isValid() ) {
            queryBuilder = queryBuilder.andIsOfType( artifactTypeToken );
         }

         if( transactionId.isValid() ) {
            queryBuilder = queryBuilder.fromTransaction( transactionId );
         }

         queryBuilder =
            includeDeleted.yes()
               ? queryBuilder.includeDeleted()
               : queryBuilder.excludeDeleted();

         var foundArtifactIdentifiers = queryBuilder.getIds();

         var artifacts =
            ArtifactLoader
               .loadArtifacts
                  (
                     foundArtifactIdentifiers,
                     branchSpecification.getBranchId(),
                     LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA,
                     LoadType.RELOAD_CACHE,
                     includeDeleted.yes()
                        ? DeletionFlag.INCLUDE_DELETED
                        : DeletionFlag.EXCLUDE_DELETED,
                     transactionId
                  );

         if( artifacts.isEmpty() ) {

            return
               Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getArtifactReadables, artifact not found." )
                                   .indentInc()
                                   .segment( "Branch Specification", branchSpecification )
                                   .segmentIndexed( "Artifact Identifiers", artifactIdentifiers, ArtifactId::getIdString, 20 )
                                   .segmentIndexed( "Artifact GUIDs",       guids,               ( t ) -> t,              20 )
                                   .segmentIfNotNull( "Artifact Name", () -> artifactName, artifactName )
                                   .segmentIfNot( "Artifact Type Token", artifactTypeToken, ArtifactTypeToken.SENTINEL )
                                   .segmentIfNot( "Transaction Identifier", transactionId, TransactionId.SENTINEL )
                                   .segment( "Include Deleted", includeDeleted )
                                   .toString(),
                            Cause.NOT_FOUND
                         )
               );

         }

         var publishingArtifacts =
                (
                   branchSpecification.hasView()
                      ? artifacts
                           .stream()
                           .map( ( artifact ) -> (ArtifactReadable) new WordRenderArtifactWrapperClientImpl( artifact, branchSpecification ) )
                      : artifacts
                           .stream()
                           .map( ( artifact ) -> (ArtifactReadable) new WordRenderArtifactWrapperClientImpl( artifact ) )
                ).collect( Collectors.toList() );

         return Result.<List<ArtifactReadable>,DataAccessException>ofValue( publishingArtifacts );

      } catch( Exception e ) {

         return
            Result.ofError
            (
               new DataAccessException
                      (
                         new Message()
                                .title( "DataAccessOperationsImplArtifact::getArtifactReadables, failed to load artifacts." )
                                .indentInc()
                                .segment( "Branch Specification", branchSpecification )
                                .segmentIndexed( "Artifact Identifiers", artifactIdentifiers, ArtifactId::getIdString, 20 )
                                .segmentIndexed( "Artifact GUIDs",       guids,               ( t ) -> t,              20 )
                                .segmentIfNotNull( "Artifact Name", () -> artifactName, artifactName )
                                .segmentIfNot( "Artifact Type Token", artifactTypeToken, ArtifactTypeToken.SENTINEL )
                                .segmentIfNot( "Transaction Identifier", transactionId, TransactionId.SENTINEL )
                                .segment( "Include Deleted", includeDeleted )
                                .reasonFollows( e )
                                .toString(),
                         Cause.ERROR,
                         e
                      )
            );
      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public Result<List<ArtifactReadable>, DataAccessException> getArtifactReadablesListOfParents(
      BranchSpecification branchSpecification, ArtifactId childArtifactId) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public Result<List<ArtifactToken>, DataAccessException> getArtifactTokens(BranchSpecification branchSpecification,
      Collection<ArtifactId> artifactIdentifiers, ArtifactTypeToken artifactTypeToken,
      RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<Branch, DataAccessException> getBranchByIdentifier(BranchId branchId) {

      //@formatter:off
      try {

         var modelBranch = BranchManager.getBranch( branchId );

         if( Objects.isNull( modelBranch ) ) {

            return
               Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getBranch, failed to load branch." )
                                   .indentInc()
                                   .segment( "Branch Identifier", branchId )
                                   .toString(),
                            Cause.NOT_FOUND
                         )
               );

         }

         var branch =
            new Branch
                   (
                      modelBranch.getId(),
                      modelBranch.getName(),
                      modelBranch.getAssociatedArtifactId(),
                      modelBranch.getBaseTransaction(),
                      modelBranch.getSourceTransaction(),
                      modelBranch.getParentBranch(),
                      modelBranch.isArchived(),
                      modelBranch.getBranchState(),
                      modelBranch.getBranchType(),
                      modelBranch.isInheritAccessControl(),
                      modelBranch.getViewId()
                   );

         return Result.<Branch,DataAccessException>ofValue( branch );


      } catch( Exception e ) {

         return
            Result.ofError
            (
               new DataAccessException
                      (
                         new Message()
                                .title( "DataAccessOperationsImplArtifact::getBranch, failed to load branch." )
                                .indentInc()
                                .segment( "Branch Identifier", branchId )
                                .reasonFollows( e )
                                .toString(),
                         Cause.ERROR,
                         e
                      )
            );

      }

   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getBranchViewsForApplicability(
      BranchSpecification branchSpecification, ApplicabilityId applicabilityId) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getChildrenArtifactIdentifiers(
      BranchSpecification branchSpecification, ArtifactId parent, ArtifactTypeToken artifactTypeToken,
      AttributeTypeId attributeTypeId, String attributeValue, ProcessRecursively processRecursively) {
      //@formatter:off
      try {

         var queryBuilder = ServiceUtil.getOseeClient().createQueryBuilder( branchSpecification.getBranchId() );

         if( artifactTypeToken.isValid() ) {
            queryBuilder = queryBuilder.andIsOfType( artifactTypeToken );
         }

         switch(   ( Objects.nonNull( attributeTypeId ) && attributeTypeId.isValid() ? 1 : 0 )
                 + ( Objects.nonNull( attributeValue  )                              ? 2 : 0 ) ) {
            case 1:
               queryBuilder = queryBuilder.andExists( attributeTypeId );
               break;
            case 3:
               queryBuilder = queryBuilder.and( attributeTypeId, attributeValue );
               break;
         }

         queryBuilder =
            processRecursively.yes()
               ? queryBuilder.andRelatedRecursive( CoreRelationTypes.DefaultHierarchical_Child, parent )
               : queryBuilder.andRelatedTo( CoreRelationTypes.DefaultHierarchical_Parent, parent );

         var foundArtifactIdentifiers = queryBuilder.getIds();

         return Result.<List<ArtifactId>,DataAccessException>ofValue( foundArtifactIdentifiers );

      } catch( Exception e ) {

         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getChildrenArtifactIdentifiers, failed to load artifact identifiers." )
                                   .indentInc()
                                   .segment( "Branch Specification", branchSpecification )
                                   .segment( "Artifact Identifier", parent.getIdString() )
                                   .segmentIfNot( "Artifact Type Token", artifactTypeToken, ArtifactTypeToken.SENTINEL )
                                   .segmentIfNot( "Attribute Type Id", attributeTypeId, AttributeTypeId.SENTINEL )
                                   .segmentIfNot( "Attribute Value", attributeValue, Strings.isInvalidOrBlank( attributeValue ) )
                                   .segment( "Process Recursively", processRecursively )
                                   .reasonFollows( e )
                                   .toString(),
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

         var queryBuilder = ServiceUtil.getOseeClient().createQueryBuilder( branchSpecification.getBranchId() );

         queryBuilder =
            processRecursively.yes()
               ? queryBuilder.andRelatedRecursive( CoreRelationTypes.DefaultHierarchical_Parent, artifactId )
               : queryBuilder.andRelatedTo( CoreRelationTypes.DefaultHierarchical_Parent, artifactId );

         var foundArtifactIdentifiers = queryBuilder.getIds();

         return Result.<List<ArtifactId>,DataAccessException>ofValue( foundArtifactIdentifiers );

      } catch( Exception e ) {

         return
            Result.ofError
               (
                  new DataAccessException
                         (
                            new Message()
                                   .title( "DataAccessOperationsImplArtifact::getChildrenArtifactIdentifiers, failed to load artifact identifiers." )
                                   .indentInc()
                                   .segment( "Branch Specification", branchSpecification )
                                   .segment( "Artifact Identifier", artifactId.getIdString() )
                                   .segment( "Process Recursively", processRecursively )
                                   .reasonFollows( e )
                                   .toString(),
                            Cause.ERROR,
                            e
                         )
               );

      }
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException
    */

   @Override
   public Result<List<ArtifactId>, DataAccessException> getWorkFlowArtifactIdentifiers(ArtifactId artifactId) {
      throw new UnsupportedOperationException();
   }

}
/* EOF */
