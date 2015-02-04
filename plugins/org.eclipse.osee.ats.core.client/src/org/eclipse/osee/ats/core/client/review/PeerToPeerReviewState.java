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
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.core.workflow.state.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewState extends StateTypeAdapter {
   public static PeerToPeerReviewState Prepare = new PeerToPeerReviewState("Prepare", StateType.Working);
   public static PeerToPeerReviewState Review = new PeerToPeerReviewState("Review", StateType.Working);
   public static PeerToPeerReviewState Meeting = new PeerToPeerReviewState("Meeting", StateType.Working);
   public static PeerToPeerReviewState Completed = new PeerToPeerReviewState("Completed", StateType.Completed);
   public static PeerToPeerReviewState Cancelled = new PeerToPeerReviewState("Completed", StateType.Cancelled);

   private PeerToPeerReviewState(String pageName, StateType StateType) {
      super(PeerToPeerReviewState.class, pageName, StateType);
   }

   public static PeerToPeerReviewState valueOf(String pageName) {
      return StateTypeAdapter.valueOfPage(PeerToPeerReviewState.class, pageName);
   }

   public static List<PeerToPeerReviewState> values() {
      return StateTypeAdapter.pages(PeerToPeerReviewState.class);
   }

};
