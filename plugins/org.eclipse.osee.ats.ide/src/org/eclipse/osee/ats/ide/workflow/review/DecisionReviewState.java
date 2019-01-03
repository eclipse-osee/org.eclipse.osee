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
package org.eclipse.osee.ats.ide.workflow.review;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class DecisionReviewState extends StateTypeAdapter {
   public static DecisionReviewState Prepare = new DecisionReviewState("Prepare", StateType.Working);
   public static DecisionReviewState Decision = new DecisionReviewState("Decision", StateType.Working);
   public static DecisionReviewState Followup = new DecisionReviewState("Followup", StateType.Working);
   public static DecisionReviewState Completed = new DecisionReviewState("Completed", StateType.Completed);

   private DecisionReviewState(String pageName, StateType StateType) {
      super(DecisionReviewState.class, pageName, StateType);
   }

   public static DecisionReviewState valueOf(String pageName) {
      return StateTypeAdapter.valueOfPage(DecisionReviewState.class, pageName);
   }

   public static List<DecisionReviewState> values() {
      return StateTypeAdapter.pages(DecisionReviewState.class);
   }

};
