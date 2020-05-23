/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workflow.util;

import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public interface IDuplicateWorkflowListener {

   default IAtsGoal addToGoal(IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
      return null;
   }

   /**
    * @return false if operation should be cancelled
    */
   default boolean handleChanges(IAtsTeamWorkflow newTeamWf, IAtsChangeSet changes) {
      return true;
   }

}
