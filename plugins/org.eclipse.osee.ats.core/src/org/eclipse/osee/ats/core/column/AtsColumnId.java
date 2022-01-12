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
 * NOTE: column ids can NOT be changed without affecting the stored customizations<br/>
 * <br/>
 * Architecture/Design<br/>
 * <br/>
 * - First, declare a column id. This needs to be a unique string that will be used to store/restore customizations<br/>
 * <br/>
 * - Second, declare a column token for each column id. This either specifies an attr column (preferred) or value
 * column.<br/>
 * - If attr column, this is what brings the id together with column data and the attr type token<br/>
 * <br/>
 * - Third, add to AtsColumnService or an AtsColumnProvider to resolve and instantiate columns<br/>
 * <br/>
 * - Fourth, add to Register column in WorldXViewerFactory(ATS bundle) or an IAtsWorldEditorItem (other bundles)<br/>
 *
 * @author Donald G. Dunne
 */
public enum AtsColumnId implements IAtsColumnId {

   ActionableItem("ats.column.actionableItems"),
   ActivityId("ats.column.activityId"),
   AgileFeatureGroup("ats.column.agileFeatureGroup"),
   AgileTeamPoints("ats.agileTeam.Points"),
   Assignees("ats.column.assignees"),
   AtsId("ats.id"),
   CancelReason("ats.column.cancelReason"),
   CancelledBy("ats.column.cancelledBy"),
   CancelledDate("ats.column.cancelledDate"),
   CancelledReason("ats.column.cancelledReason"),
   CancelledReasonDetails("ats.column.cancelledReasonDetails"),
   ChangeType("ats.column.changetype"),
   CompletedBy("ats.column.completedBy"),
   CompletedCancelledBy("ats.column.cmpCnclBy"),
   CompletedCancelledDate("ats.column.cmpCnclDate"),
   CompletedDate("ats.column.completedDate"),
   CrashOrBlankDisplay("ats.column.crash.or.blank.display"),
   CreatedDate("ats.column.createdDate"),
   ExternalReference("ats.column.external.reference"),
   FoundInVersion("ats.column.foundInVersion"),
   GitChangeId("ats.column.git.change.id"),
   IncorporatedIn("ats.column.incorporated.in"),
   Id("framework.id"),
   Implementers("ats.column.implementer"),
   Insertion("ats.column.insertion"),
   InsertionActivity("ats.column.insertionActivity"),
   LegacyPcrId("ats.column.legacyPcr"),
   Name("framework.artifact.name"),
   Notes("ats.column.notes"),
   ParentTitle("ats.column.parentTitle"),
   PercentCompleteTasks("ats.column.taskPercentComplete"),
   PercentCompleteWorkflow("ats.column.workflowPercentComplete"),
   Points("ats.column.points"),
   Priority("ats.column.priority"),
   ReleaseDate("ats.column.releaseDate"),
   SiblingAtsIds("ats.sibling.id"),
   SprintOrder("ats.column.sprintOrder"),
   State("ats.column.state"),
   TargetedVersion("ats.column.versionTarget"),
   TaskToRelatedArtifactType("ats.column.taskToRelArtType"),
   Team("ats.column.team"),
   Title("framework.artifact.name.Title"),
   Type("ats.column.type"),
   UnPlannedWork("ats.Unplanned Work"),
   WorkDefinition("ats.column.workDefinition"),
   WorkPackageId("ats.column.workPackageId"),
   WorkPackageName("ats.column.workPackageName"),
   WorkPackageProgram("ats.column.workPackageProgram"),
   WorkPackageType("ats.column.workPackageType");

   private final String id;

   private AtsColumnId(String id) {
      this.id = id;
   }

   @Override
   public String getId() {
      return id;
   }

}
