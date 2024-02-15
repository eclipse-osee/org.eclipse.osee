/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.operations.publisher.publishing;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactSpecification;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSpecification;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.publishing.DataAccessOperations;
import org.eclipse.osee.framework.core.publishing.FilterForView;
import org.eclipse.osee.framework.core.publishing.IncludeDeleted;
import org.eclipse.osee.framework.core.publishing.PublishingArtifact;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader.BranchIndicator;
import org.eclipse.osee.framework.core.publishing.PublishingArtifactLoader.WhenNotFound;
import org.eclipse.osee.framework.core.publishing.PublishingErrorLog;
import org.eclipse.osee.framework.jdk.core.type.Result;
import org.eclipse.osee.framework.jdk.core.util.ListMap;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * Provides a lookup for artifacts in a publish that have been modified on the publishing branch.
 *
 * @author Loren K. Ashley
 */

public class ChangedArtifactsTracker {

   /**
    * Saves a handle to the {@link IAtsBranchService}.
    */

   private final IAtsBranchService atsBranchService;

   /**
    * Saves a handle to the {@link atsQueryService}.
    */

   private final IAtsQueryService atsQueryService;

   /**
    * Caches the changed artifacts by {@link ArtifactId}.
    */

   private final ListMap<ArtifactId, ArtifactReadable> map;

   /**
    * Saves a handle to the {@link PublishingErrorLog} for recording errors.
    */

   private final PublishingErrorLog publishingErrorLog;

   /**
    * Saves a handle to the {@link DataAccessOperations} for database queries.
    */

   private final DataAccessOperations dataAccessOperations;

   /**
    * Creates a new empty {@link ChangedArtifactsTracker}.
    *
    * @param atsApi a handle to the {@link AtsApi}.
    * @param dataAccessOperations a handle to a {@link DataAccessOperations} implementation.
    * @param publishingErrorLog a handle to the {@link PublishingErrorLog} for the publish.
    * @throws NullPointerException when any of the parameters <code>atsApi</code>, <code>dataAccessOperations</code>, or
    * <code>publishingErrorLog</code> are <code>node</code>.
    */

   public ChangedArtifactsTracker(AtsApi atsApi, DataAccessOperations dataAccessOperations,
      PublishingErrorLog publishingErrorLog) {
      Objects.requireNonNull(atsApi, "ChangedArtifactsTracker::new, parameter \"atsApi\" cannot be null.");
      Objects.requireNonNull(dataAccessOperations,
         "ChangedArtifactsTracker::new, parameter \"dataAccessOperations\" cannot be null.");
      Objects.requireNonNull(publishingErrorLog,
         "ChangedArtifactsTracker::new, parameter \"publishingErrorLog\" cannot be null.");
      this.dataAccessOperations = Objects.requireNonNull(dataAccessOperations);
      this.atsBranchService = Objects.requireNonNull(atsApi.getBranchService());
      this.atsQueryService = Objects.requireNonNull(atsApi.getQueryService());
      this.publishingErrorLog = Objects.requireNonNull(publishingErrorLog);
      this.map = new ListMap<>();
   }

   /**
    * Gets a stream of the artifact identifiers of the ATS Team Workflow artifacts that indicate the
    * <code>goalArtifactIdentifier</code> is a changed artifact.
    *
    * @param goalArtifactIdentifier the artifact to find the associated ATS Team Workflow artifacts.
    * @return a {@link Stream} of {@link ArtifactId} objects.
    */

   private Stream<ArtifactId> getTeamworkflowArtifactIdentifiers(ArtifactId goalArtifactIdentifier) {
      //@formatter:off
      return
         this.dataAccessOperations
            .getWorkFlowArtifactIdentifiers( goalArtifactIdentifier )
            .orElseGet
               (
                  ( dataAccessException ) ->
                  {
                     this.publishingErrorLog.error
                        (
                           goalArtifactIdentifier,
                           new Message()
                                  .title( "Failed to get ATS Team Workflows for the goal Artifact." )
                                  .segment( "Goal Artifact Identifier", goalArtifactIdentifier )
                                  .reasonFollows( dataAccessException )
                                  .toString()
                        );

                    return Result.ofValue( List.of() );
                  },
                  List::of
               )
            .stream();
      //@formatter:on
   }

   /**
    * Predicate to determine if the ATS Team Workflow branch is committed and the same branch as the
    * <code>goalArtifactBranchIdentifier</code>.
    *
    * @param teamWorkflow the ATS Team Workflow to check the branch of.
    * @param goalArtifactBranchIdentifier the expected ATS Team Workflow branch.
    * @return <code>true</code>, when the ATS Team Workflow branch is committed and matches the expected branch;
    * otherwise, <code>null</code>.
    */

