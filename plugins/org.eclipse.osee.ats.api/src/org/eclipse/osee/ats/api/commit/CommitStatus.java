/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.commit;

/**
 * @author Donald G. Dunne
 */
public enum CommitStatus {
   Working_Branch_Not_Created("Working Branch Not Created"),
   Branch_Not_Configured("Branch Not Configured"),
   Branch_Commit_Disabled("Branch Commit Disabled"),
   Commit_Needed("Start Commit"),
   No_Commit_Needed("No Commit Needed"),
   Merge_In_Progress("Merge in Progress"),
   Committed("Committed"),
   Committed_With_Merge("Committed With Merge"),
   Rebaseline_In_Progress("Rebaseline In Progress"),
   Commit_Overridden("Commit Overridden");

   private final String displayName;

   private CommitStatus(String displayName) {
      this.displayName = displayName;
   }

   /**
    * @return the displayName
    */
   public String getDisplayName() {
      return displayName;
   }
};
