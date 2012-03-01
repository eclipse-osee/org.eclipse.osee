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
package org.eclipse.osee.ats.core.client.review;

import java.util.List;
import org.eclipse.osee.ats.core.workflow.WorkPageAdapter;
import org.eclipse.osee.ats.core.workflow.WorkPageType;

public class PeerToPeerReviewState extends WorkPageAdapter {
   public static PeerToPeerReviewState Prepare = new PeerToPeerReviewState("Prepare", WorkPageType.Working);
   public static PeerToPeerReviewState Review = new PeerToPeerReviewState("Review", WorkPageType.Working);
   public static PeerToPeerReviewState Meeting = new PeerToPeerReviewState("Meeting", WorkPageType.Working);
   public static PeerToPeerReviewState Completed = new PeerToPeerReviewState("Completed", WorkPageType.Completed);

   private PeerToPeerReviewState(String pageName, WorkPageType workPageType) {
      super(PeerToPeerReviewState.class, pageName, workPageType);
   }

   public static PeerToPeerReviewState valueOf(String pageName) {
      return WorkPageAdapter.valueOfPage(PeerToPeerReviewState.class, pageName);
   }

   public static List<PeerToPeerReviewState> values() {
      return WorkPageAdapter.pages(PeerToPeerReviewState.class);
   }

};
