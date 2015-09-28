/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactToken {

   public static final IArtifactToken AtsConfig =
      TokenFactory.createArtifactToken(5367053, "Af7L9SYYgCcBCKZ8CCwA", "ATS Config", CoreArtifactTypes.GeneralData);
   public static final IArtifactToken RuleDefinitions = TokenFactory.createArtifactToken(7880473,
      "Ar1Z9KIRtUZgcxbnyFwA", "Rule Definitions", AtsArtifactTypes.RuleDefinition);
   public static final IArtifactToken Users =
      TokenFactory.createArtifactToken(95459, "AAABGTAGb+8BauB5Cw1QNA", "Users", CoreArtifactTypes.Folder);
   public static IArtifactToken HeadingFolder = TokenFactory.createArtifactToken(114713, "AAABER+3yR4A8O7WYQ+Xbw",
      "Action Tracking System", CoreArtifactTypes.Folder);
   public static IArtifactToken TopTeamDefinition =
      TokenFactory.createArtifactToken(56004, "AAABER+35b4A8O7WHrXTiA", "Teams", AtsArtifactTypes.TeamDefinition);
   public static IArtifactToken TopActionableItem = TokenFactory.createArtifactToken(122894, "AAABER+37QEA8O7WSQaqJQ",
      "Actionable Items", AtsArtifactTypes.ActionableItem);
   public static IArtifactToken CountryFolder =
      TokenFactory.createArtifactToken(7968155, "AYi8V8esln8KJkjesqAA", "Countries", CoreArtifactTypes.Folder);
   public static IArtifactToken ProgramFolder =
      TokenFactory.createArtifactToken(90442279, "AYi8V9LSJgjD2734BqQA", "Programs", CoreArtifactTypes.Folder);
   public static IArtifactToken ConfigFolder =
      TokenFactory.createArtifactToken(113036, "AAABF4n18eYAc1ruQSSWdg", "Config", CoreArtifactTypes.Folder);
   public static IArtifactToken ConfigsFolder =
      TokenFactory.createArtifactToken(5086714, "BEmf1DEFnwkUqC9o3hwA", "Configs", CoreArtifactTypes.Folder);
   public static IArtifactToken WorkDefinitionsFolder =
      TokenFactory.createArtifactToken(284655, "ADTfjCLEj2DH2WYyeOgA", "Work Definitions", CoreArtifactTypes.Folder);
   public static IArtifactToken WebPrograms = TokenFactory.createArtifactToken(277592, "Awsk_RtncCochcuSxagA",
      "Web Programs", CoreArtifactTypes.UniversalGroup);
   public static IArtifactToken WorkDef_State_Names = TokenFactory.createArtifactToken(1330130, "BFqfTrN8W3QmQSFAi6wA",
      "WorkDef_State_Names", CoreArtifactTypes.GeneralData);
   public static IArtifactToken AtsAdmin =
      TokenFactory.createArtifactToken(136750, "AAABHaItoVsAG6ZAAMyhQw", "AtsAdmin", CoreArtifactTypes.UserGroup);
   public static IArtifactToken AtsTempAdmin =
      TokenFactory.createArtifactToken(5367074, "AAABHaItoVsAG7ZAAMyhQw", "AtsTempAdmin", CoreArtifactTypes.UserGroup);

   // Default Work Definitions
   public static IArtifactToken WorkDef_Goal =
      TokenFactory.createArtifactToken(142177, "BAB1YhIbUQWluN2+SVgA", "WorkDef_Goal", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Review_Decision = TokenFactory.createArtifactToken(25335,
      "A__fwv3zpnS2oEVTaDQA", "WorkDef_Review_Decision", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Review_PeerToPeer = TokenFactory.createArtifactToken(25334,
      "A__WLyb77Cl_diWCUmAA", "WorkDef_Review_PeerToPeer", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Task_Default = TokenFactory.createArtifactToken(105373, "BABAJntqsGoKosWZtKgA",
      "WorkDef_Task_Default", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Team_Default = TokenFactory.createArtifactToken(72301, "BADtJXMuZ1e9+5AgyRwA",
      "WorkDef_Team_Default", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Team_Simple = TokenFactory.createArtifactToken(72302, "BAEWlOp78DShE_gJymAA",
      "WorkDef_Team_Simple", AtsArtifactTypes.WorkDefinition);

   // Safety Workflow configuration
   public static final IArtifactToken SafetyActionableItem = TokenFactory.createArtifactToken(7244546,
      "AKDKq_Ar2WLw+ntiAVgA", "System Safety", AtsArtifactTypes.ActionableItem);
   public static final IArtifactToken SafetyTeamDefinition = TokenFactory.createArtifactToken(7241589,
      "AKDG_MAfzDSILQB_crAA", "System Safety Team", AtsArtifactTypes.TeamDefinition);

   // Agile
   public static IArtifactToken TopAgileFolder =
      TokenFactory.createArtifactToken(6915493, "APFgz7OwBmpcrGt0DbQA", "Agile", CoreArtifactTypes.Folder);

   private AtsArtifactToken() {
      // Constants
   }

}
