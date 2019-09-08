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
