/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.api.commit;

import java.util.Collection;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;

/**
 * Operations in support of overriding required commits
 *
 * @author Donald G. Dunne
 */
public interface CommitOverrideOperations {

   CommitOverride getCommitOverride(IAtsTeamWorkflow teamWf, BranchId destinationBranch);

   Collection<CommitOverride> getCommitOverrides(IAtsTeamWorkflow teamWf);

   Result setCommitOverride(IAtsTeamWorkflow teamWf, BranchId branch, String reason);

   Result removeCommitOverride(IAtsTeamWorkflow teamWf, BranchId branch);

}
