/*
 * Created on Apr 15, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.widgets;

import java.util.Collections;
import org.eclipse.osee.ats.core.review.DecisionReviewState;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewState;

/**
 * @author Donald G. Dunne
 */
public class XReviewStateSearchCombo extends XStateSearchCombo {
   public static final String WIDGET_ID = XReviewStateSearchCombo.class.getSimpleName();

   @Override
   protected synchronized void ensurePopulated() {
      if (validStates.isEmpty()) {
         validStates.add("--select--");
         for (DecisionReviewState state : DecisionReviewState.values()) {
            if (!validStates.contains(state.getPageName())) {
               validStates.add(state.getPageName());
            }
         }
         for (PeerToPeerReviewState state : PeerToPeerReviewState.values()) {
            if (!validStates.contains(state.getPageName())) {
               validStates.add(state.getPageName());
            }
         }
         Collections.sort(validStates);
      }
   }

}
