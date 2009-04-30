/*
 * Created on Apr 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets.commit;

/**
 * @author Donald G. Dunne
 */
public enum CommitStatus {
   Working_Branch_Not_Created("Working Branch Not Created"),
   Branch_Not_Configured("Branch Not Configured"),
   Branch_Commit_Disabled("Branch Commit Disabled"),
   Commit_Needed("Start Commit"),
   Merge_In_Progress("Merge in Progress"),
   Committed("Committed"),
   Committed_With_Merge("Committed With Merge");

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
