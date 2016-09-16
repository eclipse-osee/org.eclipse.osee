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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactToken {

   public static final ArtifactToken AtsConfig =
      TokenFactory.createArtifactToken(5367053, "Af7L9SYYgCcBCKZ8CCwA", "ATS Config", CoreArtifactTypes.GeneralData);
   public static final ArtifactToken RuleDefinitions = TokenFactory.createArtifactToken(7880473,
      "Ar1Z9KIRtUZgcxbnyFwA", "Rule Definitions", AtsArtifactTypes.RuleDefinition);
   public static final ArtifactToken Users =
      TokenFactory.createArtifactToken(95459, "AAABGTAGb+8BauB5Cw1QNA", "Users", CoreArtifactTypes.Folder);
   public static ArtifactToken HeadingFolder = TokenFactory.createArtifactToken(114713, "AAABER+3yR4A8O7WYQ+Xbw",
      "Action Tracking System", CoreArtifactTypes.Folder);
   public static ArtifactToken TopTeamDefinition =
      TokenFactory.createArtifactToken(56004, "AAABER+35b4A8O7WHrXTiA", "Teams", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken TopActionableItem = TokenFactory.createArtifactToken(122894, "AAABER+37QEA8O7WSQaqJQ",
      "Actionable Items", AtsArtifactTypes.ActionableItem);
   public static ArtifactToken CountryFolder =
      TokenFactory.createArtifactToken(7968155, "AYi8V8esln8KJkjesqAA", "Countries", CoreArtifactTypes.Folder);
   public static ArtifactToken ProgramFolder =
      TokenFactory.createArtifactToken(90442279, "AYi8V9LSJgjD2734BqQA", "Programs", CoreArtifactTypes.Folder);
   public static ArtifactToken ConfigFolder =
      TokenFactory.createArtifactToken(113036, "AAABF4n18eYAc1ruQSSWdg", "Config", CoreArtifactTypes.Folder);
   public static ArtifactToken ConfigsFolder =
      TokenFactory.createArtifactToken(5086714, "BEmf1DEFnwkUqC9o3hwA", "Configs", CoreArtifactTypes.Folder);
   public static ArtifactToken WorkDefinitionsFolder =
      TokenFactory.createArtifactToken(284655, "ADTfjCLEj2DH2WYyeOgA", "Work Definitions", CoreArtifactTypes.Folder);
   public static ArtifactToken WebPrograms = TokenFactory.createArtifactToken(277592, "Awsk_RtncCochcuSxagA",
      "Web Programs", CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken EVReportPrograms = TokenFactory.createArtifactToken(8174118, "ABPMYxe8_1EZYA8obTQA",
      "EV Report Programs", CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken AtsAdmin =
      TokenFactory.createArtifactToken(136750, "AAABHaItoVsAG6ZAAMyhQw", "AtsAdmin", CoreArtifactTypes.UserGroup);
   public static ArtifactToken AtsTempAdmin =
      TokenFactory.createArtifactToken(5367074, "AAABHaItoVsAG7ZAAMyhQw", "AtsTempAdmin", CoreArtifactTypes.UserGroup);

   // Default Work Definitions
   public static ArtifactToken WorkDef_Goal =
      TokenFactory.createArtifactToken(142177, "BAB1YhIbUQWluN2+SVgA", "WorkDef_Goal", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Review_Decision = TokenFactory.createArtifactToken(25335,
      "A__fwv3zpnS2oEVTaDQA", "WorkDef_Review_Decision", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Review_PeerToPeer = TokenFactory.createArtifactToken(25334,
      "A__WLyb77Cl_diWCUmAA", "WorkDef_Review_PeerToPeer", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Task_Default = TokenFactory.createArtifactToken(105373, "BABAJntqsGoKosWZtKgA",
      "WorkDef_Task_Default", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Team_Default = TokenFactory.createArtifactToken(72301, "BADtJXMuZ1e9+5AgyRwA",
      "WorkDef_Team_Default", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Team_Simple = TokenFactory.createArtifactToken(72302, "BAEWlOp78DShE_gJymAA",
      "WorkDef_Team_Simple", AtsArtifactTypes.WorkDefinition);

   // Safety Workflow configuration
   public static final ArtifactToken SafetyActionableItem = TokenFactory.createArtifactToken(7244546,
      "AKDKq_Ar2WLw+ntiAVgA", "System Safety", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SafetyTeamDefinition = TokenFactory.createArtifactToken(7241589,
      "AKDG_MAfzDSILQB_crAA", "System Safety Team", AtsArtifactTypes.TeamDefinition);

   // Agile
   public static ArtifactToken TopAgileFolder =
      TokenFactory.createArtifactToken(6915493, "APFgz7OwBmpcrGt0DbQA", "Agile", CoreArtifactTypes.Folder);

   private AtsArtifactToken() {
      // Constants
   }

}
