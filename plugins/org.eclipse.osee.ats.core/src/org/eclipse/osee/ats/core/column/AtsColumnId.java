/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.column.IAtsColumnId;

/**
 * @author Donald G. Dunne
 */
public enum AtsColumnId implements IAtsColumnId {

   ActionableItem("ats.column.actionableItems"),
   ActivityId("ats.column.activityId"),
   Assignees("ats.column.assignees"),
   AtsId("ats.id"),
   ChangeType("ats.Change Type"),
   CreatedDate("ats.column.createdDate"),
   Insertion("ats.column.insertion"),
   InsertionActivity("ats.column.insertionActivity"),
   LegacyPcrId("ats.column.legacyPcr"),
   Name("framework.artifact.name"),
   Notes("ats.column.notes"),
   PercentCompleteWorkflow("ats.column.workflowPercentComplete"),
   PercentCompleteTasks("ats.column.taskPercentComplete"),
   Priority("ats.column.Priority"),
   State("ats.column.state"),
   Team("ats.column.team"),
   TargetedVersion("ats.column.versionTarget"),
   Title("framework.artifact.name.Title"),
   Type("ats.column.type"),
   Uuid("framework.uuid"),
   WorkPackageName("ats.column.workPackageName"),
   WorkPackageId("ats.column.workPackageId"),
   WorkPackageType("ats.column.workPackageType"),
   WorkPackageProgram("ats.column.workPackageProgram"),
   WorkPackageGuid("ats.column.workPackageGuid"),
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
