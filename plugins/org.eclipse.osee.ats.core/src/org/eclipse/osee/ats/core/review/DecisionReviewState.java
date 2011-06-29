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
package org.eclipse.osee.ats.core.review;

import java.util.List;
import org.eclipse.osee.framework.core.util.WorkPageAdapter;
import org.eclipse.osee.framework.core.util.WorkPageType;

public class DecisionReviewState extends WorkPageAdapter {
   public static DecisionReviewState Prepare = new DecisionReviewState("Prepare", WorkPageType.Working);
   public static DecisionReviewState Decision = new DecisionReviewState("Decision", WorkPageType.Working);
   public static DecisionReviewState Followup = new DecisionReviewState("Followup", WorkPageType.Working);
   public static DecisionReviewState Completed = new DecisionReviewState("Completed", WorkPageType.Completed);

   private DecisionReviewState(String pageName, WorkPageType workPageType) {
      super(DecisionReviewState.class, pageName, workPageType);
   }

   public static DecisionReviewState valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(DecisionReviewState.class, pageName);
   }

   public static List<DecisionReviewState> values() {
      return WorkPageAdapter.pages(DecisionReviewState.class);
   }

};
