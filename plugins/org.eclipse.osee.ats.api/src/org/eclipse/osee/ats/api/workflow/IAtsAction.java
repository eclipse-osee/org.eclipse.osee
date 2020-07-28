/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamWorkflowProvider;

/**
 * @author Donald G. Dunne
 */

public interface IAtsAction extends IAtsObject, IAtsTeamWorkflowProvider {

   public String getAtsId();

   void setAtsId(String atsId);

   default boolean isTeamWorkflow() {
      return isOfType(AtsArtifactTypes.TeamWorkflow);
   }
}
