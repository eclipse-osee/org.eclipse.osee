/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
