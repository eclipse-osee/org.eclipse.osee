/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collections;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewState;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewState;

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
