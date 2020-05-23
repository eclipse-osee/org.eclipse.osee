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

package org.eclipse.osee.ats.api.workdef;

/**
 * @author Mark Joy
 * @author Donald G. Dunne
 */
public enum RuleEventType {
   CreateBranch("Tasks generated upon creation of a new branch"),
   CommitBranch("Tasks generated upon commit of a new branch"),
   CreateWorkflow("Tasks generated upon creation of a new workflow"),
   TransitionTo("Tasks generated upon transition of workflow to state"),
   Manual("Tasks generated upon selection by user on Task Tab"),
   ChangeReportTasks("Tasks generated off Change Report from Branch");

   private final String description;

   private RuleEventType(String description) {
      this.description = description;
   }

   public String getDescription() {
      return description;
   }

}
