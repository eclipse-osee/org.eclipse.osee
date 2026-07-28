/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.demo;

import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;

/**
 * @author Donald G. Dunne
 */
public class DemoWorkDefinitions {

   // @formatter:off
   public static AtsWorkDefinitionToken WorkDefTeamDemoProblemReport = new AtsWorkDefinitionToken(235235123L, "WorkDefTeamDemoProblemReport");
   public static AtsWorkDefinitionToken WorkDefTeamDemoChangeRequest = new AtsWorkDefinitionToken(458293458L, "WorkDefTeamDemoChangeRequest");
   public static AtsWorkDefinitionToken WorkDefTeamDemoCode = new AtsWorkDefinitionToken(48427403L, "WorkDefTeamDemoCode");
   public static AtsWorkDefinitionToken WorkDefTeamDemoSwDesign = new AtsWorkDefinitionToken(3625963L, "WorkDefTeamDemoSwDesign");
   public static AtsWorkDefinitionToken WorkDefTeamDemoTest = new AtsWorkDefinitionToken(2892554L, "WorkDefTeamDemoTest");
   public static AtsWorkDefinitionToken WorkDefTeamDemoReq = new AtsWorkDefinitionToken(46891154L, "WorkDefTeamDemoReq");
   public static AtsWorkDefinitionToken WorkDefTeamDemoReqSimple = new AtsWorkDefinitionToken(25391621L, "WorkDefTeamDemoReqSimple");

   public static AtsWorkDefinitionToken WorkDefReviewPeerDemoSwDesign = new AtsWorkDefinitionToken(2342662L, "WorkDefReviewPeerDemoSwDesign");

   public static AtsWorkDefinitionToken WorkDefTaskDemoSwDesign = new AtsWorkDefinitionToken(2234432L, "WorkDefTaskDemoSwDesign");
   public static AtsWorkDefinitionToken WorkDefTaskDemoForCrEstimating = new AtsWorkDefinitionToken(32922123L, "WorkDefTaskDemoForCrEstimating");
   // @formatter:on

}
