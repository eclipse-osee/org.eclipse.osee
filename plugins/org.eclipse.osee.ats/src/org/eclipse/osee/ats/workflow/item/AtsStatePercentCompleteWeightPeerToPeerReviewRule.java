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