   private boolean isTeamWorkflowBranchValidAndCommitted(IAtsTeamWorkflow teamWorkflow,
      BranchId goalArtifactBranchIdentifier) {

      var workflowBranchToken = this.atsBranchService.getBranch(teamWorkflow);

      if (!workflowBranchToken.isValid()) {
         return false;
      }

      //@formatter:off
      if(
             !this.atsBranchService.isBranchesAllCommitted( teamWorkflow )
          && workflowBranchToken.equals( goalArtifactBranchIdentifier ) ) {

         this.publishingErrorLog.error
            (
               teamWorkflow,
               new Message()
                      .title( "The ATS Team Workflow branch has not been committed and will not be included in the publish." )
                      .indentInc()
                      .segment( "Goal Branch", goalArtifactBranchIdentifier.getIdString() )
                      .toString()
            );

         return false;
      }
      //@formatter:on

      return true;
   }

   /**
    * Finds the artifacts that have been changed under one of the ATS Team Workflows that are associated with the
    * artifact specified by <code>artifactSpecification</code> and that are hierarchical descendants of one of the
    * artifacts in <code>headerArtifacts</code>.
    * <p>
    * Call only one of the methods {@link #loadByAtsTeamworkflow} or {@link #loadByTransactionComment} only once per
    * instantiation of the {@link ChangedArtifactsTracker} class.
    * <p>
    * If the parameter <code>headerArtifacts</code> is an empty {@link Collection}, no changed artifacts will be loaded.
    *
    * @param headerArtifacts find changed artifacts that are hierarchical descendants of these artifacts.
    * @param artifactSpecification ATS Team Workflows related to this artifact are used to find the changed artifacts.
    * @throws NullPointerException when <code>headerArtifacts</code> or <code>artifactSpecification</code> are
    * <code>null</code>.
    * @implNote This method is for taking a Goal artifact and gathering the ArtifactIds of all changed artifacts
    * associated with the workflows under the goal. There is a future plan to publish off of working branches, once
    * implemented, the transaction query will need to be modified. Also, if a working branch is selected, it is okay for
    * that branch to not be committed. If publishing off of a baseline branch, any uncommitted workflows will be logged
    * and changes will not be included in the publish. Will move to MSWordTemplatePublisher when completed.
    */

   public void loadByAtsTeamWorkflow(Collection<? extends PublishingArtifact> headerArtifacts,
      ArtifactId artifactIdentifier, PublishingArtifactLoader publishingArtifactLoader,
      BranchSpecification branchSpecification, Map<ArtifactId, PublishingArtifact> map) {

      Objects.requireNonNull(headerArtifacts,
         "ChangedArtifactsTracker::loadByAtsTeamWorkflow, parameter \"headerArtifacts\" cannot be null.");
      Objects.requireNonNull(artifactIdentifier,
         "ChangedArtifactsTracker::loadByAtsTeamWorkflow, parameter \"artifactIdentifier\" cannot be null.");

      var artifactSpecification = new ArtifactSpecification(branchSpecification, artifactIdentifier);
      if (headerArtifacts.isEmpty()) {
         return;
      }

      //@formatter:off
      var changedArtifactIds =
         this.getTeamworkflowArtifactIdentifiers( artifactSpecification.getArtifactId() )
            .map( this.atsQueryService::getTeamWf )
            .filter( ( teamWorkflow ) -> this.isTeamWorkflowBranchValidAndCommitted( teamWorkflow, artifactSpecification.getBranchId() ) )
            .map( ( teamWorkflow ) -> this.atsBranchService.getCommitTransactionRecord( teamWorkflow, artifactSpecification.getBranchId() ) )
            .flatMap( ( commitTx ) -> this.atsBranchService.getChangeData( commitTx ).stream() )
            .map( ChangeItem::getArtId )
            .collect( Collectors.toSet() );

      if( changedArtifactIds.isEmpty() ) {
         return;
      }

      var goodParents = new HashSet<PublishingArtifact>(headerArtifacts);

      //this.streamChangedArtifacts( artifactSpecification, changedArtIds )
      publishingArtifactLoader
         .getPublishingArtifactsByArtifactIdentifiers
            (
               BranchIndicator.PUBLISHING_BRANCH,
               changedArtifactIds,
               FilterForView.NO,
               WhenNotFound.EMPTY,
               TransactionId.SENTINEL,
               IncludeDeleted.NO
            )
         .orElseThrow()
         .stream()
         .filter( ( artifactReadable ) -> publishingArtifactLoader.isRecursivelyRelated(goodParents, artifactReadable, FilterForView.NO ) )
         .collect
            (
               Collectors.toMap
                  (
                     Function.identity(),
                     Function.identity(),
                     ( a, b ) -> a,
                     () -> this.map.mapView()
                  )
            );
      //@formatter:on
   }

}

/* EOF */
