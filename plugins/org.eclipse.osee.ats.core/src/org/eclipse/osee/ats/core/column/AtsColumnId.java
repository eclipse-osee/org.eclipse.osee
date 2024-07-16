/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.column.IAtsColumnId;

/**
 * @author Donald G. Dunne
 */
public enum AtsColumnId implements IAtsColumnId {

   ActionableItem("ats.column.actionableItems"),
   ActivityId("ats.column.activityId"),
   AgileTeamPoints("ats.agileTeam.Points"),
   Assignees("ats.column.assignees"),
   AtsId("ats.id"),
   SiblingAtsIds("ats.sibling.id"),
   ChangeType("ats.column.changetype"),
   CreatedDate("ats.column.createdDate"),
   CompletedDate("ats.column.completedDate"),
   CancelledDate("ats.column.cancelledDate"),
   CancelledBy("ats.column.cancelledBy"),
   CompletedBy("ats.column.completedBy"),
   CompletedCancelledBy("ats.column.cmpCnclBy"),
   CompletedCancelledDate("ats.column.cmpCnclDate"),
   CancelledReason("ats.column.cancelledReason"),
   CancelledReasonDetails("ats.column.cancelledReasonDetails"),
   CancelReason("ats.column.cancelReason"),
   AgileFeatureGroup("ats.column.agileFeatureGroup"),
   Insertion("ats.column.insertion"),
   InsertionActivity("ats.column.insertionActivity"),
   LegacyPcrId("ats.column.legacyPcr"),
   Name("framework.artifact.name"),
   Notes("ats.column.notes"),
   ParentTitle("ats.column.parentTitle"),
   PercentCompleteWorkflow("ats.column.workflowPercentComplete"),
   PercentCompleteTasks("ats.column.taskPercentComplete"),
   Points("ats.column.points"),
   Priority("ats.column.priority"),
   ReleaseDate("ats.column.releaseDate"),
   State("ats.column.state"),
   SprintOrder("ats.column.sprintOrder"),
   TaskToRelatedArtifactType("ats.column.taskToRelArtType"),
   Team("ats.column.team"),
   TargetedVersion("ats.column.versionTarget"),
   FoundInVersion("ats.column.foundInVersion"),
   Title("framework.artifact.name.Title"),
   Type("ats.column.type"),
   Id("framework.id"),
   UnPlannedWork("ats.Unplanned Work"),
   WorkDefinition("ats.column.workDefinition"),
   WorkPackageName("ats.column.workPackageName"),
   WorkPackageId("ats.column.workPackageId"),
   WorkPackageType("ats.column.workPackageType"),
   WorkPackageProgram("ats.column.workPackageProgram"),
   Implementers("ats.column.implementer"),;

   private final String id;

   private AtsColumnId(String id) {
      this.id = id;
   }

   @Override
   public String getId() {
      return id;
   }

}
