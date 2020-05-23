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

package org.eclipse.osee.ats.api.branch;

/**
 * @author Donald G. Dunne
 */
public enum BranchStatus {
   Not_Started("No Working Branch", false),
   Changes_InProgress("Changes In Progress", true),
   Changes_NotPermitted__BranchCommitted("Branch Committed - No Changes Permitted", false),
   Changes_NotPermitted__CreationInProgress("Branch Being Created - No Changes Permitted", false),
   Changes_NotPermitted__CommitInProgress("Branch Being Committed - No Changes Permitted", false);

   private final String displayName;
   private final boolean changesPermitted;

   private BranchStatus(String displayName, boolean changesPermitted) {
      this.displayName = displayName;
      this.changesPermitted = changesPermitted;
   }

   public String getDisplayName() {
      return displayName;
   }

   public boolean isChangesPermitted() {
      return changesPermitted;
   }
}
