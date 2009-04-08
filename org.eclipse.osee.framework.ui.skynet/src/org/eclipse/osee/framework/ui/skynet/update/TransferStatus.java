/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

/**
 * @author Jeff C. Phillips
 */
public enum TransferStatus {

   UPDATE_DROP("Revert artifact and Update"),
   UPDATE_SOMEWHERE_ON_BRANCH("Artifact exist on branch - Revert artifact and update"),
   ADD_TO_BASELINE("Add artifact to baseline transaction"),
   ADD_NOT_TO_BASELINE("Add artifact as a new artifact to this branch"),
   ERROR("This artifact will not be updated");

   private String message;

   private TransferStatus(String message) {
      this.message = message;
   }

   public String getMessage() {
      return message;
   }

}
