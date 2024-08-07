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

package org.eclipse.osee.ats.api.review;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

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
