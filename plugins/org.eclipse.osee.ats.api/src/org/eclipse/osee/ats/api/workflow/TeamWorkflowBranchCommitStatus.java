/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import org.eclipse.osee.ats.api.commit.CommitStatus;
import org.eclipse.osee.framework.core.data.BranchToken;

public class TeamWorkflowBranchCommitStatus {

   private final BranchToken branch;
   private final CommitStatus commitStatus;

   public TeamWorkflowBranchCommitStatus(BranchToken branch, CommitStatus commitStatus) {
      this.branch = branch;
      this.commitStatus = commitStatus;
   }

   public BranchToken getBranch() {
      return branch;
   }

   public CommitStatus getCommitStatus() {
      return commitStatus;
   }

}
