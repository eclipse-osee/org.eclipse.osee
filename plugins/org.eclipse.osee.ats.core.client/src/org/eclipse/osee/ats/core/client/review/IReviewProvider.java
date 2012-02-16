/*
 * Created on Sep 9, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.review;



public interface IReviewProvider {

   /**
    * Notification that a review was created. This allows the extension to do necessary initial tasks after the review
    * workflow artifact is created. All changes made to review will be persisted after this call.
    */
   public void reviewCreated(AbstractReviewArtifact reviewArt);

}
