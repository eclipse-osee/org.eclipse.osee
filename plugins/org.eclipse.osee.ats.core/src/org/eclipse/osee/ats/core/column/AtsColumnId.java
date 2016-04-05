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
   LegacyPcrId("ats.column.legacyPcr"),
   Name("framework.artifact.name"),
   PercentCompleteWorkflow("ats.column.workflowPercentComplete"),
   PercentCompleteTasks("ats.column.taskPercentComplete"),
   State("ats.column.state"),
   Team("ats.column.team"),
   Title("framework.artifact.name.Title"),
   Uuid("framework.uuid"),
   WorkPackageName("ats.column.workPackageName"),
   WorkPackageId("ats.column.workPackageId"),
   WorkPackageType("ats.column.workPackageType"),
   WorkPackageProgram("ats.column.workPackageProgram"),
   WorkPackageGuid("ats.column.workPackageGuid");

   private final String id;

   private AtsColumnId(String id) {
      this.id = id;
   }

   @Override
   public String getId() {
      return id;
   }

}
