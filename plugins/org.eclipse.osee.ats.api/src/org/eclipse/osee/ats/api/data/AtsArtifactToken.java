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

   public static final IArtifactToken AtsConfig = TokenFactory.createArtifactToken("Af7L9SYYgCcBCKZ8CCwA", "AtsConfig",
      CoreArtifactTypes.GeneralData);
   public static final IArtifactToken Users = TokenFactory.createArtifactToken("AAABGTAGb+8BauB5Cw1QNA", "Users",
      CoreArtifactTypes.Folder);
   public static IArtifactToken HeadingFolder = TokenFactory.createArtifactToken("AAABER+3yR4A8O7WYQ+Xbw",
      "Action Tracking System", CoreArtifactTypes.Folder);
   public static IArtifactToken TopTeamDefinition = TokenFactory.createArtifactToken("AAABER+35b4A8O7WHrXTiA", "Teams",
      AtsArtifactTypes.TeamDefinition);
   public static IArtifactToken TopActionableItem = TokenFactory.createArtifactToken("AAABER+37QEA8O7WSQaqJQ",
      "Actionable Items", AtsArtifactTypes.ActionableItem);
   public static IArtifactToken ConfigFolder = TokenFactory.createArtifactToken("AAABF4n18eYAc1ruQSSWdg", "Config",
      CoreArtifactTypes.Folder);
   public static IArtifactToken ConfigsFolder = TokenFactory.createArtifactToken("BEmf1DEFnwkUqC9o3hwA", "Configs",
      CoreArtifactTypes.Folder);
   public static IArtifactToken WorkDefinitionsFolder = TokenFactory.createArtifactToken("ADTfjCLEj2DH2WYyeOgA",
      "Work Definitions", CoreArtifactTypes.Folder);
   public static IArtifactToken WebPrograms = TokenFactory.createArtifactToken("Awsk_RtncCochcuSxagA", "Web Programs",
      CoreArtifactTypes.UniversalGroup);
   public static IArtifactToken WorkDef_State_Names = TokenFactory.createArtifactToken("BFqfTrN8W3QmQSFAi6wA",
      "WorkDef_State_Names", CoreArtifactTypes.GeneralData);
   public static IArtifactToken AtsAdmin = TokenFactory.createArtifactToken("AAABHaItoVsAG6ZAAMyhQw", "AtsAdmin",
      CoreArtifactTypes.UserGroup);
   public static IArtifactToken AtsTempAdmin = TokenFactory.createArtifactToken("AAABHaItoVsAG7ZAAMyhQw",
      "AtsTempAdmin", CoreArtifactTypes.UserGroup);

   // Default Work Definitions
   public static IArtifactToken WorkDef_Goal = TokenFactory.createArtifactToken("BAB1YhIbUQWluN2+SVgA", "WorkDef_Goal",
      AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Review_Decision = TokenFactory.createArtifactToken("A__fwv3zpnS2oEVTaDQA",
      "WorkDef_Review_Decision", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Review_PeerToPeer = TokenFactory.createArtifactToken("A__WLyb77Cl_diWCUmAA",
      "WorkDef_Review_PeerToPeer", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Task_Default = TokenFactory.createArtifactToken("BABAJntqsGoKosWZtKgA",
      "WorkDef_Task_Default", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Team_Default = TokenFactory.createArtifactToken("BADtJXMuZ1e9+5AgyRwA",
      "WorkDef_Team_Default", AtsArtifactTypes.WorkDefinition);
   public static IArtifactToken WorkDef_Team_Simple = TokenFactory.createArtifactToken("BAEWlOp78DShE_gJymAA",
      "WorkDef_Team_Simple", AtsArtifactTypes.WorkDefinition);

   private AtsArtifactToken() {
      // Constants
   }

}
