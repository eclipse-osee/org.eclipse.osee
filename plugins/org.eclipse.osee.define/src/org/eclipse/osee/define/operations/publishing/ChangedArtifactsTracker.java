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

package org.eclipse.osee.define.operations.publishing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.osee.framework.core.model.change.ChangeItem;
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
    * Saves a handle to the {@link PublishingUtils} for database queries.
    */

   private final PublishingUtils publishingUtils;

   /**
    * Creates a new empty {@link ChangedArtifactsTracker}.
    *
    * @param atsApi a handle to the {@link AtsApi}.
    * @param publishingUtils a handle to a {@link PublishingUtils} object.
    * @param publishingErrorLog a handle to the {@link PublishingErrorLog} for the publish.
    * @throws NullPointerException when any of the parameters <code>atsApi</code>, <code>publishingUtils</code>, or
    * <code>publishingErrorLog</code> are <code>node</code>.
    */

   public ChangedArtifactsTracker(AtsApi atsApi, PublishingUtils publishingUtils, PublishingErrorLog publishingErrorLog) {
      Objects.requireNonNull(atsApi, "ChangedArtifactsTracker::new, parameter \"atsApi\" cannot be null.");
      Objects.requireNonNull(publishingUtils,
         "ChangedArtifactsTracker::new, parameter \"publishingUtils\" cannot be null.");
      Objects.requireNonNull(publishingErrorLog,
         "ChangedArtifactsTracker::new, parameter \"publishingErrorLog\" cannot be null.");
      this.publishingUtils = Objects.requireNonNull(publishingUtils);
      this.atsBranchService = Objects.requireNonNull(atsApi.getBranchService());
      this.atsQueryService = Objects.requireNonNull(atsApi.getQueryService());
      this.publishingErrorLog = Objects.requireNonNull(publishingErrorLog);
      this.map = new ListMap<>();
   }

   /**
    * Gets an unmodifiable list view of the changed artifacts for the publish.
    *
    * @return list of the changed artifacts.
    */

   public List<ArtifactReadable> getList() {
      return Collections.unmodifiableList(this.map.listView());
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
         this.publishingUtils
            .getWorkFlowArtifactIdentifiers( goalArtifactIdentifier )
            .orElseGet
               (
                  () ->
                  {
                     this.publishingErrorLog.error
                        (
                           goalArtifactIdentifier,
                           new Message()
                                  .title( "Failed to get ATS Team Workflows for the goal Artifact." )
                                  .segment( "Goal Artifact Identifier", goalArtifactIdentifier )
                                  .segment( "Cause", this.publishingUtils.getLastCause() )
                                  .reasonFollowsIfPresent( this.publishingUtils.getLastError() )
                                  .toString()
                        );

                    return List.of();
                  }
               )
            .stream();
      //@formatter:on
   }

   /**
    * Predicate to determine if an artifact for the publish has been changed.
    *
    * @param artifactId the identifier of the artifact to check.
    * @return <code>true</code>, when the artifact has been changed; otherwise, <code>false</code>.
    */

   public boolean isChangedArtifact(ArtifactId artifactId) {

      return this.map.mapView().containsKey(artifactId);
   }

   /**
    * Predicate to determine if the <code>artifact</code> is a hierarchical descendant of an artifact in
    * <code>goodParents</code>. When the <code>artifact</code> is found to be a hierarchical descendant, the artifact
    * and it's hierarchical parents that are not in <code>goodParents</code> are added.
    *
    * @param goodParents the set of artifact's to test if <code>artifact</code> is a hierarchical descendant of one.
    * @param artifact the artifact to test.
    * @return <code>true</code>, when <code>artifact</code> is a hierarchical descendant of an artifact in
    * <code>goodParents</code>; otherwise, <code>false</code>.
    */

   private boolean isRecursivelyRelated(HashSet<ArtifactReadable> goodParents, ArtifactReadable artifact) {
      //@formatter:off
      if( goodParents.contains( artifact ) ) {
         return true;
      }

      var maybeParents = new LinkedList<ArtifactReadable>();
      maybeParents.add( artifact );

      for( var ancestor = artifact.getParent();
               Objects.nonNull( ancestor );
               ancestor = ancestor.getParent() ) {

         maybeParents.add( ancestor );

         if( goodParents.contains( ancestor ) ) {
            goodParents.addAll(maybeParents);
            return true;
         }
      }

      return false;
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

   public void loadByAtsTeamWorkflow(Collection<ArtifactReadable> headerArtifacts,
      ArtifactSpecification artifactSpecification) {

      Objects.requireNonNull(headerArtifacts,
         "ChangedArtifactsTracker::loadByAtsTeamWorkflow, parameter \"headerArtifacts\" cannot be null.");
      Objects.requireNonNull(artifactSpecification,
         "ChangedArtifactsTracker::loadByAtsTeamWorkflow, parameter \"artifactSpecification\" cannot be null.");

      if (headerArtifacts.isEmpty()) {
         return;
      }

      //@formatter:off
      var changedArtIds =
         this.getTeamworkflowArtifactIdentifiers( artifactSpecification.getArtifactId() )
            .map( this.atsQueryService::getTeamWf )
            .filter( ( teamWorkflow ) -> this.isTeamWorkflowBranchValidAndCommitted( teamWorkflow, artifactSpecification.getBranchId() ) )
            .map( ( teamWorkflow ) -> this.atsBranchService.getCommitTransactionRecord( teamWorkflow, artifactSpecification.getBranchId() ) )
            .flatMap( ( commitTx ) -> this.atsBranchService.getChangeData( commitTx ).stream() )
            .map( ChangeItem::getArtId )
            .collect( Collectors.toSet() );

      if( changedArtIds.isEmpty() ) {
         return;
      }

      var goodParents = new HashSet<ArtifactReadable>(headerArtifacts);

      this.streamChangedArtifacts( artifactSpecification, changedArtIds )
         .filter( ( artifactReadable ) -> this.isRecursivelyRelated(goodParents, artifactReadable ) )
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

   /**
    * Loads all artifacts from a branch with a transaction comment that indicates the artifact has been modified on the
    * branch.
    *
    * @param branchSpecification the branch to load modified artifacts from.
    * @throws NullPointerException when the parameter <code>branchSpecification</code> is <code>null</code>.
    */

   public void loadByTransactionComment(BranchSpecification branchSpecification) {

      Objects.requireNonNull(branchSpecification,
         "ChangedArtifactsTracker::loadByTransactionComment, parameter \"branchSpecification\" cannot be null.");

      //@formatter:off
      this.streamChangedArtifactReadables( branchSpecification )
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

   /**
    * Streams all artifacts on the <code>branchSpecification</code> branch that have a transaction comment that
    * indicates the artifact was modified on the branch.
    *
    * @param branchSpecification the branch to stream changed artifacts from.
    * @return a {@link Stream} of the changed artifacts as {@link ArtifactReadable} objects.
    */

   private Stream<ArtifactReadable> streamChangedArtifactReadables(BranchSpecification branchSpecification) {
      //@formatter:off
      return
         this.publishingUtils
            .getArtifactReadablesFilterByTxCommentForChange(branchSpecification)
            .orElseGet
               (
                  () ->
                  {
                     this.publishingErrorLog.error
                        (
                           new Message()
                              .title( "Failed to get changed artifacts from branch by transaction comments." )
                              .segmentToMessage( "Goal Branch", branchSpecification )
                              .segment( "Cause", this.publishingUtils.getLastCause() )
                              .reasonFollowsIfPresent( this.publishingUtils.getLastError() )
                              .toString()
                        );

                     return List.of();
                  }
               )
            .stream();
      //@formatter:on
   }

   /**
    * Streams artifacts from the <code>branchSpecification</code> branch whose identifier is in the
    * <code>changedArtifactIds</code> collection.
    *
    * @param branchSpecification the branch to load artifacts from.
    * @param changedArtifactIds the identifiers of the artifacts to load.
    * @return a {@link Stream} of the {@link ArtifactReadable} objects loaded from the branch with an identifier in the
    * collection.
    */

   private Stream<ArtifactReadable> streamChangedArtifacts(BranchSpecification branchSpecification,
      Collection<ArtifactId> changedArtifactIds) {
      //@formatter:off
      return
         this.publishingUtils
            .getArtifactReadablesByIdentifiers( branchSpecification, changedArtifactIds )
            .orElseGet
               (
                  () ->
                  {
                     this.publishingErrorLog.error
                        (
                           new Message()
                                  .title( "Failed to get changed artifacts identified from the ATS Team Workflow Artifacts for the goal Artifact." )
                                  .segmentToMessage( "Goal Branch", branchSpecification )
                                  .segment( "Cause", this.publishingUtils.getLastCause() )
                                  .reasonFollowsIfPresent( this.publishingUtils.getLastError() )
                                  .toString()
                        );

                     return List.of();
                  }
               )
            .stream();
      //@formatter:on
   }
}

/* EOF */
