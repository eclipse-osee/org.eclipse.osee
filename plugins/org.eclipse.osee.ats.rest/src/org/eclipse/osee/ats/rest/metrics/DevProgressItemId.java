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
package org.eclipse.osee.ats.rest.metrics;

/**
 * @author Stephen J. Molaro
 */
public enum DevProgressItemId {
   ACT("Action ID"),
   ActionName("Action Name"),
   Program("Program"),
   Build("Build"),
   Date("Date"),
   Created("Created"),
   Requirements("Requirements Workflow"),
   WorkType("Workflow Type"),
   TW("ATS ID"),
   Name("Name"),
   State("State"),
   Endorse("Endorse Date"),
   Analyze("Analyze Date"),
   Authorize("Authorize Date"),
   Implement("Implement Date"),
   Complete("Complete Date"),
   Cancelled("Cancelled Date"),
   TotalCount("Statusable UI Count"),
   CompletedCount("Completed UI Count"),
   CancelledCount("Cancelled UI Count"),
   TotalAddModCount("Statusable Add/Mod UI Count"),
   CompletedAddModCount("Completed Add/Mod UI Count"),
   CancelledAddModCount("Cancelled Add/Mod UI Count"),
   TotalDeletedCount("Statusable Deleted UI Count"),
   CompletedDeletedCount("Completed Deleted UI Count"),
   CancelledDeletedCount("Cancelled Deleted UI Count"),

   TSK("Task ID"),
   TSKName("Task Name"),
   TSKType("Task Type"),
   TSKEndorse("Task Endorse Date"),
   TSKAnalyze("Task Analyze Date"),
   TSKAuthorize("Task Authorize Date"),
   TSKImplement("Task Implement Date"),
   TSKComplete("Task Complete Date"),
   TSKCancelled("Task Cancelled Date");

   private final String displayName;

   private DevProgressItemId(String displayName) { //
      this.displayName = displayName;

   }

   public String getDisplayName() {
      return this.displayName;
   }
}
