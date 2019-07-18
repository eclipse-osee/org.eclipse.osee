/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class StateToken extends NamedIdBase {

   private static Map<Long, StateToken> tokens = new HashMap<>();

   public static StateToken Analyze = StateToken.valueOf(593820493L, "Analyze");
   public static StateToken Authorize = StateToken.valueOf(91727489234L, "Authorize");
   public static StateToken Awaiting_Code = StateToken.valueOf(1271780231L, "Awaiting_Code");
   public static StateToken Awaiting_Review = StateToken.valueOf(928685590L, "Awaiting_Review");
   public static StateToken Awaiting_Testing = StateToken.valueOf(1121865829L, "Awaiting_Testing");
   public static StateToken Backlog = StateToken.valueOf(857399155L, "Backlog");
   public static StateToken Build = StateToken.valueOf(1442293546L, "Build");
   public static StateToken Cancelled = StateToken.valueOf(48239402L, "Cancelled");
   public static StateToken Close = StateToken.valueOf(1506448425L, "Close");
   public static StateToken Closed = StateToken.valueOf(1054771617L, "Closed");
   public static StateToken ClosedWithProblem = StateToken.valueOf(1371821748L, "Closed With Problem");
   public static StateToken Closed_With_Problem = StateToken.valueOf(156138841L, "Closed_With_Problem");
   public static StateToken ClosedwithProblem = StateToken.valueOf(990678280L, "Closed with Problem");
   public static StateToken Coded = StateToken.valueOf(673601381L, "Coded");
   public static StateToken Complete = StateToken.valueOf(1653174502L, "Complete");
   public static StateToken Completed = StateToken.valueOf(3532702930L, "Completed");
   public static StateToken Decision = StateToken.valueOf(98983282387L, "Decision");
   public static StateToken Deferred = StateToken.valueOf(1034976443L, "Deferred");
   public static StateToken Detail = StateToken.valueOf(2070391728L, "Detail");
   public static StateToken Endorse = StateToken.valueOf(23420230948L, "Endorse");
   public static StateToken Develop = StateToken.valueOf(463695258L, "Develop");
   public static StateToken Failed = StateToken.valueOf(1822124311L, "Failed");
   public static StateToken Followup = StateToken.valueOf(88983282387L, "Followup");
   public static StateToken Hold = StateToken.valueOf(1560853708L, "Hold");
   public static StateToken Implement = StateToken.valueOf(43298928340L, "Implement");
   public static StateToken InReview = StateToken.valueOf(9939475738L, "InReview");
   public static StateToken InWork = StateToken.valueOf(32432487L, "InWork");
   public static StateToken In_DTE_Test = StateToken.valueOf(853010543L, "In_DTE_Test");
   public static StateToken In_Review = StateToken.valueOf(1557258414L, "In_Review");
   public static StateToken In_STE_Test = StateToken.valueOf(1101827835L, "In_STE_Test");
   public static StateToken In_Work = StateToken.valueOf(1229182233L, "In_Work");
   public static StateToken Investigating = StateToken.valueOf(852637227L, "Investigating");
   public static StateToken Meeting = StateToken.valueOf(4383477878L, "Meeting");
   public static StateToken Monitor = StateToken.valueOf(100482566L, "Monitor");
   public static StateToken Need_Hot_Bench_Testing = StateToken.valueOf(1142841900L, "Need_Hot_Bench_Testing");
   public static StateToken Need_Inspection = StateToken.valueOf(1195778084L, "Need_Inspection");
   public static StateToken Need_Requirement_Fix = StateToken.valueOf(483355346L, "Need_Requirement_Fix");
   public static StateToken Need_Station_Or_Tool_Fix = StateToken.valueOf(1193006007L, "Need_Station_Or_Tool_Fix");
   public static StateToken No_Change = StateToken.valueOf(1282275901L, "No_Change");
   public static StateToken None = StateToken.valueOf(38383883L, "None");
   public static StateToken NotRequired = StateToken.valueOf(233223455L, "NotRequired");
   public static StateToken Not_Required = StateToken.valueOf(1364159198L, "Not_Required");
   public static StateToken Open = StateToken.valueOf(244724326L, "Open");
   public static StateToken Passed = StateToken.valueOf(1102103105L, "Passed");
   public static StateToken Planning = StateToken.valueOf(1806629388L, "Planning");
   public static StateToken PotentialFutureDesignChange =
      StateToken.valueOf(1740186209L, "Potential Future Design Change");
   public static StateToken PreRelease = StateToken.valueOf(1511032143L, "PreRelease");
   public static StateToken Prepare = StateToken.valueOf(32483247988L, "Prepare");
   public static StateToken Promote = StateToken.valueOf(735151783L, "Promote");
   public static StateToken Promoted = StateToken.valueOf(1583887904L, "Promoted");
   public static StateToken ReOpened = StateToken.valueOf(419015550L, "Re-Opened");
   public static StateToken Ready_For_Acceptance = StateToken.valueOf(1901237863L, "Ready_For_Acceptance");
   public static StateToken Release = StateToken.valueOf(1885160995L, "Release");
   public static StateToken Removed = StateToken.valueOf(636322964L, "Removed");
   public static StateToken Review = StateToken.valueOf(98937432L, "Review");
   public static StateToken Superceded = StateToken.valueOf(563282700L, "Superceded");
   public static StateToken Test = StateToken.valueOf(1665675868L, "Test");
   public static StateToken TaskAttributes = StateToken.valueOf(1974423185L, "Task Attribute");
   public static StateToken Unit_Tested = StateToken.valueOf(484956090L, "Unit_Tested");
   public static StateToken Verification = StateToken.valueOf(1967264839L, "Verification");
   public static StateToken Verify = StateToken.valueOf(1990859957L, "Verify");
   public static StateToken Waiting = StateToken.valueOf(2053770648L, "Waiting");

   public StateToken(Long id, String name) {
      super(id, name);
   }

   public static StateToken valueOf(Long id, String name) {
      StateToken state = tokens.get(id);
      if (state != null) {
         System.err.println("No two state tokens can have same id. Named: " + name);
      }
      state = new StateToken(id, name);
      tokens.put(id, state);
      return state;
   }
}
