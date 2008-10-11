/*
 * Created on Sep 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.item;

import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact.PeerToPeerReviewState;

/**
 * @author Donald G. Dunne
 */
public class AtsStatePercentCompleteWeightPeerToPeerReviewRule extends AtsStatePercentCompleteWeightRule {

   public static String ID = "atsStatePercentCompleteWeight.PeerToPeerReview";

   public AtsStatePercentCompleteWeightPeerToPeerReviewRule() {
      super(ID, ID);
      setDescription("State Percent Complete rule for PeerToPeer Review.");
      addWorkDataKeyValue(PeerToPeerReviewState.Prepare.name(), ".20");
      addWorkDataKeyValue(PeerToPeerReviewState.Review.name(), ".79");
      addWorkDataKeyValue(PeerToPeerReviewState.Completed.name(), ".01");
   }

}
