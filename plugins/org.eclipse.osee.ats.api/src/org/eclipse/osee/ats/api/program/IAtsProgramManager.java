/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.api.program;

import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.operation.IOperation;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgramManager {

   public boolean isApplicable(IAtsTeamWorkflow teamWf);

   public IOperation createValidateReqChangesOp(IAtsTeamWorkflow teamWf);

   public String getName();

   default public String getXProgramComboWidgetName() {
      return null;
   }

   default public ArtifactToken getPidsReviewAssigneeUserGroup() {
      return null;
   }

}
