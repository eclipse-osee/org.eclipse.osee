/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collections;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;

/**
 * @author Donald G. Dunne
 */
public class XReviewStateSearchCombo extends XStateSearchCombo {

   @Override
   protected synchronized void ensurePopulated() {
      if (validStates.isEmpty()) {
         validStates.add("--select--");
         for (DecisionReviewState state : DecisionReviewState.values()) {
            if (!validStates.contains(state.getName())) {
               validStates.add(state.getName());
            }
         }
         for (PeerToPeerReviewState state : PeerToPeerReviewState.values()) {
            if (!validStates.contains(state.getName())) {
               validStates.add(state.getName());
            }
         }
         Collections.sort(validStates);
      }
   }

}
